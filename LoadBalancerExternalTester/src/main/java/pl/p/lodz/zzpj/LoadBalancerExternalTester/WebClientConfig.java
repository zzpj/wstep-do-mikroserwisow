package pl.p.lodz.zzpj.LoadBalancerExternalTester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@LoadBalancerClient(name = "user-manager", configuration = UserManagerInstanceConfig.class)
public class WebClientConfig {

    @Autowired
    private ReactorLoadBalancerExchangeFilterFunction lbFunction;

    @LoadBalanced
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter(lbFunction)
                .build();
    }
}
