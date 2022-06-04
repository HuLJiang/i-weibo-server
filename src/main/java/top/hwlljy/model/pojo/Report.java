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
@Table(name = "weibo_report",
        indexes = @Index(name = "",columnList = "relation_id"))
@Entity
public class Report extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1234L;

    @Column(name = "relation_id", columnDefinition = "varchar(32) NULL COMMENT '被举报id'")
    private String relationId;

    @Column(name = "type", columnDefinition = "varchar(3) NULL COMMENT '被举报类型（0用户 1作品）'")
    private String type;

    @Column(name = "reason", columnDefinition = "varchar(255) NULL COMMENT '举报原因'")
    private String reason;
}
