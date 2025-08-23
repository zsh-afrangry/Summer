package com.zshyyds.backend.service.user;

import com.zshyyds.backend.entity.user.User;
import com.zshyyds.backend.repository.user.UserRepository;
import com.zshyyds.backend.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    //如果在字段上加上@Autowired，就表示这里的依赖注入方式 是 注释注入，而不是Setter注入或者构造函数注入
    private final UserRepository userRepository;
    
    // 构造函数注入
    @Autowired // 加上注释就表示，我希望Spring用这个构造函数来实现依赖注入，而不是别的构造函数
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean login(User user) {
        // 1. 根据用户名查找用户
        Optional<User> existingUserOpt = userRepository.findByUsername(user.getUsername());
        
        if (existingUserOpt.isEmpty()) {
            return false; // 用户不存在
        }
        
        User existingUser = existingUserOpt.get();
        
        // 2. 验证密码：将输入的明文密码进行MD5加密后与数据库中的哈希值比较
        return MD5Util.verify(user.getPassword(), existingUser.getPassword());
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
            // 对密码进行MD5加密
            String hashedPassword = MD5Util.encrypt(user.getPassword());
            
            // 创建新用户，存储加密后的密码
            User newUser = new User(user.getUsername().trim(), hashedPassword);
            userRepository.save(newUser);
            return "success";
        } catch (Exception e) {
            return "注册失败，请重试";
        }
    }
} 