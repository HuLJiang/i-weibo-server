package top.hwlljy.utils;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import top.hwlljy.model.constant.Constants;
import top.hwlljy.model.pojo.User;
import top.hwlljy.model.vo.UserVo;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class UserUtil {

    @Value(value = "${top.hwlljy.file.root}")
    private static String basePath;

    private UserUtil() {

    }

    public static String getCheckImg() {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200,100);
        String path = basePath + UserUtil.uuid() + ".png";
        File file = new File(path);
        String base64 = Constants.MSG_ERROR.getVal();
        try {
            if(file.createNewFile()) {
                SessionUtil.setCheckMsg(lineCaptcha.getCode());
                lineCaptcha.write(file);
                base64 = "data:image/png;base64," +  Base64.encode(file);
                Files.delete(file.toPath());
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        }

        return base64;
    }

    public static String setMd5Password(String password) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(password.getBytes());
        } catch (NoSuchAlgorithmException e) {
            log.error("不支持md5加密");
            e.printStackTrace();
            return "";
        }
        StringBuilder md5Code = new StringBuilder((new BigInteger(1, secretBytes)).toString(16));
        for(int i = 0; i < 32 - md5Code.length(); ++i) {
            md5Code.insert(0, "0");
        }
        return md5Code.toString();
    }

    public static String uuid() {
        Pattern pattern = Pattern.compile("\\-");
        Matcher matcher = pattern.matcher(UUID.randomUUID().toString());
        return matcher.replaceAll("");
    }

    public static String randomPsd() {
        SecureRandom random = new SecureRandom();
        char[] ch = {'~','!','@','#','$','%','^','&','*','_','.'};
        StringBuilder res = new StringBuilder();
        for(int i = 0;i < 16;i ++) {
            int t = random.nextInt(4);
            switch (t) {
                case 0:
                    res.append(random.nextInt(10));
                    break;
                case 1:
                    res.append((char)(random.nextInt(26) + 'a'));
                    break;
                case 2:
                    res.append((char)(random.nextInt(26) + 'A'));
                    break;
                case 3:
                    res.append(ch[random.nextInt(ch.length)]);
                    break;
                default:
            }
        }
        return res.toString();
    }

    public static synchronized User deleteUser(User user) {
        user.setIsDelete("1");
        user.setRole(0);
        user.setAbout("");
        user.setHeadImg("");
        return user;
    }

    public static UserVo userPojoToVo(User user) {
        UserVo userVo = new UserVo();
        userVo.setBirthday(user.getBirthday());
        userVo.setCreatTime(user.getCreateTime());
        userVo.setId(user.getId());
        userVo.setHeadImg(user.getHeadImg());
        userVo.setNickname(user.getNickname());
        userVo.setUsername(user.getUsername());
        userVo.setRole(user.getRole());
        userVo.setAbout(user.getAbout());
        userVo.setAllNum(user.getAllNum());
        userVo.setSex(user.getSex());
        return userVo;
    }
}
