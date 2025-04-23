package com.example.springboot.service;

public interface UserDao {
    void addUser(String allowAccess);
    void deleteUser();
}
