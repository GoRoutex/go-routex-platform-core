package platform.management.service.application.command.trip;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchRoundTripResult(
        List<SearchTripItemResult> outBoundTrip,
        List<SearchTripItemResult> returnTrip
) {
}
