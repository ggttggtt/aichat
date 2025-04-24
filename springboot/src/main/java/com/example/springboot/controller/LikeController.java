package com.example.springboot.controller;

import com.example.springboot.entity.Like;
import com.example.springboot.service.LikeService;
import com.example.springboot.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户喜欢关系控制器
 */
@RestController
@RequestMapping("/api/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * 喜欢或不喜欢用户
     * @param userId 当前用户ID
     * @param likedUserId 被喜欢/不喜欢的用户ID
     * @param type 类型：1-喜欢，0-不喜欢
     * @return 操作结果
     */
    @PostMapping("/action")
    public Result likeOrDislikeUser(
            @RequestParam Integer userId,
            @RequestParam Integer likedUserId,
            @RequestParam Integer type) {
        
        if (userId.equals(likedUserId)) {
            return Result.error("400", "不能对自己执行此操作");
        }
        
        boolean success = likeService.likeOrDislike(userId, likedUserId, type);
        
        if (success) {
            return Result.success("操作成功");
        } else {
            return Result.error("500", "操作失败");
        }
    }

    /**
     * 获取喜欢状态
     * @param userId 当前用户ID
     * @param likedUserId 被查询的用户ID
     * @return 喜欢状态
     */
    @GetMapping("/status")
    public Result getLikeStatus(
            @RequestParam Integer userId,
            @RequestParam Integer likedUserId) {
        
        Like likeStatus = likeService.getLikeStatus(userId, likedUserId);
        
        Map<String, Object> data = new HashMap<>();
        if (likeStatus != null) {
            data.put("type", likeStatus.getType());
            data.put("createTime", likeStatus.getCreateTime());
        } else {
            data.put("type", null);
        }
        
        return Result.success(data);
    }

    /**
     * 获取互相喜欢的用户ID列表
     * @param userId 当前用户ID
     * @return 互相喜欢的用户ID列表
     */
    @GetMapping("/mutual")
    public Result getMutualLikes(@RequestParam Integer userId) {
        List<Integer> mutualLikeUserIds = likeService.getMutualLikeUserIds(userId);
        return Result.success(mutualLikeUserIds);
    }
} 