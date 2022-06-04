package top.hwlljy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.hwlljy.model.dto.superadmin.RoleDto;
import top.hwlljy.service.SuperAdminService;
import top.hwlljy.utils.ResultBody;

@RestController
@RequestMapping("/superAdmin")
public class SuperAdmin {

    @Autowired
    private SuperAdminService superAdminService;

    @PostMapping("/setRole")
    public ResultBody setRole(@RequestBody @Validated RoleDto roleDto) {
        return superAdminService.setRole(roleDto);
    }
}
