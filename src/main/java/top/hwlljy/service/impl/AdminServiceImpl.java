package top.hwlljy.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import top.hwlljy.model.constant.Constants;
import top.hwlljy.model.dto.admin.ModifyDto;
import top.hwlljy.model.dto.admin.ListDto;
import top.hwlljy.model.dto.user.RegisterUserDto;
import top.hwlljy.model.pojo.User;
import top.hwlljy.model.pojo.UserWork;
import top.hwlljy.model.vo.UserVo;
import top.hwlljy.repository.UserRepository;
import top.hwlljy.repository.WorkRepository;
import top.hwlljy.service.AdminService;
import top.hwlljy.service.UserService;
import top.hwlljy.utils.ResultBody;
import top.hwlljy.utils.SessionUtil;
import top.hwlljy.utils.UserUtil;

import java.util.*;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private UserService userService;

    @Override
    public ResultBody addUser() {
        User user = new User();
        user.setUsername(userService.getUsername());
        user.setNickname(userService.getUsername());
        String password = UserUtil.randomPsd();
        user.setPassword(UserUtil.setMd5Password(password));
        user.setIsDelete("0");
        user.setIsBan("0");
        user.setRole(1);
        user.setIsLock("0");
        user = userRepository.save(user);
        user.setPassword(password);
        Map<String, Object> result = new HashMap<>();
        result.put("data",user);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody delete(String id,String type) {
        if("0".equals(type)) {
            Optional<User> userOptional = userRepository.findById(id);
            if(userOptional.isPresent()) {
                User user = userOptional.get();
                int role = Constants.ADMIN.getValue() + Constants.SUPER_ADMIN.getValue();
                if((user.getRole() & role) == 0 && userService.deleteUser(user)) {
                    return ResultBody.success();
                }
            }
        }else if("1".equals(type)) {
            Optional<UserWork> userWorkOptional = workRepository.findById(id);
            if(userWorkOptional.isPresent()) {
                UserWork userWork = userWorkOptional.get();
                userWork.setIsDelete("1");
                workRepository.save(userWork);
                Optional<User> userOptional = userRepository.findById(userWork.getUserId());
                if(userOptional.isPresent()) {
                    User user = userOptional.get();
                    user.setAllNum(user.getAllNum() - 1);
                    userRepository.save(user);
                }
                return ResultBody.success();
            }
        }

        return ResultBody.fail();
    }

    @Override
    public ResultBody resetPassword(String userId) {
        Map<String, Object> result = new HashMap<>();
        String password = UserUtil.randomPsd();
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(UserUtil.setMd5Password(password));
            user.setIsLock("0");
            user.setTryLoginTimes(0);
            userRepository.save(user);
            result.put("data",password);
            return ResultBody.success(result);
        }
        return ResultBody.fail("用户不存在");
    }

    @Override
    public ResultBody modifyUser(ModifyDto modifyDto) {
        return null;
    }

    @Override
    public ResultBody getWorkList(ListDto listDto) {
        List<UserWork> rows;
        int total;
        String type = listDto.getType();
        Pageable pageable = PageRequest.of(listDto.getPageNum() - 1,listDto.getPageSize());
        if(type.equals(Constants.ALL_LIST.getVal())) {
            rows = workRepository.getAllWorkList(pageable);
            total = workRepository.getAllWorkListTotal();
        }else if(type.equals(Constants.DELETE_LIST.getVal())) {
            rows = workRepository.getDeleteList(pageable);
            total = workRepository.getDeleteListTotal();
        }else if(type.equals(Constants.REPORT_LIST.getVal())) {
            rows = workRepository.getReportList(pageable);
            total = workRepository.getReportListTotal();
        }else {
            return ResultBody.fail();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("rows",rows);
        result.put("total",total);
        return ResultBody.success(result);
    }

    @Override
    public ResultBody getUserList(ListDto listDto) {
        List<User> data;
        int total = 0;
        String type = listDto.getType();
        Pageable pageable = PageRequest.of(listDto.getPageNum() - 1,listDto.getPageSize());
        if(type.equals(Constants.ALL_LIST.getVal())) {
            data = userRepository.getUserList(pageable);
            total = userRepository.getUserListTotal();
        }else if(type.equals(Constants.DELETE_LIST.getVal())) {
            data = userRepository.getDeleteList(pageable);
            total = userRepository.getDeleteListTotal();
        }else if(type.equals(Constants.REPORT_LIST.getVal())) {
            data = userRepository.getReportList(pageable);
            total = userRepository.getReportTotal();
        }else {
            return ResultBody.fail();
        }
        List<UserVo> rows = new ArrayList<>();
        data.forEach(item -> {
            UserVo userVo = UserUtil.userPojoToVo(item);
            userVo.setShow((SessionUtil.getUser().getRole() & Constants.SUPER_ADMIN.getValue()) ==
                    Constants.SUPER_ADMIN.getValue());
            userVo.setIsDelete(item.getIsDelete());
            rows.add(userVo);
        });
        Map<String, Object> result = new HashMap<>();
        result.put("rows",rows);
        result.put("total",total);
        return ResultBody.success(result);
    }

}
