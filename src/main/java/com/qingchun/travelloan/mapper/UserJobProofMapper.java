package com.qingchun.travelloan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qingchun.travelloan.entity.UserJobProof;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户兼职证明Mapper接口
 *
 * @author Qingchun Team
 */
@Mapper
public interface UserJobProofMapper extends BaseMapper<UserJobProof> {
    
    /**
     * 根据用户ID查询工作证明列表（包含关联的兼职信息）
     */
    List<UserJobProof> selectByUserIdWithJobInfo(@Param("userId") Long userId);
}
