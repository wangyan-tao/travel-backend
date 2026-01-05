package com.qingchun.travelloan.service;

import com.qingchun.travelloan.entity.UserLocation;
import com.qingchun.travelloan.mapper.UserLocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户位置服务
 * 
 * @author Qingchun Team
 */
@Service
public class UserLocationService {

    @Autowired
    private UserLocationMapper userLocationMapper;

    /**
     * 获取用户位置信息
     */
    public UserLocation getUserLocation(Long userId) {
        return userLocationMapper.selectByUserId(userId);
    }
}

