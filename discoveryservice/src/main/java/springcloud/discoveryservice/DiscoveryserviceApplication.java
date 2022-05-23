package springcloud.discoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer //eureka server 역할을 한다.
public class DiscoveryserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscoveryserviceApplication.class, args);
	}

}
