package rahmatullin.dev.userservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rahmatullin.dev.userservice.models.User;
import reactor.core.publisher.Mono;

@RestController
public class UserController {

    @GetMapping("/user/{id}")
    public Mono<User> getUserById(@PathVariable Long id) {
        return Mono.just(User.builder()
                .id(id)
                .name("Damir Rahmatullin")
                .build());
    }
}
