package com.qingchun.travelloan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingchun.travelloan.entity.AcademicHonor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学业荣誉证明Mapper接口
 *
 * @author Qingchun Team
 */
@Mapper
public interface AcademicHonorMapper extends BaseMapper<AcademicHonor> {
    
    /**
     * 根据用户ID查询学业荣誉列表
     */
    List<AcademicHonor> selectByUserId(@Param("userId") Long userId);
}
