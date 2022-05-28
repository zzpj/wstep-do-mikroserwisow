package com.zzpj2021.UserConsumerService;

import com.zzpj2021.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class UserConsumer {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/users")
    public String getUsers() {

        List<User> userList = restTemplate.getForObject("http://localhost:8011/getUsers/", List.class);

        User user = restTemplate.getForObject("http://localhost:8012/getUser/10", User.class);

        return userList + " one user " + user;
    }

    @RequestMapping("/info")
    public String getInfo() {

        String info = restTemplate.getForObject("http://user-manager-service/info", String.class);
        return info;
    }
}
