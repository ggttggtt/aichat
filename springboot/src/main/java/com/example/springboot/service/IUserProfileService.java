package com.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.entity.UserMatch;

import java.util.List;

/**
 * <p>
 * 用户资料表 服务类
 * </p>
 *
 * @author ZXL
 * @since 2025-04-25
 */
public interface IUserProfileService extends IService<UserProfile> {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户信息，包含token
     */
    UserProfile login(String username, String password);

    /**
     * 用户注册
     * @param userProfile 注册参数
     * @return 注册成功的用户信息
     */
    UserProfile register(UserProfile userProfile);

    /**
     * 分页查询用户
     * @param page 分页对象
     * @param username 用户名
     * @param address 地址
     * @return 分页结果
     */
    Page<UserProfile> findPage(Page<UserProfile> page, String username, String address);

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

    /**
     * 获取用户详细资料
     * @param userId 用户ID
     * @return 详细用户资料，包含照片和标签等
     */
    UserProfile getUserProfileDetail(Integer userId);

    /**
     * 更新用户资料
     * @param userProfile 用户资料
     * @return 是否更新成功
     */
    boolean updateUserProfile(UserProfile userProfile);
}
