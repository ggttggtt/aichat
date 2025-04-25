package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.Like;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 * 用户喜欢关系 Mapper 接口
 * </p>
 *
 * @author ZXL
 * @since 2025-04-30
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {
    
    /**
     * 根据用户ID和点赞类型获取该用户点赞的所有对象ID
     *
     * @param userId 用户ID
     * @param type   点赞类型(0:用户,1:动态)
     * @return 点赞对象ID列表
     */
    @Select("SELECT liked_user_id FROM t_like WHERE user_id = #{userId} AND type = #{type}")
    List<Integer> getLikedIdsByUserIdAndType(@Param("userId") Integer userId, @Param("type") Integer type);
    
    /**
     * 根据用户ID和被点赞对象ID以及类型查询点赞记录
     *
     * @param userId      用户ID
     * @param likedUserId 被点赞对象ID
     * @param type        点赞类型
     * @return 点赞记录
     */
    @Select("SELECT * FROM t_like WHERE user_id = #{userId} AND liked_user_id = #{likedUserId} AND type = #{type}")
    Like getLike(@Param("userId") Integer userId, @Param("likedUserId") Integer likedUserId, @Param("type") Integer type);
    
    /**
     * 取消点赞
     *
     * @param userId      用户ID
     * @param likedUserId 被点赞对象ID
     * @param type        点赞类型
     * @return 影响的行数
     */
    @Delete("DELETE FROM t_like WHERE user_id = #{userId} AND liked_user_id = #{likedUserId} AND type = #{type}")
    int unlike(@Param("userId") Integer userId, @Param("likedUserId") Integer likedUserId, @Param("type") Integer type);
    
    /**
     * 统计对象收到的点赞数
     *
     * @param likedUserId 被点赞对象ID
     * @param type        点赞类型
     * @return 点赞数量
     */
    @Select("SELECT COUNT(*) FROM t_like WHERE liked_user_id = #{likedUserId} AND type = #{type}")
    int countLikes(@Param("likedUserId") Integer likedUserId, @Param("type") Integer type);
    
    /**
     * 从用户列表中筛选出点赞了指定用户的用户ID列表
     *
     * @param userId     要查询的用户ID
     * @param userIdList 用户ID列表
     * @return 从userIdList中筛选出点赞了userId的用户ID列表
     */
    @Select("<script>"
            + "SELECT user_id FROM t_like WHERE liked_user_id = #{userId} AND type = 0 "
            + "AND user_id IN "
            + "<foreach collection='userIdList' item='id' open='(' separator=',' close=')'>"
            + "#{id}"
            + "</foreach>"
            + "</script>")
    List<Integer> getLikedByUserIds(@Param("userId") Integer userId, @Param("userIdList") List<Integer> userIdList);
    
    /**
     * 获取喜欢指定用户的用户ID列表
     *
     * @param likedUserId 被喜欢的用户ID
     * @param type        喜欢类型(0:用户,1:动态)
     * @return 喜欢该用户的用户ID列表
     */
    @Select("SELECT user_id FROM t_like WHERE liked_user_id = #{likedUserId} AND type = #{type}")
    List<Integer> getWhoLikedUser(@Param("likedUserId") Integer likedUserId, @Param("type") Integer type);
} 