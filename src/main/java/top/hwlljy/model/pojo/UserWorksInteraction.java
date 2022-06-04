package top.hwlljy.model.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "weibo_user_works_interaction",
        indexes = {@Index(columnList = "work_id"), @Index(columnList = "user_id"),
                @Index(columnList = "father",name = "father_index"),@Index(columnList = "reply_id",name = "reply_id_index")})
@Entity
public class UserWorksInteraction extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1234L;

    @Column(name = "work_id", columnDefinition = "varchar(32) NOT NULL COMMENT '作品id'")
    private String workId;

    @Column(name = "user_id", columnDefinition = "varchar(32) NOT NULL COMMENT '用户id'")
    private String userId;

    @Column(name = "head_img", columnDefinition = "varchar(255) NULL COMMENT '用户头像'")
    private String headImg;

    @Column(name = "user_nickname", columnDefinition = "varchar(50) NOT NULL COMMENT '用户昵称'")
    private String userNickname;

    @Column(name = "username", columnDefinition = "varchar(50) NOT NULL COMMENT '用户'")
    private String username;

    @Column(name = "to_user_id", columnDefinition = "varchar(32) NOT NULL COMMENT '被点赞或评论用户id'")
    private String toUserId;

    @Column(name = "to_username", columnDefinition = "varchar(32) NOT NULL COMMENT '被点赞或评论用户'")
    private String toUsername;

    @Column(name = "to_user_nickname", columnDefinition = "varchar(32) NOT NULL COMMENT '被点赞或评论用户昵称'")
    private String toUserNickname;

    @Column(name = "type", columnDefinition = "varchar(3) NULL COMMENT '互动类别（0点赞 1评论 2艾特）'")
    private String type;

    @Column(name = "message", columnDefinition = "varchar(200) NULL COMMENT '互动消息，点赞时为空'")
    private String message;

    @Column(name = "is_blogger", columnDefinition = "varchar(3) NULL DEFAULT '0' COMMENT '是否是博主（0不是 1是）'")
    private String isBlogger;

    @Column(name = "level", columnDefinition = "varchar(3) NULL DEFAULT '0' COMMENT '回复评论等级0回复博主（一级评论）1回复一级评论（二级评论）2回复二级评论（会有@标识）'")
    private String level;

    @Column(name = "father", columnDefinition = "varchar(32) NULL DEFAULT '0' COMMENT '父级评论'")
    private String father;

    @Column(name = "reply_id", columnDefinition = "varchar(32) NULL DEFAULT '0' COMMENT '回复评论id'")
    private String replyId;

    @Column(name = "is_read", columnDefinition = "varchar(3) NULL DEFAULT '0' COMMENT '是否读过（0未读 1已读）'")
    private String isRead;

    @Column(name = "is_delete", columnDefinition = "varchar(3) NULL DEFAULT '0' COMMENT '0未删除 1已删除'")
    private String isDelete;
}
