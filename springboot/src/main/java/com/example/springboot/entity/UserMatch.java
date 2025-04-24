package com.example.springboot.entity;

import java.util.Date;

/**
 * 用户匹配实体类
 */
public class UserMatch {
    private Integer id;
    private Integer userId;
    private Integer matchedUserId;
    private Date matchTime;
    
    // 可能需要的用户信息
    private UserProfile matchedUserProfile;
    
    // 最后一条聊天消息
    private ChatMessage lastMessage;
    
    // 未读消息数
    private Integer unreadCount;

    public UserMatch() {
    }

    public UserMatch(Integer userId, Integer matchedUserId) {
        this.userId = userId;
        this.matchedUserId = matchedUserId;
        this.matchTime = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMatchedUserId() {
        return matchedUserId;
    }

    public void setMatchedUserId(Integer matchedUserId) {
        this.matchedUserId = matchedUserId;
    }

    public Date getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(Date matchTime) {
        this.matchTime = matchTime;
    }

    public UserProfile getMatchedUserProfile() {
        return matchedUserProfile;
    }

    public void setMatchedUserProfile(UserProfile matchedUserProfile) {
        this.matchedUserProfile = matchedUserProfile;
    }
    
    public ChatMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }
} 