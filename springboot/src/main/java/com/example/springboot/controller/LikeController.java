package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Like;
import com.example.springboot.entity.User;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.service.LikeService;
import com.example.springboot.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户喜欢关系控制器
 */
@RestController
@RequestMapping("/likes")
public class LikeController {

    private static final Logger log = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    private LikeService likeService;
    
    /**
     * 获取当前用户，如果未登录则返回模拟用户
     * @return 当前用户
     */
    private User getCurrentUser() {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            // 开发模式：使用模拟用户
            log.warn("警告: 未登录，使用模拟用户ID=1！");
            currentUser = new User();
            currentUser.setId(1); // 使用ID为1的模拟用户
        }
        return currentUser;
    }

    /**
     * 添加喜欢
     * @param likedUserId 被喜欢的用户ID
     * @return 结果
     */
    @PostMapping
    public Result addLike(
            @RequestParam(required = false) Integer userId,
            @RequestParam Integer likedUserId,
            @RequestParam(defaultValue = "0") Integer type) {
        
        // 如果未提供userId，使用当前登录用户ID
        if (userId == null) {
            userId = getCurrentUser().getId();
        }
        
        boolean success = likeService.addLike(userId, likedUserId, type);
        if (success) {
            return Result.success("添加成功");
        } else {
            return Result.error("400", "已经喜欢过该用户");
        }
    }

    /**
     * 删除喜欢
     * @param likedUserId 被喜欢的用户ID
     * @return 结果
     */
    @DeleteMapping
    public Result removeLike(
            @RequestParam(required = false) Integer userId,
            @RequestParam Integer likedUserId) {
        
        // 如果未提供userId，使用当前登录用户ID
        if (userId == null) {
            userId = getCurrentUser().getId();
        }
        
        boolean success = likeService.removeLike(userId, likedUserId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("400", "未喜欢该用户");
        }
    }

    /**
     * 获取互相喜欢的用户列表
     * @return 用户列表
     */
    @GetMapping("/mutual")
    public Result getMutualLikes(@RequestParam(required = false) Integer userId) {
        
        // 如果未提供userId，使用当前登录用户ID
        if (userId == null) {
            userId = getCurrentUser().getId();
        }
        
        List<UserProfile> users = likeService.getMutualLikes(userId);
        return Result.success(users);
    }

    /**
     * 检查是否喜欢某用户
     * @param likedUserId 被检查的用户ID
     * @return 是否喜欢
     */
    @GetMapping("/check")
    public Result checkLike(
            @RequestParam(required = false) Integer userId,
            @RequestParam Integer likedUserId) {
        
        // 如果未提供userId，使用当前登录用户ID
        if (userId == null) {
            userId = getCurrentUser().getId();
        }
        
        Like like = likeService.checkLike(userId, likedUserId);
        return Result.success(like != null);
    }
} 