package top.hwlljy.model.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "weibo_user_works",
        indexes = @Index(columnList = "user_id", name = "user_id_index"))
@Entity
public class UserWork extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1234L;

    @Column(name = "content", columnDefinition = "varchar(1000) NULL COMMENT '作品内容'")
    private String content;

    @Column(name = "reshare_num", columnDefinition = "int NULL DEFAULT 0 COMMENT '分享次数'")
    private int reshareNum;

    @Column(name = "up_num", columnDefinition = "int NULL DEFAULT 0 COMMENT '点赞数量'")
    private int upNum;

    @Column(name = "talk_num", columnDefinition = "int NULL DEFAULT 0 COMMENT '评论数量'")
    private int talkNum;

    @Column(name = "count", columnDefinition = "int NULL DEFAULT 0 COMMENT '用户点击或者点赞总次数'")
    private int count;

    @Column(name = "hot", columnDefinition = "int NULL DEFAULT 0 COMMENT '热度'")
    private int hot;

    @Column(name = "report", columnDefinition = "int NULL DEFAULT 0 COMMENT '举报次数'")
    private int report;

    @Column(name = "user_id", columnDefinition = "varchar(32) NULL COMMENT '用户id'")
    private String userId;

    @Column(name = "username", columnDefinition = "varchar(50) NULL COMMENT '用户账号'")
    private String username;

    @Column(name = "user_nickname", columnDefinition = "varchar(50) NULL COMMENT '用户名称'")
    private String userNickname;

    @Column(name = "head_img", columnDefinition = "varchar(255) NULL COMMENT '用户头像'")
    private String headImg;

    @Column(name = "share_scope", columnDefinition = "varchar(3) NULL COMMENT '0公开 1粉丝 2自己可见'")
    private String shareScope;

    @Column(name = "has_attaches", columnDefinition = "varchar(3) NULL DEFAULT '0' COMMENT '是否包含附件(图片)'")
    private String hasAttaches;

    @Column(name = "is_reshare", columnDefinition = "varchar(3) NULL DEFAULT '0' COMMENT '是否是转发'")
    private String isReshare;

    @Column(name = "from_username", columnDefinition = "varchar(32) NULL COMMENT '转发的用户的用户名'")
    private String fromUsername;

    @Column(name = "from_user_nickname", columnDefinition = "varchar(50) NULL COMMENT '转发的用户昵称'")
    private String fromUserNickname;

    @Column(name = "reshare_content", columnDefinition = "varchar(500) NULL COMMENT '转发的内容'")
    private String reshareContent;

    @Column(name = "is_delete", columnDefinition = "varchar(3) NULL DEFAULT '0' COMMENT '0未删除 1已删除'")
    private String isDelete;

}
