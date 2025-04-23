package com.example.springboot.service.impl;

import com.example.springboot.service.UserDao;
import org.springframework.stereotype.Component;

/**
 * @ClassName: UserDaoImpl
 * @Description: TODO
 * @Author: 代刘斌
 * @Date: 2023/10/9 - 10 - 09 - 16:02
 * @version: 1.0
 **/
@Component("userDaoImpl")
public class UserDaoImpl implements UserDao {
    @Override
    public void addUser(String allowAccess) {
        System.out.println("添加用户");
    }

    @Override
    public void deleteUser() {
        System.out.println("删除用户");
    }
}
