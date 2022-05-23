package springcloud_MSA.userservice.service;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import springcloud_MSA.userservice.client.OrderServiceClient;
import springcloud_MSA.userservice.dto.UserDto;
import springcloud_MSA.userservice.entity.UserEntity;
import springcloud_MSA.userservice.entity.UserRepository;
import springcloud_MSA.userservice.vo.ResponseOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private Environment env;
    private RestTemplate restTemplate; //resttemplate
    private OrderServiceClient orderServiceClient;
    private CircuitBreakerFactory circuitBreakerFactory;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           Environment env,
                           RestTemplate restTemplate,
                           OrderServiceClient orderServiceClient,
                           CircuitBreakerFactory circuitBreakerFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
        this.restTemplate = restTemplate;
        this.orderServiceClient = orderServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    //회원가입
    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper mapper = new ModelMapper(); //클래스간 변환
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); //매칭 환경 세팅
        UserEntity userEntity = mapper.map(userDto, UserEntity.class); //userDto->UserEntity 변환
        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        userRepository.save(userEntity);

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class);

        return returnUserDto;
    }

    //사용자 아이디로 검색
    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

//        List<ResponseOrder> orders = new ArrayList<>();
/*
        //RestTemplate
        String orderUrl = String.format(env.getProperty("order_service.url"), userId); //"http://127.0.0.1:8000/order-service/%s/orders";
        ResponseEntity<List<ResponseOrder>> orderListResponse =
                restTemplate.exchange(orderUrl, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<ResponseOrder>>() {
                });

        List<ResponseOrder> orderList = orderListResponse.getBody();
*/
/*
        //feign client + feign exception
        List<ResponseOrder> orderList = null;
        try {
            orderServiceClient.getOrders(userId);
        } catch (FeignException e) {
            log.error(e.getMessage());
        }
*/

        //feign errordecoder
//        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);

        //CircuitBreaker resilience4j 적용
        log.info("Before call orders microservice");
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orderList = circuitbreaker.run(() -> orderServiceClient.getOrders(userId), //처리하고,
                throwable -> new ArrayList<>());//오류가 발생하면 빈값을 보냄
        log.info("After called orders microservice");

        userDto.setOrders(orderList);

        return userDto;
    }

    //회원 리스트 전체
    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }
}
