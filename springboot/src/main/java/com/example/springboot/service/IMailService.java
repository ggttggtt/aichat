package com.example.springboot.service;


public interface IMailService {

    //发送邮件
    boolean sendMail(String email, String code);

    String randomCode();

}
