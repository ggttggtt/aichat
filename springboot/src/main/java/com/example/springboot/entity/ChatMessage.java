package com.example.springboot.entity;

import java.util.Date;

/**
 * 聊天消息实体类
 */
public class ChatMessage {
    
    /**
     * 消息ID
     */
    private Integer id;
    
    /**
     * 匹配关系ID
     */
    private Integer matchId;
    
    /**
     * 发送者用户ID
     */
    private Integer senderId;
    
    /**
     * 接收者用户ID
     */
    private Integer receiverId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型: 1-文本, 2-图片, 3-语音, 4-视频
     */
    private Integer messageType;
    
    /**
     * 是否已读: 0-未读, 1-已读
     */
    private Integer isRead;
    
    /**
     * 发送时间
     */
    private Date sendTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 格式化后的时间（非数据库字段，用于前端显示）
     */
    private String formattedTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }
} 