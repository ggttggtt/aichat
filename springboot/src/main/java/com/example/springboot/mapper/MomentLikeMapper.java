package com.example.springboot.mapper;

import com.example.springboot.entity.MomentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 动态点赞关系Mapper接口
 */
@Mapper
public interface MomentLikeMapper {
    
    /**
     * 查询点赞记录
     * @param momentId 动态ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    MomentLike checkLike(@Param("momentId") Integer momentId, @Param("userId") Integer userId);
    
    /**
     * 添加点赞记录
     * @param momentId 动态ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    int insert(@Param("momentId") Integer momentId, @Param("userId") Integer userId);
    
    /**
     * 删除点赞记录
     * @param momentId 动态ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    int delete(@Param("momentId") Integer momentId, @Param("userId") Integer userId);
    
    /**
     * 根据动态ID删除所有点赞记录
     * @param momentId 动态ID
     * @return 影响的行数
     */
    int deleteByMomentId(@Param("momentId") Integer momentId);
} 