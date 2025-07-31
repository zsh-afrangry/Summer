package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.User;
import com.zshyyds.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean login(User user) {
        // 先根据用户名查找用户
        Optional<User> existingUserOpt = userRepository.findByUsername(user.getUsername());
        
        if (existingUserOpt.isEmpty()) {
            return false;
        }
        
        User existingUser = existingUserOpt.get();
        String inputPassword = user.getPassword();
        String storedPassword = existingUser.getPassword();
        
        // 检查密码是否匹配
        // 如果存储的密码是BCrypt格式（以$2a$开头），使用BCrypt验证
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            // TODO: 这里需要添加BCrypt验证，暂时用简单比较
            // 由于没有BCrypt依赖，我们暂时返回false让用户使用明文密码的账户
            return false;
        } else {
            // 明文密码比较
            return storedPassword.equals(inputPassword);
        }
    }
    
    @Override
    public String register(User user) {
        System.out.println("注册服务 - 接收到用户对象: " + user);
        System.out.println("用户名: '" + user.getUsername() + "', 密码: '" + user.getPassword() + "', 邮箱: '" + user.getEmail() + "'");
        
        // 检查用户名和密码是否为空
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            System.out.println("用户名为空或null");
            return "用户名不能为空";
        }
        
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            System.out.println("密码为空或null: " + user.getPassword());
            return "密码不能为空";
        }
        
        // 检查邮箱格式（如果提供）
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            if (!isValidEmail(user.getEmail())) {
                return "邮箱格式不正确";
            }
            // 检查邮箱是否已存在
            if (userRepository.existsByEmail(user.getEmail().trim())) {
                return "邮箱已被使用";
            }
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
        if (userRepository.existsByUsername(user.getUsername().trim())) {
            return "用户名已存在";
        }
        
        try {
            // 创建新用户
            User newUser = new User(user.getUsername().trim(), user.getPassword(), user.getEmail());
            System.out.println("保存新用户: " + newUser);
            userRepository.save(newUser);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "注册失败，请重试";
        }
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public Optional<User> findByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier);
    }
    
    @Override
    public String updateUser(User user) {
        if (user.getId() == null) {
            return "用户ID不能为空";
        }
        
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (!existingUser.isPresent()) {
            return "用户不存在";
        }
        
        try {
            User userToUpdate = existingUser.get();
            
            // 更新用户名（如果提供且不同）
            if (user.getUsername() != null && !user.getUsername().equals(userToUpdate.getUsername())) {
                if (userRepository.existsByUsername(user.getUsername())) {
                    return "用户名已存在";
                }
                userToUpdate.setUsername(user.getUsername());
            }
            
            // 更新邮箱（如果提供且不同）
            if (user.getEmail() != null && !user.getEmail().equals(userToUpdate.getEmail())) {
                if (!isValidEmail(user.getEmail())) {
                    return "邮箱格式不正确";
                }
                if (userRepository.existsByEmail(user.getEmail())) {
                    return "邮箱已被使用";
                }
                userToUpdate.setEmail(user.getEmail());
            }
            
            // 更新头像
            if (user.getAvatar() != null) {
                userToUpdate.setAvatar(user.getAvatar());
            }
            
            userRepository.save(userToUpdate);
            return "success";
        } catch (Exception e) {
            return "更新失败，请重试";
        }
    }
    
    @Override
    public String updateUserStatus(Long id, Integer status) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return "用户不存在";
        }
        
        try {
            User user = userOptional.get();
            user.setStatus(status);
            userRepository.save(user);
            return "success";
        } catch (Exception e) {
            return "状态更新失败";
        }
    }
    
    @Override
    public String changePassword(Long id, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return "用户不存在";
        }
        
        User user = userOptional.get();
        if (!user.getPassword().equals(oldPassword)) {
            return "原密码不正确";
        }
        
        if (newPassword.length() < 6) {
            return "新密码长度不能少于6个字符";
        }
        
        try {
            user.setPassword(newPassword);
            userRepository.save(user);
            return "success";
        } catch (Exception e) {
            return "密码修改失败";
        }
    }
    
    @Override
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    @Override
    public List<User> getActiveUsers() {
        return userRepository.findByStatus(1);
    }
    
    @Override
    public List<User> getRecentUsers(int limit) {
        List<User> users = userRepository.findRecentUsers();
        return users.size() > limit ? users.subList(0, limit) : users;
    }
    
    @Override
    public Long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
    
    @Override
    public String deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return "用户不存在";
        }
        
        try {
            User user = userOptional.get();
            user.setStatus(0); // 软删除，设置状态为禁用
            userRepository.save(user);
            return "success";
        } catch (Exception e) {
            return "删除失败";
        }
    }
    
    // 私有辅助方法
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
} 