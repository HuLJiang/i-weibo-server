package top.hwlljy.cache;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import top.hwlljy.model.pojo.User;
import top.hwlljy.model.pojo.UserToken;
import top.hwlljy.repository.FollowerRepository;
import top.hwlljy.repository.TokenRepository;
import top.hwlljy.utils.RedisUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class UserCache {
    //缓存默认储存时间,单位分钟
    private static final int CACHE_TIME = 30;
    //缓存储存作品图片时间，单位分钟
    private static final int ATTACH_CACHE = 5 * 24 * 60;
    //缓存图片redis储存后缀
    private static final String ATTACH_SUFFIX = "ATTACH";

    private static final String HOT_SUFFIX = "HOT_NUM";
    //缓存关注的人的id
    private static final String FOLLOW_SUFFIX = "Followers";
    //缓存关注的人的缓存时间，单位分钟
    private static final int FOLLOW_TIME = 100;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private FollowerRepository followerRepository;

    public boolean checkToken(User user) {
        UserToken userToken = null;
        try {
            userToken = (UserToken) redisUtil.get(user.getId());
            if(userToken == null) {
                userToken = tokenRepository.findAllByUserId(user.getId());
                redisUtil.set(user.getId(),userToken,CACHE_TIME);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //redis挂了
            userToken = tokenRepository.findAllByUserId(user.getId());
        }
        if(userToken == null) {
            return false;
        }
        Date now = new Date();
        if(userToken.getEndTime().compareTo(now) >= 0) {
            redisUtil.set(userToken.getToken(),user,CACHE_TIME);
            return true;
        }
        tokenRepository.delete(userToken);
        return false;

    }

    public void removeToken(String token) {
        redisUtil.del(token);
    }

    public boolean checkTokenExpire(String token,User user) {
        UserToken userToken = null;
        try {
            userToken = (UserToken) redisUtil.get(token);
            if(userToken == null) {
                userToken = tokenRepository.findAllByToken(token);
            }
        } catch (Exception e) {
            userToken = tokenRepository.findAllByToken(token);
        }
        //没有token或者token与用户不匹配
        if(userToken == null || !userToken.getUserId().equals(user.getId())) {
            return false;
        }
        Date now = new Date();
        redisUtil.set(userToken.getToken(),userToken,CACHE_TIME);
        if(userToken.getEndTime().compareTo(now) >= 0) {
            return true;
        }
        //token过期了自动续token时长
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY,5);
        userToken.setEndTime(calendar.getTime());
        redisUtil.set(userToken.getToken(),userToken,CACHE_TIME);
        tokenRepository.save(userToken);
        return true;
    }

    public void saveToken(UserToken userToken) {
        try {
            redisUtil.set(userToken.getToken(),userToken,CACHE_TIME);
        }catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    public void setWorkAttach(String workId, List<String> img) {
        redisUtil.set(workId + ATTACH_SUFFIX, img, ATTACH_CACHE);
    }

    public boolean setHot(String workId, Integer num) {
        return redisUtil.set(workId + HOT_SUFFIX,num,CACHE_TIME);
    }

    public List<String> getWorkAttach(String workId) {
        return (List<String>) redisUtil.get(workId + ATTACH_SUFFIX);
    }

    public void setFollower(String userId, List<String> ids) {
        if(CollUtil.isEmpty(ids)) {
            ids.add("1");
        }
        ids.forEach(item -> redisUtil.sSetAndTime(userId + FOLLOW_SUFFIX,FOLLOW_TIME,item));
    }

    public void setFollower(String userId, String toId) {
        if(redisUtil.hasKey(userId + FOLLOW_SUFFIX)) {
            redisUtil.sSetAndTime(userId + FOLLOW_SUFFIX,FOLLOW_TIME,toId);
        }
    }

    public void removeFollower(String userId, String toId) {
        if(redisUtil.hasKey(userId + FOLLOW_SUFFIX)) {
            redisUtil.setRemove(userId + FOLLOW_SUFFIX,toId);
        }
    }

    public boolean checkIsFollow(String userId,String toId) {
        if(redisUtil.hasKey(userId + FOLLOW_SUFFIX)) {
            return redisUtil.sHasKey(userId + FOLLOW_SUFFIX,toId);
        }
        int total = followerRepository.getFollowerTotal(userId);
        if(total == 0) {
            setFollower(userId,new ArrayList<>());
            return false;
        }
        Pageable pageable = PageRequest.of(0,total);
        List<String> ids = followerRepository.getFollowerIds(userId,pageable);
        setFollower(userId,ids);
        return redisUtil.sHasKey(userId + FOLLOW_SUFFIX,toId);
    }

}
