package rahmatullin.dev.discoveryserver.services;

import rahmatullin.dev.discoveryserver.dto.requests.ServiceRegisterRequest;
import rahmatullin.dev.discoveryserver.dto.responses.ServiceAddressResponse;
import rahmatullin.dev.discoveryserver.dto.responses.ServiceRegisterResponse;
import reactor.core.publisher.Mono;

public interface DiscoveryService {
    Mono<ServiceRegisterResponse> saveServiceToRegistry(String host, ServiceRegisterRequest request);
    Mono<ServiceAddressResponse> getServiceAddressByName(String serviceName);
}
