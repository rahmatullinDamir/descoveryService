package rahmatullin.dev.discoveryclient;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import rahmatullin.dev.discoveryclient.dto.responses.ServiceAddressResponse;
import rahmatullin.dev.discoveryclient.props.DiscoveryProperties;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
public class DiscoveryClient {
    private final WebClient webClient;

    public Mono<URI> getInstance(String serviceName) {
        String instanceUri = "/discover/" + serviceName;

        return webClient
                .get()
                .uri(instanceUri)
                .retrieve()
                .bodyToMono(ServiceAddressResponse.class)
                .timeout(Duration.ofSeconds(3))
                .map(response -> URI.create("http://" + response.host() + ":" + response.port()))
                .doOnError(e -> log.error("Error getting instance for service: {}", serviceName, e))
                .onErrorComplete();

    }


}

