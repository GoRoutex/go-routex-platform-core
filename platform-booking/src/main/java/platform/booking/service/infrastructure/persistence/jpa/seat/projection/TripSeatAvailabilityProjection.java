package platform.booking.service.infrastructure.persistence.jpa.seat.projection;

public interface TripSeatAvailabilityProjection {

    String getRouteId();

    Long getAvailableSeat();
}

