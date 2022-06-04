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
@Table(name = "weibo_user_work_attaches",
        indexes = @Index(columnList = "work_id"))
@Entity
public class UserWorkAttach extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1234L;

    @Column(name = "type", columnDefinition = "varchar(3) NULL COMMENT '资源类型（0图片 1视频）'")
    private String type;

    @Column(name = "url", columnDefinition = "varchar(255) NULL COMMENT '资源url'")
    private String url;

    @Column(name = "work_id", columnDefinition = "varchar(32) NULL COMMENT '作品id'")
    private String workId;
}
