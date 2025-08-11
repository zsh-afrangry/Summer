package com.zshyyds.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user", schema = "my_blog")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "avatar", length = 255)
    private String avatar;
    
    @ColumnDefault("1")
    @Column(name = "status")
    private Byte status;
    
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;
    
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    // 自定义构造函数 - 用于简单创建用户
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = (byte) 1; // 默认正常状态
    }
    
    // 自定义构造函数 - 包含邮箱
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.status = (byte) 1; // 默认正常状态
    }
} 