package com.zshyyds.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "article_stats")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ArticleStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "article_id", nullable = false, unique = true)
    private Long articleId;
    
    @Column(name = "view_count", columnDefinition = "INT DEFAULT 0")
    private Integer viewCount = 0;
    
    @Column(name = "like_count", columnDefinition = "INT DEFAULT 0")
    private Integer likeCount = 0;
    
    @Column(name = "comment_count", columnDefinition = "INT DEFAULT 0")
    private Integer commentCount = 0;
    
    @Column(name = "share_count", columnDefinition = "INT DEFAULT 0")
    private Integer shareCount = 0;
    
    @Column(name = "collect_count", columnDefinition = "INT DEFAULT 0")
    private Integer collectCount = 0;
    
    @Column(name = "last_view_at")
    private LocalDateTime lastViewAt;
    
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
    
    // 建立与Article的关联关系
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Article article;
    
    public ArticleStats() {}
    
    public ArticleStats(Long articleId) {
        this.articleId = articleId;
    }
    
    // 便捷方法：增加浏览量
    public void incrementViewCount() {
        this.viewCount++;
        this.lastViewAt = LocalDateTime.now();
    }
    
    // 便捷方法：增加点赞量
    public void incrementLikeCount() {
        this.likeCount++;
    }
    
    // 便捷方法：减少点赞量
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
} 