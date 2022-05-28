package com.zzpj2021.UserConsumerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "userconsumerclient", configuration = UserManagerConfig.class)
public class UserConsumerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserConsumerServiceApplication.class, args);
	}

}
