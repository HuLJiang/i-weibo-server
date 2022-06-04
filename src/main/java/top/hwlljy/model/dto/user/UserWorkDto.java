package top.hwlljy.model.dto.user;

import lombok.Data;
import top.hwlljy.model.dto.PageQuery;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserWorkDto extends PageQuery {

    @NotNull
    @NotBlank
    private String username;
}