package com.qingchun.travelloan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingchun.travelloan.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知消息Mapper接口
 *
 * @author Qingchun Team
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
