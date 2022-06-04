package top.hwlljy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.hwlljy.model.pojo.UserToken;

public interface TokenRepository extends JpaRepository<UserToken, String> {

    UserToken findAllByUserId(String userId);

    UserToken findAllByToken(String token);

    void deleteAllByUserId(String userId);
}
