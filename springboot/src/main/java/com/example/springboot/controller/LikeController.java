package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Like;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.service.LikeService;
import com.example.springboot.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
    private UserProfile getCurrentUser() {
        UserProfile currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            // 开发模式：使用模拟用户
            log.warn("警告: 未登录，使用模拟用户ID=1！");
            currentUser = new UserProfile();
            currentUser.setId(1); // 使用ID为1的模拟用户
        }
        return currentUser;
    }

    /**
     * 添加喜欢（接收查询参数）
     * 支持前端直接发送带有参数的POST请求
     * @param userId 用户ID
     * @param likedUserId 被喜欢的用户ID
     * @param type 喜欢类型(0:不喜欢,1:喜欢)
     * @return 操作结果
     */
    @PostMapping("")
    public Result addLike(
            @RequestParam(required = false) Integer userId,
            @RequestParam Integer likedUserId,
            @RequestParam(defaultValue = "1") Integer type) {
        
        // 如果没有提供userId，使用当前登录用户
        if (userId == null) {
            UserProfile currentUser = getCurrentUser();
            userId = currentUser.getId();
        }
        
        // 添加喜欢记录
        boolean success = likeService.addLike(userId, likedUserId, type);
        
        if (success) {
            return Result.success("操作成功");
        } else {
            return Result.error("400", "操作失败");
        }
    }

    /**
     * 添加喜欢
     * @param likedUserId 被喜欢的用户ID
     * @return 操作结果
     */
    @PostMapping("/{likedUserId}")
    public Result like(@PathVariable Integer likedUserId) {
        // 获取当前用户
        UserProfile currentUser = getCurrentUser();
        
        // 喜欢用户
        boolean success = likeService.likeOrDislike(currentUser.getId(), likedUserId, 1);
        
        if (success) {
            return Result.success();
        } else {
            return Result.error("400", "操作失败");
        }
    }
    
    /**
     * 不喜欢用户
     * @param likedUserId 被不喜欢的用户ID
     * @return 操作结果
     */
    @PostMapping("/dislike/{likedUserId}")
    public Result dislike(@PathVariable Integer likedUserId) {
        // 获取当前用户
        UserProfile currentUser = getCurrentUser();
        
        // 不喜欢用户
        boolean success = likeService.likeOrDislike(currentUser.getId(), likedUserId, 0);
        
        if (success) {
            return Result.success();
        } else {
            return Result.error("400", "操作失败");
        }
    }
    
    /**
     * 获取互相喜欢的用户列表
     * @return 用户列表
     */
    @GetMapping("/mutual")
    public Result getMutualLikes() {
        // 获取当前用户
        UserProfile currentUser = getCurrentUser();
        
        // 获取互相喜欢的用户
        List<UserProfile> mutualLikes = likeService.getMutualLikes(currentUser.getId());
        
        return Result.success(mutualLikes);
    }
    
    /**
     * 检查当前用户是否喜欢指定用户
     * @param likedUserId 被喜欢的用户ID
     * @return 喜欢状态
     */
    @GetMapping("/check/{likedUserId}")
    public Result checkLikeStatus(@PathVariable Integer likedUserId) {
        // 获取当前用户
        UserProfile currentUser = getCurrentUser();
        
        // 检查喜欢状态
        boolean hasLiked = likeService.hasLiked(currentUser.getId(), likedUserId, 1);
        
        Map<String, Object> data = new HashMap<>();
        data.put("liked", hasLiked);
        
        return Result.success(data);
    }
    
    /**
     * 移除喜欢
     * @param likedUserId 被移除喜欢的用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{likedUserId}")
    public Result removeLike(@PathVariable Integer likedUserId) {
        // 获取当前用户
        UserProfile currentUser = getCurrentUser();
        
        // 移除喜欢
        boolean success = likeService.removeLike(currentUser.getId(), likedUserId);
        
        if (success) {
            return Result.success();
        } else {
            return Result.error("400", "操作失败");
        }
    }
    
    /**
     * 获取指定用户被喜欢的数量
     * @param userId 用户ID
     * @return 被喜欢的数量
     */
    @GetMapping("/count/{userId}")
    public Result getLikeCount(@PathVariable Integer userId) {
        // 获取用户被喜欢的数量（类型1表示用户喜欢）
        int count = likeService.countLikes(userId, 1);
        
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        
        return Result.success(data);
    }
} 