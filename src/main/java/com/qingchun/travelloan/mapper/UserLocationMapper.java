package com.qingchun.travelloan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingchun.travelloan.entity.UserLocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 位置信息Mapper接口
 *
 * @author Qingchun Team
 */
@Mapper
public interface UserLocationMapper extends BaseMapper<UserLocation> {
    
    /**
     * 根据用户ID查询位置信息
     */
    UserLocation selectByUserId(@Param("userId") Long userId);
}
