package com.zshyyds.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "profile")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Profile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(length = 50)
    private String nickname;
    
    @Column(name = "real_name", length = 50)
    private String realName;
    
    @Column(columnDefinition = "TEXT")
    private String bio; // 个人简介
    
    @Column(name = "job_title", length = 100)
    private String jobTitle;
    
    @Column(length = 100)
    private String company;
    
    @Column(length = 100)
    private String location;
    
    @Column(length = 255)
    private String website;
    
    @Column(length = 255)
    private String github;
    
    @Column(length = 255)
    private String linkedin;
    
    @Column(length = 255)
    private String twitter;
    
    @Column(length = 255)
    private String motto; // 个人座右铭
    
    @Column(name = "years_experience", columnDefinition = "INT DEFAULT 0")
    private Integer yearsExperience = 0;
    
    @Column(name = "project_count", columnDefinition = "INT DEFAULT 0")
    private Integer projectCount = 0;
    
    @Column(name = "article_count", columnDefinition = "INT DEFAULT 0")
    private Integer articleCount = 0;
    
    @Column(name = "code_commits", columnDefinition = "INT DEFAULT 0")
    private Integer codeCommits = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 建立与User的关联关系
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;
    
    public Profile() {}
    
    public Profile(Long userId, String nickname, String bio) {
        this.userId = userId;
        this.nickname = nickname;
        this.bio = bio;
    }
} 