package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.entity.User;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.entity.UserMatch;

import java.util.List;

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

    /**
     * 获取推荐用户列表
     * @param userId 当前用户ID
     * @param latitude 当前用户纬度
     * @param longitude 当前用户经度
     * @return 推荐用户列表
     */
    List<UserProfile> getRecommendations(Integer userId, Double latitude, Double longitude);

    /**
     * 获取用户的匹配列表
     * @param userId 当前用户ID
     * @return 匹配列表，包含匹配用户信息和最后一条消息等
     */
    List<UserMatch> getMatches(Integer userId);
}
