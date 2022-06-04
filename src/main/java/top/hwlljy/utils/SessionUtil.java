package top.hwlljy.utils;


import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.hwlljy.model.constant.Constants;
import top.hwlljy.model.pojo.User;

import java.util.Objects;

public class SessionUtil {

    private SessionUtil() {

    }

    public static User getUser() {
        return (User) ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY.getVal());
    }

    public static void removeUser() {
        ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getSession().removeAttribute(Constants.LOGIN_USER_SESSION_KEY.getVal());
    }

    public static void setUser(User user) {
        ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getSession().setAttribute(Constants.LOGIN_USER_SESSION_KEY.getVal(),user);
    }

    public static String tt() {
        return (String) ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY.getVal());
    }

    public static String getUserId() {
        return getUser().getId();
    }

    public static String getCheckMsg() {
        return (String) ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getSession().getAttribute(Constants.CHECK_MSG.getVal());
    }

    public static void setCheckMsg(String code) {
        ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                .getRequest().getSession().setAttribute(Constants.CHECK_MSG.getVal(),code);
    }
}
