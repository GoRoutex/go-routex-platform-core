package platform.merchant.service.application.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import platform.core.common.service.domain.trip.TripStatus;
import platform.core.common.service.domain.vehicle.VehicleStatus;
import platform.merchant.service.domain.assignment.TripAssignmentStatus;
import platform.merchant.service.domain.assignment.model.TripAssignmentRecord;
import platform.merchant.service.domain.assignment.port.TripAssignmentRepositoryPort;
import platform.merchant.service.domain.driver.OperationStatus;
import platform.merchant.service.domain.driver.model.DriverProfile;
import platform.merchant.service.domain.driver.port.DriverProfileRepositoryPort;
import platform.merchant.service.domain.trip.model.TripAggregate;
import platform.merchant.service.domain.trip.port.TripAggregateRepositoryPort;
import platform.merchant.service.domain.vehicle.model.VehicleProfile;
import platform.merchant.service.domain.vehicle.port.VehicleProfileRepositoryPort;
import vn.com.go.routex.identity.security.log.SystemLog;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Lazy(false)
@Component
@RequiredArgsConstructor
public class OverdueTripAutoCompletionScheduler {

    private static final String SYSTEM_ACTOR = "system:overdue-trip-auto-completion";

    private final TripAggregateRepositoryPort tripAggregateRepositoryPort;
    private final TripAssignmentRepositoryPort tripAssignmentRepositoryPort;
    private final VehicleProfileRepositoryPort vehicleProfileRepositoryPort;
    private final DriverProfileRepositoryPort driverProfileRepositoryPort;
    private final SystemLog sLog = SystemLog.getLogger(this.getClass());

    @Value("${merchant.trip-auto-completion.enabled:true}")
    private boolean enabled;

    @Value("${merchant.trip-auto-completion.batch-size:100}")
    private int batchSize;

    @Value("${merchant.trip-auto-completion.zone-id:Asia/Ho_Chi_Minh}")
    private String zoneId;

    @Scheduled(
            fixedDelayString = "${merchant.trip-auto-completion.fixed-delay-ms:3600000}",
            initialDelayString = "${merchant.trip-auto-completion.initial-delay-ms:60000}"
    )
    @Transactional
    public void completeOverdueAssignedTrips() {
        if (!enabled) {
            return;
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of(zoneId));
        OffsetDateTime cutoff = now.toLocalDate()
                .atStartOfDay(ZoneId.of(zoneId))
                .toOffsetDateTime();

        int totalCompleted = 0;
        List<TripAggregate> overdueTrips;
        do {
            overdueTrips = tripAggregateRepositoryPort.findAssignedTripsBeforeForUpdate(cutoff, batchSize);
            if (overdueTrips.isEmpty()) {
                break;
            }
            completeBatch(overdueTrips, now);
            totalCompleted += overdueTrips.size();
        } while (overdueTrips.size() == batchSize);

        if (totalCompleted > 0) {
            sLog.info("[TRIP-AUTO-COMPLETION] Completed {} overdue assigned trip(s), cutoff={}", totalCompleted, cutoff);
        }
    }

    private void completeBatch(List<TripAggregate> trips, OffsetDateTime now) {
        List<String> tripIds = trips.stream()
                .map(TripAggregate::getId)
                .toList();

        Map<String, TripAssignmentRecord> activeAssignments = tripAssignmentRepositoryPort.findLatestActiveByTripIds(tripIds);

        trips.forEach(trip -> {
            trip.setStatus(TripStatus.COMPLETED);
            trip.setUpdatedAt(now);
            trip.setUpdatedBy(SYSTEM_ACTOR);
        });
        tripAggregateRepositoryPort.saveAll(trips);

        List<TripAssignmentRecord> completedAssignments = activeAssignments.values()
                .stream()
                .filter(assignment -> TripAssignmentStatus.ASSIGNED.equals(assignment.getStatus()))
                .peek(assignment -> assignment.complete(SYSTEM_ACTOR, now))
                .toList();
        tripAssignmentRepositoryPort.saveAll(completedAssignments);

        releaseIdleVehicles(completedAssignments);
        releaseIdleDrivers(completedAssignments);
    }

    private void releaseIdleVehicles(List<TripAssignmentRecord> completedAssignments) {
        Set<String> vehicleIds = completedAssignments.stream()
                .map(TripAssignmentRecord::getVehicleId)
                .filter(Objects::nonNull)
                .filter(vehicleId -> !vehicleId.isBlank())
                .collect(Collectors.toSet());

        if (vehicleIds.isEmpty()) {
            return;
        }

        Map<String, VehicleProfile> vehicles = vehicleProfileRepositoryPort.findByIdIn(vehicleIds)
                .stream()
                .collect(Collectors.toMap(VehicleProfile::getId, Function.identity()));

        vehicleIds.forEach(vehicleId -> {
            boolean hasActiveAssignment = !tripAssignmentRepositoryPort.findActiveByVehicle(vehicleId).isEmpty();
            VehicleProfile vehicle = vehicles.get(vehicleId);
            if (!hasActiveAssignment && vehicle != null && VehicleStatus.IN_SERVICE.equals(vehicle.getStatus())) {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
                vehicleProfileRepositoryPort.save(vehicle);
            }
        });
    }

    private void releaseIdleDrivers(List<TripAssignmentRecord> completedAssignments) {
        Set<String> driverIds = completedAssignments.stream()
                .map(TripAssignmentRecord::getDriverId)
                .filter(Objects::nonNull)
                .filter(driverId -> !driverId.isBlank())
                .collect(Collectors.toSet());

        if (driverIds.isEmpty()) {
            return;
        }

        Map<String, DriverProfile> drivers = driverProfileRepositoryPort.findByIdIn(driverIds)
                .stream()
                .collect(Collectors.toMap(DriverProfile::getId, Function.identity()));

        driverIds.forEach(driverId -> {
            boolean hasActiveAssignment = !tripAssignmentRepositoryPort.findActiveByDriver(driverId).isEmpty();
            DriverProfile driver = drivers.get(driverId);
            if (!hasActiveAssignment && driver != null && OperationStatus.ON_TRIP.equals(driver.getOperationStatus())) {
                driver.setOperationStatus(OperationStatus.AVAILABLE);
                driverProfileRepositoryPort.save(driver);
            }
        });
    }
}
