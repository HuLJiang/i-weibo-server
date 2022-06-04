package top.hwlljy.model.dto.superadmin;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RoleDto {

    @NotNull
    @NotBlank
    private String id;

    private int role;
}
