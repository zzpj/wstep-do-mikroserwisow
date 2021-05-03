# Mikroserwisy - edycja 2021
## Do poczytania oraz oglądania
### Jak zacząć
* [https://microservices.io/](https://microservices.io/)
* [12factor.net](https://12factor.net/)
### Kursy
* https://app.pluralsight.com/library/courses/getting-started-microservices
* https://app.pluralsight.com/library/courses/microservices-fundamentals
* https://app.pluralsight.com/library/courses/building-reactive-microservices (Java demo app)
* https://app.pluralsight.com/library/courses/microservices-security-fundamentals
* https://app.pluralsight.com/library/courses/java-microservices-spring-cloud-developing-services
* https://app.pluralsight.com/library/courses/java-microservices-spring-cloud-coordinating-services
### Youtube
* https://youtu.be/P4iomsHmOW0
* https://youtu.be/GBTdnfD6s5Q

## Scenariusz do prezentowanego livecodingu

### Discovery Server Installation and Configuration
1. Enter [Spring Initializr website](https://start.spring.io/)
1. Complete Metadata section
1. Select following dependencies: Spring Web, Eureka Server, Spring Boot Actuator
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
        <groupId>pl.p.lodz.zzpj2021</groupId>
        <artifactId>Microservices2021-DemoApp2</artifactId>
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
1. Add snippet below to display some basic information about our service
   ```properties
      info.app.name=${spring.application.name}
      info.app.description=This is my first spring boot application
      info.app.version=1.0.0
   ```

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
1. Add `@RibbonClient` annotation and complete `name` and `configuration` arguments (example:  `@RibbonClient(name = "UserConsumer", configuration = UserManagerConfig.class)`) to main class 
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
       USER-SERVICE-EXAMPLE.ribbon.ServerListRefreshInterval=1000
       USER-SERVICE-EXAMPLE.ribbon.eureka.enabled=false
       USER-SERVICE-EXAMPLE.ribbon.listOfServers=localhost:8030,localhost:8040

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

/EOF
