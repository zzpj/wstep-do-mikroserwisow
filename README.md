# Microservice 2021
## Przydatne linki
* link1
* link2
* link
## Scenariusz do prezentowanego livecodingu

### Discovery Server Installation and Configuration
1. Enter [Spring Initializr website](https://start.spring.io/)
1. Complete Metadata section
1. Select following dependencies: Spring Boot Devtools, Lombok, Spring Web, Eureka Server, Spring Boot Actuator
1. Click Generate button and download zipped package.
1. Unzip package and open generated project in IntelliJ
1. Open main class with `@SpringBootApplication` annotation
1. Use Spring Cloud’s `@EnableEurekaServer` to stand up a registry with which other applications can communicate. This is a regular Spring Boot application with one annotation added to enable the service registry.
1. By default, the registry also tries to register itself, so you need to disable that behavior as well in  `application.properties` file.
    ```properties
    eureka.client.register-with-eureka=false
    eureka.client.fetch-registry=false
    ```
1. Select the port which will be used by Eureka Server 
    ```properties
    server.port=8761
    ```
1. Enter the URL: `http://localhost:8761/`

### Implementing First Discovery Client
1. Open again [Spring Initializr website](https://start.spring.io/)
1. Complete Metadata section: set Artifact name as `UserManagerService`
1. Select following dependencies: Spring Boot Devtools, Lombok, Spring Web, Eureka Discovery Client, Spring Boot Actuator
1. Click Generate button, download and unzip package
1. Create new a folder with any name
1. Create inside it a new `pom.xml` file with first (one for now) child module named `UserManagerService`
1. Example of such `pom.xml`:

    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <groupId>pl.p.lodz.zzpj2020</groupId>
        <artifactId>Microservices2020-DemoApp2</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <packaging>pom</packaging>
    
        <modules>
            <module>UserManagerService</module>
        </modules>
    </project>
    ```
1. Copy unzipped `UserManagerService` folder into your project folder
1. Open IntelliJ using "external" pom.xml and determine if `UserManagerService` is child module
1. Add annotation `@EnableDiscoveryClient` to main class
1. Add some properties into `application.properties`
    ```properties
    server.port=8010
    spring.application.name=user-manager-service
    eureka.client.serviceUrl.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}
    ```
1. Run `UserManagerService` Application and determine if service has been registered in Eureka Discovery Server by entering `http://localhost:8761/` or using logs.
1. Prepare webservice providing simple API, it will be useful in next part of exercise
1. Add `management.endpoints.web.exposure.include=*` property into your  `application.properties` file in order to see how actuators work
1. Rerun `UserManagerService` and go to `http://localhost:8010/actuator/`. For more info about actuators, refer [documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html) 

### Register second client for consuming your web service
1. Open again [Spring Initializr website](https://start.spring.io/)
1. Complete Metadata section: set Artifact name as `UserConsumerService`
1. Select following dependencies: Spring Boot Devtools, Lombok, Spring Web, Eureka Discovery Client, Spring Boot Actuator
1. Click Generate button, download and unzip package
1. Copy unzipped `UserConsumerService` folder into your project folder and add child module to `pom.xml`

    ```xml
	   <modules>
		     <module>UserManagerService</module>
		     <module>UserConsumerService</module>
	  </modules>
    ```
1. Add annotation `@EnableDiscoveryClient` to main class
1. Add some properties into `application.properties`
    ```properties
    server.port=8020

    spring.application.name=user-consumer-service
    eureka.client.serviceUrl.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}
    ```
1. Run `UserConsumerService` Application and determine if service has been registered in Eureka Discovery Server by entering `http://localhost:8761/` or using logs.
1. Prepare `UserConsumer` class which will be a client for prepared in previous section user web service.
1. Verify and go to URL: `http://localhost:8020/`

Useful links:
[1](https://spring.io/guides/gs/service-registration-and-discovery/)
[2](https://spring.io/guides/tutorials/rest/)

### Ribbon as load balancer 
1. Stop running `UserManagerService` and comment `server.port` properties
1. Run two (or more) instances using Spring Boot Run Configuration, use Environment > VM Options for setting ports: `-Dserver.port=8011`
1. Refresh Eureka Discovery page and determine if both instances of the same service are available 
1. Add Ribbon dependency to `UserConsumerService`
    ```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
        </dependency>
    ```
1. Add `@RibbonClient` annotation and complete `name` and `configuration` arguments (example:  `@RibbonClient(name = "UserConsumer", configuration = UserManagerConfig.class)`) 
1. Add `@LoadBalanced` annotation to rest template bean
1. Create and complete configuration class, use proper filtering strategy
    ```java
    public class UserManagerConfig {
        @Autowired
        IClientConfig ribbonClientConfig;
    
        @Bean
        public IPing ribbonPing(IClientConfig config) {
            return new PingUrl();
        }
    
        @Bean
        public IRule ribbonRule(IClientConfig config) {
            return new AvailabilityFilteringRule();
        }
    }
    ```
1. Complete properties file
    ```properties
    user-manager-service.ribbon.ServerListRefreshInterval=1000
    user-manager-service.ribbon.eureka.enabled=true
    
    eureka.client.healthcheck.enabled=true
    eureka.instance.leaseRenewalIntervalInSeconds=1
    eureka.instance.leaseExpirationDurationInSeconds=2
    ```
1. Correct url address in rest template
1. Verify and go to URL: `http://localhost:8020/info`

Useful links:
[1](https://spring.io/guides/gs/client-side-load-balancing/)
[2](https://www.baeldung.com/spring-cloud-rest-client-with-netflix-ribbon)
[3](https://howtodoinjava.com/spring-cloud/spring-boot-ribbon-eureka/)
[4](https://spring.io/blog/2020/03/25/spring-tips-spring-cloud-loadbalancer)

### Zuul as reverse proxy 
1. Open again [Spring Initializr website](https://start.spring.io/)
1. Complete Metadata section: set Artifact name as `ZuulProxyService`
1. Select following dependencies: Spring Boot Devtools, Lombok, Spring Web, Eureka Discovery Client, Spring Boot Actuator, **Zuul**
1. Click Generate button, download and unzip package
1. Copy unzipped `ZuulProxyService` folder into your project folder and add child module to `pom.xml`

    ```xml
	   <modules>
		     <module>UserManagerService</module>
		     <module>UserConsumerService</module>
             <module>ZuulProxyServer</module>
	  </modules>
    ```
1. Add annotation `@EnableDiscoveryClient` amd `@EnableZuulProxy` to main class
1. Add some properties into `application.properties`
    ```properties
    server.port=8050

    spring.application.name=zuul-proxy-service
    eureka.client.serviceUrl.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}

    management.endpoints.web.exposure.include=*
    ```
1. Run `ZuulProxyService` application and determine if service has been registered in Eureka Discovery Server by entering `http://localhost:8761/` or using logs.
1. Verify and go to URL: `http://localhost:8050/actuators`
1. Rerun `ZuulProxyService` application with other proxies
    ```properties
    zuul.routes.user-manager-service.path=/users/**
    zuul.routes.user-manager-service.serviceId=USER-MANAGER-SERVICE
    ```
1. Verify URL `http://localhost:8050/users/user/1`
1. Add filter class extending `ZuulFilter`
1. The example of the class
    ```java
    public class UserFilter extends ZuulFilter {
        @Override
        public String filterType() {
            return "pre";
        }
    
        @Override
        public int filterOrder() {
            return 1;
        }
    
        @Override
        public boolean shouldFilter() {
            return true;
        }
    
        @Override
        public Object run() throws ZuulException {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
    
            System.out.println(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
    
            return null;
        }
    }
    ```
1. Add `@Bean` to main spring class 

Useful links:
[1](https://www.baeldung.com/zuul-load-balancing)
[2](https://stackabuse.com/spring-cloud-routing-with-zuul-and-gateway/)
[3](https://springbootdev.com/2018/02/03/microservices-declare-zuul-routes-with-eureka-serviceid-spring-cloud-zuul-eureka/)

/EOF
