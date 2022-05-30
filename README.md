# Wstęp do architektury mikroserwisowej
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
### Spring Cloud
* https://spring.io/projects/spring-cloud
### Prezentacja
* [kilka slajdów z prezentacji z 2020 roku](https://github.com/zzpj/wstep-do-mikroserwisow/blob/main/ZZPJ2020-microservices.pdf)


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
1. Select following dependencies: Lombok, Spring Web, Eureka Discovery Client, Spring Boot Actuator
1. Click Generate button, download and unzip package
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
1. Select following dependencies: Lombok, Spring Web, Eureka Discovery Client, Spring Boot Actuator, Spring Reactive Web, Cloud LoadBalancer
1. Click Generate button, download and unzip package
1. Copy unzipped `UserConsumerService` folder into your project folder
1. Add annotation `@EnableDiscoveryClient` to main class
1. Add some properties into `application.properties`
    ```properties
    server.port=8020

    spring.application.name=user-consumer-service
    eureka.client.serviceUrl.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}
	
	management.endpoints.web.exposure.include=*
    ```
1. Run `UserConsumerService` application and determine if service has been registered in Eureka Discovery Server by entering `http://localhost:8761/` or using logs.
1. Prepare `UserConsumer` class which will be a client for prepared in previous section user web service.
	```java
	@Configuration
	public class WebClientConfig {

		@Bean
		public WebClient userManagerWebClient() {
			return WebClient.builder().build();
		}
	}
	```

	```java
	@RestController
	public class UserConsumerController {

		@Autowired
		WebClientConfig webClientConfig;

		@GetMapping("/getInfo")
		public String getUserAppInfo() {

			Mono<String> stringMono = webClientConfig.userManagerWebClient()
					.get()
					.uri("http://localhost:8010/info")
					.retrieve()
					.bodyToMono(String.class);

			return stringMono.block();

		}

		@GetMapping("/getUsers")
		public List<?> getUsers() {

			Mono<List> listMono = webClientConfig
					.userManagerWebClient().get()
					.uri("http://localhost:8010/users")
					.retrieve()
					.bodyToMono(List.class);
			return listMono.block();
		}
	}
	```
1. Verify and go to URL: `http://localhost:8020/`

Useful links:
[1](https://spring.io/guides/gs/service-registration-and-discovery/)
[2](https://spring.io/guides/tutorials/rest/)
[3](https://www.baeldung.com/spring-webflux)

### Spring Cloud Client Load balancer 
1. Stop running `UserManagerService` and comment `server.port` properties
1. Run two (or more) instances using Spring Boot Run Configuration, use Environment > VM Options for setting ports: `-Dserver.port=8011`
1. Refresh Eureka Discovery page and determine if both instances of the same service are available 
1. Let's use web flux client
	```java
	@RestController
	public class UserConsumerController {

		@Autowired
		private WebClientConfig webClientConfig;

		@GetMapping("/getInfo")
		public String getInfoX() {
			return webClientConfig.webClient()
					.get()
					.uri("http://user-manager/info")
					.retrieve()
					.bodyToMono(String.class)
					.block();
		}
	}	
	```
1. While preparing bean of `webClientConfig`, remember about proper autowired filter and add `@LoadBalancerClient` annotation
	```java
	@LoadBalancerClient(name = "user-manager", configuration = UserManagerInstanceConfig.class)
	public class WebClientConfig {

		@Autowired
		private ReactorLoadBalancerExchangeFilterFunction lbFunction;

		@LoadBalanced
		@Bean
		public WebClient webClient() {
			return WebClient.builder()
					.filter(lbFunction)
					.build();
		}
	}
	```
1. Prepare load balancer config:
	```
	@Configuration
	public class UserManagerInstanceConfig {

		@Bean
		public ServiceInstanceListSupplier serviceInstanceListSupplier(){
			return new UserManagerSupplier("user-manager");
		}
	}
	```
1. Define implementation for service instance list supplier
	```java
	public class UserManagerSupplier implements ServiceInstanceListSupplier {

		private final String serviceId;

		public UserManagerSupplier(String serviceId) {
			this.serviceId = serviceId;
		}

		@Override
		public String getServiceId() {
			return serviceId;
		}

		@Override
		public Flux<List<ServiceInstance>> get() {

			DefaultServiceInstance int1 = new DefaultServiceInstance(serviceId + "1", serviceId, "localhost", 8011, false);
			DefaultServiceInstance int2 = new DefaultServiceInstance(serviceId + "2", serviceId, "localhost", 8012, false);
			DefaultServiceInstance int3 = new DefaultServiceInstance(serviceId + "3", serviceId, "localhost", 8013, false);
			return Flux.just(Stream.of(int1
					, int2
					, int3
			).collect(Collectors.toList()));
		}
	}
	```
1. Verify and go to URL: `http://localhost:8020/getInfo`


### Config Server
#### Prepare Config Server Implementation
1. Open again [Spring Initializr website](https://start.spring.io/)
1. Complete Metadata section: set Artifact name as `UserConfigServer`
1. Select following dependencies: Lombok, Spring Web, Eureka Discovery Client, Spring Boot Actuator, Config Server
1. Click Generate button, download and unzip package
1. Copy unzipped `UserConfigServer` folder into your project folder
1. Add follwing annotations: `@EnableEurekaClient` & `@EnableConfigServer` into main class
1. Add some properties into `application.properties`
	```properties
	server.port=8060
	spring.application.name=user-config-server

	eureka.client.serviceUrl.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}

	#spring.cloud.config.server.git.uri=https://github.com/zzpj/demo-config-server.git
	spring.cloud.config.server.git.uri=file://E://ZaawansowanaJava22//demo-config-server-local
	#spring.cloud.config.server.git.default-label=main
	spring.cloud.config.server.git.default-label=master
	spring.cloud.config.server.git.clone-on-start=true

	management.endpoints.web.exposure.include=*
	```
1. Run config server project & determine eureka main page
#### Prepare either local repo or github once
1. local repo: go to new folder and `git init`
1. OR create new repo in github

#### Move properties of created services 
1. 
1. Remember about following properties naming rules
```
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```