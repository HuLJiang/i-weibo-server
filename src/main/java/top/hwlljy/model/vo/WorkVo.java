package top.hwlljy.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WorkVo {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm", timezone="GMT+8")
    private Date createTime;

    private int hot;

    private String id;

    private String content;

    private String isReshare;

    private int upNum;

    private int talkNum;

    private int reshareNum;

    private String fromUserNickname;

    private String fromUsername;

    private String reshareContent;

    private String headImg;

    private String userNickname;

    private String username;

    private String userId;

    private String shareScope;

    private String hasAttaches;

    private String myUp;

    private String myTalk;

    private List<String> imgs;

    private String isMe = "0";

    private String isFollow = "0";
}
