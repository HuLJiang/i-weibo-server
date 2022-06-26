package top.hwlljy.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.hwlljy.cache.UserCache;
import top.hwlljy.model.constant.Constants;
import top.hwlljy.model.dto.PageQuery;
import top.hwlljy.model.dto.user.*;
import top.hwlljy.model.pojo.*;
import top.hwlljy.model.vo.LikeMeVo;
import top.hwlljy.model.vo.TalkVo;
import top.hwlljy.model.vo.UserVo;
import top.hwlljy.model.vo.WorkVo;
import top.hwlljy.repository.*;
import top.hwlljy.service.UserService;
import top.hwlljy.utils.ResultBody;
import top.hwlljy.utils.SessionUtil;
import top.hwlljy.utils.SocketManager;
import top.hwlljy.utils.UserUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final Lock lockFollow = new ReentrantLock();

    private static final Lock userUp = new ReentrantLock();

    @Value(value = "${top.hwlljy.user.attach}")
    private String basePath;

    @Value(value = "${top.hwlljy.user.prefix}")
    private String hostPrefix;

    @Value(value = "${top.hwlljy.user.tryTimes}")
    private int userTryTimes;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private AttachRepository attachRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private WorkInteractionRepository workInteractionRepository;

    @Autowired
    private FollowerRepository followerRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private WorkBlackRepository workBlackRepository;

    @Autowired
    private BlackListRepository blackListRepository;

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private UserCache userCache;

    @Override
    public ResultBody register(RegisterUserDto registerUserDto) {
        Map<String, Object> result = new HashMap<>();
        if(!registerUserDto.getCheckMsg().equals(SessionUtil.getCheckMsg())) {
            return ResultBody.fail("验证码错误");
        }
        String psd = registerUserDto.getPassword();
        String nickname = registerUserDto.getNickname();
        if(!psd.equals(registerUserDto.getRepassword())) {
            return ResultBody.fail("两次密码不一致");
        }
        if(psd.length() > 16 || psd.length() < 6) {
            return ResultBody.fail("密码长度不符合");
        }
        List<User> users = userRepository.findAllByUsernameOrNickname(nickname,nickname);
        if(!CollUtil.isEmpty(users)) {
            return ResultBody.fail("用户名已存在");
        }
        if(nickname.length() < 2 || nickname.length() > 16) {
            return ResultBody.fail("用户昵称长度不符合");
        }
        String username = getUsername();
        User user = new User();
        user.setNickname(registerUserDto.getNickname());
        user.setPassword(UserUtil.setMd5Password(psd));
        user.setRole(Constants.USER.getValue());
        user.setUsername(username);
        user.setBirthday(registerUserDto.getBirthday());
        user.setIsBan("0");
        user.setIsDelete("0");
        user.setIsLock("0");
        user.setSex("未知");
        userRepository.save(user);
        result.put("user",user);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody login(LoginDto loginDto) {
        Map<String, Object> result = new HashMap<>();
        log.info(SessionUtil.getCheckMsg());
        if(!loginDto.getCheckMsg().equalsIgnoreCase(SessionUtil.getCheckMsg())) {
            return ResultBody.fail("验证码错误");
        }
        String username = loginDto.getUsername();
        String loginError = "用户名或密码错误";
        List<User> users =
                userRepository.findAllByUsernameOrNickname(username,username);
        if(CollUtil.isEmpty(users)) {
            return ResultBody.fail(loginError);
        }
        User user = users.get(0);
        //账号已被删除或者封禁
        if(user.getIsBan().equals(Constants.COMMON_TRUE.getVal()) ||
                user.getIsDelete().equals(Constants.COMMON_TRUE.getVal())) {
            return ResultBody.fail(loginError);
        }
        boolean password = user.getPassword().equals(UserUtil.setMd5Password(loginDto.getPassword()));
        if(user.getIsLock().equals(Constants.USER_LOCK.getVal())) {
            return ResultBody.fail("用户已被锁定");
        }
        if(!password) {
            int tryTimes = user.getTryLoginTimes();
            user.setTryLoginTimes(tryTimes + 1);
            if(tryTimes > userTryTimes) {
                user.setIsLock(Constants.USER_LOCK.getVal());
            }
            //用户不是管理员和超级管理员
            if((user.getRole() & 68) == 0) {
                userRepository.save(user);
            }
            return ResultBody.fail(loginError);
        }
        UserToken token = tokenRepository.findAllByUserId(user.getId());
        if(token != null) {
            tokenRepository.delete(token);
        }
        UserToken userToken = new UserToken();
        userToken.setUserId(user.getId());
        userToken.setToken(UserUtil.uuid());
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,Constants.TOKEN_EXPIRE.getValue());
        userToken.setEndTime(c.getTime());
        tokenRepository.save(userToken);
        SessionUtil.setUser(user);
        result.put("token",userToken.getToken());
        return ResultBody.success(result);
    }

    @Override
    public ResultBody update(UpdateDto updateDto) {
        User user = SessionUtil.getUser();
        String sex = updateDto.getSex();
        if(sex == null || (!sex.equals(Constants.SEX_MAN.getVal()) && !sex.equals(Constants.SEX_WOMAN.getVal()))) {
            user.setSex(Constants.SEX_UN_KNOW.getVal());
        }else {
            user.setSex(sex);
        }
        user.setNickname(updateDto.getNickname());
        user.setHeadImg(updateDto.getHeadImg());
        user.setSex(updateDto.getSex());
        user.setAbout(updateDto.getAbout());
        user.setBirthday(updateDto.getBirthday());
        user = userRepository.save(user);
        Map<String, Object> result = new HashMap<>();
        result.put("data",UserUtil.userPojoToVo(user));
        return ResultBody.success(result);
    }

    @Override
    public ResultBody getUserInfo() {
        User user = SessionUtil.getUser();
        UserVo userVo = UserUtil.userPojoToVo(user);
        int followNum = followerRepository.getFollowerTotal(user.getId());
        int fansNum = followerRepository.getFansTotal(user.getId());
        userVo.setFollowNum(followNum);
        userVo.setFansNum(fansNum);
        userVo.setIsMe("1");
        Map<String, Object> result = new HashMap<>();
        result.put("data",userVo);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody getUserInfo(String username) {
        User user = userRepository.findAllByUsername(username);
        if(user != null) {
            UserVo userVo = UserUtil.userPojoToVo(user);
            int followNum = followerRepository.getFollowerTotal(user.getId());
            int fansNum = followerRepository.getFansTotal(user.getId());
            User me = SessionUtil.getUser();
            if(me != null) {
                if(userCache.checkIsFollow(me.getId(),user.getId())) {
                    userVo.setIsFollow("1");
                }
                if(user.getId().equals(me.getId())) {
                    userVo.setIsMe("1");
                }
                UserBlackList userBlackList = blackListRepository.findAllByUserIdAndToUserId(me.getId(),user.getId());
                if(userBlackList != null) {
                    userVo.setIsBlack("1");
                }
            }
            userVo.setFollowNum(followNum);
            userVo.setFansNum(fansNum);
            Map<String, Object> result = new HashMap<>();
            result.put("data",userVo);
            return ResultBody.success(result);
        }
        return ResultBody.fail("找不到该用户");
    }

    @Override
    public ResultBody tokenLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = request.getHeader("token");
        UserToken userToken = tokenRepository.findAllByToken(token);
        if(userToken == null) {
            return ResultBody.fail();
        }
        Date now = new Date();
        if(now.compareTo(userToken.getEndTime()) < 0) {
            Optional<User> optionalUser = userRepository.findById(userToken.getUserId());
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                session.setAttribute(Constants.LOGIN_USER_SESSION_KEY.getVal(),user);
                return ResultBody.success();
            }
            return ResultBody.fail();
        }
        tokenRepository.delete(userToken);
        return ResultBody.fail();
    }

    @Transactional
    @Override
    public ResultBody logout() {
        UserToken userToken = tokenRepository.findAllByUserId(SessionUtil.getUserId());
        tokenRepository.delete(userToken);
        userCache.removeToken(userToken.getToken());
        SessionUtil.removeUser();
        return ResultBody.success();
    }

    @Transactional
    @Override
    public ResultBody cancellation() {
        User user = SessionUtil.getUser();
        if(deleteUser(user)) {
            ResultBody.success();
        }
        return ResultBody.fail("发生错误");
    }

    @Override
    public ResultBody workDetail(String workId) {
        User user = SessionUtil.getUser();
        List<Map<String, Object>> userWorks;
        WorkVo workVo;
        if(user == null) {
            Optional<UserWork> userWorkOptional = workRepository.findById(workId);
            if(!userWorkOptional.isPresent()) {
                return ResultBody.fail("作品不存在");
            }
            UserWork userWork = userWorkOptional.get();
            if(Constants.COMMON_TRUE.getVal().equals(userWork.getIsDelete())) {
                return ResultBody.fail("作品不存在");
            }
            workVo = workPojoToVo(userWork);
            User user1 = userRepository.findAllByUsername(workVo.getUsername());
            workVo.setUsername(user1.getNickname());
            workVo.setHeadImg(user1.getHeadImg());
        }else {
            String userId = user.getId();
            userWorks = workRepository.getWorkDetail(userId,workId);
            if(CollUtil.isEmpty(userWorks)) {
                return ResultBody.fail();
            }
            Map<String, Object> userWork = userWorks.get(0);
            workVo = workMapToVo(userWork);
            String shareScope = workVo.getShareScope();
            //仅自己可见查看详情
            if(Constants.SHARE_SCOPE_ONLY.getVal().equals(shareScope) && !userId.equals(workVo.getUserId())) {
                return ResultBody.fail();
            }
            //粉丝可见详情
            if(Constants.SHARE_SCOPE_FANS.getVal().equals(shareScope)) {
                UserFollower userFollower = followerRepository.findAllByUserIdAndToUserId(userId,workVo.getUserId());
                if(userFollower == null) {
                    return ResultBody.fail();
                }
            }
        }

        return ResultBody.success(workVo);
    }

    @Override
    public ResultBody resetShareScope(ShareScopeDto scopeDto) {
        String workId = scopeDto.getWorkId();
        String scope = scopeDto.getShareScope();
        if(!scope.equals(Constants.SHARE_SCOPE_FANS.getVal()) &&
            !scope.equals(Constants.SHARE_SCOPE_ALL.getVal()) &&
            !scope.equals(Constants.SHARE_SCOPE_ONLY.getVal())) {
            return ResultBody.fail();
        }
        String userId = SessionUtil.getUserId();
        Optional<UserWork> userWorkOptional = workRepository.findById(workId);
        if(userWorkOptional.isPresent()) {
            UserWork userWork = userWorkOptional.get();
            if(userId.equals(userWork.getUserId())) {
                userWork.setShareScope(scopeDto.getShareScope());
                workRepository.save(userWork);
                return ResultBody.success();
            }
        }
        return ResultBody.fail();
    }

    @Override
    public ResultBody workList(PageQuery pageQuery) {
        Map<String, Object> result = new HashMap<>();
        User user = SessionUtil.getUser();
        List<Map<String, Object>> userWorks;
        List<String> workBlacks = new ArrayList<>();
        List<String> userBlacks = new ArrayList<>();
        if(user != null) {
            String userId = user.getId();
            userBlacks = blackListRepository.getUserBlack(userId);
            workBlacks = workBlackRepository.getWorkBlack(userId);

        } else {
            //未登录储存假的id
            user = new User();
            user.setId("1");
        }
        String userId = user.getId();
        //普通热门列表
        userWorks = getList(pageQuery,userBlacks,workBlacks,Constants.WORK_LIST_ALL.getValue(),0,user);
        List<WorkVo> rows = setAttach(userWorks);
        rows.forEach(item -> item.setIsMe(item.getUserId().equals(userId) ? "1" : "0"));
        rows.sort((o1, o2) -> o2.getHot() - o1.getHot());
        result.put("rows",rows);
        return ResultBody.success(result);
    }

    //获取点赞列表
   @Override
   public ResultBody getLikeWorks(PageQuery pageQuery) {
        User user = SessionUtil.getUser();
        Pageable pageable = PageRequest.of(pageQuery.getPageNum() - 1,pageQuery.getPageSize());
        List<String> ids = workInteractionRepository.getLikeIds(user.getId(),pageable);
        Map<String, Object> result = new HashMap<>();
        if(CollUtil.isEmpty(ids)) {
            result.put("rows",new ArrayList<>());
            return ResultBody.success(result);
        }
        List<WorkVo> rows = solve(ids,user);
        result.put("rows",rows);
        return ResultBody.success(result);
   }

   private List<WorkVo> solve(List<String> ids,User user) {
        Map<String, Integer> data = new HashMap<>();
        for(int i = 0;i < ids.size();i ++) {
            data.put(ids.get(i),i);
        }
        List<Map<String, Object>> works = workRepository.getLikeWorks(ids,user.getId());
        List<WorkVo> rows = setAttach(works);
        //排列数据
        for(int i = 0;i < rows.size();i ++) {
            String workId = rows.get(i).getId();
            int idx = data.get(workId);
            if(i == idx) {
                continue;
            }
            WorkVo workVo = rows.get(idx);
            rows.set(idx,rows.get(i));
            rows.set(i,workVo);
        }
        return rows;
    }

    @Override
    public ResultBody getUserWorkList(UserWorkDto userWorkDto){
        String username = userWorkDto.getUsername();
        User user = userRepository.findAllByUsername(username);
        if(user == null) {
            return ResultBody.fail();
        }
        return ResultBody.success(getSomeOneWorkList(user.getId(),userWorkDto));
    }

    @Override
    public ResultBody getMyWorkList(PageQuery pageQuery){
        String userId = SessionUtil.getUserId();
        return ResultBody.success(getSomeOneWorkList(userId,pageQuery));
    }

    private Map<String, Object> getSomeOneWorkList(String userId,PageQuery pageQuery) {
        int start = getPageStart(pageQuery);
        List<Map<String, Object>> maps = workRepository.getUserWorkList(userId,start,pageQuery.getPageSize());
        List<WorkVo> rows = setAttach(maps);
        int total = workRepository.getOneWorksTotal(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("rows",rows);
        result.put("total",total);
        return result;
    }

    private int getPageStart(PageQuery pageQuery) {
        if(pageQuery.getPageNum() > 0) {
            return (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        }
        return 0;
    }

    @Override
    public ResultBody followWorkList(WorkListDto workListDto) {
        User user = SessionUtil.getUser();
        List<String> workBlacks;
        List<String> userIds = new ArrayList<>();
        String userId = user.getId();
//        int total = followerRepository.getFollowerTotal(userId);
//        if(total > 0) {
//            Pageable pageable1 = PageRequest.of(0,total);
//            userIds = followerRepository.getFollowerIds(userId,pageable1);
//        }
        userIds = blackListRepository.getUserBlack(userId);
        workBlacks = workBlackRepository.getWorkBlack(userId);
        List<Map<String, Object>> userWorks = getList(workListDto,userIds,workBlacks,
                Constants.WORK_LIST_FOLLOW.getValue(),workListDto.getNewest(),user);
        List<WorkVo> rows = setAttach(userWorks);
        Map<String, Object> result = new HashMap<>();
        result.put("rows",rows);
        return ResultBody.success(result);
    }

    private List<WorkVo> setAttach(List<Map<String, Object>> userWorks) {
        List<WorkVo> rows = new ArrayList<>();
        if(CollUtil.isEmpty(userWorks)) {
            return rows;
        }
        for (Map<String, Object> userWork : userWorks) {
            WorkVo wlv = workMapToVo(userWork);
            if (Constants.COMMON_TRUE.getVal().equals(wlv.getHasAttaches())) {
                String workId = wlv.getId();
                List<String> imgs = userCache.getWorkAttach(workId);
                if (CollUtil.isEmpty(imgs)) {
                    imgs = attachRepository.getImgList(workId);
                    userCache.setWorkAttach(workId, imgs);
                }
                wlv.setImgs(imgs);
            }
            rows.add(wlv);
        }
        return rows;
    }

    private List<Map<String, Object>> getList(PageQuery pageQuery,List<String> userIds,List<String> workBlacks,int type,int newest,User user) {
        //防止控列表无法查出正确数据
        if(CollUtil.isEmpty(userIds)) {
            userIds = new ArrayList<>();
            userIds.add("1");
        }
        //防止控列表无法查出正确数据
        if(CollUtil.isEmpty(workBlacks)) {
            workBlacks = new ArrayList<>();
            workBlacks.add("1");
        }
        int pageNum = pageQuery.getPageNum() > 1 ? pageQuery.getPageNum() - 1 : 0;
        int start = pageNum * pageQuery.getPageSize();
        if(type == Constants.WORK_LIST_FOLLOW.getValue()) {
            if(newest == Constants.HOT_LIST.getValue()) {
                return workRepository.getFollowHotListForUser(userIds,workBlacks,user.getId(),start,pageQuery.getPageSize());
            }
            return workRepository.getFollowAllListForUser(userIds,workBlacks,user.getId(),start,pageQuery.getPageSize());
        }
        return workRepository.getWorkListForUser(userIds,workBlacks,user.getId(),start,pageQuery.getPageSize());
    }

    private WorkVo workPojoToVo(UserWork userWork) {
        User user = SessionUtil.getUser();
        WorkVo workVo = new WorkVo();
        workVo.setId(userWork.getId());
        workVo.setContent(userWork.getContent());
        workVo.setCreateTime(userWork.getCreateTime());
        workVo.setHeadImg(userWork.getHeadImg());
        workVo.setUserNickname(userWork.getUserNickname());
        workVo.setUserId(userWork.getUserId());
        workVo.setUpNum(userWork.getUpNum());
        workVo.setTalkNum(userWork.getTalkNum());
        workVo.setReshareNum(userWork.getReshareNum());
        workVo.setShareScope(userWork.getShareScope());
        workVo.setUsername(userWork.getUsername());
        workVo.setHasAttaches(userWork.getHasAttaches());
        if(user != null) {
            String id = user.getId();
            workVo.setIsFollow(userCache.checkIsFollow(id,userWork.getUserId()) ? "1" : "0");
            workVo.setIsMe(userWork.getUserId().equals(id) ? "1" : "0");
        }
        if(Constants.COMMON_TRUE.getVal().equals(userWork.getIsReshare())) {
            workVo.setIsReshare(userWork.getIsReshare());
            workVo.setFromUsername(userWork.getFromUsername());
            workVo.setFromUserNickname(userWork.getFromUserNickname());
            workVo.setReshareContent(userWork.getReshareContent());
        }
        workVo.setHasAttaches(userWork.getHasAttaches());
        return workVo;
    }

    private WorkVo workMapToVo(Map<String, Object> item) {
        User user = SessionUtil.getUser();
        WorkVo workVo = new WorkVo();
        workVo.setId(strMapGet(item,"id"));
        workVo.setContent(strMapGet(item,"content"));
        workVo.setCreateTime((Date) item.get("create_time"));
        workVo.setHeadImg(strMapGet(item,"head_img2"));
        workVo.setUserNickname(strMapGet(item,"nickname2"));
        workVo.setUpNum((Integer) item.get("up_num"));
        workVo.setTalkNum((Integer) item.get("talk_num"));
        workVo.setReshareNum((Integer) item.get("reshare_num"));
        workVo.setShareScope(strMapGet(item,"share_scope"));
        workVo.setHasAttaches(strMapGet(item,"has_attaches"));
        workVo.setMyTalk(strMapGet(item,"myTalk"));
        workVo.setMyUp(strMapGet(item,"myUp"));
        workVo.setUserId(strMapGet(item,"user_id"));
        workVo.setUsername(strMapGet(item,"username"));
        workVo.setHot((Integer) item.get("hot"));
        if(user != null) {
            String id = user.getId();
            workVo.setIsFollow(userCache.checkIsFollow(id,workVo.getUserId()) ? "1" : "0");
            workVo.setIsMe(workVo.getUserId().equals(id) ? "1" : "0");
        }
        if(Constants.COMMON_TRUE.getVal().equals(strMapGet(item,"is_reshare"))) {
            workVo.setIsReshare(strMapGet(item,"is_reshare"));
            workVo.setFromUsername(strMapGet(item,"from_username"));
            workVo.setFromUserNickname(strMapGet(item,"from_user_nickname"));
            workVo.setReshareContent(strMapGet(item,"reshare_content"));
        }
        return workVo;
    }

    private String strMapGet(Map<String, Object> item,String key) {
        return String.valueOf(item.get(key));
    }

    @Override
    public ResultBody getCheckMsg(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        String data = UserUtil.getCheckImg();
        if(Constants.MSG_ERROR.getVal().equals(data)) {
            return ResultBody.fail("发生错误");
        }
        result.put("data",data);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody uploadImg(MultipartFile file) {
        String pattern = "^.+\\.((jpg)|(png)|(gif)|(bmp))$";
        String fileName = file.getOriginalFilename();
        if(fileName == null || !fileName.matches(pattern)) {
            return ResultBody.fail("图片格式不符合,图片上传失败");
        }
        String suffix = fileName.substring(fileName.indexOf("."));
        String newfilename = UserUtil.uuid() + suffix;
        String path = basePath + newfilename;

        File attachFile = new File(path);
        if(!attachFile.getParentFile().exists()) {
            boolean f = attachFile.getParentFile().mkdirs();
            if(!f) {
                return ResultBody.fail("发生错误,图片上传失败");
            }
        }
        UserWorkAttach userWorkAttach = new UserWorkAttach();
        try {
            file.transferTo(attachFile);
            userWorkAttach.setType("0");
            userWorkAttach.setUrl(hostPrefix + newfilename);
            attachRepository.save(userWorkAttach);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultBody.fail("图片上传失败");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data",userWorkAttach);
        return ResultBody.success(result);
    }

    @Transactional
    @Override
    public ResultBody shareWork(ShareDto shareDto) {
        String scope = shareDto.getShareScope();
        if(!scope.equals(Constants.SHARE_SCOPE_ALL.getVal()) &&
            !scope.equals(Constants.SHARE_SCOPE_FANS.getVal()) && !scope.equals(Constants.SHARE_SCOPE_ONLY.getVal())) {
            return ResultBody.fail();
        }
        Map<String, Object> result = new HashMap<>();
        UserWork userWork = new UserWork();
        String content = shareDto.getContent();
        userWork.setShareScope(shareDto.getShareScope());
        userWork.setContent(content);
        User user = SessionUtil.getUser();
        userWork.setUserId(user.getId());
        userWork.setUserNickname(user.getNickname());
        userWork.setHeadImg(user.getHeadImg());
        userWork.setUsername(user.getUsername());
        userWork.setIsDelete("0");
        userWork.setShareScope(scope);
        List<UserWorkAttach> attaches = shareDto.getAttaches();
        if(!CollUtil.isEmpty(attaches)) {
            userWork.setHasAttaches("1");
        }else {
            userWork.setHasAttaches("0");
        }
        UserWork uw = workRepository.save(userWork);
        user.setAllNum(user.getAllNum() + 1);
        userRepository.save(user);
        List<String> imgs = new ArrayList<>();
        if(!CollUtil.isEmpty(attaches)) {
            for(UserWorkAttach attach : attaches) {
                attach.setWorkId(uw.getId());
                imgs.add(attach.getUrl());
            }
            attachRepository.saveAll(attaches);
        }
        WorkVo workVo = workPojoToVo(uw);
        workVo.setMyUp("0");
        workVo.setMyTalk("0");
        workVo.setImgs(imgs);
        userCache.setWorkAttach(uw.getId(),imgs);
        result.put("data", workVo);
        return ResultBody.success(result);
    }

    @Transactional
    @Override
    public ResultBody interaction(InteractionDto interactionDto) {
        User user = SessionUtil.getUser();
        String workId = interactionDto.getWorkId();
        Optional<UserWork> userWorkOptional = workRepository.findById(workId);
        UserWork userWork = null;
        if(userWorkOptional.isPresent()) {
            userWork = userWorkOptional.get();
        }
        if(userWork == null) {
            return ResultBody.fail();
        }
        UserWorksInteraction userWorksInteraction;
        //用户点赞
        if(Constants.USER_UP.getVal().equals(interactionDto.getType())) {
            return doUp(interactionDto,user,userWork);
        }else {
            if(interactionDto.getMessage() == null || interactionDto.getMessage().equals("")) {
                return ResultBody.fail("不能评论空内容");
            }
            //用户评论
            userWorksInteraction = interactionDtoToPojo(interactionDto,user);
            userWorksInteraction.setHeadImg(user.getHeadImg());
            if(user.getId().equals(userWork.getUserId())) {
                userWorksInteraction.setIsBlogger("1");
            } else {
                userWorksInteraction.setIsBlogger("0");
            }
            //一级评论
            if(interactionDto.getLevel() == 0) {
                userWorksInteraction.setLevel("0");
            }else if(interactionDto.getLevel() == 1) {
                //二级评论
                userWorksInteraction.setLevel("1");
            }else if(interactionDto.getLevel() == 2) {
                //三级评论
                userWorksInteraction.setLevel("2");
            }else {
                return ResultBody.fail();
            }
            userWorksInteraction = workInteractionRepository.save(userWorksInteraction);
            userWork.setTalkNum(userWork.getTalkNum() + 1);
            userWork.setHot(userWork.getHot() + 10);
            userWork.setCount(userWork.getCount() + 1);
            workRepository.save(userWork);
            SocketManager.setMessage(interactionDto.getToUserId(),Constants.USER_TALK.getVal());
        }
        return ResultBody.success(userWorksInteraction);
    }
    private ResultBody doUp(InteractionDto interactionDto,User user,UserWork userWork) {
        boolean locked = false;
        try {
            locked = userUp.tryLock(1,TimeUnit.SECONDS);
            if(locked) {
                UserWorksInteraction uwi =
                        workInteractionRepository.findAllByUserIdAndWorkIdAndToUserIdAndType(user.getId(),interactionDto.getWorkId(),interactionDto.getToUserId(),"0");
                if(uwi == null) {
                    uwi = interactionDtoToPojo(interactionDto,user);
                    if(user.getId().equals(userWork.getUserId())) {
                        uwi.setIsBlogger("1");
                    } else {
                        uwi.setIsBlogger("0");
                    }
                    uwi.setHeadImg(user.getHeadImg());
                    workInteractionRepository.save(uwi);
                    userWork.setHot(userWork.getHot() + 5);
                    userWork.setCount(userWork.getCount() + 1);
                    userWork.setUpNum(userWork.getUpNum() + 1);
                    SocketManager.setMessage(interactionDto.getToUserId(),Constants.USER_UP.getVal());
                }else {
                    workInteractionRepository.delete(uwi);
                    userWork.setUpNum(userWork.getUpNum() - 1);
                }
                workRepository.save(userWork);
            } else {
                return ResultBody.fail("网络超时，请重试");
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
            Thread.currentThread().interrupt();
        } finally {
            if(locked) {
                userUp.unlock();
            }
        }
        return ResultBody.success();
    }

    private UserWorksInteraction interactionDtoToPojo(InteractionDto interactionDto,User user) {
        UserWorksInteraction userWorksInteraction = new UserWorksInteraction();
        userWorksInteraction.setMessage(interactionDto.getMessage());
        userWorksInteraction.setIsRead("0");
        userWorksInteraction.setType(interactionDto.getType());
        userWorksInteraction.setToUserId(interactionDto.getToUserId());
        userWorksInteraction.setToUserNickname(interactionDto.getToUserNickname());
        userWorksInteraction.setToUsername(interactionDto.getToUsername());
        userWorksInteraction.setWorkId(interactionDto.getWorkId());
        userWorksInteraction.setUserId(user.getId());
        userWorksInteraction.setUserNickname(user.getNickname());
        userWorksInteraction.setUsername(user.getUsername());
        userWorksInteraction.setFather(interactionDto.getFather());
        userWorksInteraction.setReplyId(interactionDto.getReply());
        return userWorksInteraction;
    }

    @Override
    public ResultBody deleteWork(String workId) {
        Optional<UserWork> userWorkOptional = workRepository.findById(workId);
        if(userWorkOptional.isPresent()) {
            UserWork userWork = userWorkOptional.get();
            User user = SessionUtil.getUser();
            if(userWork.getUserId().equals(user.getId())) {
                userWork.setIsDelete(Constants.COMMON_TRUE.getVal());
                workRepository.save(userWork);
                user.setAllNum(user.getAllNum() - 1);
                userRepository.save(user);
                return ResultBody.success();
            }
        }
        return ResultBody.error("错误");
    }

    @Transactional
    @Override
    public ResultBody reportWork(String workId,String reason) {
        Optional<UserWork> userWorkOptional = workRepository.findById(workId);
        if(userWorkOptional.isPresent()) {
            UserWork userWork = userWorkOptional.get();
            userWork.setReport(userWork.getReport() + 1);
            workRepository.save(userWork);
            report(workId,reason,Constants.REPORT_WORK.getVal());
            return ResultBody.success();
        }

        return ResultBody.fail();
    }


    @Transactional
    @Override
    public ResultBody reportUser(String userId,String reason) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setReport(user.getReport() + 1);
            userRepository.save(user);
            report(userId,reason,Constants.REPORT_USER.getVal());
            return ResultBody.success();
        }
        return ResultBody.fail();
    }

    private void report(String relationId,String reason,String type) {
        Report report = new Report();
        report.setReason(reason);
        report.setRelationId(relationId);
        report.setType(type);
        reportRepository.save(report);
    }

    @Transactional
    @Override
    public ResultBody follow(String toUserId,String toUserNickname) {
        boolean locked = false;
        try {
            locked = lockFollow.tryLock(1, TimeUnit.SECONDS);
            if(locked) {
                User user = SessionUtil.getUser();
                UserFollower userFollower = followerRepository.findAllByUserIdAndToUserId(user.getId(),toUserId);
                if(userFollower == null) {
                    Optional<User> userOptional = userRepository.findById(toUserId);
                    if(!userOptional.isPresent()) {
                        return ResultBody.fail();
                    }
                    User toUser = userOptional.get();
                    if(user.getId().equals(toUser.getId())) {
                        return ResultBody.fail();
                    }
                    userFollower = new UserFollower();
                    userFollower.setToUserId(toUserId);
                    userFollower.setToUserNickname(toUserNickname);
                    userFollower.setUserId(user.getId());
                    userFollower.setUserNickname(SessionUtil.getUser().getNickname());
                    followerRepository.save(userFollower);
                    userCache.setFollower(user.getId(),toUserId);
                    SocketManager.setMessage(toUserId,"3");
                } else {
                    return ResultBody.fail("请勿重复关注");
                }
            }else {
                return ResultBody.fail("请求超时");
            }

        } catch (InterruptedException e) {
            log.error("error",e);
            Thread.currentThread().interrupt();
        } finally {
            if(locked) {
                lockFollow.unlock();
            }
        }
        return ResultBody.success();
    }

    @Transactional
    @Override
    public ResultBody noFollow(String toUserId) {
        String userId = SessionUtil.getUserId();
        UserFollower userFollower = followerRepository.findAllByUserIdAndToUserId(userId,toUserId);
        if(userFollower == null) {
            return ResultBody.fail();
        }
        followerRepository.delete(userFollower);
        userCache.removeFollower(userId,toUserId);
        return ResultBody.success();
    }


    @Override
    public ResultBody followList(PageQuery pageQuery) {
        String userId = SessionUtil.getUserId();
        Pageable pageable = PageRequest.of(pageQuery.getPageNum() - 1,pageQuery.getPageSize());
        List<String> ids = followerRepository.getFollowerIds(userId,pageable);
        List<Map<String, Object>> data = userRepository.getUsersByIds(ids);
        List<UserVo> rows = getUserVos(data,userId);
        int total = followerRepository.getFollowerTotal(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("rows",rows);
        result.put("total",total);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody fansList(PageQuery pageQuery) {
        Pageable pageable = PageRequest.of(pageQuery.getPageNum() - 1,pageQuery.getPageSize());
        String userId = SessionUtil.getUserId();
        List<String> ids = followerRepository.getFansIds(userId,pageable);
        int total = followerRepository.getFansTotal(userId);
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> data = userRepository.getUsersByIds(ids);
        List<UserVo> rows = getUserVos(data,userId);
        result.put("rows",rows);
        result.put("total",total);
        return ResultBody.success(result);
    }

    private List<UserVo> getUserVos(List<Map<String, Object>> param,String userId) {
        List<UserVo> res = new ArrayList<>();
        param.forEach(item -> {
            UserVo userVo = new UserVo();
            userVo.setUsername(strMapGet(item,"username"));
            userVo.setNickname(strMapGet(item,"nickname"));
            userVo.setId(strMapGet(item,"id"));
            userVo.setAbout(strMapGet(item,"about"));
            if(userCache.checkIsFollow(userId,strMapGet(item,"id"))) {
                userVo.setIsFollow("1");
            }
            res.add(userVo);
        });
        return res;
    }

    @Transactional
    @Override
    public ResultBody readMessage(String type) {
        workInteractionRepository.readMessage(SessionUtil.getUserId(),type);
        return ResultBody.success();
    }

    @Override
    public ResultBody search(String word) {
        return null;
    }

    //非对外接口
    @Transactional
    @Override
    public boolean deleteUser(User user) {
        try {
            user.setUsername(getUsername());
            user.setNickname(getUsername());
            user.setHeadImg("");
            user.setRole(0);
            user.setAbout("");
            user.setIsDelete("1");
            user.setSex("");
            followerRepository.deleteAllByUserIdAndToUserId(user.getId(),user.getId());
            userRepository.save(user);
            List<UserWork> userWorks = workRepository.findAllByUserId(user.getId());
            userWorks.forEach(item -> item.setIsDelete("1"));
            workRepository.saveAll(userWorks);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
        return true;
    }

    @Override
    public String getUsername() {
        String username = RandomUtil.randomString(12);
        while (!CollUtil.isEmpty(userRepository.findAllByUsernameOrNickname(username,username))) {
            username = RandomUtil.randomString(12);
        }
        return username;
    }

    @Override
    public ResultBody getUserTalk(TalkDto talkDto) {
        Map<String, Object> result = new HashMap<>();
        int level = talkDto.getLevel();
        if(level < 0 || level > 1) {
            return ResultBody.fail();
        }
        Pageable pageable = PageRequest.of(talkDto.getPageNum() - 1,talkDto.getPageSize());
        List<Map<String, Object>> talks;
        //一级评论
        if(level == 0) {
            talks = workInteractionRepository.getUserTalk(talkDto.getWorkId(),pageable);

        }else {
            //二级三级评论
            talks = workInteractionRepository.getTwoLevelTalk(talkDto.getTalkId(),pageable);
        }
        List<TalkVo> rows = new ArrayList<>();
        if(!CollUtil.isEmpty(talks)) {
            talks.forEach(item -> {
                TalkVo talkVo = mapToTalkVo(item);
                rows.add(talkVo);
            });
        }
        result.put("rows",rows);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody searchTip(SearchDto searchDto) {
        String word = searchDto.getWord();
        Map<String, Object> result = new HashMap<>();
        if(word == null || word.equals("")) {
            result.put("rows",new ArrayList<>());
            return ResultBody.success(result);
        }
        User user = SessionUtil.getUser();
        List<User> data = userRepository.searchUserList("%" + word + "%",user == null ? "-1" : user.getId(),
                PageRequest.of(searchDto.getPageNum() - 1,searchDto.getPageSize()));
        List<UserVo> rows = new ArrayList<>();
        data.forEach(item -> rows.add(UserUtil.userPojoToVo(item)));
        if(user != null) {
            String userId = user.getId();
            rows.forEach(item -> item.setIsFollow(userCache.checkIsFollow(userId,item.getId()) ? "1" : "0"));
        }
        result.put("rows",rows);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody likeMe(PageQuery pageQuery) {
        User user = SessionUtil.getUser();
        int start = getPageStart(pageQuery);
        List<Map<String, Object>> data =
                workInteractionRepository.getLikeMeList(user.getId(),start,pageQuery.getPageSize());
        List<LikeMeVo> rows = getInteractList(data,user);
        Map<String, Object> result = new HashMap<>();
        result.put("rows",rows);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody getReply(ReplyDto replyDto) {
        Map<String, Object> result = new HashMap<>();
        User user = SessionUtil.getUser();
        int start = getPageStart(replyDto);
        String type = replyDto.getType();
        List<Map<String, Object>> data = new ArrayList<>();
        if(type.equals(Constants.REPLY_ME.getVal())) {
            data = workInteractionRepository.getMyTalk(user.getId(),start,replyDto.getPageSize());
        }else if(type.equals(Constants.REPLY_TO_ME.getVal())) {
            data = workInteractionRepository.getToMyTalk(user.getId(),start,replyDto.getPageSize());
        }else {
            result.put("rows",data);
            return ResultBody.success(result);
        }
        List<LikeMeVo> rows = getInteractList(data,user);
        result.put("rows",rows);
        return ResultBody.success(result);
    }

    private List<LikeMeVo> getInteractList(List<Map<String, Object>> data,User user) {
        List<LikeMeVo> rows = new ArrayList<>();
        data.forEach(item -> {
            LikeMeVo likeMeVo = mapToLikeVo(item);
            likeMeVo.setIHeadImg(user.getHeadImg());
            likeMeVo.setINickname(user.getNickname());
            rows.add(likeMeVo);
        });
        return rows;
    }

    private LikeMeVo mapToLikeVo(Map<String, Object> item) {
        LikeMeVo likeMeVo = new LikeMeVo();
        likeMeVo.setCreateTime((Date) item.get("create_time"));
        likeMeVo.setHeadImg(strMapGet(item,"head_img"));
        likeMeVo.setUserId(strMapGet(item,"user_id"));
        likeMeVo.setUsername(strMapGet(item,"username"));
        likeMeVo.setWorkContent(strMapGet(item,"content"));
        likeMeVo.setNickname(strMapGet(item,"nickname"));
        likeMeVo.setWorkId(strMapGet(item,"work_id"));
        likeMeVo.setTId(strMapGet(item,"id"));
        likeMeVo.setLevel(strMapGet(item,"level"));
        likeMeVo.setMessage(strMapGet(item,"message"));
        likeMeVo.setMsg(strMapGet(item,"msg"));
        likeMeVo.setWorkImg(strMapGet(item,"workImg"));
        likeMeVo.setWorkNickname(strMapGet(item,"workNickname"));
        likeMeVo.setFather(strMapGet(item,"father"));
        return likeMeVo;
    }

    @Override
    public ResultBody getMsg() {
        String id = SessionUtil.getUserId();
        List<Map<String, Object>> rows = workInteractionRepository.getReplyTotal(id);
        Map<String, Object> result = new HashMap<>();
        result.put("rows",rows);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody getHotWord() {
        List<String> words = new ArrayList<>();
        for(int i = 0;i < 100;i ++) {
            words.add("热搜词语" + i);
        }
        Map<String, Object> result = new HashMap<>();
        Collections.shuffle(words);
        result.put("rows",words.subList(0,10));
        return ResultBody.success(result);
    }

    @Override
    public synchronized ResultBody black(String id,String type) {
        String userId = SessionUtil.getUserId();
        if(Constants.BLACK_USER.getVal().equals(type)) {
            UserBlackList userBlackList = blackListRepository.findAllByUserIdAndToUserId(userId,id);
            if(userBlackList == null) {
                userBlackList = new UserBlackList();
                userBlackList.setToUserId(id);
                userBlackList.setUserId(userId);
                blackListRepository.save(userBlackList);
            }else {
                blackListRepository.delete(userBlackList);
            }
        }else if(Constants.BLACK_WORK.getVal().equals(type)) {
            WorkBlack workBlack = workBlackRepository.findAllByUserIdAndWorkId(userId,id);
            if(workBlack == null) {
                workBlack = new WorkBlack();
                workBlack.setUserId(userId);
                workBlack.setWorkId(id);
                workBlackRepository.save(workBlack);
            }else {
                workBlackRepository.delete(workBlack);
            }
        }else {
            return ResultBody.fail("信息有误");
        }
        return ResultBody.success();
    }

    @Override
    public ResultBody resetPsd(ResetPsdDto resetPsdDto) {
        User user = SessionUtil.getUser();
        if(user.getPassword().equals(UserUtil.setMd5Password(resetPsdDto.getOldPassword()))) {
            if(resetPsdDto.getPassword().equals(resetPsdDto.getRePassword())) {
                user.setPassword(UserUtil.setMd5Password(resetPsdDto.getPassword()));
                userRepository.save(user);
                SessionUtil.setUser(user);
                return ResultBody.success();
            }else {
                return ResultBody.fail("两次密码不一致");
            }
        }
        return ResultBody.fail("原密码错误");
    }

    private TalkVo mapToTalkVo(Map<String, Object> item) {
        TalkVo talkVo = new TalkVo();
        talkVo.setCreateTime((Date) item.get("create_time"));
        talkVo.setLevel(strMapGet(item,"level"));
        talkVo.setMessage(strMapGet(item,"message"));
        talkVo.setToUserId(strMapGet(item,"to_user_id"));
        talkVo.setToUserNickname(strMapGet(item,"realToNickname"));
        talkVo.setUserId(strMapGet(item,"user_id"));
        talkVo.setUserNickname(strMapGet(item,"realNickname"));
        talkVo.setHeadImg(strMapGet(item,"realHead"));
        talkVo.setWorkId(strMapGet(item,"work_id"));
        talkVo.setTalkCnt(strMapGet(item,"sonTalk"));
        talkVo.setId(strMapGet(item,"id"));
        talkVo.setUsername(strMapGet(item,"username"));
        talkVo.setToUsername(strMapGet(item,"to_username"));
        return talkVo;
    }

}
