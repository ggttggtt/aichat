package com.example.springboot.service.impl;

import com.example.springboot.service.IMailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Random;

/**
 * @ClassName: MailServiceImpl
 * @Description: TODO
 * @Author: 代刘斌
 * @Date: 2023/10/10 - 10 - 10 - 21:53
 * @version: 1.0
 **/
@Service
public class MailServiceImpl implements IMailService {

    @Resource
    private JavaMailSender mailSender;//一定要用@Autowired

    //application.properties中已配置的值
    @Value("${spring.mail.username}")
    private String username;

    @Override
    public boolean sendMail(String email, String code) {
        //判断邮箱是否为空
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        //标题
        String subject = "邮箱验证码";
        //正文内容
        String text = "你的验证码为" + code + "，有效时间为5分钟，请尽快使用并且不要告诉别人。";

        SimpleMailMessage msg = new SimpleMailMessage();
        //发送邮件的邮箱
        msg.setFrom(username);
        //发送到哪(邮箱)
        msg.setTo(email);
        //邮箱标题
        msg.setSubject(subject);
        //邮箱文本
        msg.setText(text);
        try {
            mailSender.send(msg);
            System.out.println("msg=====>" + msg);
        } catch (MailException ex) {
            ex.getMessage();
        }
        return true;
    }

    /**
     * 随机生成6位数的验证码
     * @return String code
     */
    @Override
    public String randomCode(){
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}
