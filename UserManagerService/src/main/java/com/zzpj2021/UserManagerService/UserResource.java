package com.zzpj2021.UserManagerService;


import com.zzpj2021.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserResource {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment environment;

    @RequestMapping("/getUsers")
    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    @RequestMapping("/getUser/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.getUser(id);
    }

    @RequestMapping("/info")
    public String getInfo() {

        String serverPort = environment.getProperty("server.port");
        String appName = environment.getProperty("spring.application.name");

        return appName + ":" + serverPort;
    }
}
