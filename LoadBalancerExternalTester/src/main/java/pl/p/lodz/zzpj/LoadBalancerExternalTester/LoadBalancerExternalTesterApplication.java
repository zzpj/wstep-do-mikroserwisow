package pl.p.lodz.zzpj.LoadBalancerExternalTester;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class LoadBalancerExternalTesterApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoadBalancerExternalTesterApplication.class, args);
    }
}