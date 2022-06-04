package top.hwlljy.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class RegisterUserDto {
    @NotNull
    @NotBlank
    private String nickname;
    @NotNull
    @NotBlank
    private String password;
    @NotNull
    @NotBlank
    private String repassword;

    private Date birthday;

    @NotNull
    @NotBlank
    private String checkMsg;
}
