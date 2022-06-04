package top.hwlljy.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.hwlljy.model.pojo.User;

import java.util.List;
import java.util.Map;


public interface UserRepository extends JpaRepository<User, String> {

    User findAllByNickname(String nickname);

    User findAllByUsername(String username);

    List<User> findAllByUsernameOrNickname(String username,String nickname);


    @Query(value = "SELECT id,nickname,username,about FROM weibo_user WHERE id IN :ids", nativeQuery = true)
    List<Map<String, Object>> getUsersByIds(@Param(value = "ids") List<String> ids);

    @Query(value = "select * from weibo_user where is_delete != '1'", nativeQuery = true)
    List<User> getUserList(Pageable pageable);

    @Query(value = "select count(*) from weibo_user where is_delete != '1'", nativeQuery = true)
    int getUserListTotal();

    @Query(value = "select * from weibo_user where is_delete != '1' and report>0 order by report desc", nativeQuery = true)
    List<User> getReportList(Pageable pageable);

    @Query(value = "select count(*) from weibo_user where is_delete != '1' and report>0 order by report desc", nativeQuery = true)
    int getReportTotal();

    @Query(value = "select * from weibo_user where is_delete = '1'", nativeQuery = true)
    List<User> getDeleteList(Pageable pageable);

    @Query(value = "select count(*) from weibo_user where is_delete = '1'", nativeQuery = true)
    int getDeleteListTotal();


    @Query(value = "select * " +
            "from weibo_user " +
            "where (nickname like :word or username like :word) and id!=:userId",nativeQuery = true)
    List<User> searchUserList(@Param(value = "word") String word, @Param(value = "userId") String userId,
                              Pageable pageable);

}
