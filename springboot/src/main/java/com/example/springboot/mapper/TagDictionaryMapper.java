package com.example.springboot.mapper;

import com.example.springboot.entity.TagDictionary;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagDictionaryMapper {
    
    List<TagDictionary> getAllTags();
    
    List<TagDictionary> getTagsByCategory(String category);
    
    TagDictionary getByTagName(String tagName);
} 