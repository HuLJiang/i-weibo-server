package top.hwlljy.model.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "weibo_user",
        uniqueConstraints = @UniqueConstraint(columnNames = {"username", "nickname"}))
@Entity
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1234L;

    @Column(name = "username",columnDefinition = "varchar(50) NULL COMMENT '账号'")
    private String username;

    @Column(name = "nickname",columnDefinition = "varchar(50) NULL COMMENT '昵称'")
    private String nickname;

    @Column(name = "role",columnDefinition = "int NULL COMMENT '权限'")
    private int role;

    @Column(name = "password",columnDefinition = "varchar(50) NULL COMMENT '用户密码'")
    private String password;

    @Column(name = "head_img",columnDefinition = "varchar(255) NULL COMMENT '用户头像'")
    private String headImg;

    @Column(name = "all_num", columnDefinition = "int NULL DEFAULT 0 COMMENT '全部微博数量'")
    private int allNum;

    @Column(name = "about", columnDefinition = "varchar(1000) NULL COMMENT '简介'")
    private String about;

    @Column(name = "sex", columnDefinition = "varchar(6) NULL DEFAULT '2' COMMENT '性别0男1女2未知'")
    private String sex;

    @Column(name = "birthday", columnDefinition = "date NULL COMMENT '生日'")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @Column(name = "report", columnDefinition = "int NULL DEFAULT 0 COMMENT '举报次数'")
    private int report;

    @Column(name = "try_login_times", columnDefinition = "int NULL DEFAULT 0 COMMENT '用户登录次数'")
    private int tryLoginTimes;

    @Column(name = "is_lock", columnDefinition = "varchar(3) NULL DEFAULT '0' COMMENT '用户是否被锁（0没锁 1锁）'")
    private String isLock;

    @Column(name = "is_ban", columnDefinition = "varchar(3) NULL DEFAULT '0' COMMENT '0正常 1封禁'")
    private String isBan;

    @Column(name = "is_delete", columnDefinition = "varchar(3) NULL DEFAULT '0' COMMENT '0未删除 1已删除'")
    private String isDelete;
}
