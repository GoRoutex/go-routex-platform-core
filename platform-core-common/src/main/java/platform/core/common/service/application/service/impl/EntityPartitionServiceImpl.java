package platform.core.common.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import platform.core.common.service.application.service.EntityPartitionService;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;

@RequiredArgsConstructor
@Service
public class EntityPartitionServiceImpl implements EntityPartitionService {

    private final JdbcTemplate jdbcTemplate;
    @Override
    public void ensureTripPartition(OffsetDateTime departureTime) {
        if(departureTime == null) {
            return;
        }

        YearMonth yearMonth = YearMonth.from(departureTime);
        String partitionName = "trip_" + String.format("%02d", yearMonth.getMonthValue()) + yearMonth.getYear();

        jdbcTemplate.execute("SELECT pg_advisory_lock(hashtext('" + partitionName + "'))");

        // Example of creating table trip_062026 (uses for 01/06 -> 01/07)
        try {
            LocalDate start = yearMonth.atDay(1);
            LocalDate end = yearMonth.plusMonths(1).atDay(1);

            String query = """
                        CREATE TABLE IF NOT EXISTS %s
                        PARTITION OF TRIP
                        FOR VALUES FROM ('%s') TO ('%s')
                    """.formatted(partitionName, start, end);

            jdbcTemplate.execute(query);
        } finally {
            jdbcTemplate.execute("SELECT pg_advisory_unlock(hashtext('" + partitionName + "'))");
        }
    }

    @Override
    public void ensureTicketPartition(OffsetDateTime issuedAt) {
        if(issuedAt == null) {
            return;
        }

        YearMonth yearMonth = YearMonth.from(issuedAt);
        String partitionName = "ticket_" + String.format("%02d", yearMonth.getMonthValue()) + yearMonth.getYear();

        jdbcTemplate.execute("SELECT pg_advisory_lock(hashtext('" + partitionName + "'))");
        try {
            LocalDate start = yearMonth.atDay(1);
            LocalDate end = yearMonth.plusMonths(1).atDay(1);

            String query = """
                    
                    CREATE TABLE IF NOT EXISTS %s
                    PARTITION OF TICKET
                    FOR VALUES FROM ('%s') TO ('%s')
                    """.formatted(partitionName, start, end);

            jdbcTemplate.execute(query);

        } finally {
            jdbcTemplate.execute("SELECT pg_advisory_unlock(hashtext('" + partitionName + "'))");
        }
    }
}
