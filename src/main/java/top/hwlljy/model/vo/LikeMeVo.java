package top.hwlljy.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class LikeMeVo {

    private String tId;

    private String workId;

    private String userId;

    private String username;

    private String nickname;

    private String headImg;

    private String level;

    private String message;

    private String msg;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone="GMT+8")
    private Date createTime;

    private String workContent;

    private String iNickname;

    private String iHeadImg;

    private String workImg;

    private String workNickname;

    private String father;

}
