package com.zshyyds.backend.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "article")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 500)
    private String summary;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    
    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1; // 1-发布，0-草稿，-1-删除
    
    @Column(name = "read_time", columnDefinition = "INT DEFAULT 0")
    private Integer readTime = 0; // 预估阅读时间(分钟)
    
    @Column(name = "cover_image", length = 255)
    private String coverImage;
    
    @Column(name = "is_top", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isTop = 0; // 是否置顶：1-是，0-否
    
    @Column(name = "allow_comment", columnDefinition = "TINYINT DEFAULT 1")
    private Integer allowComment = 1; // 是否允许评论：1-是，0-否
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (status == 1 && publishedAt == null) {
            publishedAt = now;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == 1 && publishedAt == null) {
            publishedAt = LocalDateTime.now();
        }
    }
    
    // 建立与User的关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User author;
    
    // 建立与Tag的多对多关联关系
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "article_tag",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
    
    // 建立与ArticleStats的一对一关联关系
    @OneToOne(mappedBy = "article", fetch = FetchType.LAZY)
    private ArticleStats stats;
    
    public Article() {}
    
    public Article(String title, String summary, String content, Long authorId) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.authorId = authorId;
    }
} 