package com.qingchun.travelloan.service;

import com.qingchun.travelloan.entity.UserLocation;
import com.qingchun.travelloan.exception.BusinessException;
import com.qingchun.travelloan.mapper.UserLocationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户位置服务
 * 
 * @author Qingchun Team
 */
@Service
public class UserLocationService {

    private static final Logger log = LoggerFactory.getLogger(UserLocationService.class);

    @Autowired
    private UserLocationMapper userLocationMapper;

    /**
     * 获取用户位置信息
     */
    public UserLocation getUserLocation(Long userId) {
        return userLocationMapper.selectByUserId(userId);
    }

    /**
     * 更新用户位置信息
     * 参考实名认证时的保存方式
     */
    public UserLocation updateUserLocation(Long userId, String province, String city) {
        log.info("保存用户位置信息 - userId: {}, province: {}, city: {}", userId, province, city);
        
        // 如果省份和城市都为空，跳过保存
        if ((province == null || province.trim().isEmpty()) && 
            (city == null || city.trim().isEmpty())) {
            log.warn("位置信息为空，跳过保存 - userId: {}", userId);
            return userLocationMapper.selectByUserId(userId);
        }

        try {
            UserLocation location = userLocationMapper.selectByUserId(userId);
            
            if (location == null) {
                // 创建新记录
                log.info("创建新的用户位置记录 - userId: {}, province: {}, city: {}", userId, province, city);
                location = new UserLocation();
                location.setUserId(userId);
                location.setCurrentProvince(province != null && !province.trim().isEmpty() ? province.trim() : null);
                location.setCurrentCity(city != null && !city.trim().isEmpty() ? city.trim() : null);
                location.setCreatedAt(LocalDateTime.now());
                location.setUpdatedAt(LocalDateTime.now());
                userLocationMapper.insert(location);
                log.info("用户位置记录创建成功 - userId: {}, province: {}, city: {}", userId, province, city);
            } else {
                // 更新现有记录
                log.info("更新用户位置记录 - userId: {}, oldProvince: {}, oldCity: {}, newProvince: {}, newCity: {}", 
                        userId, location.getCurrentProvince(), location.getCurrentCity(), province, city);
                
                // 只更新非空字段
                if (province != null && !province.trim().isEmpty()) {
                    location.setCurrentProvince(province.trim());
                }
                if (city != null && !city.trim().isEmpty()) {
                    location.setCurrentCity(city.trim());
                }
                location.setUpdatedAt(LocalDateTime.now());
                userLocationMapper.updateById(location);
                log.info("用户位置记录更新成功 - userId: {}, province: {}, city: {}", userId, province, city);
            }
            
            return location;
        } catch (Exception e) {
            log.error("保存用户位置信息失败 - userId: {}, province: {}, city: {}", userId, province, city, e);
            throw new BusinessException("保存位置信息失败: " + e.getMessage());
        }
    }
}

