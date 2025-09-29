package rahmatullin.dev.discoveryserver.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import rahmatullin.dev.discoveryserver.dto.enums.StatusEnum;
import rahmatullin.dev.discoveryserver.dto.responses.ErrorResponse;
import rahmatullin.dev.discoveryserver.dto.responses.ServiceRegisterResponse;
import rahmatullin.dev.discoveryserver.exceptions.GetServiceAddressByNameException;
import rahmatullin.dev.discoveryserver.exceptions.HealthCheckServiceError;
import rahmatullin.dev.discoveryserver.exceptions.RegistrationServiceFailedException;

@ControllerAdvice
@Slf4j
public class DiscoveryControllerAdvice {

    @ExceptionHandler(RegistrationServiceFailedException.class)
    public ResponseEntity<ServiceRegisterResponse> handleRegistrationFailedException(RegistrationServiceFailedException ex) {
        log.error("Exception in registration service: " + ex);
        return ResponseEntity.internalServerError()
                .body(new ServiceRegisterResponse(StatusEnum.UNREGISTERED));
    }

    @ExceptionHandler(GetServiceAddressByNameException.class)
    public ResponseEntity<ErrorResponse> handleGetServiceAddressByNameException(GetServiceAddressByNameException ex) {
        log.error("Exception in getting service address by name: " + ex);

        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(HealthCheckServiceError.class)
    public void handleHealthCheckServiceError(HealthCheckServiceError err) {
        log.warn("Error in handle health check of service instance: " + err);
    }
}
