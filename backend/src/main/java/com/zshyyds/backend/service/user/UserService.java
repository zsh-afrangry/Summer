package com.zshyyds.backend.service.user;

import com.zshyyds.backend.entity.user.User;

public interface UserService {
    
    /**
     * 用户登录验证
     * @param user 用户信息
     * @return 是否登录成功
     */
    boolean login(User user);
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果消息
     */
    String register(User user);
} 