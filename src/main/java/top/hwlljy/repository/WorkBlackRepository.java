package top.hwlljy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.hwlljy.model.pojo.WorkBlack;

import java.util.List;

public interface WorkBlackRepository extends JpaRepository<WorkBlack, String> {


    @Query(value = "select work_id from weibo_work_black wwb where user_id = :userId", nativeQuery = true)
    List<String> getWorkBlack(@Param(value = "userId") String userId);

    WorkBlack findAllByUserIdAndWorkId(String userId,String workId);
}
