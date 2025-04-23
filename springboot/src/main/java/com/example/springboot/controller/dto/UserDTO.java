package com.example.springboot.controller.dto;

/**
 * @Auther: 代刘斌
 * @Date: 2023/6/6 - 06 - 06 - 22:13
 * @Description: com.example.springboot.controller.dto
 * @version: 1.0
 */

import lombok.Data;

@Data
//登录界面的实体类
public class UserDTO {
    private String username;
    private String password;
    private String nickname;
    private String avatarUrl;
    private String token;
}
