package com.example.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springboot.entity.Like;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.mapper.LikeMapper;
import com.example.springboot.mapper.UserProfileMapper;
import com.example.springboot.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户喜欢关系服务实现类
 */
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Autowired
    private LikeMapper likeMapper;
    
    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public boolean addLike(Integer userId, Integer likedUserId, Integer type) {
        // 检查参数
        if (userId == null || likedUserId == null || type == null) {
            return false;
        }
        
        // 检查是否已存在该点赞记录
        Like existingLike = likeMapper.getLike(userId, likedUserId, type);
        if (existingLike != null) {
            return true; // 已存在点赞记录
        }
        
        // 创建新点赞记录
        Like like = new Like();
        like.setUserId(userId);
        like.setLikedUserId(likedUserId);
        like.setType(type);
        like.setCreateTime(new Date());
        like.setUpdateTime(new Date());
        
        // 插入数据库
        return likeMapper.insert(like) > 0;
    }

    @Override
    @Transactional
    public boolean unlike(Integer userId, Integer likedUserId, Integer type) {
        // 检查参数
        if (userId == null || likedUserId == null || type == null) {
            return false;
        }
        
        return likeMapper.unlike(userId, likedUserId, type) > 0;
    }

    @Override
    public boolean hasLiked(Integer userId, Integer likedUserId, Integer type) {
        // 检查参数
        if (userId == null || likedUserId == null || type == null) {
            return false;
        }
        
        return likeMapper.getLike(userId, likedUserId, type) != null;
    }

    @Override
    public List<Integer> getLikedIds(Integer userId, Integer type) {
        // 检查参数
        if (userId == null || type == null) {
            return new ArrayList<>();
        }
        
        return likeMapper.getLikedIdsByUserIdAndType(userId, type);
    }

    @Override
    public List<UserProfile> getMutualLikes(Integer userId) {
        // 检查参数
        if (userId == null) {
            return new ArrayList<>();
        }
        
        // 获取当前用户点赞的用户ID列表（type=0表示用户类型）
        List<Integer> likedUserIds = likeMapper.getLikedIdsByUserIdAndType(userId, 0);
        if (likedUserIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 从这些用户中筛选出也点赞了当前用户的用户
        List<Integer> mutualLikeUserIds = likeMapper.getLikedByUserIds(userId, likedUserIds);
        if (mutualLikeUserIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询这些互相点赞用户的详细信息
        List<UserProfile> mutualLikeUsers = new ArrayList<>();
        for (Integer mutualLikeUserId : mutualLikeUserIds) {
            UserProfile userProfile = userProfileMapper.getById(mutualLikeUserId);
            if (userProfile != null) {
                mutualLikeUsers.add(userProfile);
            }
        }
        
        return mutualLikeUsers;
    }

    @Override
    public int countLikes(Integer likedUserId, Integer type) {
        // 检查参数
        if (likedUserId == null || type == null) {
            return 0;
        }
        
        return likeMapper.countLikes(likedUserId, type);
    }

    @Override
    @Transactional
    public boolean likeOrDislike(Integer userId, Integer likedUserId, int type) {
        if (userId == null || likedUserId == null) {
            return false;
        }
        
        // 检查是否已存在该记录
        Like existingLike = likeMapper.getLike(userId, likedUserId, 1); // 查询用户喜欢记录
        
        if (existingLike != null) {
            // 更新现有记录的类型
            existingLike.setType(type);
            existingLike.setUpdateTime(new Date());
            return this.updateById(existingLike);
        } else {
            // 创建新记录
            Like like = new Like();
            like.setUserId(userId);
            like.setLikedUserId(likedUserId);
            like.setType(type);
            like.setCreateTime(new Date());
            like.setUpdateTime(new Date());
            return likeMapper.insert(like) > 0;
        }
    }
    
    @Override
    @Transactional
    public boolean removeLike(Integer userId, Integer likedUserId) {
        if (userId == null || likedUserId == null) {
            return false;
        }
        
        // 默认移除用户类型的点赞（type=1表示用户喜欢）
        return likeMapper.unlike(userId, likedUserId, 1) > 0;
    }
} 