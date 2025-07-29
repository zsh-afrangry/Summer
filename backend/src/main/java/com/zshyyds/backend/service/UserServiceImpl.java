package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.User;
import com.zshyyds.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    // 构造函数注入
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean login(User user) {
        Optional<User> existingUser = userRepository.findByUsernameAndPassword(
            user.getUsername(), 
            user.getPassword()
        );
        return existingUser.isPresent();
    }
    
    @Override
    public String register(User user) {
        // 检查用户名和密码是否为空
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return "用户名不能为空";
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return "密码不能为空";
        }
        
        // 检查用户名长度
        if (user.getUsername().trim().length() < 3) {
            return "用户名长度不能少于3个字符";
        }
        
        // 检查密码长度
        if (user.getPassword().length() < 6) {
            return "密码长度不能少于6个字符";
        }
        
        // 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername().trim());
        if (existingUser.isPresent()) {
            return "用户名已存在";
        }
        
        try {
            // 创建新用户
            User newUser = new User(user.getUsername().trim(), user.getPassword());
            userRepository.save(newUser);
            return "success";
        } catch (Exception e) {
            return "注册失败，请重试";
        }
    }
} 