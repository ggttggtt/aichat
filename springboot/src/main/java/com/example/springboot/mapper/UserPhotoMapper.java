package com.example.springboot.mapper;

import com.example.springboot.entity.UserPhoto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户照片Mapper接口
 */
@Mapper
public interface UserPhotoMapper {
    
    /**
     * 根据用户ID获取照片列表
     * @param userId 用户ID
     * @return 照片列表
     */
    List<UserPhoto> getByUserId(@Param("userId") Integer userId);
    
    /**
     * 新增照片
     * @param userPhoto 照片信息
     * @return 影响的行数
     */
    int insert(UserPhoto userPhoto);
    
    /**
     * 更新照片信息
     * @param userPhoto 照片信息
     * @return 影响的行数
     */
    int update(UserPhoto userPhoto);
    
    /**
     * 删除照片
     * @param id 照片ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据用户ID删除所有照片
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteByUserId(@Param("userId") Integer userId);
} 