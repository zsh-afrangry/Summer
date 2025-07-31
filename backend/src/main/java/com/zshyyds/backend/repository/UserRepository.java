package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 根据用户名查找用户
    Optional<User> findByUsername(String username);
    
    // 根据邮箱查找用户
    Optional<User> findByEmail(String email);
    
    // 根据用户名和密码查找用户
    Optional<User> findByUsernameAndPassword(String username, String password);
    
    // 根据用户名或邮箱查找用户
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);
    
    // 根据状态查找用户
    List<User> findByStatus(Integer status);
    
    // 查找正常状态的用户
    List<User> findByStatusOrderByCreatedAtDesc(Integer status);
    
    // 根据创建时间范围查找用户
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 检查用户名是否存在
    boolean existsByUsername(String username);
    
    // 检查邮箱是否存在
    boolean existsByEmail(String email);
    
    // 统计用户总数
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1")
    Long countActiveUsers();
    
    // 获取最近注册的用户
    @Query("SELECT u FROM User u WHERE u.status = 1 ORDER BY u.createdAt DESC")
    List<User> findRecentUsers();
} 