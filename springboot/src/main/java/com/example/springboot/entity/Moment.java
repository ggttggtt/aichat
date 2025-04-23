package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 朋友圈动态实体类
 */
@Data
public class Moment {
    
    private Integer id;
    private Integer userId;
    private String content;
    private String location;
    private Date createTime;
    
    // 非数据库字段
    private UserProfile user;
    private List<String> photos;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean hasLiked;  // 当前登录用户是否已点赞
    private List<MomentComment> comments;
    private String formattedTime;
} 