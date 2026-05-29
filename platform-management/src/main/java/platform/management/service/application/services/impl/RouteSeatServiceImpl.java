package platform.management.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.go.routex.identity.security.log.SystemLog;
import platform.management.service.application.command.seat.SearchSeatCommand;
import platform.management.service.application.command.seat.SearchSeatResult;
import platform.management.service.application.services.RouteSeatService;
import platform.core.common.service.domain.seat.model.SeatTemplate;
import platform.core.common.service.domain.seat.model.TripSeat;
import platform.core.common.service.domain.seat.port.RouteSeatRepositoryPort;
import platform.core.common.service.domain.seat.port.SeatTemplateRepositoryPort;
import platform.management.service.infrastructure.cache.redis.models.TripCacheSeat;
import platform.management.service.infrastructure.cache.redis.service.TripSeatCacheService;
import platform.management.service.infrastructure.persistence.exception.BusinessException;
import platform.management.service.infrastructure.persistence.utils.ExceptionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.management.service.infrastructure.persistence.constant.ErrorConstant.ROUTE_SEAT_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class RouteSeatServiceImpl implements RouteSeatService {

    private final RouteSeatRepositoryPort routeSeatRepositoryPort;
    private final SeatTemplateRepositoryPort seatTemplateRepositoryPort;
    private final TripSeatCacheService tripSeatCacheService;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public SearchSeatResult searchSeat(SearchSeatCommand command) {

        sLog.info("[SEARCH-SEAT] Search Seat Command: {}", command);
        List<TripCacheSeat> cacheSeats = tripSeatCacheService.getSeats(command.tripId());

        if(cacheSeats.isEmpty()) {
            List<TripSeat> seatLists = routeSeatRepositoryPort.findAllByTripIdOrderBySeatNoAsc(command.tripId());

            if (seatLists.isEmpty()) {
                throw new BusinessException(command.context().requestId(), command.context().requestDateTime(), command.context().channel(),
                        ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, String.format(ROUTE_SEAT_NOT_FOUND, command.tripId())));
            }

            Set<String> seatTemplateIds = seatLists
                    .stream()
                    .map(TripSeat::getSeatTemplateId)
                    .collect(Collectors.toSet());

            List<SeatTemplate> seatTemplates = seatTemplateRepositoryPort.findAllByIdIn(seatTemplateIds);

            Map<String, SeatTemplate> templateMap = seatTemplates.stream()
                    .collect(Collectors.toMap(
                            SeatTemplate::getId,
                            Function.identity()
                            ));

            cacheSeats = seatLists.stream()
                    .map(s -> {
                        SeatTemplate seatTemplate = templateMap.get(s.getSeatTemplateId());
                        return TripCacheSeat.builder()
                                .seatId(s.getId())
                                .seatTemplateId(s.getSeatTemplateId())
                                .tripId(s.getTripId())
                                .seatNo(s.getSeatNo())
                                .status(s.getStatus())
                                .floor(seatTemplate != null ? seatTemplate.getFloor() : null)
                                .colNo(seatTemplate != null ? seatTemplate.getColumnNo() : 0)
                                .rowNo(seatTemplate != null ? seatTemplate.getRowNo() : 0)
                                .build();
                    })
                    .sorted(Comparator.comparing(TripCacheSeat::seatNo))
                    .toList();
            tripSeatCacheService.putSeats(command.tripId(), cacheSeats);
        }
        List<SearchSeatResult.SearchSeatResultData> seats = cacheSeats.stream()
                .map(rs -> SearchSeatResult.SearchSeatResultData.builder()
                        .seatId(rs.seatId())
                        .floor(rs.floor())
                        .code(rs.seatNo())
                        .status(rs.status())
                        .rowNo(rs.rowNo())
                        .colNo(rs.colNo())
                        .build())
                .sorted(Comparator.comparing(SearchSeatResult.SearchSeatResultData::code))
                .toList();

        sLog.info("[SEARCH-SEAT] Search Seat Result Data: {}", seats);
        return SearchSeatResult.builder()
                .data(seats)
                .build();
    }
}
