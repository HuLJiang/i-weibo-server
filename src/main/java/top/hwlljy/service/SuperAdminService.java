package top.hwlljy.service;

import top.hwlljy.model.dto.superadmin.RoleDto;
import top.hwlljy.utils.ResultBody;

public interface SuperAdminService {

    //设置用户权限
    ResultBody setRole(RoleDto roleDto);
}
