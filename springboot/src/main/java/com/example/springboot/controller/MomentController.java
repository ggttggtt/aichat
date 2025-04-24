package com.example.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Moment;
import com.example.springboot.entity.User;
import com.example.springboot.service.MomentService;
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

    /**
     * 获取动态列表
     * @param userId 当前用户ID
     * @param page 页码
     * @param size 每页条数
     * @return 动态列表
     */
    @GetMapping
    public Result getMoments(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        List<Moment> moments = momentService.listMoments(userId, page, size);
        return Result.success(moments);
    }

    /**
     * 获取好友动态列表
     * @param userId 当前用户ID
     * @param page 页码
     * @param size 每页条数
     * @return 动态列表
     */
    @GetMapping("/friends")
    public Result getFriendMoments(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        List<Moment> moments = momentService.listFriendMoments(userId, page, size);
        return Result.success(moments);
    }

    /**
     * 获取用户动态列表
     * @param userId 用户ID
     * @param visitorId 访问者ID
     * @param page 页码
     * @param size 每页条数
     * @return 动态列表
     */
    @GetMapping("/user/{userId}")
    public Result getUserMoments(
            @PathVariable Integer userId,
            @RequestParam Integer visitorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        List<Moment> moments = momentService.listUserMoments(userId, visitorId, page, size);
        return Result.success(moments);
    }

    /**
     * 获取动态详情
     * @param id 动态ID
     * @param userId 当前用户ID
     * @return 动态详情
     */
    @GetMapping("/{id}")
    public Result getMoment(@PathVariable Integer id, @RequestParam Integer userId) {
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
     * @param momentBody 请求体
     * @return 操作结果
     */
    @PostMapping
    public Result publishMoment(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "0") Integer visibility,
            @RequestBody(required = false) Moment momentBody) {
        try {
            // 获取当前登录用户
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("401", "未登录");
            }
            
            // 创建动态对象
            Moment moment = new Moment();
            moment.setUserId(currentUser.getId());
            
            // 处理请求体和表单参数
            if (momentBody != null) {
                // 优先使用请求体中的数据
                if (momentBody.getContent() != null) {
                    moment.setContent(momentBody.getContent());
                }
                if (momentBody.getLocation() != null) {
                    moment.setLocation(momentBody.getLocation());
                }
                if (momentBody.getVisibility() != null) {
                    moment.setVisibility(momentBody.getVisibility());
                }
                if (momentBody.getImageUrls() != null) {
                    moment.setImageUrls(momentBody.getImageUrls());
                }
            } else {
                // 使用表单参数
                if (content == null || content.trim().isEmpty()) {
                    return Result.error("400", "动态内容不能为空");
                }
                moment.setContent(content);
                moment.setLocation(location);
                moment.setVisibility(visibility);
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
        } catch (Exception e) {
            log.error("发布动态异常: ", e);
            return Result.error("500", "服务器异常: " + e.getMessage());
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
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("401", "未登录");
        }
        
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
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("401", "未登录");
        }
        
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
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("401", "未登录");
        }
        
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
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("401", "未登录");
        }
        
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
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            return Result.error("401", "未登录");
        }
        
        Map<String, Object> profile = new HashMap<>();
        // 这里可以添加用户动态的统计信息，例如：
        // - 发布的动态总数
        // - 获得的点赞总数
        // - 最近一周发布的动态数
        // 等等
        
        return Result.success(profile);
    }
} 