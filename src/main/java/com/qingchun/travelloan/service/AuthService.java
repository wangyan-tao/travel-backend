package com.qingchun.travelloan.service;

import com.qingchun.travelloan.dto.LoginRequest;
import com.qingchun.travelloan.dto.RegisterRequest;
import com.qingchun.travelloan.entity.User;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.UserMapper;
import com.qingchun.travelloan.utils.JwtUtil;
import com.qingchun.travelloan.utils.SM4Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务类
 * 
 * @author Qingchun Team
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(request.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        existUser = userMapper.selectByPhone(request.getPhone());
        if (existUser != null) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole("USER");
        user.setStatus("ACTIVE");

        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException("注册失败");
        }

        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        return response;
    }

    /**
     * 用户登录
     */
    public Map<String, Object> login(LoginRequest request) {
        // 查询用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        String rawPassword;
        try {
            byte[] key = SM4Util.deriveKey(request.getUsername());
            rawPassword = SM4Util.decryptHexEcb(request.getPassword(), key);
        } catch (Exception e) {
            rawPassword = request.getPassword();
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }

        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());

        // 生成token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);

        return response;
    }

    /**
     * 获取当前用户信息
     */
    public User getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    /**
     * 用户退出登录
     */
    public void logout(Long userId) {
        log.info("用户退出登录: userId={}", userId);
        // JWT是无状态的，这里主要记录日志
        // 如果需要实现token黑名单，可以在这里添加逻辑
    }
}
