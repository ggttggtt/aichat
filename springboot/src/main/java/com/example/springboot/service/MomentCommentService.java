package com.example.springboot.service;

import com.example.springboot.entity.MomentComment;

import java.util.List;

/**
 * 动态评论服务接口
 */
public interface MomentCommentService {
    
    /**
     * 获取动态的评论列表
     * @param momentId 动态ID
     * @return 评论列表
     */
    List<MomentComment> getCommentsByMomentId(Integer momentId);
    
    /**
     * 添加评论
     * @param comment 评论信息
     * @return 新增的评论
     */
    MomentComment addComment(MomentComment comment);
    
    /**
     * 删除评论
     * @param id 评论ID
     * @param userId 当前用户ID（用于权限检查）
     * @return 是否成功
     */
    boolean deleteComment(Integer id, Integer userId);
} 