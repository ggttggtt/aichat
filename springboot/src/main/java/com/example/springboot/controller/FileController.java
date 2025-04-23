package com.example.springboot.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.common.Result;
import com.example.springboot.service.IFileUpload;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.example.springboot.service.IFileService;
import com.example.springboot.entity.File;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 代刘斌
 * @since 2023-09-27
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private IFileService fileService;

    @Resource
    private IFileUpload iFileUpload;

    //上传文件到阿里云
    @PostMapping("/upload")
    public Result upload(@RequestBody MultipartFile multipartFile) {
        return Result.success(iFileUpload.upload(multipartFile));
    }

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody File file) {
        fileService.saveOrUpdate(file);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        fileService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        fileService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(fileService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(fileService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize) {
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return Result.success(fileService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

}

