package top.hwlljy.model.dto.admin;

import lombok.Data;

@Data
public class ModifyDto {
    private String nickname;

    private String username;

    private String id;

    private String isBan;

    private String idDelete = "0";
}
