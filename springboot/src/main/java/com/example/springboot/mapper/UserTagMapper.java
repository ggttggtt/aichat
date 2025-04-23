package com.example.springboot.mapper;

import com.example.springboot.entity.UserTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserTagMapper {
    
    List<UserTag> getByUserId(Integer userId);
    
    int insert(UserTag userTag);
    
    int batchInsert(List<UserTag> userTags);
    
    int deleteByUserId(Integer userId);
} 