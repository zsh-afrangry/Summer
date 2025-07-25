package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 根据用户名查找用户
    Optional<User> findByUsername(String username);
    
    // 根据用户名和密码查找用户
    Optional<User> findByUsernameAndPassword(String username, String password);
} 