package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

/**
 * 用户匹配实体类
 */
@Data
public class UserMatch {
    
    private Integer id;
    private Integer userId1;
    private Integer userId2;
    private Date matchTime;
    
    // 非数据库字段
    private UserProfile matchedUser;
    private ChatMessage lastMessage;
    private Integer unreadCount;
} 