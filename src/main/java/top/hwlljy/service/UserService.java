package top.hwlljy.service;

import org.springframework.web.multipart.MultipartFile;
import top.hwlljy.model.dto.PageQuery;
import top.hwlljy.model.dto.user.*;
import top.hwlljy.model.pojo.User;
import top.hwlljy.utils.ResultBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface UserService {

    //注册账号
    ResultBody register(RegisterUserDto registerUserDto);

    //登录
    ResultBody login(LoginDto loginDto);

    //用户修改信息
    ResultBody update(UpdateDto updateDto);

    //获取用户信息
    ResultBody getUserInfo();

    //获取用户信息
    ResultBody getUserInfo(String username);

    //token登录
    ResultBody tokenLogin(HttpServletRequest request);

    //退出登录
    ResultBody logout();

    //注销账号
    ResultBody cancellation();

    //作品详情列表
    ResultBody workDetail(String workId);

    //修改作品分享阈
    ResultBody resetShareScope(ShareScopeDto scopeDto);

    //作品列表
    ResultBody workList(PageQuery pageQuery);

    //获取点赞列表
    ResultBody getLikeWorks(PageQuery pageQuery);

    //获取某用户列表
    ResultBody getUserWorkList(UserWorkDto userWorkDto);

    //获取我的列表
    ResultBody getMyWorkList(PageQuery pageQuery);

    //作品列表
    ResultBody followWorkList(WorkListDto workListDto);

    //作品列表
    ResultBody getCheckMsg(HttpSession session);

    //作品附件（图片）
    ResultBody uploadImg(MultipartFile file);

    //分享作品
    ResultBody shareWork(ShareDto shareDto);

    //点赞或者评论作品
    ResultBody interaction(InteractionDto interactionDto);

    //删除自己的作品
    ResultBody deleteWork(String workId);

    //举报别人作品
    ResultBody reportWork(String workId,String reason);

    //举报别人
    ResultBody reportUser(String userId,String reason);

    //关注
    ResultBody follow(String userId,String userNickname);

    //取消关注
    ResultBody noFollow(String userId);

    //关注列表
    ResultBody followList(PageQuery pageQuery);

    //粉丝列表
    ResultBody fansList(PageQuery pageQuery);

    //读消息
    ResultBody readMessage(String type);

    //搜索
    ResultBody search(String word);

    //获取用户评论列表
    ResultBody getUserTalk(TalkDto talkDto);

    //搜索提示
    ResultBody searchTip(SearchDto searchDto);

    //获取别人点赞我的消息
    ResultBody likeMe(PageQuery pageQuery);

    //获取我的评论，或者评论我的
    ResultBody getReply(ReplyDto replyDto);

    ResultBody getMsg();

    ResultBody getHotWord();

    ResultBody black(String id,String type);

    //非对外接口，内部使用
    boolean deleteUser(User user);
    //非对外接口，内部使用
    String getUsername();

}
