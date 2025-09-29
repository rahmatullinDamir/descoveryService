package rahmatullin.dev.discoveryserver.dto;

public record ServiceInstance(
        String host,
        int port,
        String serviceName
) {
}
