package top.hwlljy.service;

import top.hwlljy.model.dto.admin.ModifyDto;
import top.hwlljy.model.dto.admin.ListDto;
import top.hwlljy.model.dto.user.RegisterUserDto;
import top.hwlljy.utils.ResultBody;

public interface AdminService {

    ResultBody addUser();

    ResultBody delete(String userId);

    ResultBody resetPassword(String userId);

    ResultBody modifyUser(ModifyDto modifyDto);

    ResultBody getWorkList(ListDto listDto);

    ResultBody getUserList(ListDto listDto);
}
