package com.example.springboot.service;

import com.example.springboot.entity.Like;

import java.util.List;

/**
 * 用户喜欢关系服务接口
 */
public interface LikeService {
    /**
     * 添加或更新喜欢关系
     * @param userId 用户ID
     * @param likedUserId 被喜欢用户ID
     * @param type 类型：1-喜欢，0-不喜欢
     * @return 是否成功
     */
    boolean likeOrDislike(Integer userId, Integer likedUserId, Integer type);

    /**
     * 查询是否喜欢某用户
     * @param userId 用户ID
     * @param likedUserId 被喜欢用户ID
     * @return 喜欢状态，null表示未设置
     */
    Like getLikeStatus(Integer userId, Integer likedUserId);

    /**
     * 获取互相喜欢的用户ID列表
     * @param userId 用户ID
     * @return 互相喜欢的用户ID列表
     */
    List<Integer> getMutualLikeUserIds(Integer userId);
} 