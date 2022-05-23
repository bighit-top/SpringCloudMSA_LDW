package springcloud.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//url filter 설정 : gateway router, forwarding 설정이랄까
// 1.application.yml(=application.properties)
// 2.java code
//@Configuration
public class FilterConfig {
//    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()  //route() route할 정보 입력
                .route(r -> r.path("/first-service/**") //request(요청) 받는 url
                        .filters(f -> f.addRequestHeader("first-request", "first-request-header") //request header에 추가
                                .addResponseHeader("first-response", "first-response-header")) //response header에 추가
                        .uri("http://localhost:8081")) //이동할 url. response로 연결해줄 url
                .route(r -> r.path("/second-service/**") //second service 등록
                        .filters(f -> f.addRequestHeader("second-request", "second-request-header")
                                .addResponseHeader("second-response", "second-response-header"))
                        .uri("http://localhost:8082"))
                .build(); //route 정보 메모리에 반영
    }
}
