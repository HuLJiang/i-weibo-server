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
@Table(name = "weibo_user_search",indexes = {@Index(columnList = "user_id")})
@Entity
public class UserSearch extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1234L;

    @Column(name = "word", columnDefinition = "varchar(255) NULL COMMENT '用户搜索关键词'")
    private String word;

    @Column(name = "hot", columnDefinition = "int NULL DEFAULT 0 COMMENT '热度'")
    private int hot;

    @Column(name = "user_id", columnDefinition = "varchar(32) NULL COMMENT '用户id'")
    private String userId;
}
