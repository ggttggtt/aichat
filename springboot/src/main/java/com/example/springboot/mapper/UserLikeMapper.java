package com.example.springboot.mapper;

import com.example.springboot.entity.UserLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserLikeMapper {
    
    UserLike getByUserIdAndLikedUserId(@Param("userId") Integer userId, @Param("likedUserId") Integer likedUserId);
    
    List<UserLike> getLikesByUserId(Integer userId);
    
    List<UserLike> getLikedByUserId(Integer likedUserId);
    
    int insert(UserLike userLike);
    
    int update(UserLike userLike);
    
    List<Integer> getMutualLikeUserIds(Integer userId);
} 