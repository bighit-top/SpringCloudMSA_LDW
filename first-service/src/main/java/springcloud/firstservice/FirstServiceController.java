package springcloud.firstservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

// http://localhost:8081/welcome : gateway를 거치지 않으면 root
// http://localhost:8081/first-service/welcome : gateway를 거치면서 root 아래로 microservice 이 붙는다. gateway application.yml 설정을 통해 경로를 변경할 수 있다.
//@RequestMapping("/")
@RestController //@Controller와의 차이 : request,response 바디 구현 여부에 따라 구분
@RequestMapping("/first-service/")
@Slf4j //log
public class FirstServiceController {

    Environment env;

    @Autowired
    public FirstServiceController(Environment env) {
        this.env = env;
    }


    //forwading 페이지 단순 확인
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the First page~";
    }

    //Java code로 filter 설정 + http 헤더 추가 확인
    @GetMapping("/message") //Gateway service에서 추가한 header 값 확인
    public String message(@RequestHeader("first-request") String header) {
        log.info(header);
        return "Hello World in First Service. Test Gateway.";
    }

    //Java code로 custom filter 설정 확인
    @GetMapping("/check")
    public String check() {
        return "Hi, there. This is a message from First service.";
    }

    //서비스 포트번호를 동적으로 부여할 때, 포트번호 확인
    // 1. HttpServletRequest.request.getServerPort()
    // 2. Environment.env.getProperty(local.server.port): appication.yml에 설정된 값을 가져온다. 실제 할당되는 포트.
    @GetMapping("/checkport")
    public String checkPort(HttpServletRequest request) {
        log.info("Server port = {}", request.getServerPort()); //
        return String.format("Hi, there. This is a message from First Service on PORT %s"
                , env.getProperty("local.server.port"));
    }
}
