package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.User;
import com.zshyyds.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    

    
    // 用户登录验证
    public boolean login(User user) {
        Optional<User> existingUser = userRepository.findByUsernameAndPassword(
            user.getUsername(), 
            user.getPassword()
        );
        return existingUser.isPresent();
    }
    

} 