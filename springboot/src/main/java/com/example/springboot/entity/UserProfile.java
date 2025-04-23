package com.example.springboot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户资料实体类
 */
@Data

public class UserProfile {
    
    private Integer id;
    private String openId;
    private String nickname;
    private String gender;
    private Date birthdate;
    private String location;
    private String bio;
    private String avatarUrl;
    private Double latitude;
    private Double longitude;
    private Date registerTime;
    private Date lastActiveTime;
    private Boolean isProfileCompleted;
    
    // 非数据库字段
    private List<String> photos;
    private List<String> tags;
    private Integer age;
    private Double distance;
    
    @JsonIgnore
    public String getOpenId() {
        return openId;
    }
} 