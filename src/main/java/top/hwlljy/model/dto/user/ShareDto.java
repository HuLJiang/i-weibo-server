package top.hwlljy.model.dto.user;

import lombok.Data;
import top.hwlljy.model.pojo.UserWorkAttach;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ShareDto {

    @NotNull
    @NotBlank
    private String content;

    @NotNull
    @NotBlank
    private String shareScope;

    private List<UserWorkAttach> attaches;
}
