package com.example.springboot.mapper;

import com.example.springboot.entity.UserTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户标签Mapper接口
 */
@Mapper
public interface UserTagMapper {
    
    /**
     * 根据用户ID获取标签列表
     * @param userId 用户ID
     * @return 标签列表
     */
    List<UserTag> getByUserId(@Param("userId") Integer userId);
    
    /**
     * 新增标签
     * @param userTag 标签信息
     * @return 影响的行数
     */
    int insert(UserTag userTag);
    
    /**
     * 批量插入标签
     * @param tags 标签列表
     * @return 影响的行数
     */
    int batchInsert(List<UserTag> tags);
    
    /**
     * 删除标签
     * @param id 标签ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据用户ID删除所有标签
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteByUserId(@Param("userId") Integer userId);
} 