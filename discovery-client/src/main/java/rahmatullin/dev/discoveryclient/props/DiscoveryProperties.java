package rahmatullin.dev.discoveryclient.props;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "discovery")
public class DiscoveryProperties {
    private String serverUrl;
    private String serviceName;
    private int servicePort;
}




