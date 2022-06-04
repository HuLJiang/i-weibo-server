package top.hwlljy.model.constant;

public enum  Constants {
    SUPER_ADMIN(64,"超级管理员"),
    ADMIN(4,"管理员"),
    USER(1,"普通用户"),
    TOKEN_EXPIRE(15,"token过期时间，单位，天"),
    TOKEN_RENEW(30,"token续期时间，单位，分钟"),
    USER_LOCK(0,"1","用户已被锁定，无法登录"),
    USER_UNLOCK(0,"0","用户状态正常"),
    LOGIN_USER_SESSION_KEY(0,"LOGIN_USER","session储存用户用的key"),
    CHECK_MSG(0,"CHECK_MSG","验证码"),
    WORK_LIST_ALL(0,"获取普通作品列表"),
    WORK_LIST_FOLLOW(1,"获取关注用户作品列表"),
    TIME_LIST(1,"获取常规按照时间倒叙作品列表"),
    HOT_LIST(0,"获取最热门的作品列表"),
    SHARE_SCOPE_ALL(0,"0","作品分享公开"),
    SHARE_SCOPE_FANS(1,"1","作品分享粉丝可见"),
    SHARE_SCOPE_ONLY(2,"2","作品分享自己可见"),
    COMMON_FALSE(0,"0","某资源为0，否，没有"),
    COMMON_TRUE(1,"1","某资源为1，是，有"),
    USER_UP(0,"0","用户操作点赞功能"),
    USER_TALK(1,"1","用户操作评论功能"),
    MSG_ERROR(1,"-1","获取验证码失败"),
    REPORT_WORK(1,"1","举报作品"),
    REPORT_USER(0,"0","举报用户"),
    ALL_LIST(0,"0","管理员查看常规列表"),
    DELETE_LIST(1,"1","管理员查看已被删除的列表"),
    REPORT_LIST(2,"2","管理员查看被举报的列表"),
    SEX_MAN(0,"0","性别男"),
    SEX_WOMAN(1,"1","性别女"),
    SEX_UN_KNOW(2,"2","性别未知"),
    REPLY_ME(0,"0","我的回复"),
    REPLY_TO_ME(1,"1","回复我的"),
    BLACK_USER(0,"0","拉黑用户"),
    BLACK_WORK(1,"1","拉黑作品")
    ;

    private int value;
    private String val;
    private String name;

    Constants(int value, String name) {
        this.value = value;
        this.name = name;
    }

    Constants(int value, String val, String name) {
        this.value = value;
        this.val = val;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getVal() {
        return val;
    }
}
