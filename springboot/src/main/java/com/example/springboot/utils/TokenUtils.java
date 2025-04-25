package com.example.springboot.utils;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.service.IUserProfileService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenUtils {

    private static IUserProfileService staticUserService;

    @Resource
    private IUserProfileService userProfileService;

    @PostConstruct
    public void setUserService() {
        staticUserService = userProfileService;
    }

    /**
     * 生成token
     *
     * @return
     */
    public static String genToken(String userId, String sign) {
        return JWT.create().withAudience(userId) // 将 user id 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) // 2小时后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }

    /**
     * 使用OpenID生成token
     * @param userId 用户ID
     * @param openid 微信OpenID
     * @return token字符串
     */
    public static String genTokenWithOpenid(String userId, String openid) {
        return JWT.create()
                .withAudience(userId) // 用户ID作为token的载荷
                .withClaim("openid", openid) // 将openid添加到token的claim中
                .withExpiresAt(DateUtil.offsetDay(new Date(), 30)) // 30天后过期
                .sign(Algorithm.HMAC256(openid)); // 使用openid作为签名密钥
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return userProfile对象
     */
    public static UserProfile getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StrUtil.isNotBlank(token)) {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.getUserProfileDetail(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
