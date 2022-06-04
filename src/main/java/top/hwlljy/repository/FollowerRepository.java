package top.hwlljy.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.hwlljy.model.pojo.UserFollower;

import java.util.List;
import java.util.Map;

public interface FollowerRepository extends JpaRepository<UserFollower, String> {

    UserFollower findAllByUserIdAndToUserId(String userId,String toUserId);

    @Query(value = "SELECT to_user_id id FROM weibo_user_follower \n" +
            "\t\tWHERE user_id = :userId ORDER BY create_time DESC", nativeQuery = true)
    List<String> getFollowerIds(@Param(value = "userId") String userId, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM weibo_user_follower \n" +
            "\t\tWHERE user_id = :userId", nativeQuery = true)
    int getFollowerTotal(@Param(value = "userId") String userId);


    @Query(value = "SELECT user_id id FROM weibo_user_follower \n" +
            "\t\tWHERE to_user_id = :userId ORDER BY create_time DESC", nativeQuery = true)
    List<String> getFansIds(@Param(value = "userId") String userId,  Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM weibo_user_follower \n" +
            "\t\tWHERE to_user_id = :userId", nativeQuery = true)
    int getFansTotal(@Param(value = "userId") String userId);

    void deleteAllByUserIdAndToUserId(String userId, String toUserId);
}
