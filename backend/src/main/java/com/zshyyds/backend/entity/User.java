package com.zshyyds.backend.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "user")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    public User() {}
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
} 