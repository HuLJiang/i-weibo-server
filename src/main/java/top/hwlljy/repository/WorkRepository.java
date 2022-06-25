package top.hwlljy.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.hwlljy.model.pojo.UserWork;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface WorkRepository extends JpaRepository<UserWork, String> {

    @Query(value = "select *" +
            "\tfrom weibo_user_works where is_delete!='1' and user_id not in :userIds and id not in :workIds\n" +
            "\tand share_scope='0'" +
            "\torder by hot desc,create_time desc", nativeQuery = true)
    List<UserWork> getWorkList(@Param(value = "userIds") List<String> userIds, @Param(value = "workIds") List<String> workIds,
                               Pageable pageable);

    @Query(value = "select t1.*,IF(IFNULL(t2.id,'0') = '0','0','1') myUp,IF(IFNULL(t3.id,'0') = '0','0','1') myTalk ,t4.nickname2,t4.head_img2\n" +
            "\tfrom (\n" +
            "\t\tselect *\t\n" +
            "\t\tfrom weibo_user_works \n" +
            "\t\twhere is_delete!='1' and user_id not in :userIds and id not in :workIds\n" +
            "\t\tand (share_scope='0' or (\n" +
            "\t\t\t\tshare_scope='1' and user_id in (\n" +
            "\t\t\t\tselect to_user_id from weibo_user_follower where user_id=:userId)\n" +
            "\t\t\t)\n" +
            "\t\t)\t\n" +
            "\t\torder by hot desc,create_time desc limit :start,:size\n" +
            "\t) t1 left join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction t\n" +
            "\t\twhere t.user_id = :userId and t.type = '0'\n" +
            "\t) t2 on t1.id = t2.id\n" +
            "\tleft join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction tt\n" +
            "\t\twhere tt.user_id = :userId and tt.type = '1'\n" +
            "\t) t3 on t1.id = t3.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname nickname2,head_img head_img2 from weibo_user\n" +
            "\t) t4 on t1.user_id=t4.id order by t1.hot desc,t1.create_time desc", nativeQuery = true)
    List<Map<String, Object>> getWorkListForUser(@Param(value = "userIds") List<String> userIds, @Param(value = "workIds") List<String> workIds,
                                              @Param(value = "userId") String userId,@Param(value = "start") int start,
                                              @Param(value = "size") int size);

    @Query(value = "select *" +
            "\tfrom weibo_user_works where is_delete!='1' and user_id in :userIds and id not in :workIds\n" +
            "\tand share_scope!='2'" +
            "\torder by hot desc,create_time desc", nativeQuery = true)
    List<UserWork> getWorkFollowHotList(@Param(value = "userIds") List<String> userIds, @Param(value = "workIds") List<String> workIds,
                                        Pageable pageable);

    @Query(value = "select t1.*,IF(IFNULL(t2.id,'0') = '0','0','1') myUp,IF(IFNULL(t3.id,'0') = '0','0','1') myTalk ,t4.nickname2,t4.head_img2\n" +
            "\tfrom (\n" +
            "\t\tselect *\t\n" +
            "\t\tfrom weibo_user_works \n" +
            "\t\twhere is_delete!='1' and user_id not in :userIds and id not in :workIds\n" +
            "\t\tand (share_scope='0' or (\n" +
            "\t\t\t\tshare_scope='1' and user_id in (\n" +
            "\t\t\t\tselect to_user_id from weibo_user_follower where user_id=:userId)\n" +
            "\t\t\t)\n" +
            "\t\t)\t\n" +
            "\t\torder by create_time desc limit :start,:size\n" +
            "\t) t1 left join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction t\n" +
            "\t\twhere t.user_id = :userId and t.type = '0'\n" +
            "\t) t2 on t1.id = t2.id\n" +
            "\tleft join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction tt\n" +
            "\t\twhere tt.user_id = :userId and tt.type = '1'\n" +
            "\t) t3 on t1.id = t3.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname nickname2,head_img head_img2 from weibo_user\n" +
            "\t) t4 on t1.user_id=t4.id order by t1.create_time desc", nativeQuery = true)
    List<Map<String, Object>> getFollowHotListForUser(@Param(value = "userIds") List<String> userIds, @Param(value = "workIds") List<String> workIds,
                                                      @Param(value = "userId") String userId,@Param(value = "start") int start,
                                                      @Param(value = "size") int size);

    @Query(value = "select t1.*,IF(IFNULL(t2.id,'0') = '0','0','1') myUp,IF(IFNULL(t3.id,'0') = '0','0','1') myTalk ,t4.nickname2,t4.head_img2\n" +
            "\tfrom (\n" +
            "\t\tselect *\t\n" +
            "\t\tfrom weibo_user_works \n" +
            "\t\twhere is_delete!='1' and user_id not in :userIds and id not in :workIds\n" +
            "\t\tand (share_scope='0' or (\n" +
            "\t\t\t\tshare_scope='1' and user_id in (\n" +
            "\t\t\t\tselect to_user_id from weibo_user_follower where user_id=:userId)\n" +
            "\t\t\t)\n" +
            "\t\t)\t\n" +
            "\t\torder by create_time desc limit :start,:size\n" +
            "\t) t1 left join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction t\n" +
            "\t\twhere t.user_id = :userId and t.type = '0'\n" +
            "\t) t2 on t1.id = t2.id\n" +
            "\tleft join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction tt\n" +
            "\t\twhere tt.user_id = :userId and tt.type = '1'\n" +
            "\t) t3 on t1.id = t3.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname nickname2,head_img head_img2 from weibo_user\n" +
            "\t) t4 on t1.user_id=t4.id order by t1.create_time desc", nativeQuery = true)
    List<Map<String, Object>> getFollowAllListForUser(@Param(value = "userIds") List<String> userIds, @Param(value = "workIds") List<String> workIds,
                                                      @Param(value = "userId") String userId,@Param(value = "start") int start,
                                                      @Param(value = "size") int size);

    @Query(value = "select *" +
            "\tfrom weibo_user_works where is_delete!='1' and user_id in :userIds and id not in :workIds\n" +
            "\torder by create_time desc", nativeQuery = true)
    List<UserWork> getWorkFollowAllList(@Param(value = "userIds") List<String> userIds, @Param(value = "workIds") List<String> workIds,
                                        Pageable pageable);

    @Query(value = "SELECT id,hot,talk_num talkNum,up_num upNum\n" +
            "\tFROM weibo_user_works \n" +
            "\tWHERE is_delete='0'\n" +
            "\tORDER BY hot DESC", nativeQuery = true)
    List<Map<String, Object>> getWorkNum(Pageable pageable);

    @Query(value = "select t1.*,IF(IFNULL(t2.id,'0') = '0','0','1') myUp,IF(IFNULL(t3.id,'0') = '0','0','1') myTalk ,t4.nickname2,t4.head_img2\n" +
            "\tfrom (\n" +
            "\t\tselect *\t\n" +
            "\t\tfrom weibo_user_works \n" +
            "\t\twhere is_delete!='1' and id=:workId\n" +
            "\t) t1 left join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction t\n" +
            "\t\twhere t.user_id = :userId and t.type = '0' and work_id=:workId\n" +
            "\t) t2 on t1.id = t2.id\n" +
            "\tleft join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction tt\n" +
            "\t\twhere tt.user_id = :userId and tt.type = '1' and work_id=:workId\n" +
            "\t) t3 on t1.id = t3.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname nickname2,head_img head_img2 from weibo_user\n" +
            "\t) t4 on t1.user_id=t4.id", nativeQuery = true)
    List<Map<String, Object>> getWorkDetail(@Param(value = "userId") String userId,@Param(value = "workId") String workId);

    @Query(value = "select t1.*,'1' myUp,IF(IFNULL(t3.id,'0') = '0','0','1') myTalk ,t4.nickname2,t4.head_img2\n" +
            "\tfrom (\n" +
            "\t\tselect *\t\n" +
            "\t\tfrom weibo_user_works \n" +
            "\t\twhere is_delete!='1' and id in :ids\n" +
            "\t) t1 left join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction tt\n" +
            "\t\twhere tt.user_id = :userId and tt.type = '1' and tt.work_id in :ids\n" +
            "\t) t3 on t1.id = t3.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname nickname2,head_img head_img2 from weibo_user\n" +
            "\t) t4 on t1.user_id=t4.id", nativeQuery = true)
    List<Map<String, Object>> getLikeWorks(@Param(value = "ids") List<String> ids,@Param(value = "userId") String userId);



    @Query(value = "select t1.*,IF(IFNULL(t2.id,'0') = '0','0','1') myUp,IF(IFNULL(t3.id,'0') = '0','0','1') myTalk ,t4.nickname2,t4.head_img2\n" +
            "\tfrom (\n" +
            "\t\tselect *\t\n" +
            "\t\tfrom weibo_user_works \n" +
            "\t\twhere is_delete!='1' and user_id = :userId\n" +
            "\t\torder by create_time desc limit :start,:size\n" +
            "\t) t1 left join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction t\n" +
            "\t\twhere t.user_id = :userId and t.type = '0'\n" +
            "\t) t2 on t1.id = t2.id\n" +
            "\tleft join (\n" +
            "\t\tselect DISTINCT work_id id from weibo_user_works_interaction tt\n" +
            "\t\twhere tt.user_id = :userId and tt.type = '1'\n" +
            "\t) t3 on t1.id = t3.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname nickname2,head_img head_img2 from weibo_user\n" +
            "\t) t4 on t1.user_id=t4.id", nativeQuery = true)
    List<Map<String, Object>> getUserWorkList(@Param(value = "userId") String userId,@Param(value = "start") int start,
                                              @Param(value = "size") int size);

    @Query(value = "select count(*) from weibo_user_works where user_id=:userId and is_delete!=1", nativeQuery = true)
    int getOneWorksTotal(@Param(value = "userId") String userId);


    @Query(value = "select * from weibo_user_works where is_delete != '1'", nativeQuery = true)
    List<UserWork> getAllWorkList(Pageable pageable);

    @Query(value = "select count(*) from weibo_user_works where is_delete != '1'", nativeQuery = true)
    int getAllWorkListTotal();


    @Query(value = "select * from weibo_user_works where is_delete != '1' and report>0 order by report desc", nativeQuery = true)
    List<UserWork> getReportList(Pageable pageable);

    @Query(value = "select count(*) from weibo_user_works where is_delete != '1' and report>0 order by report desc", nativeQuery = true)
    int getReportListTotal();

    @Query(value = "select * from weibo_user_works where is_delete = '1'", nativeQuery = true)
    List<UserWork> getDeleteList(Pageable pageable);

    @Query(value = "select count(*) from weibo_user_works where is_delete = '1'", nativeQuery = true)
    int getDeleteListTotal();

    @Modifying
    @Query(value = "update weibo_user_works set hot=0 where create_time<:theDate", nativeQuery = true)
    void updateHot(@Param(value = "theDate")Date theDate);

    @Query(value = "select * from weibo_user_works where create_time <= end and create_time > start", nativeQuery = true)
    List<UserWork> getUserWorkBetween(@Param(value = "start") Date start,@Param(value = "end") Date end);

}
