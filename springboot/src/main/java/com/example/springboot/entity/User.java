package com.example.springboot.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Auther: 代刘斌
 * @Date: 2023/6/3 - 06 - 03 - 12:19
 * @Description: com.example.springboot.controller
 * @version: 1.0
 */

@Data
@TableName("sys_user")
public class User {

    @TableId(type = IdType.AUTO)

    @ExcelIgnore
    @ExcelProperty("编号")
    private Integer id;
    @ExcelProperty("用户名")
    private String username;
    @ExcelProperty("密码")
    private String password;
    @ExcelProperty("昵称")
    private String nickname;
    @ExcelProperty("邮箱")
    private String email;
    @ExcelProperty("电话号码")
    private String phone;
    @ExcelProperty("地址")
    private String address;
    private Date createTime;

    private String avatarUrl;

    private String role;

}
