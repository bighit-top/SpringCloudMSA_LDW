package springcloud_MSA.userservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springcloud_MSA.userservice.service.UserService;

import javax.servlet.Filter;

@Configuration //다른 빈들보다 우선순위를 가짐
@EnableWebSecurity //websecurity용도 선언
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private Environment env;
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //권한 처리
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable(); //csrf 사용안함
//        http.authorizeRequests().antMatchers("/users/**").permitAll(); //사용 작업 제한 : /users/** 은 모든 요청 인증 없이 통과
        http.authorizeRequests().antMatchers("/actuator/**").permitAll(); //actuator
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress("172.18.0.5") //.access("hasIpAddress('192.168.35.128')")
                .and()
                .addFilter(getAuthenticationFilter()); //filter를 통과해야함

        http.headers().frameOptions().disable(); //html frame 사용안함
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(authenticationManager(), userService, env);
//        authenticationFilter.setAuthenticationManager(authenticationManager()); //spring security authentication 사용
        return authenticationFilter;
    }

    //인증 처리
    // select pwd from users where email=?
    // db_pwd(encrypted) == input_pwd(encrypted)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
