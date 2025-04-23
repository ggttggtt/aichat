package com.example.springboot.mapper;

import com.example.springboot.entity.MomentPhoto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MomentPhotoMapper {
    
    List<MomentPhoto> getByMomentId(Integer momentId);
    
    int insert(MomentPhoto momentPhoto);
    
    int batchInsert(List<MomentPhoto> momentPhotos);
    
    int deleteByMomentId(Integer momentId);
} 