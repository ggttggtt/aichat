package com.example.springboot.mapper;

import com.example.springboot.entity.Moment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户动态Mapper接口
 */
@Mapper
public interface MomentMapper {
    
    /**
     * 获取动态列表
     * @param userId 当前用户ID（用于过滤权限）
     * @param offset 偏移量
     * @param limit 每页条数
     * @return 动态列表
     */
    List<Moment> listMoments(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 获取好友的动态列表
     * @param userId 当前用户ID
     * @param offset 偏移量
     * @param limit 每页条数
     * @return 动态列表
     */
    List<Moment> listFriendMoments(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 获取用户的动态列表
     * @param userId 用户ID
     * @param visitorId 访问者ID
     * @param offset 偏移量
     * @param limit 每页条数
     * @return 动态列表
     */
    List<Moment> listUserMoments(@Param("userId") Integer userId, @Param("visitorId") Integer visitorId, 
                               @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 根据ID获取动态
     * @param id 动态ID
     * @return 动态信息
     */
    Moment getMomentById(@Param("id") Integer id);
    
    /**
     * 新增动态
     * @param moment 动态信息
     * @return 影响的行数
     */
    int insert(Moment moment);
    
    /**
     * 更新动态
     * @param moment 动态信息
     * @return 影响的行数
     */
    int update(Moment moment);
    
    /**
     * 删除动态
     * @param id 动态ID
     * @return 影响的行数
     */
    int delete(@Param("id") Integer id);
    
    /**
     * 增加点赞数
     * @param id 动态ID
     * @return 影响的行数
     */
    int increaseLikeCount(@Param("id") Integer id);
    
    /**
     * 减少点赞数
     * @param id 动态ID
     * @return 影响的行数
     */
    int decreaseLikeCount(@Param("id") Integer id);
    
    /**
     * 增加评论数
     * @param id 动态ID
     * @return 影响的行数
     */
    int increaseCommentCount(@Param("id") Integer id);
    
    /**
     * 减少评论数
     * @param id 动态ID
     * @return 影响的行数
     */
    int decreaseCommentCount(@Param("id") Integer id);
} 