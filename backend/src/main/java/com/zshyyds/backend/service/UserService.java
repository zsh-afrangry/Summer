package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.User;

import java.util.List;
import java.util.Optional;

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
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户信息
     */
    Optional<User> findById(Long id);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户名或邮箱查找用户
     * @param identifier 用户名或邮箱
     * @return 用户信息
     */
    Optional<User> findByUsernameOrEmail(String identifier);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    String updateUser(User user);
    
    /**
     * 更新用户状态
     * @param id 用户ID
     * @param status 状态
     * @return 更新结果
     */
    String updateUserStatus(Long id, Integer status);
    
    /**
     * 修改密码
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    String changePassword(Long id, String oldPassword, String newPassword);
    
    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * 检查邮箱是否可用
     * @param email 邮箱
     * @return 是否可用
     */
    boolean isEmailAvailable(String email);
    
    /**
     * 获取活跃用户列表
     * @return 活跃用户列表
     */
    List<User> getActiveUsers();
    
    /**
     * 获取最近注册的用户
     * @param limit 限制数量
     * @return 最近用户列表
     */
    List<User> getRecentUsers(int limit);
    
    /**
     * 统计活跃用户数量
     * @return 用户数量
     */
    Long countActiveUsers();
    
    /**
     * 删除用户（软删除）
     * @param id 用户ID
     * @return 删除结果
     */
    String deleteUser(Long id);
} 