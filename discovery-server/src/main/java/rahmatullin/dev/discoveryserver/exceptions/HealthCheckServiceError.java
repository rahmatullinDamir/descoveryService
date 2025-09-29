package rahmatullin.dev.discoveryserver.exceptions;

public class HealthCheckServiceError extends RuntimeException {
    public HealthCheckServiceError(String serviceUrl, String message) {
        super(serviceUrl + ":" + message);
    }
}
