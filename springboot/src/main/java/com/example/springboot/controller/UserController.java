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
import com.example.springboot.controller.dto.UserDTO;
import com.example.springboot.entity.User;
import com.example.springboot.entity.UserProfile;
import com.example.springboot.entity.UserMatch;
import com.example.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * @Auther: 代刘斌
 * @Date: 2023/6/3 - 06 - 03 - 13:46
 * @Description: com.example.springboot.controller
 * @version: 1.0
 */

@RestController
@RequestMapping("/users")
public class UserController {

    @Value("${files.upload.path}")
    private String filesUploadPath;

    @Autowired
    private IUserService userService;

    //登录界面
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        UserDTO dto = userService.login(userDTO);
        return Result.success(dto);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }
        return Result.success(userService.register(userDTO));
    }

    // 新增和修改
    @PostMapping
    public Result save(@RequestBody User user) {
        // 用户默认密码
        if (user.getId() == null && user.getPassword() == null) {  // 新增用户默认密码
            user.setPassword("123");
        }

        return Result.success(userService.saveOrUpdate(user));
    }

    // 查询所有数据
    @GetMapping
    public Result findAll() {
        return Result.success(userService.list());
    }

    @GetMapping("/role/{role}")
    public Result findUsersByRole(@PathVariable String role) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", role);
        List<User> list = userService.list(queryWrapper);
        return Result.success(list);
    }

    //删除数据
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return userService.removeById(id);
    }

    @GetMapping("/username/{username}")
    public Result findByUsername(@PathVariable String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return Result.success(userService.getOne(queryWrapper));
    }

    //批量删除
    @PostMapping("/del/batch")
    public boolean deleteBatch(@RequestBody List<Integer> ids) {
        return userService.removeByIds(ids);
    }

    @GetMapping("/page")
    public IPage<User> findPage(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize,
                                @RequestParam(defaultValue = "") String username,
                                @RequestParam(defaultValue = "") String email,
                                @RequestParam(defaultValue = "") String address) {
        IPage<User> page = new Page<>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!"".equals(username)) {//当username不为空的时候才进行判断
            queryWrapper.like("username", username);
        }
        if (!"".equals(email)) {//当nickname不为空的时候才进行判断
            queryWrapper.like("email", email);
        }
        if (!"".equals(address)) {//当address不为空的时候才进行判断
            queryWrapper.like("address", address);
        }
        queryWrapper.orderByDesc("id");
        return userService.page(page, queryWrapper);
    }

    //导出接口
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<User> list = userService.list();

        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("password", "密码");
        writer.addHeaderAlias("nickname", "昵称");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("phone", "电话");
        writer.addHeaderAlias("address", "地址");
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

    //导入数据
    @PostMapping("/import")
    public Result importExcel(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("用户名", "username");
        reader.addHeaderAlias("密码", "password");
        reader.addHeaderAlias("昵称", "nickname");
        reader.addHeaderAlias("邮箱", "email");
        reader.addHeaderAlias("电话", "phone");
        reader.addHeaderAlias("地址", "address");
        List<User> list = reader.readAll(User.class);
        userService.saveBatch(list);//插入数据
        return Result.success(true);
    }

    // 模拟获取当前登录用户ID的方法，后续需要替换为实际的认证逻辑
    private Integer getCurrentUserId() {
        // 这里应该是从 token 或 session 获取用户 ID
        // 暂时返回一个模拟 ID，例如 1
        // TODO: 实现真实的用户认证逻辑
        System.out.println("警告: 正在使用模拟用户ID！");
        // 注意：User 表 和 UserProfile 表需要关联，这里返回的 ID 应该是 UserProfile 表的 ID 或能够查到 UserProfile 的 User 表 ID。
        // 假设 User 表的 ID 和 UserProfile 表的 ID 是一致的或者可以关联查询
        return 1; 
    }
    
    /**
     * 获取推荐用户列表
     * @param latitude 当前用户纬度
     * @param longitude 当前用户经度
     * @return 推荐用户列表
     */
    @GetMapping("/recommend")
    public Result recommendUsers(
            @RequestParam(required = false) Double latitude, // 设置为非必须，以便处理前端未提供位置的情况
            @RequestParam(required = false) Double longitude) {
        
        Integer currentUserId = getCurrentUserId();
        // 检查 userService 是否有 getRecommendations 方法，如果没有，需要添加
        List<UserProfile> recommendations = userService.getRecommendations(currentUserId, latitude, longitude);
        return Result.success(recommendations);
    }

    /**
     * 获取当前用户的匹配列表
     * @return 匹配列表
     */
    @GetMapping("/matches")
    public Result getMatches() {
        Integer currentUserId = getCurrentUserId();
        List<UserMatch> matches = userService.getMatches(currentUserId);
        return Result.success(matches);
    }

    /**
     * 获取用户详情
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public Result getUserDetail(@PathVariable Integer id) {
        UserProfile userProfile = userService.getUserProfileDetail(id);
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
        boolean success = userService.updateUserProfile(userProfile);
        return Result.success(success);
    }

}

