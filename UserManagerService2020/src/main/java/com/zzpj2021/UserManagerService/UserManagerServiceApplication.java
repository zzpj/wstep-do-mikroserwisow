package com.zzpj2021.UserManagerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@SpringBootApplication
@EnableEurekaClient
public class UserManagerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserManagerServiceApplication.class, args);
	}

}
