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
@Table(name = "weibo_user_black_list",
        indexes = @Index(columnList = "user_id"))
@Entity
public class UserBlackList extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1234L;

    @Column(name = "user_id", columnDefinition = "varchar(32) NULL COMMENT '用户id'")
    private String userId;

    @Column(name = "user_nickname", columnDefinition = "varchar(50) NULL COMMENT '用户昵称'")
    private String userNickname;

    @Column(name = "to_user_id", columnDefinition = "varchar(32) NULL COMMENT '被屏蔽用户id'")
    private String toUserId;

    @Column(name = "to_user_nickname", columnDefinition = "varchar(32) NULL COMMENT '被屏蔽用户昵称'")
    private String toUserNickname;
}
