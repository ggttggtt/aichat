package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.entity.User;


/**
 * @Auther: 代刘斌
 * @Date: 2023/6/6 - 06 - 06 - 22:27
 * @Description: com.example.springboot.service
 * @version: 1.0
 */

public interface IUserService extends IService<User> {

    UserDTO login(UserDTO userDTO);

    User register(UserDTO userDTO);

    Page<User> findPage(Page<User> objectPage, String username, String email, String address);
}
