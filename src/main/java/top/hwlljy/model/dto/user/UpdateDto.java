package top.hwlljy.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class UpdateDto {

    @NotNull
    @NotBlank
    private String nickname;

    private String headImg;

    private Date birthday;

    private String sex;

    private String about;
}
