package com.example.springboot.mapper;

import com.example.springboot.entity.Moment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MomentMapper {
    
    Moment getById(Integer id);
    
    List<Moment> getMomentsByUserId(Integer userId);
    
    List<Moment> getTimelineMoments(@Param("userId") Integer userId, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    int insert(Moment moment);
    
    int delete(Integer id);
} 