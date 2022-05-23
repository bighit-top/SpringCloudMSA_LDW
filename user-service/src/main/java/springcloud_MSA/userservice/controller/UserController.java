package springcloud_MSA.userservice.controller;

import io.micrometer.core.annotation.Timed;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springcloud_MSA.userservice.dto.UserDto;
import springcloud_MSA.userservice.entity.UserEntity;
import springcloud_MSA.userservice.service.UserService;
import springcloud_MSA.userservice.vo.RequestUser;
import springcloud_MSA.userservice.vo.ResponseUser;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    //application.yml 설정 파일에 등록한 메시지 확인
//    private Greeting greeting;
    private UserService userService;
    private Environment env;

    @Autowired
    public UserController(Environment env, UserService userService) {
//        this.greeting = greeting;
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    @Timed(value = "user.status", longTask = true)
    public String status() {
//        return String.format("It's Working in User Service one PORT %s.",
//                env.getProperty("local.server.port"));

        return String.format("It's Working in User Service."
                + ", port(local.server.port) = " + env.getProperty("local.server.port")
                + ", port(server.port) = " + env.getProperty("server.port")
                + ", token secret = " + env.getProperty("token.secret")
                + ", token expiration_time = " + env.getProperty("token.expiration_time"));
    }

    @GetMapping("/welcome")
    @Timed(value = "user.welcome", longTask = true)
    public String welcome() {
        return env.getProperty("greeting.message");
//        return greeting.getMessage();
    }

    //회원가입
    @PostMapping("/users")
    public ResponseEntity createUsers(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        //Http 상태코드 201성공, 바디 입력정보 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    //회원 전체 목룍
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> resultList = new ArrayList<>();
        userList.forEach(v -> {
            resultList.add(new ModelMapper().map(v, ResponseUser.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(resultList);
    }

    //회원 검색
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser resultValue = new ModelMapper().map(userDto, ResponseUser.class);
        
        return ResponseEntity.status(HttpStatus.OK).body(resultValue);
    }
}
