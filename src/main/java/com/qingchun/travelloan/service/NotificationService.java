package com.qingchun.travelloan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qingchun.travelloan.entity.Notification;
import com.qingchun.travelloan.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务类
 * 
 * @author Qingchun Team
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 获取用户的所有通知
     */
    public List<Notification> getUserNotifications(Long userId) {
        return notificationMapper.selectList(
                new QueryWrapper<Notification>()
                        .eq("user_id", userId)
                        .orderByDesc("created_at")
        );
    }

    /**
     * 标记通知为已读
     */
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectOne(
                new QueryWrapper<Notification>()
                        .eq("id", notificationId)
                        .eq("user_id", userId)
        );

        if (notification != null && !Boolean.TRUE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notification.setUpdatedAt(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
    }

    /**
     * 标记用户所有通知为已读
     */
    public void markAllAsRead(Long userId) {
        UpdateWrapper<Notification> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                .eq("is_read", false)
                .set("is_read", true)
                .set("updated_at", LocalDateTime.now());
        notificationMapper.update(null, updateWrapper);
    }

    /**
     * 获取未读通知数量
     */
    public Long getUnreadCount(Long userId) {
        return notificationMapper.selectCount(
                new QueryWrapper<Notification>()
                        .eq("user_id", userId)
                        .eq("is_read", false)
        );
    }

    /**
     * 创建通知
     */
    public Notification createNotification(Long userId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notificationMapper.insert(notification);
        return notification;
    }
}

