package platform.management.service.application.command.trip;

import lombok.Builder;

@Builder
public record SearchRoundTripDetailQuery(
        String originName,
        String destinationName,
        String departureDate
) {
}
