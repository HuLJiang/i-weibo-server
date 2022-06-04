package top.hwlljy.model.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "weibo_user_role")
@Entity
public class UserRole extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1234L;

    @Column(name = "role_id", columnDefinition = "int NULL DEFAULT 0 COMMENT '权限id'")
    private int roleId;

    @Column(name = "role_str", columnDefinition = "varchar(32) NOT NULL COMMENT '权限表达二进制，从左到右依次为" +
            "（超级管理员 0 0 0 管理员 0 普通用户），例如管理员的表达为1000000，普通用户0000001，管理员0000100" +
            "用户可同时拥有多种权限，给用户加权限，例如，超级管理员拥有一切权限所有超级管理员的表达为1111111，也就是127'")
    private int roleStr;

    @Column(name = "role_name", columnDefinition = "varchar(50) NULL DEFAULT '' COMMENT '权限名称'")
    private String roleName;

    @Column(name = "pattern", columnDefinition = "varchar(255) NULL DEFAULT '' COMMENT '限制接口'")
    private String pattern;
}
