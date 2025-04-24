package com.example.springboot.entity;

import java.util.Date;

/**
 * 用户喜欢关系实体类
 */
public class Like {
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 被喜欢的用户ID
     */
    private Integer likedUserId;
    
    /**
     * 类型：0-不喜欢，1-喜欢
     */
    private Integer type;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;

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

    public Integer getLikedUserId() {
        return likedUserId;
    }

    public void setLikedUserId(Integer likedUserId) {
        this.likedUserId = likedUserId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
} 