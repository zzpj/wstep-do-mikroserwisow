package pl.p.lodz.UserService1;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class GreetingsController {

    @Autowired
    private Environment env;
    @Value("${config.server.demo}")
    //@Value("${general.message}")
    private String helloMessage;

    @GetMapping(
            value = "/hello",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String getGreetings() {
       // (CompositePropertySource)  ((AbstractEnvironment) env).getPropertySources();

        log.debug("do sth");
        MutablePropertySources propertySources = ((AbstractEnvironment) env).getPropertySources();
        return "Greetings: " + helloMessage;
    }
}
