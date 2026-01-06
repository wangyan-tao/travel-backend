package com.qingchun.travelloan.service;

import com.qingchun.travelloan.entity.User;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.UserMapper;
import com.qingchun.travelloan.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员用户管理服务类
 *
 * @author Qingchun Team
 */
@Service
public class AdminUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取所有用户列表（分页）
     */
    public PageResult<User> getAllUsers(String keyword, String role, Integer page, Integer size) {
        List<User> allUsers;
        
        if (role != null && !role.trim().isEmpty() && !"ALL".equals(role)) {
            allUsers = userMapper.selectByRole(role);
        } else {
            allUsers = userMapper.selectAll();
        }
        
        // 在内存中过滤关键词
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            allUsers = allUsers.stream()
                    .filter(user -> 
                        (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerKeyword)) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerKeyword)) ||
                        (user.getPhone() != null && user.getPhone().contains(keyword))
                    )
                    .collect(Collectors.toList());
        }
        
        // 分页处理
        int currentPage = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 10 : size;
        long total = allUsers.size();
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, allUsers.size());
        
        List<User> records = start < allUsers.size() 
                ? allUsers.subList(start, end) 
                : Collections.emptyList();
        
        return PageResult.of(records, total, currentPage, pageSize);
    }
    
    /**
     * 获取所有用户ID列表（用于快速切换）
     */
    public List<Long> getAllUserIds(String role) {
        List<User> allUsers;
        
        if (role != null && !role.trim().isEmpty() && !"ALL".equals(role)) {
            allUsers = userMapper.selectByRole(role);
        } else {
            allUsers = userMapper.selectAll();
        }
        
        return allUsers.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取用户详情
     */
    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    /**
     * 启用用户
     */
    public void enableUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus("ACTIVE");
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
    }

    /**
     * 禁用用户
     */
    public void disableUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus("DISABLED");
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.update(user);
    }
}

