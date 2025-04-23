package com.example.springboot.mapper;

import com.example.springboot.entity.UserMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMatchMapper {
    
    UserMatch getById(Integer id);
    
    UserMatch getByUserIds(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);
    
    List<UserMatch> getMatchesByUserId(Integer userId);
    
    int insert(UserMatch userMatch);
    
    int delete(Integer id);
} 