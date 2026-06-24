package platform.management.service.application.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import platform.core.common.service.domain.seat.model.TripSeat;
import platform.core.common.service.domain.seat.port.TripSeatRepositoryPort;
import platform.core.common.service.infrastructure.redis.models.TripCacheSeat;
import platform.core.common.service.infrastructure.redis.service.TripSeatCacheService;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.management.service.application.command.seat.SearchRoundTripSeatResult;
import platform.management.service.application.command.seat.SearchSeatCommand;
import platform.management.service.application.command.seat.SearchSeatResult;
import platform.management.service.application.services.TripSeatService;
import platform.merchant.service.domain.seat.model.SeatTemplate;
import platform.merchant.service.domain.seat.port.SeatTemplateRepositoryPort;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;
import static platform.core.common.service.persistence.constant.ErrorConstant.ROUTE_SEAT_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class TripSeatServiceImpl implements TripSeatService {

    private final TripSeatRepositoryPort tripSeatRepositoryPort;
    private final SeatTemplateRepositoryPort seatTemplateRepositoryPort;
    private final TripSeatCacheService tripSeatCacheService;

    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Override
    public SearchSeatResult searchSeat(SearchSeatCommand command) {

        sLog.info("[SEARCH-SEAT] Search Seat Command: {}", command);
        List<TripCacheSeat> cacheSeats = tripSeatCacheService.getSeats(command.tripId());

        if(cacheSeats.isEmpty()) {
            List<TripSeat> seatLists = tripSeatRepositoryPort.findAllByTripIdOrderBySeatNoAsc(command.tripId());

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
                    .sorted(Comparator.comparing(TripCacheSeat::getSeatNo))
                    .toList();
            tripSeatCacheService.putSeats(command.tripId(), cacheSeats);
        }
        List<SearchSeatResult.SearchSeatResultData> seats = cacheSeats.stream()
                .map(rs -> SearchSeatResult.SearchSeatResultData.builder()
                        .seatId(rs.getSeatId())
                        .floor(rs.getFloor())
                        .code(rs.getSeatNo())
                        .status(rs.getStatus())
                        .rowNo(rs.getRowNo())
                        .colNo(rs.getColNo())
                        .build())
                .sorted(Comparator.comparing(SearchSeatResult.SearchSeatResultData::code))
                .toList();

        sLog.info("[SEARCH-SEAT] Search Seat Result Data: {}", seats);
        return SearchSeatResult.builder()
                .data(seats)
                .build();
    }

    @Override
    public SearchRoundTripSeatResult searchRoundTripSeat(SearchSeatCommand outboundCommand, SearchSeatCommand returnCommand) {
        return SearchRoundTripSeatResult.builder()
                .outboundSeats(searchSeat(outboundCommand))
                .returnSeats(searchSeat(returnCommand))
                .build();
    }
}
