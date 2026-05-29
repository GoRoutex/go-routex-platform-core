package platform.merchant.service.infrastructure.persistence.jpa.seat.projection;

public interface TripSeatAvailabilityProjection {

    String getTripId();

    Long getAvailableSeat();
}

