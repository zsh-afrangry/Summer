package com.zshyyds.backend.controller;

import com.zshyyds.backend.entity.User;
import com.zshyyds.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // 允许跨域
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        
        boolean success = userService.login(user);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", user.getUsername());
        } else {
            result.put("code", 400);
            result.put("message", "用户名或密码错误");
            result.put("data", null);
        }
        
        return result;
    }
    
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String registerResult = userService.register(user);
            
            if ("success".equals(registerResult)) {
                result.put("code", 200);
                result.put("message", "注册成功");
                result.put("data", user.getUsername());
            } else {
                result.put("code", 400);
                result.put("message", registerResult);
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "服务器内部错误");
            result.put("data", null);
        }
        
        return result;
    }
} 