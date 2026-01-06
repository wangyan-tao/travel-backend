package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.dto.LoginRequest;
import com.qingchun.travelloan.dto.RegisterRequest;
import com.qingchun.travelloan.entity.User;
import com.qingchun.travelloan.service.AuthService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 * 
 * @author Qingchun Team
 */
@Tag(name = "认证管理", description = "用户注册、登录等认证相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        Map<String, Object> data = authService.register(request);
        return Result.success("注册成功", data);
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> data = authService.login(request);
        return Result.success("登录成功", data);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<User> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = authService.getCurrentUser(userId);
        return Result.success(user);
    }

    /**
     * 用户退出登录
     */
    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() != null) {
            Long userId = (Long) authentication.getPrincipal();
            authService.logout(userId);
        }
        return Result.success("退出登录成功");
    }
}
