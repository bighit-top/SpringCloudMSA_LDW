package springcloud_MSA.userservice.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import springcloud_MSA.userservice.dto.UserDto;
import springcloud_MSA.userservice.entity.UserEntity;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);

    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();

    UserDto getUserDetailsByEmail(String userName);
}
