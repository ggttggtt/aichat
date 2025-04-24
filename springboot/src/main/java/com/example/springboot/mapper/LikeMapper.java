package com.example.springboot.mapper;

import com.example.springboot.entity.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户喜欢关系Mapper接口
 */
@Mapper
public interface LikeMapper {
    /**
     * 添加喜欢记录
     * @param like 喜欢记录
     * @return 影响行数
     */
    int insert(Like like);

    /**
     * 根据用户ID和被喜欢用户ID查询喜欢记录
     * @param userId 用户ID
     * @param likedUserId 被喜欢用户ID
     * @return 喜欢记录
     */
    Like selectByUserIdAndLikedUserId(@Param("userId") Integer userId, @Param("likedUserId") Integer likedUserId);

    /**
     * 更新喜欢记录
     * @param like 喜欢记录
     * @return 影响行数
     */
    int update(Like like);

    /**
     * 查询互相喜欢的用户ID列表
     * @param userId 用户ID
     * @return 用户ID列表
     */
    List<Integer> selectMutualLikeUserIds(@Param("userId") Integer userId);
} 