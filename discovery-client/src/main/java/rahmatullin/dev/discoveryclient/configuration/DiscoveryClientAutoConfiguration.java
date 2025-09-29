package rahmatullin.dev.discoveryclient.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import rahmatullin.dev.discoveryclient.DiscoveryClient;
import rahmatullin.dev.discoveryclient.listener.RegistrationListener;
import rahmatullin.dev.discoveryclient.props.DiscoveryProperties;

@Configuration
@EnableConfigurationProperties(DiscoveryProperties.class)
@ConditionalOnProperty(prefix = "discovery", name = {"server-url", "service-name", "service-port"})
public class DiscoveryClientAutoConfiguration {

    @Bean
    public WebClient webClient(DiscoveryProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getServerUrl())
                .build();
    }

    @Bean
    public DiscoveryClient discoveryClient(WebClient webClient) {
        return new DiscoveryClient(webClient);
    }

    @Bean
    public RegistrationListener registrationListener(WebClient webClient, DiscoveryProperties properties) {
        return new RegistrationListener(webClient, properties);
    }

}
