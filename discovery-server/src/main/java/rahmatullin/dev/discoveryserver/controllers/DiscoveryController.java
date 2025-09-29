package rahmatullin.dev.discoveryserver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import rahmatullin.dev.discoveryserver.dto.requests.ServiceRegisterRequest;
import rahmatullin.dev.discoveryserver.dto.responses.ServiceAddressResponse;
import rahmatullin.dev.discoveryserver.dto.responses.ServiceRegisterResponse;
import rahmatullin.dev.discoveryserver.services.DiscoveryService;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ServiceRegisterResponse> registerService(@RequestBody ServiceRegisterRequest request,
                                                                         ServerWebExchange exchange) {

        String host = exchange.getRequest().getRemoteAddress().getHostString();
        return discoveryService.saveServiceToRegistry(host, request);
    }

    @GetMapping("/discover/{serviceName}")
    public Mono<ResponseEntity<ServiceAddressResponse>> getServiceAddress(@PathVariable String serviceName) {
        return discoveryService.getServiceAddressByName(serviceName)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
