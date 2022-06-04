package top.hwlljy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.hwlljy.model.pojo.UserWorkAttach;

import java.util.List;
import java.util.Map;

public interface AttachRepository extends JpaRepository<UserWorkAttach, String> {

    @Query(value = "select group_concat(url) ,work_id workId from weibo_user_work_attaches \n" +
            "\twhere work_id in :workIds and `type` = :type\n" +
            "\tgroup by work_id", nativeQuery = true)
    List<Map<String, String>> getAttaches(@Param(value = "workIds") String workIds, @Param(value = "type") String type);

    @Query(value = "select url from weibo_user_work_attaches where work_id=:workId", nativeQuery = true)
    List<String> getImgList(@Param(value = "workId") String workId);
}
