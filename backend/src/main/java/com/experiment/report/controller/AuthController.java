package com.experiment.report.controller;

import com.experiment.report.common.Result;
import com.experiment.report.dto.LoginDTO;
import com.experiment.report.service.UserService;
import com.experiment.report.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器（登录注册）
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return userService.login(dto);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<?> getUserInfo() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(userService.getUserById(userId));
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> params) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.changePassword(userId, params.get("oldPassword"), params.get("newPassword"));
    }
}
