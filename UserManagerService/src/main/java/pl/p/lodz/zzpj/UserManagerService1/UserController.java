package pl.p.lodz.zzpj.UserManagerService1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class UserController {


    @Value("${info.app.description}")
    String appDescription;
    @Autowired
    Environment environment;

    @GetMapping("/users")
    public List<User> getUsers() {

        List<User> users = Stream.of(
                User.builder().name("Asia").role("Admin").build(),
                User.builder().name("Bart").role("Mod").build()
        ).collect(Collectors.toList());
        return users;
    }

    @GetMapping("/info")
    public String getAppInfo() {

        String serverPort = environment.getProperty("server.port");
        return appDescription + " : " + serverPort;
    }
}
