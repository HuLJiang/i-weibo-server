package top.hwlljy;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.codec.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.hwlljy.utils.SessionUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
class IWeiboApplicationTests {

    @Test
    void contextLoads() throws FileNotFoundException {
        File f = new File("d:/test.txt");
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8));
        pw.println(getCheckImg());
        pw.flush();
    }
    public static String getCheckImg() {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200,100);
        File file = new File("d:/test.png");
        SessionUtil.setCheckMsg(lineCaptcha.getCode());
        lineCaptcha.write(file);
        file.deleteOnExit();
        return "data:image/png;base64," + Base64.encode(file);
    }

}
