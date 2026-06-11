package platform.merchant.service.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import platform.core.common.service.domain.ticket.TicketStatus;
import platform.core.common.service.domain.ticket.model.Ticket;
import platform.core.common.service.domain.ticket.port.TicketRepositoryPort;
import platform.core.common.service.persistence.exception.BusinessException;
import platform.core.common.service.persistence.utils.ExceptionUtils;
import platform.merchant.service.application.command.email.TicketEmailCommand;
import platform.merchant.service.application.command.ticket.CreateTicketCommand;
import platform.merchant.service.application.command.ticket.CreateTicketResult;
import platform.merchant.service.application.command.ticket.FetchCustomerTicketsQuery;
import platform.merchant.service.application.command.ticket.FetchTicketDetailQuery;
import platform.merchant.service.application.command.ticket.FetchTicketDetailResult;
import platform.merchant.service.application.command.ticket.FetchTicketListQuery;
import platform.merchant.service.application.command.ticket.FetchTicketListResult;
import platform.merchant.service.application.command.ticket.SearchTicketListQuery;
import platform.merchant.service.application.command.ticket.UpdateTicketCommand;
import platform.merchant.service.application.command.ticket.UpdateTicketResult;
import platform.merchant.service.application.event.ticket.TicketIssuedEvent;
import platform.merchant.service.application.service.TicketService;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.campaign.model.Campaign;
import platform.merchant.service.domain.campaign.port.CampaignRepositoryPort;
import platform.merchant.service.domain.driver.model.DriverProfile;
import platform.merchant.service.domain.driver.port.DriverProfileRepositoryPort;
import platform.merchant.service.domain.finance.model.RevenueTransaction;
import platform.merchant.service.domain.finance.port.RevenueRepositoryPort;
import platform.merchant.service.domain.merchant.model.Merchant;
import platform.merchant.service.domain.merchant.port.MerchantRepositoryPort;
import platform.merchant.service.domain.route.model.RouteAggregate;
import platform.merchant.service.domain.route.port.RouteAggregateRepositoryPort;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.model.VehicleTemplate;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import platform.merchant.service.domain.vehicle.port.VehicleTemplateRepositoryPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static platform.core.common.service.persistence.constant.ErrorConstant.INVALID_INPUT_ERROR;
import static platform.core.common.service.persistence.constant.ErrorConstant.RECORD_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepositoryPort ticketRepositoryPort;
    private final MerchantRepositoryPort merchantRepositoryPort;
    private final RevenueRepositoryPort revenueRepositoryPort;
    private final TripAggregateRepositoryPort tripRepositoryPort;
    private final ApplicationEventPublisher eventPublisher;
    private final RouteAggregateRepositoryPort routeRepositoryPort;
    private final TripAssignmentRepositoryPort assignmentRepositoryPort;
    private final DriverProfileRepositoryPort driverProfileRepositoryPort;
    private final VehicleProfileRepositoryPort vehicleProfileRepositoryPort;
    private final VehicleTemplateRepositoryPort vehicleTemplateRepositoryPort;
    private final CampaignRepositoryPort campaignRepositoryPort;

    @Override
    @Transactional
    public List<CreateTicketResult> createTickets(List<CreateTicketCommand> commands) {
        if (commands.isEmpty()) return List.of();

        // 1. Caches to minimize repository calls
        Map<String, Merchant> merchantCache = commands.stream()
                .map(CreateTicketCommand::merchantId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> merchantRepositoryPort.findById(id)
                                .orElseThrow(() -> new BusinessException(ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Merchant not found: " + id)))
                ));

        // 2. Caches to minimize repository calls
        Map<String, TripAggregate> tripCache = new HashMap<>();

        return commands.stream().map(command -> {
            Merchant merchant = merchantCache.get(command.merchantId());
            BigDecimal commissionRate = merchant.getCommissionRate() != null ? merchant.getCommissionRate() : BigDecimal.ZERO;

            Ticket savedTicket = ticketRepositoryPort.save(buildTicket(command));

            // 1. Handle Promotion
            PromotionResult promoResult = handlePromotion(command, merchant.getId());

            // 2. Process Financial Records (Revenue & Stats)
            processFinancialRecords(command, savedTicket.getId(), merchant.getId(), commissionRate, promoResult);

            // 3. Post-creation processes (Analytics, Email, Notifications)
            handleTripAnalyticsAndNotification(command, merchant.getId(), savedTicket, tripCache);

            return CreateTicketResult.builder()
                    .ticketId(savedTicket.getId())
                    .ticketCode(savedTicket.getTicketCode())
                    .bookingSeatId(savedTicket.getBookingSeatId())
                    .status(savedTicket.getStatus())
                    .build();
        }).collect(Collectors.toList());
    }

    private Ticket buildTicket(CreateTicketCommand command) {
        return Ticket.builder()
                .id(UUID.randomUUID().toString())
                .ticketCode(ticketRepositoryPort.generateTicketCode())
                .bookingId(command.bookingId())
                .bookingSeatId(command.bookingSeatId())
                .merchantId(command.merchantId())
                .tripId(command.tripId())
                .vehicleId(command.vehicleId())
                .seatNumber(command.seatNumber())
                .customerName(command.customerName())
                .customerPhone(command.customerPhone())
                .customerEmail(command.customerEmail())
                .price(command.price())
                .status(TicketStatus.ISSUED)
                .issuedAt(command.issuedAt() != null ? command.issuedAt() : OffsetDateTime.now())
                .createdBy(command.creator())
                .createdAt(OffsetDateTime.now())
                .pickupType(command.pickupType())
                .pickupStopId(command.pickupStopId())
                .pickupAddress(command.pickupAddress())
                .dropOffType(command.dropOffType())
                .dropOffStopId(command.dropOffStopId())
                .dropOffAddress(command.dropOffAddress())
                .build();
    }

    private PromotionResult handlePromotion(CreateTicketCommand command, String merchantId) {
        BigDecimal discountAmount = BigDecimal.ZERO;
        String campaignId = null;

        if (command.promotionCode() != null && !command.promotionCode().isBlank()) {
            Campaign campaign = campaignRepositoryPort.findByPromotionCode(command.promotionCode()).orElse(null);
            if (campaign != null && campaign.getMerchantId().equals(merchantId) && campaign.isAvailable()) {
                discountAmount = campaign.calculateDiscount(command.price());
                if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                    campaignId = campaign.getId();
                    campaign.setUsedCount(campaign.getUsedCount() + 1);
                    campaignRepositoryPort.save(campaign);
                }
            }
        }
        return new PromotionResult(discountAmount, campaignId);
    }

    private void processFinancialRecords(CreateTicketCommand command, String ticketId, String merchantId,
                                         BigDecimal commissionRate, PromotionResult promoResult) {
        BigDecimal finalAmount = command.price().subtract(promoResult.discountAmount());
        BigDecimal systemAmount = finalAmount.multiply(commissionRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal merchantAmount = finalAmount.subtract(systemAmount);

        RevenueTransaction revenueTransaction = RevenueTransaction.builder()
                .id(UUID.randomUUID().toString())
                .ticketId(ticketId)
                .merchantId(merchantId)
                .totalAmount(command.price())
                .discountAmount(promoResult.discountAmount())
                .campaignId(promoResult.campaignId())
                .commissionRate(commissionRate)
                .systemAmount(systemAmount)
                .merchantAmount(merchantAmount)
                .transactionDate(OffsetDateTime.now())
                .createdAt(OffsetDateTime.now())
                .createdBy(command.creator())
                .build();

        revenueRepositoryPort.save(revenueTransaction);
    }

    private void handleTripAnalyticsAndNotification(CreateTicketCommand command, String merchantId,
                                                   Ticket savedTicket, Map<String, TripAggregate> tripCache) {
        TripAggregate trip = tripCache.computeIfAbsent(command.tripId(),
                id -> tripRepositoryPort.findById(id).orElse(null));

        if (trip != null) {
            // Build Command for Email and Publish Event
            TicketEmailCommand emailCommand = buildEmailCommand(savedTicket, trip);
            if (emailCommand != null) {
                eventPublisher.publishEvent(new TicketIssuedEvent(emailCommand));
            }
        }
    }

    private TicketEmailCommand buildEmailCommand(Ticket ticket, TripAggregate trip) {
        if (ticket.getCustomerEmail() == null || ticket.getCustomerEmail().isBlank()) return null;

        RouteAggregate route = routeRepositoryPort.findById(trip.getRouteId()).orElse(null);
        TripAssignmentRecord assignment = assignmentRepositoryPort.findActiveByTripId(trip.getId()).orElse(null);

        TicketEmailCommand.TicketEmailCommandBuilder emailBuilder = TicketEmailCommand.builder()
                .toEmail(ticket.getCustomerEmail())
                .customerName(ticket.getCustomerName())
                .ticketCode(ticket.getTicketCode())
                .seatNumber(ticket.getSeatNumber())
                .price(ticket.getPrice())
                .departureTime(trip.getDepartureTime())
                .routeName(route != null ? route.getOriginName() + " - " + route.getDestinationName() : "N/A")
                .startPoint(route != null ? route.getOriginDepartmentName() : "N/A")
                .endPoint(route != null ? route.getDestinationDepartmentName() : "N/A");

        if (assignment != null) {
            DriverProfile driver = driverProfileRepositoryPort.findById(assignment.getDriverId()).orElse(null);
            VehicleProfile vehicle = vehicleProfileRepositoryPort.findById(assignment.getVehicleId()).orElse(null);

            if (driver != null) {
                emailBuilder.driverName(driver.getFullName())
                        .driverPhone(driver.getPhoneNumber());
            }

            if (vehicle != null) {
                VehicleTemplate template = vehicleTemplateRepositoryPort.findById(vehicle.getTemplateId()).orElse(null);
                emailBuilder.vehiclePlate(vehicle.getVehiclePlate());
                if (template != null) {
                    emailBuilder.vehicleType(template.getName());
                }
            }
        }
        return emailBuilder.build();
    }

    private record PromotionResult(BigDecimal discountAmount, String campaignId) {}

    @Override
    @Transactional
    public UpdateTicketResult updateTicket(UpdateTicketCommand command) {
        Ticket existingTicket = ticketRepositoryPort.findById(command.ticketId())
                .orElseThrow(() -> new BusinessException(ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Ticket not found")));

        // Security check: Ensure ticket belongs to merchant
        if (!existingTicket.getMerchantId().equals(command.merchantId())) {
            throw new BusinessException(ExceptionUtils.buildResultResponse("ACCESS_DENIED", "You do not have permission to update this ticket"));
        }

        // Only update certain fields as per merchant management needs
        if (command.customerName() != null) existingTicket.setCustomerName(command.customerName());
        if (command.customerPhone() != null) existingTicket.setCustomerPhone(command.customerPhone());
        if (command.customerEmail() != null) existingTicket.setCustomerEmail(command.customerEmail());
        if (command.status() != null) existingTicket.setStatus(command.status());

        Ticket saved = ticketRepositoryPort.save(existingTicket);
        return UpdateTicketResult.builder()
                .ticketId(saved.getId())
                .status(saved.getStatus())
                .build();
    }

    @Override
    public FetchTicketDetailResult getTicketDetail(FetchTicketDetailQuery query) {
        Ticket ticket = ticketRepositoryPort.findById(query.ticketId())
                .orElseThrow(() -> new BusinessException(ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Ticket not found")));

        // Security check
        if (!ticket.getMerchantId().equals(query.merchantId())) {
            throw new BusinessException(ExceptionUtils.buildResultResponse("ACCESS_DENIED", "You do not have permission to view this ticket"));
        }

        return FetchTicketDetailResult.builder()
                .ticket(ticket)
                .build();
    }

    @Override
    public FetchTicketListResult getTickets(FetchTicketListQuery query) {
        return fetchMerchantTickets(
                query.merchantId(),
                null,
                query.status(),
                query.month(),
                query.pageNumber(),
                query.pageSize()
        );
    }

    @Override
    public FetchTicketListResult searchTickets(SearchTicketListQuery query) {
        String keyword = normalizeQuery(query.keyword());
        if (keyword == null) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "keyword is required"));
        }
        return fetchMerchantTickets(
                query.merchantId(),
                keyword,
                query.status(),
                query.month(),
                query.pageNumber(),
                query.pageSize()
        );
    }

    private FetchTicketListResult fetchMerchantTickets(String merchantId,
                                                       String keyword,
                                                       TicketStatus status,
                                                       String month,
                                                       int pageNumber,
                                                       int pageSize) {
        MonthRange monthRange = resolveMonthRange(month);
        Page<Ticket> page = ticketRepositoryPort.findByMerchantFilters(
                merchantId,
                keyword,
                status,
                monthRange.from(),
                monthRange.to(),
                PageRequest.of(pageNumber - 1, pageSize)
        );

        return FetchTicketListResult.builder()
                .items(page.getContent())
                .pageNumber(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private MonthRange resolveMonthRange(String month) {
        if (month == null || month.isBlank()) {
            return new MonthRange(null, null);
        }
        try {
            YearMonth yearMonth = YearMonth.parse(month.trim());
            ZoneId zoneId = ZoneId.systemDefault();
            OffsetDateTime from = yearMonth.atDay(1).atStartOfDay(zoneId).toOffsetDateTime();
            OffsetDateTime to = yearMonth.plusMonths(1).atDay(1).atStartOfDay(zoneId).toOffsetDateTime();
            return new MonthRange(from, to);
        } catch (DateTimeParseException ex) {
            throw new BusinessException(ExceptionUtils.buildResultResponse(INVALID_INPUT_ERROR, "month must use yyyy-MM format"));
        }
    }

    private String normalizeQuery(String query) {
        return query == null || query.isBlank() ? null : query.trim();
    }

    private record MonthRange(OffsetDateTime from, OffsetDateTime to) {
    }

    @Override
    public FetchTicketListResult getCustomerTickets(FetchCustomerTicketsQuery query) {
        Page<Ticket> page = ticketRepositoryPort.findByCustomer(
                query.customerEmail(),
                query.customerPhone(),
                query.ticketCode(),
                query.fromDate(),
                query.toDate(),
                PageRequest.of(query.pageNumber() - 1, query.pageSize())
        );

        return FetchTicketListResult.builder()
                .items(page.getContent())
                .pageNumber(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public FetchTicketDetailResult getCustomerTicketDetail(FetchTicketDetailQuery query) {
        Ticket ticket = ticketRepositoryPort.findById(query.ticketId())
                .orElseThrow(() -> new BusinessException(ExceptionUtils.buildResultResponse(RECORD_NOT_FOUND, "Ticket not found")));

        // Security Check: Verify ownership by email or phone
        // In a real app, we get this from the JWT (RequestContext)
        String identityEmail = query.context().userEmail();
        String identityPhone = query.context().userPhone();

        boolean isOwner = (identityEmail != null && identityEmail.equalsIgnoreCase(ticket.getCustomerEmail())) ||
                          (identityPhone != null && identityPhone.equals(ticket.getCustomerPhone()));

        if (!isOwner) {
            throw new BusinessException(ExceptionUtils.buildResultResponse("ACCESS_DENIED", "You do not have permission to view this ticket"));
        }

        return FetchTicketDetailResult.builder()
                .ticket(ticket)
                .build();
    }
}
