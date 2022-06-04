package top.hwlljy.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.hwlljy.model.constant.Constants;
import top.hwlljy.model.dto.superadmin.RoleDto;
import top.hwlljy.model.pojo.User;
import top.hwlljy.repository.UserRepository;
import top.hwlljy.service.SuperAdminService;
import top.hwlljy.utils.ResultBody;

import java.util.Optional;

@Service
@Slf4j
public class SuperAdminServiceImpl implements SuperAdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResultBody setRole(RoleDto roleDto) {
        Optional<User> userOptional = userRepository.findById(roleDto.getId());
        if(userOptional.isPresent()) {
            int role = roleDto.getRole();
            User user = userOptional.get();
            user.setRole(role);
            userRepository.save(user);
            return ResultBody.success();
        }
        return ResultBody.fail();
    }
}
