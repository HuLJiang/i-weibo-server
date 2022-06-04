package top.hwlljy.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import top.hwlljy.cache.UserCache;
import top.hwlljy.model.constant.Constants;
import top.hwlljy.model.pojo.User;
import top.hwlljy.model.pojo.UserToken;
import top.hwlljy.repository.TokenRepository;
import top.hwlljy.utils.RedisUtil;
import top.hwlljy.utils.UserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Configuration
@EnableSwagger2
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Value(value = "${top.hwlljy.anonymous}")
    private String anonymous;

    @Value(value = "${top.hwlljy.role}")
    private String roleString;

    @Value(value = "${top.hwlljy.user.attach}")
    private String basePath;

    @Value(value = "${top.hwlljy.host}")
    private String host;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserCache userCache;


    //redis
    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // key序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // Hash key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // Hash value序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    //拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> list = Arrays.asList(anonymous.split(","));
        String[] roles = roleString.split(",");
        log.info(list.toString());
        //从数据库中读取所有权限，并且做限制
        for(String userRole : roles) {
            String[] role = userRole.split("\\-");
            int roleId = Integer.parseInt(role[0]);
            InterceptorRegistration interceptorRegistration = registry.addInterceptor(addNewInterceptor(roleId));
            interceptorRegistration.addPathPatterns(role[1]);
            if(roleId == 1) {
                interceptorRegistration.excludePathPatterns(list);
            }
        }
    }

    private HandlerInterceptor addNewInterceptor(int roleId) {
        return new HandlerInterceptor() {
            private final int role = roleId;
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                HttpSession session = request.getSession();
                User user = (User) session.getAttribute(Constants.LOGIN_USER_SESSION_KEY.getVal());
                if(user == null) {
                    response.sendRedirect(host + "/user/needLogin");
                } else {
                    String token = request.getHeader("token");
                    if(userCache.checkTokenExpire(token,user)) {
                        //权限认证
                        return (user.getRole() & role) == role;
                    }else {
                        response.sendRedirect(host + "/user/needLogin");
                    }

                }
                return false;
            }

        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/").addResourceLocations("file:" + basePath);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    //swagger配置
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("top.hwlljy.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("简易微博项目api")
                .description("")
                .version("1.0.0")
                .build();
    }
}
