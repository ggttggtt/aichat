package com.example.springboot;/**
 * @Auther: 代刘斌
 * @Date: 2023/9/18 - 09 - 18 - 16:21
 * @Description: com.example.springboot
 * @version: 1.0
 */


import com.example.springboot.service.IUserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: Test
 * @Description: TODO
 * @Author: 代刘斌
 * @Date: 2023/9/18 - 09 - 18 - 16:21
 * @version: 1.0
 **/

@RestController
@RequestMapping("/user")
public class test {

    //这里引入的是User对象具体的实现接口对象
    @Autowired
    private IUserService userService;

    @Test
    public void testAddition () {
        System.out.println("这是junit单元测试");
    }
}
