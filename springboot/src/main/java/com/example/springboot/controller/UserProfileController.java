package com.example.springboot.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Constants;
import com.example.springboot.common.Result;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.entity.UserMatch;
import com.example.springboot.service.IUserProfileService;
import com.example.springboot.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户资料表 前端控制器
 * </p>
 *
 * @author ZXL
 * @since 2025-04-25
 */
@RestController
@RequestMapping("/users")
public class UserProfileController {

    @Value("${files.upload.path}")
    private String filesUploadPath;

    @Resource
    private IUserProfileService userProfileService;

    /**
     * 用户登录
     * @param loginParam 登录参数
     * @return 登录成功的用户信息
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginParam) {
        String username = loginParam.get("username");
        String password = loginParam.get("password");
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserProfile userProfile = userProfileService.login(username, password);
        return Result.success(userProfile);
    }

    /**
     * 用户注册
     * @param userProfile 注册参数
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserProfile userProfile) {
        String nickname = userProfile.getNickname();
        String password = userProfile.getPassword();
        if (StrUtil.isBlank(nickname) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "用户名和密码不能为空");
        }
        return Result.success(userProfileService.register(userProfile));
    }

    /**
     * 新增或更新用户资料
     * @param userProfile 用户资料
     * @return 操作结果
     */
    @PostMapping
    public Result save(@RequestBody UserProfile userProfile) {
        // 新用户默认密码
        if (userProfile.getId() == null && userProfile.getPassword() == null) {
            userProfile.setPassword("123");
        }
        userProfileService.saveOrUpdate(userProfile);
        return Result.success();
    }

    /**
     * 获取所有用户
     * @return 用户列表
     */
    @GetMapping
    public Result findAll() {
        return Result.success(userProfileService.list());
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        userProfileService.removeById(id);
        return Result.success();
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/username/{username}")
    public Result findByUsername(@PathVariable String username) {
        QueryWrapper<UserProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("nickname", username);
        return Result.success(userProfileService.getOne(queryWrapper));
    }

    /**
     * 批量删除用户
     * @param ids 用户ID列表
     * @return 操作结果
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        userProfileService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 分页查询用户
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param username 用户名
     * @param address 地址
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String username,
                           @RequestParam(defaultValue = "") String address) {
        Page<UserProfile> page = new Page<>(pageNum, pageSize);
        return Result.success(userProfileService.findPage(page, username, address));
    }

    /**
     * 导出用户数据
     * @param response HTTP响应
     * @throws IOException IO异常
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<UserProfile> list = userProfileService.list();

        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("nickname", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("location", "地址");
        writer.addHeaderAlias("bio", "个人介绍");
        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    /**
     * 导入用户数据
     * @param file Excel文件
     * @return 操作结果
     * @throws IOException IO异常
     */
    @PostMapping("/import")
    public Result importExcel(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("用户名", "nickname");
        reader.addHeaderAlias("密码", "password");
        reader.addHeaderAlias("地址", "location");
        reader.addHeaderAlias("个人介绍", "bio");
        List<UserProfile> list = reader.readAll(UserProfile.class);
        userProfileService.saveBatch(list);//插入数据
        return Result.success(true);
    }

    /**
     * 获取当前用户ID，如果未登录返回默认值
     * @return 当前用户ID
     */
    private Integer getCurrentUserId() {
        UserProfile currentUser = TokenUtils.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getId();
        } else {
            // 开发模式：使用模拟用户
            System.out.println("警告: 正在使用模拟用户ID！");
            return 1; // 使用ID为1的模拟用户
        }
    }

    /**
     * 获取推荐用户列表
     * @param latitude 纬度
     * @param longitude 经度
     * @return 推荐用户列表
     */
    @GetMapping("/recommend")
    public Result recommendUsers(
            @RequestParam(required = false) String latitude,
            @RequestParam(required = false) String longitude) {
        
        Integer currentUserId = getCurrentUserId();
        
        // 将字符串转换为Double，处理"null"字符串
        Double lat = (latitude != null && !latitude.equals("null")) ? Double.parseDouble(latitude) : null;
        Double lng = (longitude != null && !longitude.equals("null")) ? Double.parseDouble(longitude) : null;
        
        List<UserProfile> recommendations = userProfileService.getRecommendations(currentUserId, lat, lng);
        return Result.success(recommendations);
    }

    /**
     * 获取匹配列表
     * @return 匹配列表
     */
    @GetMapping("/matches")
    public Result getMatches() {
        Integer currentUserId = getCurrentUserId();
        List<UserMatch> matches = userProfileService.getMatches(currentUserId);
        return Result.success(matches);
    }

    /**
     * 获取用户详情
     * @param id 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/{id}")
    public Result getUserDetail(@PathVariable Integer id) {
        UserProfile userProfile = userProfileService.getUserProfileDetail(id);
        return Result.success(userProfile);
    }

    /**
     * 获取当前用户资料
     * @return 当前用户资料
     */
    @GetMapping("/profile")
    public Result getCurrentUserProfile() {
        Integer currentUserId = getCurrentUserId();
        UserProfile userProfile = userProfileService.getUserProfileDetail(currentUserId);
        return Result.success(userProfile);
    }

    /**
     * 更新用户资料
     * @param userProfile 用户资料
     * @return 更新结果
     */
    @PutMapping("/profile")
    public Result updateUserProfile(@RequestBody UserProfile userProfile) {
        Integer currentUserId = getCurrentUserId();
        // 确保不能修改其他用户的资料
        userProfile.setId(currentUserId);
        boolean success = userProfileService.updateUserProfile(userProfile);
        return Result.success(success);
    }

    /**
     * 创建或更新用户资料
     * @param userProfile 用户资料
     * @return 更新结果
     */
    @PostMapping("/profile")
    public Result createOrUpdateUserProfile(@RequestBody UserProfile userProfile) {
        Integer currentUserId = getCurrentUserId();
        // 确保不能修改其他用户的资料
        userProfile.setId(currentUserId);
        boolean success = userProfileService.updateUserProfile(userProfile);
        return Result.success(success);
    }
}

