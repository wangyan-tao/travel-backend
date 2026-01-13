package com.qingchun.travelloan.controller;

import com.qingchun.travelloan.service.NotificationService;
import com.qingchun.travelloan.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员通知管理控制器
 * 
 * @author Qingchun Team
 */
@Tag(name = "管理员-通知管理")
@RestController
@RequestMapping("/admin/notifications")
@PreAuthorize("hasRole('ADMIN')")
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "发送通知给指定用户")
    @PostMapping("/send")
    public Result<Void> sendNotification(@RequestBody SendNotificationRequest request) {
        notificationService.createNotification(
                request.getUserId(),
                request.getTitle(),
                request.getContent(),
                request.getType()
        );
        return Result.success();
    }

    @Data
    static class SendNotificationRequest {
        private Long userId;
        private String title;
        private String content;
        private String type;
    }
}

