package pl.p.lodz.zzpj.LoadBalancerExternalTester;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class UserConsumerController {

    @Value("${general.message}")
    private String generalMessage;

    @Value("${com.general.message}")
    private String com_generalMessage;
    @Autowired
    Environment environment;
    @Autowired
    private WebClientConfig webClientConfig;

    @GetMapping("/getInfo")
    public String getInfo() {
        return webClientConfig.webClient()
                .get()
                .uri("http://user-manager/info")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    @GetMapping("/getProps")
    public String getPropertiesInfo() {

        MutablePropertySources propertySources = ((AbstractEnvironment) environment).getPropertySources();

        log.info(generalMessage);
        log.info(com_generalMessage);
        return generalMessage + " " + com_generalMessage;
    }
}
