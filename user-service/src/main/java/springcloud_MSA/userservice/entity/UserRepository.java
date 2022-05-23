package springcloud_MSA.userservice.entity;

import org.springframework.data.repository.CrudRepository;

//CrudRepository : CRUD 지원
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    //CrudRepository 메서드 구현

    UserEntity findByUserId(String userId);
    UserEntity findByEmail(String username);
}
