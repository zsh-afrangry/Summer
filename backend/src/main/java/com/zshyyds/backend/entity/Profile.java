package com.zshyyds.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "profile", schema = "my_blog")
public class Profile {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Lob
    @Column(name = "bio")
    private String bio;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "company", length = 100)
    private String company;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "website")
    private String website;

    @Column(name = "github")
    private String github;

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "twitter")
    private String twitter;

    @Column(name = "motto")
    private String motto;

    @ColumnDefault("0")
    @Column(name = "years_experience")
    private Integer yearsExperience;

    @ColumnDefault("0")
    @Column(name = "project_count")
    private Integer projectCount;

    @ColumnDefault("0")
    @Column(name = "article_count")
    private Integer articleCount;

    @ColumnDefault("0")
    @Column(name = "code_commits")
    private Integer codeCommits;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}