package com.example.springboot.service.impl;

import com.example.springboot.entity.Like;
import com.example.springboot.mapper.LikeMapper;
import com.example.springboot.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户喜欢关系服务实现类
 */
@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private LikeMapper likeMapper;

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
}