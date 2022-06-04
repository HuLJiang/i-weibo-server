package top.hwlljy.model.dto.user;

import lombok.Data;
import top.hwlljy.model.dto.PageQuery;

import javax.validation.constraints.NotNull;

@Data
public class WorkListDto extends PageQuery {
    //是否是获取最新，1最新，0热门
    @NotNull
    private int newest;
}
