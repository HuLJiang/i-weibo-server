package top.hwlljy.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TalkVo {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone="GMT+8")
    private Date createTime;

    private String workId;

    private String id;

    private String userId;

    private String userNickname;

    private String username;

    private String toUserId;

    private String toUserNickname;

    private String toUsername;

    private String message;

    private String level;

    private String headImg;

    private String talkCnt;
}
