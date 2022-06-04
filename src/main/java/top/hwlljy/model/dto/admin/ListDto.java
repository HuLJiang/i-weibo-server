package top.hwlljy.model.dto.admin;

import lombok.Data;
import top.hwlljy.model.dto.PageQuery;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ListDto extends PageQuery {

    @NotNull
    @NotBlank
    private String type;
}
