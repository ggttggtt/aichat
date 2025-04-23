package com.example.springboot.mapper;

import com.example.springboot.entity.UserPhoto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserPhotoMapper {
    
    List<UserPhoto> getByUserId(Integer userId);
    
    UserPhoto getById(Integer id);
    
    int insert(UserPhoto userPhoto);
    
    int delete(Integer id);
    
    int deleteByUserId(Integer userId);
    
    int updateOrderNums(@Param("userId") Integer userId, @Param("photoIds") List<Integer> photoIds);
} 