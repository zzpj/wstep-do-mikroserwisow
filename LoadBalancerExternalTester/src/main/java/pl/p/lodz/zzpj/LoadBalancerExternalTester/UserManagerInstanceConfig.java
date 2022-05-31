package pl.p.lodz.zzpj.LoadBalancerExternalTester;



import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserManagerInstanceConfig {

    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier(){
        return new UserManagerSupplier("user-manager");
    }
}