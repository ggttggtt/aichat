package com.example.springboot.mapper;

import com.example.springboot.entity.MomentComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 动态评论Mapper接口
 */
@Mapper
public interface MomentCommentMapper {
    
    /**
     * 获取动态的评论列表
     * @param momentId 动态ID
     * @return 评论列表
     */
    List<MomentComment> getByMomentId(@Param("momentId") Integer momentId);
    
    /**
     * 添加评论
     * @param comment 评论信息
     * @return 影响的行数
     */
    int insert(MomentComment comment);
    
    /**
     * 删除评论
     * @param id 评论ID
     * @return 影响的行数
     */
    int delete(@Param("id") Integer id);
    
    /**
     * 根据动态ID删除所有评论
     * @param momentId 动态ID
     * @return 影响的行数
     */
    int deleteByMomentId(@Param("momentId") Integer momentId);
} 