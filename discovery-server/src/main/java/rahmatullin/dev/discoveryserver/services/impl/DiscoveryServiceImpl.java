package rahmatullin.dev.discoveryserver.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;
import rahmatullin.dev.discoveryserver.dto.ServiceInstance;
import rahmatullin.dev.discoveryserver.dto.enums.StatusEnum;
import rahmatullin.dev.discoveryserver.dto.requests.ServiceRegisterRequest;
import rahmatullin.dev.discoveryserver.dto.responses.HealthResponse;
import rahmatullin.dev.discoveryserver.dto.responses.ServiceAddressResponse;
import rahmatullin.dev.discoveryserver.dto.responses.ServiceRegisterResponse;
import rahmatullin.dev.discoveryserver.exceptions.GetServiceAddressByNameException;
import rahmatullin.dev.discoveryserver.exceptions.HealthCheckServiceError;
import rahmatullin.dev.discoveryserver.exceptions.RegistrationServiceFailedException;
import rahmatullin.dev.discoveryserver.services.DiscoveryService;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class DiscoveryServiceImpl implements DiscoveryService {
    private final ConcurrentHashMap<String, List<ServiceInstance>> registry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> roundRobinIndex = new ConcurrentHashMap<>();

    private final WebClient webClient;


    @Override
    public Mono<ServiceRegisterResponse> saveServiceToRegistry(String host, ServiceRegisterRequest request) {
        String serviceName = request.serviceName();
        ServiceInstance instance = new ServiceInstance(host, request.port(), serviceName);

        try {

            registry.computeIfAbsent(serviceName, service -> new CopyOnWriteArrayList<>()).add(instance);

            roundRobinIndex.computeIfAbsent(serviceName, counter -> new AtomicInteger(0));

            return Mono.just(new ServiceRegisterResponse(StatusEnum.REGISTERED));
        } catch (Exception e) {
            throw new RegistrationServiceFailedException(e.getMessage());
        }
    }

    @Override
    public Mono<ServiceAddressResponse> getServiceAddressByName(String serviceName) {
        try {
            List<ServiceInstance> instances = registry.get(serviceName);

            if (instances == null || instances.isEmpty()) {
                return Mono.empty();
            }

            AtomicInteger currentIndex = roundRobinIndex.get(serviceName);
            int index = currentIndex.getAndIncrement() % instances.size();

            ServiceInstance instance = instances.get(index);

            return Mono.just(new ServiceAddressResponse(instance.host(), instance.port()));

        } catch (Exception e) {
            throw new GetServiceAddressByNameException(e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 10_000)
    private void checkAllInstancesHealth() {
        Collection<List<ServiceInstance>> allInstances = registry.values();

        for (List<ServiceInstance> instanceList : allInstances) {
            for (ServiceInstance instance : instanceList) {
                checkInstanceHealth(instance);
            }
        }
    }

    private void checkInstanceHealth(ServiceInstance instance) {
        String instanceUrl = "http://" + instance.host() + ":" + instance.port() + "/actuator/health";

        webClient.get().uri(instanceUrl).retrieve().bodyToMono(HealthResponse.class)
                .timeout(Duration.ofSeconds(3))
                .subscribe(healthResponse -> {
                    if (!("UP".equals(healthResponse.status()))) {
                        deleteInstance(instance);
                    }
                }, error -> {
                    deleteInstance(instance);
                    throw new HealthCheckServiceError(instanceUrl, error.getMessage());
                });
    }

    private void deleteInstance(ServiceInstance instance) {
        String serviceName = instance.serviceName();

        List<ServiceInstance> instances = registry.get(serviceName);

        if (instances == null || instances.isEmpty()) {
            throw new HealthCheckServiceError(serviceName, "can not find service");
        }

        instances.removeIf(i -> i.host().equals(instance.host()) && i.port() == instance.port());

        if (instances.isEmpty()) {
            registry.remove(serviceName);
            roundRobinIndex.remove(serviceName);
        }
    }

}
