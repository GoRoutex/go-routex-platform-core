package platform.core.common.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import platform.core.common.service.application.service.EntityPartitionService;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static platform.core.common.service.persistence.constant.ApplicationConstant.DEFAULT_ZONE;

@RequiredArgsConstructor
@Service
@Slf4j
public class EntityPartitionServiceImpl implements EntityPartitionService {

    private static final DateTimeFormatter PARTITION_BOUND_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");

    private final JdbcTemplate jdbcTemplate;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void ensureTripPartition(OffsetDateTime departureTime) {
        if(departureTime == null) {
            return;
        }

        YearMonth yearMonth = YearMonth.from(departureTime.atZoneSameInstant(DEFAULT_ZONE));
        String partitionName = "trip_" + String.format("%02d", yearMonth.getMonthValue()) + yearMonth.getYear();

        // Example: trip_062026 covers Vietnam business time [2026-06-01T00:00+07, 2026-07-01T00:00+07).
        OffsetDateTime start = atStartOfBusinessMonth(yearMonth);
        OffsetDateTime end = atStartOfBusinessMonth(yearMonth.plusMonths(1));

        ensureRangePartition("TRIP", partitionName, start, end);
    }

    @Override
    public void ensureTicketPartition(OffsetDateTime issuedAt) {
        if(issuedAt == null) {
            return;
        }

        YearMonth yearMonth = YearMonth.from(issuedAt.atZoneSameInstant(DEFAULT_ZONE));
        String partitionName = "ticket_" + String.format("%02d", yearMonth.getMonthValue()) + yearMonth.getYear();

        OffsetDateTime start = atStartOfBusinessMonth(yearMonth);
        OffsetDateTime end = atStartOfBusinessMonth(yearMonth.plusMonths(1));

        ensureRangePartition("TICKET", partitionName, start, end);
    }

    private OffsetDateTime atStartOfBusinessMonth(YearMonth yearMonth) {
        ZoneOffset offset = DEFAULT_ZONE.getRules().getOffset(yearMonth.atDay(1).atStartOfDay());
        return yearMonth.atDay(1).atStartOfDay().atOffset(offset);
    }

    private void ensureRangePartition(String parentTable, String partitionName, OffsetDateTime start, OffsetDateTime end) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        try {
            transactionTemplate.executeWithoutResult(status -> {
                jdbcTemplate.execute("SELECT pg_advisory_xact_lock(hashtext('" + partitionName + "'))");
                jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS %s
                        PARTITION OF %s
                        FOR VALUES FROM ('%s') TO ('%s')
                        """.formatted(
                                partitionName,
                                parentTable,
                                start.format(PARTITION_BOUND_FORMATTER),
                                end.format(PARTITION_BOUND_FORMATTER)
                        ));
            });
        } catch (DataAccessException ex) {
            if (isOverlappingPartitionError(ex)) {
                log.warn("Skip creating partition {} because an existing partition already overlaps range [{} - {}). "
                                + "Please normalize database partitions during maintenance. cause={}",
                        partitionName, start, end, ex.getMostSpecificCause().getMessage());
                return;
            }
            throw ex;
        }
    }

    private boolean isOverlappingPartitionError(DataAccessException ex) {
        String message = ex.getMostSpecificCause() == null ? ex.getMessage() : ex.getMostSpecificCause().getMessage();
        return message != null && message.contains("would overlap partition");
    }
}
