package com.example.springboot.service;

import com.example.springboot.entity.Like;
import com.example.springboot.entity.UserProfile;

import java.util.List;

/**
 * 用户喜欢关系服务接口
 */
public interface LikeService {
    
    /**
     * 添加点赞
     *
     * @param userId      用户ID
     * @param likedUserId 被点赞对象ID
     * @param type        点赞类型(0:用户,1:动态)
     * @return 是否成功
     */
    boolean addLike(Integer userId, Integer likedUserId, Integer type);
    
    /**
     * 取消点赞
     *
     * @param userId      用户ID
     * @param likedUserId 被点赞对象ID
     * @param type        点赞类型
     * @return 是否成功
     */
    boolean unlike(Integer userId, Integer likedUserId, Integer type);
    
    /**
     * 检查用户是否点赞了特定对象
     *
     * @param userId      用户ID
     * @param likedUserId 被点赞对象ID
     * @param type        点赞类型
     * @return 是否已点赞
     */
    boolean hasLiked(Integer userId, Integer likedUserId, Integer type);
    
    /**
     * 获取用户点赞的所有ID
     *
     * @param userId 用户ID
     * @param type   点赞类型
     * @return 点赞ID列表
     */
    List<Integer> getLikedIds(Integer userId, Integer type);
    
    /**
     * 获取互相点赞的用户列表
     *
     * @param userId 用户ID
     * @return 互相点赞的用户资料列表
     */
    List<UserProfile> getMutualLikes(Integer userId);
    
    /**
     * 统计对象收到的点赞数
     *
     * @param likedUserId 被点赞对象ID
     * @param type        点赞类型
     * @return 点赞数量
     */
    int countLikes(Integer likedUserId, Integer type);
    
    /**
     * 根据类型设置用户的喜欢状态
     *
     * @param userId      用户ID
     * @param likedUserId 被喜欢的用户ID
     * @param type        类型：0-不喜欢，1-喜欢
     * @return 是否成功
     */
    boolean likeOrDislike(Integer userId, Integer likedUserId, int type);

    /**
     * 移除用户点赞关系
     *
     * @param userId      用户ID
     * @param likedUserId 被点赞用户ID
     * @return 是否成功
     */
    boolean removeLike(Integer userId, Integer likedUserId);
} 