package com.example.springboot.entity;

import lombok.Data;

import java.util.Date;

/**
 * 聊天消息实体类
 */
@Data
public class ChatMessage {
    
    private Integer id;
    private Integer matchId;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private Date sendTime;
    private Boolean isRead;
    
    // 非数据库字段
    private String senderNickname;
    private String senderAvatar;
    private String formattedTime;
} 