package pl.p.lodz.zzpj.LoadBalancerExternalTester;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserManagerSupplier implements ServiceInstanceListSupplier {

    private final String serviceId;

    public UserManagerSupplier(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {

        DefaultServiceInstance int1 = new DefaultServiceInstance(serviceId + "1", serviceId, "localhost", 8011, false);
        DefaultServiceInstance int2 = new DefaultServiceInstance(serviceId + "2", serviceId, "localhost", 8012, false);
        DefaultServiceInstance int3 = new DefaultServiceInstance(serviceId + "3", serviceId, "localhost", 8013, false);
        return Flux.just(Stream.of(int1
                , int2
                , int3
        ).collect(Collectors.toList()));
    }
}