package top.hwlljy.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserVo {

    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date creatTime;

    private String id;

    private String username;

    private String nickname;

    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date birthday;

    private int followNum;

    private int fansNum;

    private String sex;

    private String headImg;

    private String about;

    private int role;

    private String isFollow = "0";

    private int allNum;

    private String isMe = "0";

    private boolean show;

    private String isDelete;

    private String isBlack = "0";


}
