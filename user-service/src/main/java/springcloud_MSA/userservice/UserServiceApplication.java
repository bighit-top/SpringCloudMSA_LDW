package springcloud_MSA.userservice;

import feign.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import springcloud_MSA.userservice.error.FeignErrorDecoder;

@SpringBootApplication
@EnableDiscoveryClient //eureka
@EnableFeignClients //FeignClient
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	//UserServiceImpl DI를 위해 빈을 생성해 놓는다.
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//RestTemplate : microservice간 통신 -> FeignClient 사용 시 필요없음
	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	//feign client 예외처리
	@Bean
	public Logger.Level getFeignLoggerLevel() {
		return Logger.Level.FULL;
	}

	//feign errordecoder
//	@Bean
//	public FeignErrorDecoder feignErrorDecoder() {
//		return new FeignErrorDecoder();
//	}
}
