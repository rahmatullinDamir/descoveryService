package rahmatullin.dev.discoveryserver.dto.responses;

import java.util.Map;

public record HealthResponse(String status, Map<String, Object> components) {
}
