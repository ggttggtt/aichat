package com.example.springboot.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.example.springboot.common.Result;
import com.example.springboot.entity.User;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.mapper.UserProfileMapper;
import com.example.springboot.service.IUserService;
import com.example.springboot.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
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
    private IUserService userService;

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
            // 获取微信用户的openid和session_key
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            String openid = session.getOpenid();
            
            log.info("微信用户 openid: {}", openid);
            
            // 查询用户是否存在
            UserProfile profile = userProfileMapper.getByOpenId(openid);
            User user;
            
            if (profile == null) {
                // 新用户，注册
                user = new User();
                user.setUsername(openid); // 使用openid作为用户名
                user.setPassword(openid); // 使用openid作为初始密码
                user.setNickname((String) userInfo.get("nickName"));
                user.setAvatarUrl((String) userInfo.get("avatarUrl"));
                userService.save(user);
                
                // 创建用户资料
                profile = new UserProfile();
                profile.setId(user.getId());
                profile.setOpenId(openid);
                profile.setNickname((String) userInfo.get("nickName"));
                profile.setAvatarUrl((String) userInfo.get("avatarUrl"));
                profile.setGender(String.valueOf(userInfo.get("gender")));
                profile.setIsProfileCompleted(false);
                userProfileMapper.insert(profile);
            } else {
                // 老用户，获取信息
                user = userService.getById(profile.getId());
                
                // 更新用户信息
                if (userInfo != null) {
                    user.setNickname((String) userInfo.get("nickName"));
                    user.setAvatarUrl((String) userInfo.get("avatarUrl"));
                    userService.updateById(user);
                    
                    profile.setNickname((String) userInfo.get("nickName"));
                    profile.setAvatarUrl((String) userInfo.get("avatarUrl"));
                    profile.setGender(String.valueOf(userInfo.get("gender")));
                    userProfileMapper.update(profile);
                }
            }
            
            // 生成token
            String token = TokenUtils.genToken(user.getId().toString(), user.getPassword());
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", user.getId());
            result.put("openid", openid);
            result.put("isProfileCompleted", profile.getIsProfileCompleted());
            
            return Result.success(result);
        } catch (WxErrorException e) {
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