package springcloud.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private Environment env;

    @Autowired
    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class); //ClassCastException : 부모 클래스에 Config 설정 정보를 알려줘야함.
        this.env = env;
    }

    public static class Config {
        //Put configuration properties here.
    }

    //login -> token -> users(with token) -> header(include token)
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            //http header 정보에서 인증 토큰 확인
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header.", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", ""); //Bearer을 제외한 나머지가 token 값

            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid.", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    private boolean isJwtValid(String jwt) {
        Boolean returnValue = true;

        String subject = null;
        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret")) //복호화: 암호화에 사용한 설정값 token.secret 사용.
                    .parseClaimsJws(jwt).getBody()
                    .getSubject(); //subject 값을 추출함
        } catch (Exception e) {
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

        return returnValue;
    }

    // Spring WebFlux : 클라이언트 요청시 반환 타입. 비동기 방식으로 처리. MVC가 아님.
    // - Mono : 단일 값 데이터
    // - Flux : 단일값 데이터가 아닐때
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }
}
