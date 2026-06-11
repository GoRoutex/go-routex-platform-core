package platform.management.service.application.command.trip;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchTripResult(
        List<SearchTripItemResult> data
) {
}
