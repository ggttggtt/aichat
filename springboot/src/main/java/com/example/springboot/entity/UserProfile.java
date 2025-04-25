package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户资料表
 * </p>
 *
 * @author ZXL
 * @since 2025-04-25
 */
@Getter
@Setter
@TableName("user_profile")
@ApiModel(value = "UserProfile对象", description = "用户资料表")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("微信openid")
      private String openId;

      @ApiModelProperty("昵称")
      private String nickname;

      @ApiModelProperty("密码")
      @TableField(exist = false)
      private String password;

      @ApiModelProperty("性别")
      private String gender;

      @ApiModelProperty("出生日期")
      private Date birthdate;

      @ApiModelProperty("所在地")
      private String location;

      @ApiModelProperty("个人介绍")
      private String bio;

      @ApiModelProperty("头像URL")
      private String avatarUrl;

      @ApiModelProperty("纬度")
      private Double latitude;

      @ApiModelProperty("经度")
      private Double longitude;

      @ApiModelProperty("注册时间")
      private LocalDateTime registerTime;

      @ApiModelProperty("最后活跃时间")
      private LocalDateTime lastActiveTime;

      @ApiModelProperty("是否完善资料")
      private Boolean isProfileCompleted;

      // 非持久化字段，用于前端展示
      @TableField(exist = false)
      private List<String> photos;
      
      @TableField(exist = false)
      private List<String> tags;
      
      @TableField(exist = false)
      private Integer age;
      
      @TableField(exist = false)
      private Double distance;
      
      @ApiModelProperty("登录令牌")
      @TableField(exist = false)
      private String token;
}
