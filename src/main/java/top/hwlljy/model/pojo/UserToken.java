package top.hwlljy.model.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "weibo_user_token",indexes = { @Index(columnList = "token",name = "token_index")},
        uniqueConstraints = @UniqueConstraint(name = "user_id_unique", columnNames = {"user_id"}))
@Entity
public class UserToken extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1234L;

    @Column(name = "token", columnDefinition = "varchar(32) NULL COMMENT 'token'")
    private String token;

    @Column(name = "user_id", columnDefinition = "varchar(32) NULL COMMENT '用户id'")
    private String userId;

    @Column(name = "end_time", columnDefinition = "datetime NULL COMMENT 'token截至时间'")
    private Date endTime;

}
