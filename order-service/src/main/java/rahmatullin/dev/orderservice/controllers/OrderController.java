package rahmatullin.dev.orderservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import rahmatullin.dev.discoveryclient.DiscoveryClient;
import rahmatullin.dev.orderservice.models.Order;
import rahmatullin.dev.orderservice.models.User;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
public class OrderController {
    private final DiscoveryClient discoveryClient;
    private final WebClient webClient;

    @GetMapping("/order")
    public Mono<Order> order() {
        return discoveryClient.getInstance("user-service")
                .switchIfEmpty(Mono.error(new RuntimeException("service don't response")))
            .flatMap(uri -> webClient.get()
                .uri(uri+ "/user/1")
                .retrieve()
                .bodyToMono(User.class))
                .map(user -> new Order(1L, "ZHEVACHKA", user));
    }
}
