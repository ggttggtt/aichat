package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;



/**
 * @Auther: 代刘斌
 * @Date: 2023/6/3 - 06 - 03 - 13:03
 * @Description: com.example.springboot.mapper
 * @version: 1.0
 */

@Mapper
@Component
public interface UserMapper extends BaseMapper<User> {
    Page<User> findPage(Page<User> page, @Param("username") String username, @Param("email") String email, @Param("address") String address);
}
