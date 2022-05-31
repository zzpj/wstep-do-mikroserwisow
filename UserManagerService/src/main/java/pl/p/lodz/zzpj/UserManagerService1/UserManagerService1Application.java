package pl.p.lodz.zzpj.UserManagerService1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserManagerService1Application {

	public static void main(String[] args) {
		SpringApplication.run(UserManagerService1Application.class, args);
	}

}
