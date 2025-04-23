package com.example.springboot.mapper;

import com.example.springboot.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProfileMapper {
    
    UserProfile getById(Integer id);
    
    UserProfile getByOpenId(String openId);
    
    int insert(UserProfile userProfile);
    
    int update(UserProfile userProfile);
    
    List<UserProfile> getRecommendProfiles(@Param("userId") Integer userId, 
                                         @Param("gender") String gender, 
                                         @Param("latitude") Double latitude, 
                                         @Param("longitude") Double longitude,
                                         @Param("limit") Integer limit);
    
    int updateLastActiveTime(Integer userId);
} 