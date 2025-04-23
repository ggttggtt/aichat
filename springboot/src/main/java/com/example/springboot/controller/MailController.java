package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.impl.MailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: MailController
 * @Description: TODO
 * @Author: 代刘斌
 * @Date: 2023/10/11 - 10 - 11 - 16:18
 * @version: 1.0
 **/
@RestController
@RequestMapping("/email")
public class MailController {
    @Autowired
    private MailServiceImpl mailService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/send/{email}")
    public Result sendEmail(@PathVariable String email) {
        //key 邮箱号 value 验证码
        String code = redisTemplate.opsForValue().get(email);
        //从redis获取验证码，如果获取到，则返回(因为用户可能在短时间内多次请求,那么就没必要重复去生成验证码)
        if(!StringUtils.isEmpty(code)) {
            return Result.success();
        }
        //如果没有，则生成6位随机验证码
        code = mailService.randomCode();
        //调用service方法，通过邮箱服务发送
        boolean isSend = mailService.sendMail(email,code);
        //生成的验证码放到redis里面，设置有效时间为5分钟
        if(isSend) {
            redisTemplate.opsForValue().set(email,code,5, TimeUnit.MINUTES);
            return Result.success();
        }else {
            return Result.error();
        }
    }
}
