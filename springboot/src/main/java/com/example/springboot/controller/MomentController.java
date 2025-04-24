package com.example.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Moment;
import com.example.springboot.entity.User;
import com.example.springboot.entity.MomentComment;
import com.example.springboot.service.MomentService;
import com.example.springboot.service.MomentCommentService;
import com.example.springboot.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 用户动态控制器
 */
@RestController
@RequestMapping("/moments")
public class MomentController {

    private static final Logger log = LoggerFactory.getLogger(MomentController.class);
    
    @Value("${upload.path:./uploads/}")
    private String uploadPath;
    
    @Autowired
    private MomentService momentService;
    
    @Autowired
    private MomentCommentService momentCommentService;

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
     * 获取动态列表
     * @param page 页码
     * @param size 每页条数
     * @return 动态列表
     */
    @GetMapping
    public Result getMoments(
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        // 如果未提供userId，使用当前用户ID
        if (userId == null) {
            userId = getCurrentUser().getId();
        }
        
        List<Moment> moments = momentService.listMoments(userId, page, size);
        return Result.success(moments);
    }

    /**
     * 获取好友动态列表
     * @param page 页码
     * @param size 每页条数
     * @return 动态列表
     */
    @GetMapping("/friends")
    public Result getFriendMoments(
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        // 如果未提供userId，使用当前用户ID
        if (userId == null) {
            userId = getCurrentUser().getId();
        }
        
        List<Moment> moments = momentService.listFriendMoments(userId, page, size);
        return Result.success(moments);
    }

    /**
     * 获取用户动态列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页条数
     * @return 动态列表
     */
    @GetMapping("/user/{userId}")
    public Result getUserMoments(
            @PathVariable Integer userId,
            @RequestParam(required = false) Integer visitorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        // 如果未提供visitorId，使用当前用户ID
        if (visitorId == null) {
            visitorId = getCurrentUser().getId();
        }
        
        List<Moment> moments = momentService.listUserMoments(userId, visitorId, page, size);
        return Result.success(moments);
    }

    /**
     * 获取动态详情
     * @param id 动态ID
     * @return 动态详情
     */
    @GetMapping("/{id}")
    public Result getMoment(
            @PathVariable Integer id, 
            @RequestParam(required = false) Integer userId) {
        
        // 如果未提供userId，使用当前用户ID
        if (userId == null) {
            userId = getCurrentUser().getId();
        }
        
        Moment moment = momentService.getMomentById(id, userId);
        if (moment == null) {
            return Result.error("404", "动态不存在");
        }
        return Result.success(moment);
    }

    /**
     * 发布动态
     * @param content 动态内容
     * @param images 图片文件数组
     * @param location 位置信息
     * @param visibility 可见性
     * @return 操作结果
     */
    @PostMapping
    public Result publishMoment(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "0") Integer visibility) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        // 创建动态对象
        Moment moment = new Moment();
        moment.setUserId(currentUser.getId());
        
        // 处理请求体和表单参数
        // 优先使用请求体中的数据
        if (content != null) {
            moment.setContent(content);
        }
        if (location != null) {
            moment.setLocation(location);
        }
        if (visibility != null) {
            moment.setVisibility(visibility);
        }
        if (images != null) {
            moment.setImageUrls(images[0].getOriginalFilename());
        }

        // 如果既没有提供momentBody.getImageUrls()，也没有提供images数组，则处理上传图片
        if (moment.getImageUrls() == null && images != null && images.length > 0) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    try {
                        String fileName = file.getOriginalFilename();
                        String extension = fileName.substring(fileName.lastIndexOf("."));
                        String newFileName = UUID.randomUUID().toString() + extension;
                        String relativePath = "upload/moments/" + newFileName;
                        String fullPath = uploadPath + relativePath;
                        
                        // 确保目录存在
                        File targetFile = new File(fullPath);
                        if (!targetFile.getParentFile().exists()) {
                            targetFile.getParentFile().mkdirs();
                        }
                        
                        // 保存文件
                        file.transferTo(targetFile);
                        imageUrls.add(relativePath);
                    } catch (IOException e) {
                        log.error("文件上传失败: {}", e.getMessage());
                        return Result.error("500", "文件上传失败: " + e.getMessage());
                    }
                }
            }
            moment.setImageUrls(String.join(",", imageUrls));
        }
        
        // 设置初始统计数据
        moment.setLikeCount(0);
        moment.setCommentCount(0);
        
        // 保存动态
        Moment newMoment = momentService.publishMoment(moment);
        if (newMoment != null) {
            return Result.success(newMoment);
        } else {
            return Result.error("500", "发布动态失败");
        }
    }

    /**
     * 更新动态
     * @param id 动态ID
     * @param content 动态内容
     * @param location 位置信息
     * @param visibility 可见性
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result updateMoment(
            @PathVariable Integer id,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer visibility) {
        
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        Moment moment = new Moment();
        moment.setId(id);
        moment.setUserId(currentUser.getId());
        moment.setContent(content);
        moment.setLocation(location);
        moment.setVisibility(visibility);
        
        boolean success = momentService.updateMoment(moment);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("403", "没有权限或动态不存在");
        }
    }

    /**
     * 删除动态
     * @param id 动态ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result deleteMoment(@PathVariable Integer id) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        boolean success = momentService.deleteMoment(id, currentUser.getId());
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("403", "没有权限或动态不存在");
        }
    }

    /**
     * 点赞动态
     * @param momentId 动态ID
     * @return 操作结果
     */
    @PostMapping("/like/{momentId}")
    public Result likeMoment(@PathVariable Integer momentId) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        boolean success = momentService.likeMoment(momentId, currentUser.getId());
        if (success) {
            return Result.success("点赞成功");
        } else {
            return Result.error("400", "点赞失败");
        }
    }

    /**
     * 取消点赞动态
     * @param momentId 动态ID
     * @return 操作结果
     */
    @PostMapping("/unlike/{momentId}")
    public Result unlikeMoment(@PathVariable Integer momentId) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        boolean success = momentService.unlikeMoment(momentId, currentUser.getId());
        if (success) {
            return Result.success("取消点赞成功");
        } else {
            return Result.error("400", "未点赞或动态不存在");
        }
    }

    /**
     * 获取用户动态统计信息
     * @return 用户动态统计
     */
    @GetMapping("/profile")
    public Result getUserMomentProfile() {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        Map<String, Object> profile = new HashMap<>();
        // 这里可以添加用户动态的统计信息，例如：
        // - 发布的动态总数
        // - 获得的点赞总数
        // - 最近一周发布的动态数
        // 等等
        
        return Result.success(profile);
    }

    /**
     * 获取动态评论列表
     * @param momentId 动态ID
     * @return 评论列表
     */
    @GetMapping("/{momentId}/comments")
    public Result getComments(@PathVariable Integer momentId) {
        List<MomentComment> comments = momentCommentService.getCommentsByMomentId(momentId);
        return Result.success(comments);
    }
    
    /**
     * 添加评论
     * @param momentId 动态ID
     * @param content 评论内容
     * @return 新增的评论
     */
    @PostMapping("/{momentId}/comments")
    public Result addComment(
            @PathVariable Integer momentId,
            @RequestBody Map<String, String> body) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        // 获取评论内容
        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return Result.error("400", "评论内容不能为空");
        }
        
        // 创建评论对象
        MomentComment comment = new MomentComment();
        comment.setMomentId(momentId);
        comment.setUserId(currentUser.getId());
        comment.setContent(content);
        
        // 添加评论
        MomentComment newComment = momentCommentService.addComment(comment);
        return Result.success(newComment);
    }
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/comments/{commentId}")
    public Result deleteComment(@PathVariable Integer commentId) {
        // 获取当前登录用户
        User currentUser = getCurrentUser();
        
        // 删除评论
        boolean success = momentCommentService.deleteComment(commentId, currentUser.getId());
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("403", "没有权限或评论不存在");
        }
    }
} 