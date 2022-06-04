package top.hwlljy.utils;

import java.util.HashMap;
import java.util.Map;

public class ResultBody {
    private String message;
    private boolean isSuccess;
    private Map<String, Object> data;

    public ResultBody() {

    }

    public ResultBody(String message, boolean isSuccess, Map<String, Object> data) {
        this.message = message;
        this.isSuccess = isSuccess;
        this.data = data;
    }
    public static ResultBody success(Map<String, Object> data) {
        data.put("status","1");
        return new ResultBody("请求成功",true,data);
    }
    public static ResultBody success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("data",data);
        return success(result);
    }

    public static ResultBody success() {
        Map<String, Object> result = new HashMap<>();
        result.put("status","1");
        return new ResultBody("请求成功",true,result);
    }

    public static ResultBody fail(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("status","0");
        result.put("msg",message);
        return new ResultBody("请求成功",true,result);
    }

    public static ResultBody fail() {
        Map<String, Object> result = new HashMap<>();
        result.put("status","0");
        return new ResultBody("请求成功",true,result);
    }

    public static ResultBody error(String message) {
        return new ResultBody(message,false,null);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
