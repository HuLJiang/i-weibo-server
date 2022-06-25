package top.hwlljy.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.hwlljy.model.constant.Constants;
import top.hwlljy.model.dto.PageQuery;
import top.hwlljy.model.dto.user.*;
import top.hwlljy.service.UserService;
import top.hwlljy.utils.ResultBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
@Api(value = "用户基本接口", tags = "用户基础功能接口")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "账号注册", notes = "账号注册")
    public ResultBody register(@RequestBody @Validated RegisterUserDto registerUserDto) {
        return userService.register(registerUserDto);
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "用户登录")
    public ResultBody login(@RequestBody @Validated LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @PostMapping("/update")
    @ApiOperation(value = "用户修改信息", notes = "用户修改信息")
    public ResultBody update(@RequestBody @Validated UpdateDto updateDto) {
        return userService.update(updateDto);
    }

    @PostMapping("/getUserInfo")
    @ApiOperation(value = "获取登录用户信息", notes = "获取用户信息")
    public ResultBody getUserInfo() {
        return userService.getUserInfo();
    }

    @GetMapping("/getOneInfo")
    @ApiOperation(value = "根据username获取用户信息", notes = "根据username获取用户信息")
    public ResultBody getUserInfo(@RequestParam(value = "u") String u) {
        return userService.getUserInfo(u);
    }

    @PostMapping("/tokenLogin")
    @ApiOperation(value = "token登录", notes = "记住用户时，进入网站验证token通过直接自动登录")
    public ResultBody tokenLogin(HttpServletRequest request) {
        return userService.tokenLogin(request);
    }

    @GetMapping("/logout")
    @ApiOperation(value = "用户下线", notes = "用户下线")
    public ResultBody logout() {
        return userService.logout();
    }

    @GetMapping("/cancellation")
    @ApiOperation(value = "账号注销", notes = "账号注销")
    public ResultBody cancellation() {
        return userService.cancellation();
    }

    @GetMapping("/workDetail")
    @ApiOperation(value = "作品详情", notes = "作品详情")
    public ResultBody workList(@RequestParam(value = "workId") String workId) {
        return userService.workDetail(workId);
    }

    @PostMapping("/resetShareScope")
    @ApiOperation(value = "改变分享阈", notes = "改变分享阈")
    public ResultBody resetShareScope(@RequestBody @Validated ShareScopeDto scopeDto) {
        return userService.resetShareScope(scopeDto);
    }

    @PostMapping("/workList")
    @ApiOperation(value = "作品列表", notes = "作品列表")
    public ResultBody workList(@RequestBody PageQuery pageQuery) {
        return userService.workList(pageQuery);
    }

    @PostMapping("/getLikeWorks")
    @ApiOperation(value = "获取点过赞作品", notes = "获取点过赞作品")
    public ResultBody getLikeWorks(@RequestBody PageQuery pageQuery) {
        return userService.getLikeWorks(pageQuery);
    }

    @PostMapping("/getUserWorkList")
    @ApiOperation(value = "获取某用户的作品列表", notes = "获取某用户的作品列表")
    public ResultBody getUserWorkList(@RequestBody @Validated UserWorkDto userWorkDto) {
        return userService.getUserWorkList(userWorkDto);
    }

    @PostMapping("/getMyWorkList")
    @ApiOperation(value = "获取我的作品列表", notes = "获取我的作品列表")
    public ResultBody getMyWorkList(@RequestBody PageQuery pageQuery) {
        return userService.getMyWorkList(pageQuery);
    }

    @PostMapping("/followWorkList")
    @ApiOperation(value = "关注的作品列表", notes = "关注的作品列表")
    public ResultBody followWorkList(@RequestBody @Validated WorkListDto workListDto) {
        return userService.followWorkList(workListDto);
    }

    @GetMapping("/getCheckMsg")
    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    public ResultBody getCheckMsg(HttpSession session) {
        return userService.getCheckMsg(session);
    }

    @PostMapping(value = "/uploadImg")
    @ApiOperation(value = "上传图片", notes = "上传图片")
    public ResultBody uploadImg(MultipartFile file) {
        return userService.uploadImg(file);
    }

    @PostMapping("/shareWork")
    @ApiOperation(value = "分享作品", notes = "分享作品")
    public ResultBody shareWork(@RequestBody @Validated ShareDto shareDto) {
        return userService.shareWork(shareDto);
    }

    @PostMapping("/interaction")
    @ApiOperation(value = "互动", notes = "互动")
    public ResultBody interaction(@RequestBody @Validated InteractionDto interactionDto) {
        return userService.interaction(interactionDto);
    }

    @GetMapping("/deleteWork")
    @ApiOperation(value = "删除作品", notes = "删除作品")
    public ResultBody deleteWork(@RequestParam String workId) {
        return userService.deleteWork(workId);
    }

    @GetMapping("/reportWork")
    @ApiOperation(value = "举报作品", notes = "举报作品")
    public ResultBody reportWork(@RequestParam String workId, @RequestParam String reason) {
        return userService.reportWork(workId,reason);
    }

    @GetMapping("/reportUser")
    @ApiOperation(value = "举报用户", notes = "举报用户")
    public ResultBody reportUser(@RequestParam String userId, @RequestParam String reason) {
        return userService.reportUser(userId,reason);
    }

    @GetMapping("/follow")
    @ApiOperation(value = "关注", notes = "关注")
    public ResultBody follow(@RequestParam String userId,@RequestParam(name = "userNickname") String userNickname) {
        return userService.follow(userId,userNickname);
    }

    @GetMapping("/noFollow")
    @ApiOperation(value = "取消关注", notes = "取消关注")
    public ResultBody noFollow(@RequestParam String userId) {
        return userService.noFollow(userId);
    }

    @PostMapping("/followList")
    @ApiOperation(value = "关注列表", notes = "关注列表")
    public ResultBody followList(@RequestBody @Validated PageQuery pageQuery) {
        return userService.followList(pageQuery);
    }

    @PostMapping("/fansList")
    @ApiOperation(value = "粉丝列表", notes = "粉丝列表")
    public ResultBody fansList(@RequestBody @Validated PageQuery pageQuery) {
        return userService.fansList(pageQuery);
    }

    @GetMapping("/readMessage")
    @ApiOperation(value = "读消息", notes = "读消息")
    public ResultBody readMessage(@RequestParam(value = "type") String type) {
        return userService.readMessage(type);
    }

    @GetMapping("/search")
    @ApiOperation(value = "搜索", notes = "搜索")
    public ResultBody search(@RequestParam String word) {
        return userService.search(word);
    }

    @PostMapping("/getUserTalk")
    @ApiOperation(value = "获取评论", notes = "获取评论")
    public ResultBody getUserTalk(@RequestBody @Validated TalkDto talkDto) {
        return userService.getUserTalk(talkDto);
    }

    @RequestMapping("/needLogin")
    public ResultBody index() {
        return ResultBody.error("");
    }

    @GetMapping("/session")
    public ResultBody expire(HttpSession session) {
        if(session.getAttribute(Constants.LOGIN_USER_SESSION_KEY.getVal()) != null) {
            return ResultBody.success();
        }
        return ResultBody.fail();
    }

    @PostMapping("/search")
    public ResultBody searchTip(@RequestBody @Validated SearchDto searchDto) {
        return userService.searchTip(searchDto);
    }

    @PostMapping("/likeMe")
    public ResultBody likeMe(@RequestBody PageQuery pageQuery) {
        return userService.likeMe(pageQuery);
    }

    @PostMapping("/getReply")
    public ResultBody searchTip(@RequestBody @Validated ReplyDto replyDto) {
        return userService.getReply(replyDto);
    }

    @GetMapping("/getMsg")
    public ResultBody getMsg() {
        return userService.getMsg();
    }

    @GetMapping("/getHotWord")
    public ResultBody getHotWord() {
        return userService.getHotWord();
    }

    @GetMapping("/black")
    public ResultBody black(@RequestParam String id,@RequestParam String type) {
        return userService.black(id,type);
    }


    @PostMapping("/resetPsd")
    @ApiOperation(value = "关注的作品列表", notes = "关注的作品列表")
    public ResultBody resetPsd(@RequestBody @Validated ResetPsdDto resetPsdDto) {
        return userService.resetPsd(resetPsdDto);
    }
}
