package com.example.springboot.mapper;

import com.example.springboot.entity.UserMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户匹配Mapper接口
 */
@Mapper
public interface UserMatchMapper {
    
    /**
     * 根据用户ID获取匹配列表
     * @param userId 用户ID
     * @return 匹配列表
     */
    List<UserMatch> getMatchesByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据ID获取匹配信息
     * @param id 匹配记录ID
     * @return 匹配信息
     */
    UserMatch getById(@Param("id") Integer id);
    
    /**
     * 新增匹配
     * @param userMatch 匹配信息
     * @return 影响的行数
     */
    int insert(UserMatch userMatch);
    
    /**
     * 删除匹配
     * @param id 匹配ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 删除用户之间的匹配
     * @param userId 用户ID
     * @param matchedUserId 匹配的用户ID
     * @return 影响的行数
     */
    int deleteMatch(@Param("userId") Integer userId, @Param("matchedUserId") Integer matchedUserId);
    
    /**
     * 检查两个用户是否匹配
     * @param userId 用户ID
     * @param matchedUserId 匹配的用户ID
     * @return 匹配记录，不存在则返回null
     */
    UserMatch checkMatch(@Param("userId") Integer userId, @Param("matchedUserId") Integer matchedUserId);
} 