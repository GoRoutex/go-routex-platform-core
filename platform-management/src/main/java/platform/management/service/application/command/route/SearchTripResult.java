package platform.management.service.application.command.route;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchTripResult(
        List<SearchTripItemResult> data
) {
}
