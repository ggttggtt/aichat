package com.example.springboot.service.impl;

import com.example.springboot.entity.MomentComment;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.mapper.MomentCommentMapper;
import com.example.springboot.mapper.MomentMapper;
import com.example.springboot.mapper.UserProfileMapper;
import com.example.springboot.service.MomentCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 动态评论服务实现类
 */
@Service
public class MomentCommentServiceImpl implements MomentCommentService {

    @Autowired
    private MomentCommentMapper momentCommentMapper;
    
    @Autowired
    private MomentMapper momentMapper;
    
    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    public List<MomentComment> getCommentsByMomentId(Integer momentId) {
        List<MomentComment> comments = momentCommentMapper.getByMomentId(momentId);
        
        // 加载用户信息
        for (MomentComment comment : comments) {
            UserProfile userProfile = userProfileMapper.getById(comment.getUserId());
            comment.setUser(userProfile);
        }
        
        return comments;
    }

    @Override
    @Transactional
    public MomentComment addComment(MomentComment comment) {
        // 插入评论
        momentCommentMapper.insert(comment);
        
        // 增加动态评论数
        momentMapper.increaseCommentCount(comment.getMomentId());
        
        // 加载用户信息
        UserProfile userProfile = userProfileMapper.getById(comment.getUserId());
        comment.setUser(userProfile);
        
        return comment;
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer id, Integer userId) {
        // 查询评论信息（暂未实现）
        // TODO: 添加权限检查，确保只能删除自己的评论
        
        // 获取评论信息，用于后续减少动态评论数
        // MomentComment comment = momentCommentMapper.getById(id);
        
        // 删除评论
        boolean success = momentCommentMapper.delete(id) > 0;
        
        // 如果删除成功且能获取到评论信息，减少动态评论数
        // if (success && comment != null) {
        //     momentMapper.decreaseCommentCount(comment.getMomentId());
        // }
        
        return success;
    }
} 