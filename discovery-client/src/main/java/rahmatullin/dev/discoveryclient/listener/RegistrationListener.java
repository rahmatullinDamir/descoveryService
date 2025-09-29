package rahmatullin.dev.discoveryclient.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import rahmatullin.dev.discoveryclient.dto.requests.ServiceRegisterRequest;
import rahmatullin.dev.discoveryclient.dto.responses.ServiceRegisterResponse;
import rahmatullin.dev.discoveryclient.dto.enums.StatusEnum;
import rahmatullin.dev.discoveryclient.props.DiscoveryProperties;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationListener implements ApplicationListener<ApplicationReadyEvent> {

    private final WebClient webClient;
    private final DiscoveryProperties properties;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ServiceRegisterRequest request = new ServiceRegisterRequest(properties.getServiceName(), properties.getServicePort());

        webClient.post()
                .uri( "/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ServiceRegisterResponse.class)
                .filter(response -> response.status() == StatusEnum.REGISTERED)
                .switchIfEmpty(Mono.error(new RuntimeException("Service not registered")))
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(2))
                        .maxBackoff(Duration.ofSeconds(30))
                        .multiplier(2.0)
                        .transientErrors(true))
                .doOnSuccess(response -> log.info("Successfully registered service"))
                .doOnError(error -> log.error("Failed to register after retries", error))
                .block();
    }
}
