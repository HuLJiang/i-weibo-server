package top.hwlljy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.hwlljy.model.pojo.UserBlackList;

import java.util.List;

public interface BlackListRepository extends JpaRepository<UserBlackList, String> {

    @Query(value = "select to_user_id from weibo_user_black_list where user_id = :userId", nativeQuery = true)
    List<String> getUserBlack(@Param(value = "userId") String userId);

    UserBlackList findAllByUserIdAndToUserId(String userId,String toUserId);
}
