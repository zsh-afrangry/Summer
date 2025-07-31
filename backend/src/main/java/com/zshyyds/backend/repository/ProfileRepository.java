package com.zshyyds.backend.repository;

import com.zshyyds.backend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    // 根据用户ID查找资料
    Optional<Profile> findByUserId(Long userId);
    
    // 根据昵称查找资料
    Optional<Profile> findByNickname(String nickname);
    
    // 根据职位查找资料
    List<Profile> findByJobTitle(String jobTitle);
    
    // 根据公司查找资料
    List<Profile> findByCompany(String company);
    
    // 根据地区查找资料
    List<Profile> findByLocation(String location);
    
    // 检查昵称是否存在
    boolean existsByNickname(String nickname);
    
    // 获取有经验年限的资料，按经验排序
    @Query("SELECT p FROM Profile p WHERE p.yearsExperience > 0 ORDER BY p.yearsExperience DESC")
    List<Profile> findByExperienceOrderByYearsDesc();
    
    // 获取项目数量最多的资料
    @Query("SELECT p FROM Profile p WHERE p.projectCount > 0 ORDER BY p.projectCount DESC")
    List<Profile> findByProjectCountOrderByCountDesc();
    
    // 获取文章数量最多的资料
    @Query("SELECT p FROM Profile p WHERE p.articleCount > 0 ORDER BY p.articleCount DESC")
    List<Profile> findByArticleCountOrderByCountDesc();
    
    // 根据关键词搜索资料（昵称、简介、职位）
    @Query("SELECT p FROM Profile p WHERE " +
           "LOWER(p.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "p.bio LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(p.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Profile> searchByKeyword(@Param("keyword") String keyword);
    
    // 统计资料总数
    @Query("SELECT COUNT(p) FROM Profile p")
    Long countProfiles();
} 