package top.hwlljy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.hwlljy.model.dto.admin.ListDto;
import top.hwlljy.model.dto.admin.ModifyDto;
import top.hwlljy.model.dto.user.RegisterUserDto;
import top.hwlljy.service.AdminService;
import top.hwlljy.utils.ResultBody;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/addUser")
    ResultBody addUser() {
        return adminService.addUser();
    }

    @GetMapping("/delete")
    ResultBody delete(@RequestParam String id,@RequestParam String type) {
        return adminService.delete(id,type);
    }

    @GetMapping("/resetPassword")
    ResultBody resetPassword(@RequestParam String userId) {
        return adminService.resetPassword(userId);
    }

    @PostMapping("/modifyUser")
    ResultBody modifyUser(@RequestBody @Validated ModifyDto modifyDto) {
        return adminService.modifyUser(modifyDto);
    }

    @PostMapping("/getWorkList")
    ResultBody getWorkList(@RequestBody @Validated ListDto listDto) {
        return adminService.getWorkList(listDto);
    }

    @PostMapping("/getUserList")
    ResultBody getUserList(@RequestBody @Validated ListDto listDto) {
        return adminService.getUserList(listDto);
    }
}
