package com.zshyyds.backend.controller;

import com.zshyyds.backend.common.ApiResponse;
import com.zshyyds.backend.entity.User;
import com.zshyyds.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        try {
            System.out.println("登录请求 - 用户名: " + user.getUsername() + ", 密码: " + user.getPassword());
            boolean success = userService.login(user);
            
            if (success) {
                // 获取完整用户信息
                Optional<User> userInfo = userService.findByUsername(user.getUsername());
                return ApiResponse.success("登录成功", userInfo.orElse(null));
            } else {
                return ApiResponse.error("用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.serverError();
        }
    }
    
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        try {
            System.out.println("注册请求 - 用户名: " + user.getUsername() + ", 密码: " + user.getPassword() + ", 邮箱: " + user.getEmail());
            String registerResult = userService.register(user);
            
            if ("success".equals(registerResult)) {
                return ApiResponse.success("注册成功", user.getUsername());
            } else {
                return ApiResponse.error(registerResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                return ApiResponse.success(user.get());
            } else {
                return ApiResponse.notFound();
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/username/{username}")
    public Map<String, Object> getUserByUsername(@PathVariable String username) {
        try {
            Optional<User> user = userService.findByUsername(username);
            if (user.isPresent()) {
                return ApiResponse.success(user.get());
            } else {
                return ApiResponse.notFound();
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            String result = userService.updateUser(user);
            
            if ("success".equals(result)) {
                return ApiResponse.success("更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{id}/status")
    public Map<String, Object> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            String result = userService.updateUserStatus(id, status);
            
            if ("success".equals(result)) {
                return ApiResponse.success("状态更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/{id}/password")
    public Map<String, Object> changePassword(@PathVariable Long id, 
                                            @RequestParam String oldPassword, 
                                            @RequestParam String newPassword) {
        try {
            String result = userService.changePassword(id, oldPassword, newPassword);
            
            if ("success".equals(result)) {
                return ApiResponse.success("密码修改成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/check/username")
    public Map<String, Object> checkUsername(@RequestParam String username) {
        try {
            boolean available = userService.isUsernameAvailable(username);
            return ApiResponse.success("检查完成", available);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/check/email")
    public Map<String, Object> checkEmail(@RequestParam String email) {
        try {
            boolean available = userService.isEmailAvailable(email);
            return ApiResponse.success("检查完成", available);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/active")
    public Map<String, Object> getActiveUsers() {
        try {
            List<User> users = userService.getActiveUsers();
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/recent")
    public Map<String, Object> getRecentUsers(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<User> users = userService.getRecentUsers(limit);
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/count")
    public Map<String, Object> countActiveUsers() {
        try {
            Long count = userService.countActiveUsers();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        try {
            String result = userService.deleteUser(id);
            
            if ("success".equals(result)) {
                return ApiResponse.success("删除成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
}