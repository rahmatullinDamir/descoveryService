package rahmatullin.dev.discoveryserver.dto.requests;

public record ServiceRegisterRequest(
        String serviceName,
        int port
) {
}
