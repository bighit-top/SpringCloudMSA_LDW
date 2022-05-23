package springcloud_MSA.userservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import springcloud_MSA.userservice.dto.UserDto;
import springcloud_MSA.userservice.service.UserService;
import springcloud_MSA.userservice.vo.RequestLogin;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;

    @Autowired
    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            //postmapping은 값을 볼수없음 -> request.getInputStream() 사용
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            //인증 요청 : email, password 비교해줌
            return getAuthenticationManager().authenticate(
                    //토큰 변환 생성 : 이메일, 패스워드, 권한
                    new UsernamePasswordAuthenticationToken(creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
//        log.debug(((User)authResult.getPrincipal()).getUsername());

        String userName = ((User)authResult.getPrincipal()).getUsername();
        UserDto userDetails = userService.getUserDetailsByEmail(userName);

        //JWT: token 생성
        String token = Jwts.builder()
                .setSubject(userDetails.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time")))) //토큰 유효기간 설정
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret")) //암호화: 알고리즘 추가
                .compact();

        //response header 정보에 추가
        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());
    }
}
