package top.hwlljy.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import top.hwlljy.model.pojo.UserWorksInteraction;

import java.util.List;
import java.util.Map;

public interface WorkInteractionRepository extends JpaRepository<UserWorksInteraction, String> {

    UserWorksInteraction findAllByUserIdAndWorkIdAndToUserIdAndType(String userId,String workId,String toUserId,String type);

    //查找一级评论
    @Query(value = "select t1.*,IFNULL(t2.cnt,0) sonTalk,t3.nickname realNickname,t3.head_img realHead,t4.nickname realToNickname from (\n" +
            "\t\tselect * from weibo_user_works_interaction \n" +
            "\t\twhere work_id=:workId and `level`='0' and type='1'\n" +
            "\t) t1 left join (\n" +
            "\t\tselect father id,count(*) cnt from weibo_user_works_interaction \n" +
            "\t\twhere work_id=:workId and `level`!='0' and type='1'\n" +
            "\t\tgroup by father\n" +
            "\t) t2 on t1.id = t2.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname,head_img from weibo_user\n" +
            "\t) t3 on t1.user_id=t3.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname from weibo_user\n" +
            "\t) t4 on t1.to_user_id=t4.id order by t1.create_time", nativeQuery = true)
    List<Map<String, Object>> getUserTalk(@Param(value = "workId") String workId, Pageable pageable);

    //查找二级评论
    @Query(value = "select t1.*,t3.nickname realNickname,t3.head_img realHead,t4.nickname realToNickname from \n" +
            "\t(select * from weibo_user_works_interaction \n" +
            "\twhere father=:talkId and `level`!='0' and type='1') t1\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname,head_img from weibo_user\n" +
            "\t) t3 on t1.user_id=t3.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname from weibo_user\n" +
            "\t) t4 on t1.to_user_id=t4.id order by create_time", nativeQuery = true)
    List<Map<String, Object>> getTwoLevelTalk(@Param(value = "talkId") String talkId, Pageable pageable);

    //分页查询点过赞的作品id
    @Query(value = "select work_id from weibo_user_works_interaction where type='0' and user_id=:userId order by create_time desc", nativeQuery = true)
    List<String> getLikeIds(@Param(value = "userId") String userId, Pageable pageable);

    //分页查询被点过赞的作品id
    @Query(value = "select work_id from weibo_user_works_interaction where type='0' and to_user_id=:userId order by create_time desc", nativeQuery = true)
    List<String> getLikeMeIds(@Param(value = "userId") String userId, Pageable pageable);

    @Query(value = "select t1.create_time,t1.work_id,t1.id,t1.user_id,t2.head_img,t2.nickname,t2.username,t3.content from (\n" +
            "\tselect * from weibo_user_works_interaction \n" +
            "\twhere type='0' and to_user_id=:userId\n" +
            "\torder by create_time desc limit :start,:size\n" +
            "\t) t1 left join (\n" +
            "\t\tselect id,head_img,nickname,username from weibo_user\n" +
            "\t) t2 on t1.user_id=t2.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,content from weibo_user_works where user_id=:userId\n" +
            "\t) t3 on t1.work_id=t3.id order by t1.create_time desc",nativeQuery = true)
    List<Map<String, Object>> getLikeMeList(@Param(value = "userId") String userId,@Param(value = "start") int start,
                                            @Param(value = "size") int size);

    @Query(value = "select t1.create_time,t1.work_id,t1.id,t1.user_id,\n" +
            "\tt1.message,t1.level,t2.msg,t3.content,t4.username,t4.nickname,t5.head_img workImg,t5.nickname workNickname from (\n" +
            "\tselect * from weibo_user_works_interaction \n" +
            "\twhere type='1' and user_id=:userId\n" +
            "\torder by create_time desc limit :start,:size\n" +
            "\t) t1 left join (\n" +
            "\t\tselect id,message msg from weibo_user_works_interaction\n" +
            "\t) t2 on t1.reply_id=t2.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,content,user_id from weibo_user_works\n" +
            "\t) t3 on t1.work_id=t3.id \n" +
            "\tleft join ( \n" +
            "\tselect id,nickname,username from weibo_user\n" +
            "\t) t4 on t1.to_user_id=t4.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname,head_img from weibo_user\n" +
            "\t) t5 on t3.user_id=t5.id order by t1.create_time desc",nativeQuery = true)
    List<Map<String, Object>> getMyTalk(@Param(value = "userId") String userId,@Param(value = "start") int start,
                                        @Param(value = "size") int size);

    @Query(value = "select t1.create_time,t1.work_id,t1.id,t1.user_id,t1.message,\n" +
            "\tt1.level,t1.father,t2.head_img,t2.nickname,t2.username,t3.content,t5.head_img workImg,t5.nickname workNickname from (\n" +
            "\tselect * from weibo_user_works_interaction \n" +
            "\twhere type='1' and to_user_id=:userId\n" +
            "\torder by create_time desc limit :start,:size\n" +
            "\t) t1 left join (\n" +
            "\t\tselect id,head_img,nickname,username from weibo_user\n" +
            "\t) t2 on t1.user_id=t2.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,content,user_id from weibo_user_works\n" +
            "\t) t3 on t1.work_id=t3.id\n" +
            "\tleft join (\n" +
            "\t\tselect id,nickname,head_img from weibo_user\n" +
            "\t) t5 on t3.user_id=t5.id order by t1.create_time desc",nativeQuery = true)
    List<Map<String, Object>> getToMyTalk(@Param(value = "userId") String userId,@Param(value = "start") int start,
                                        @Param(value = "size") int size);

    @Query(value = "select count(*) num,type from weibo_user_works_interaction where to_user_id=:userId and is_read='0' group by type", nativeQuery = true)
    List<Map<String, Object>> getReplyTotal(@Param(value = "userId") String userId);

    @Modifying
    @Query(value = "update weibo_user_works_interaction set is_read='1' where to_user_id=:userId and type=:type", nativeQuery = true)
    void readMessage(@Param(value = "userId") String userId,@Param(value = "type") String type);
}
