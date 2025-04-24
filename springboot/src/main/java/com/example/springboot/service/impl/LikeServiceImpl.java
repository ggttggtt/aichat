package com.example.springboot.service.impl;

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
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;
    
    @Autowired
    private UserProfileMapper userProfileMapper;

    @Override
    @Transactional
    public boolean likeOrDislike(Integer userId, Integer likedUserId, Integer type) {
        if (userId == null || likedUserId == null || type == null) {
            return false;
        }
        
        Like existingLike = likeMapper.selectByUserIdAndLikedUserId(userId, likedUserId);
        
        if (existingLike == null) {
            // 不存在记录，插入新记录
            Like like = new Like();
            like.setUserId(userId);
            like.setLikedUserId(likedUserId);
            like.setType(type);
            return likeMapper.insert(like) > 0;
        } else {
            // 更新已有记录
            existingLike.setType(type);
            return likeMapper.update(existingLike) > 0;
        }
    }

    @Override
    public Like getLikeStatus(Integer userId, Integer likedUserId) {
        if (userId == null || likedUserId == null) {
            return null;
        }
        return likeMapper.selectByUserIdAndLikedUserId(userId, likedUserId);
    }

    @Override
    public List<Integer> getMutualLikeUserIds(Integer userId) {
        if (userId == null) {
            return null;
        }
        return likeMapper.selectMutualLikeUserIds(userId);
    }
    
    @Override
    @Transactional
    public boolean addLike(Integer userId, Integer likedUserId, Integer type) {
        if (userId == null || likedUserId == null) {
            return false;
        }
        
        // 检查是否已经喜欢
        Like existingLike = likeMapper.selectByUserIdAndLikedUserId(userId, likedUserId);
        if (existingLike != null) {
            return false; // 已经喜欢过该用户
        }
        
        // 创建新的喜欢记录
        Like like = new Like();
        like.setUserId(userId);
        like.setLikedUserId(likedUserId);
        like.setType(type);
        like.setCreateTime(new Date());
        like.setUpdateTime(new Date());
        
        return likeMapper.insert(like) > 0;
    }
    
    @Override
    @Transactional
    public boolean removeLike(Integer userId, Integer likedUserId) {
        if (userId == null || likedUserId == null) {
            return false;
        }
        
        // 检查是否已经喜欢
        Like existingLike = likeMapper.selectByUserIdAndLikedUserId(userId, likedUserId);
        if (existingLike == null) {
            return false; // 未喜欢该用户
        }
        
        // 删除记录（这里假设LikeMapper有delete方法，如果没有需要添加）
        // 假设删除逻辑是通过update修改type为0
        existingLike.setType(0);
        existingLike.setUpdateTime(new Date());
        return likeMapper.update(existingLike) > 0;
    }
    
    @Override
    public List<UserProfile> getMutualLikes(Integer userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        
        // 获取互相喜欢的用户ID列表
        List<Integer> mutualUserIds = likeMapper.selectMutualLikeUserIds(userId);
        
        // 如果没有互相喜欢的用户，返回空列表
        if (mutualUserIds == null || mutualUserIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取用户详情
        List<UserProfile> userProfiles = new ArrayList<>();
        for (Integer mutualUserId : mutualUserIds) {
            UserProfile profile = userProfileMapper.getById(mutualUserId);
            if (profile != null) {
                userProfiles.add(profile);
            }
        }
        
        return userProfiles;
    }
    
    @Override
    public Like checkLike(Integer userId, Integer likedUserId) {
        if (userId == null || likedUserId == null) {
            return null;
        }
        
        Like like = likeMapper.selectByUserIdAndLikedUserId(userId, likedUserId);
        // 只有type为1才表示喜欢
        return (like != null && like.getType() == 1) ? like : null;
    }
}