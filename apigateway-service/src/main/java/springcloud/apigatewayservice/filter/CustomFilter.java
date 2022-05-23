package springcloud.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        //Custom Pre Filter
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom Pre Filter: request id -> {}", request.getId());

            //Custom Post Filter
            return chain.filter(exchange) //chain: filter연결. Pre filter가 적용되면,
                    .then(Mono.fromRunnable(() -> { //Mono: 비동기방식에서 단일 값 전달. spring5에 추가된 webFlux 기능.
                log.info("Custom Post Filter: response code -> {}", response.getStatusCode());
            }));
        };

    }

    public static class Config {
        //Put the configuration properties
    }

}
