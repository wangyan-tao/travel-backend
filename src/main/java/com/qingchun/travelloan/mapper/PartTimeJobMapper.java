package com.qingchun.travelloan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingchun.travelloan.entity.PartTimeJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 兼职商铺Mapper接口
 *
 * @author Qingchun Team
 */
@Mapper
public interface PartTimeJobMapper extends BaseMapper<PartTimeJob> {
    
    /**
     * 根据条件查询兼职列表
     */
    List<PartTimeJob> selectByConditions(@Param("params") Map<String, Object> params);
}
