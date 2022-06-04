package top.hwlljy.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class InteractionDto {
    @NotNull
    @NotBlank
    private String type;

    @NotNull
    @NotBlank
    private String workId;

    @NotNull
    @NotBlank
    private String toUserId;

    @NotNull
    @NotBlank
    private String toUserNickname;

    @NotNull
    @NotBlank
    private String toUsername;

    private int level;

    private String father;

    private String reply;

    private String message;
}
