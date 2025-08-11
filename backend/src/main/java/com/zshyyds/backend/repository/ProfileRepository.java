package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.Profile;
import com.zshyyds.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    /**
     * 根据用户ID查找资料
     */
    Optional<Profile> findByUserId(Long userId);
    
    /**
     * 根据用户对象查找资料
     */
    Optional<Profile> findByUser(User user);
    
    /**
     * 根据昵称查找资料
     */
    Optional<Profile> findByNickname(String nickname);
    
    /**
     * 检查昵称是否存在
     */
    boolean existsByNickname(String nickname);
    
    /**
     * 根据昵称模糊查询
     */
    List<Profile> findByNicknameContainingIgnoreCase(String nickname);
    
    /**
     * 根据职位标题查找
     */
    List<Profile> findByJobTitleContainingIgnoreCase(String jobTitle);
    
    /**
     * 根据公司查找
     */
    List<Profile> findByCompanyContainingIgnoreCase(String company);
    
    /**
     * 根据地址查找
     */
    List<Profile> findByLocationContainingIgnoreCase(String location);
    
    /**
     * 根据工作年限范围查找
     */
    List<Profile> findByYearsExperienceBetween(Integer minYears, Integer maxYears);
    
    /**
     * 获取文章数量排行榜
     */
    @Query("SELECT p FROM Profile p ORDER BY p.articleCount DESC")
    List<Profile> findTopByArticleCount();
    
    /**
     * 获取项目数量排行榜
     */
    @Query("SELECT p FROM Profile p ORDER BY p.projectCount DESC")
    List<Profile> findTopByProjectCount();
    
    /**
     * 更新用户的文章数量
     */
    @Modifying
    @Query("UPDATE Profile p SET p.articleCount = (SELECT COUNT(a) FROM Article a WHERE a.author.id = p.user.id AND a.status = 1) WHERE p.user.id = :userId")
    void updateArticleCount(@Param("userId") Long userId);
    
    /**
     * 批量更新所有用户的文章数量
     */
    @Modifying
    @Query("UPDATE Profile p SET p.articleCount = (SELECT COUNT(a) FROM Article a WHERE a.author.id = p.user.id AND a.status = 1)")
    void updateAllArticleCounts();
    
    /**
     * 根据GitHub地址查找
     */
    Optional<Profile> findByGithub(String github);
    
    /**
     * 根据LinkedIn地址查找
     */
    Optional<Profile> findByLinkedin(String linkedin);
    
    /**
     * 根据个人网站查找
     */
    Optional<Profile> findByWebsite(String website);
    
    /**
     * 获取有个人网站的用户资料
     */
    @Query("SELECT p FROM Profile p WHERE p.website IS NOT NULL AND p.website != ''")
    List<Profile> findWithWebsite();
    
    /**
     * 获取有GitHub的用户资料
     */
    @Query("SELECT p FROM Profile p WHERE p.github IS NOT NULL AND p.github != ''")
    List<Profile> findWithGithub();
    
    /**
     * 统计资料完整度
     */
    @Query("SELECT COUNT(p) FROM Profile p WHERE p.bio IS NOT NULL AND p.jobTitle IS NOT NULL AND p.company IS NOT NULL")
    long countCompleteProfiles();
}