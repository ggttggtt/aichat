package com.example.springboot.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.mapper.UserProfileMapper;
import com.example.springboot.service.IUserProfileService;
import com.example.springboot.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序相关接口
 */
@RestController
@Slf4j
public class WxMaController {

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private IUserProfileService userProfileService;

    @Autowired
    private UserProfileMapper userProfileMapper;

    /**
     * 微信小程序登录
     * @param params 登录请求参数，包含code和用户信息
     * @return 登录结果
     */
    @PostMapping("/wx/login")
    public Result login(@RequestBody Map<String, Object> params) {
        String code = (String) params.get("code");
        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>) params.get("userInfo");

        if (code == null || code.isEmpty()) {
            return Result.error("400", "登录失败，code不能为空");
        }

        try {
            // 检查wxMaService是否为null
            if (wxMaService == null) {
                log.error("wxMaService未注入，请检查配置");
                return Result.error("500", "服务器配置错误");
            }

            // 获取微信用户的openid和session_key
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            String openid = session.getOpenid();
            
            log.info("微信用户 openid: {}", openid);
            
            // 检查userProfileMapper是否为null
            if (userProfileMapper == null) {
                log.error("userProfileMapper未注入，请检查配置");
                return Result.error("500", "服务器配置错误");
            }
            
            // 查询用户是否存在
            UserProfile profile = userProfileMapper.getByOpenId(openid);
            
            if (profile == null) {
                // 新用户，注册
                profile = new UserProfile();
                profile.setOpenId(openid);
                
                // 安全地设置用户资料
                if (userInfo != null) {
                    profile.setNickname(userInfo.get("nickName") != null ? (String) userInfo.get("nickName") : "微信用户");
                    profile.setAvatarUrl(userInfo.get("avatarUrl") != null ? (String) userInfo.get("avatarUrl") : "");
                    profile.setGender(userInfo.get("gender") != null ? String.valueOf(userInfo.get("gender")) : "0");
                } else {
                    profile.setNickname("微信用户");
                    profile.setAvatarUrl("");
                    profile.setGender("0");
                }
                
                profile.setIsProfileCompleted(false);
                
                // 检查userProfileService是否为null
                if (userProfileService == null) {
                    log.error("userProfileService未注入，请检查配置");
                    return Result.error("500", "服务器配置错误");
                }
                
                userProfileService.save(profile);
            } else {
                // 老用户，更新信息
                if (userProfileService == null) {
                    log.error("userProfileService未注入，请检查配置");
                    return Result.error("500", "服务器配置错误");
                }
                
                // 更新用户信息
                if (userInfo != null) {
                    profile.setNickname(userInfo.get("nickName") != null ? (String) userInfo.get("nickName") : profile.getNickname());
                    profile.setAvatarUrl(userInfo.get("avatarUrl") != null ? (String) userInfo.get("avatarUrl") : profile.getAvatarUrl());
                    profile.setGender(userInfo.get("gender") != null ? String.valueOf(userInfo.get("gender")) : profile.getGender());
                    userProfileService.updateById(profile);
                }
            }
            
            // 生成token，使用openid作为签名
            String token = TokenUtils.genTokenWithOpenid(profile.getId().toString(), openid);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", profile.getId());
            result.put("openid", openid);
            result.put("isProfileCompleted", profile.getIsProfileCompleted());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("微信登录失败", e);
            return Result.error("500", "微信登录失败: " + e.getMessage());
        }
    }

    /**
     * 服务器连接测试
     * @return 连接测试结果
     */
    @GetMapping("/wx/ping")
    public Result ping() {
        return Result.success("pong");
    }
} 