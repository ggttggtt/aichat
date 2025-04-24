package com.example.springboot.entity;

import java.util.Date;

/**
 * 动态点赞关系实体类
 */
public class MomentLike {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 动态ID
     */
    private Integer momentId;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 创建时间
     */
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMomentId() {
        return momentId;
    }

    public void setMomentId(Integer momentId) {
        this.momentId = momentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
} 