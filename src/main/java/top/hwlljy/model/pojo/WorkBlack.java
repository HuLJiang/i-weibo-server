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
@Table(name = "weibo_work_black",
        indexes = @Index(columnList = "user_id"))
@Entity
public class WorkBlack extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1234L;

    @Column(name = "user_id", columnDefinition = "varchar(32) NULL COMMENT '用户id'")
    private String userId;

    @Column(name = "work_id", columnDefinition = "varchar(32) NULL COMMENT '被屏蔽作品id'")
    private String workId;
}
