package com.example.springboot.mapper;

import com.example.springboot.entity.MomentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MomentLikeMapper {
    
    List<MomentLike> getByMomentId(Integer momentId);
    
    int getLikeCount(Integer momentId);
    
    MomentLike getByMomentIdAndUserId(@Param("momentId") Integer momentId, @Param("userId") Integer userId);
    
    int insert(MomentLike momentLike);
    
    int delete(@Param("momentId") Integer momentId, @Param("userId") Integer userId);
    
    int deleteByMomentId(Integer momentId);
} 