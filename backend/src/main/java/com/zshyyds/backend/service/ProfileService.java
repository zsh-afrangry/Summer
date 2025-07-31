package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileService {
    
    /**
     * 根据用户ID获取资料
     * @param userId 用户ID
     * @return 用户资料
     */
    Optional<Profile> getProfileByUserId(Long userId);
    
    /**
     * 创建或更新用户资料
     * @param profile 用户资料
     * @return 操作结果
     */
    String saveOrUpdateProfile(Profile profile);
    
    /**
     * 根据昵称查找资料
     * @param nickname 昵称
     * @return 用户资料
     */
    Optional<Profile> getProfileByNickname(String nickname);
    
    /**
     * 检查昵称是否可用
     * @param nickname 昵称
     * @param excludeUserId 排除的用户ID（用于编辑时排除自己）
     * @return 是否可用
     */
    boolean isNicknameAvailable(String nickname, Long excludeUserId);
    
    /**
     * 根据职位查找资料
     * @param jobTitle 职位
     * @return 资料列表
     */
    List<Profile> getProfilesByJobTitle(String jobTitle);
    
    /**
     * 根据公司查找资料
     * @param company 公司
     * @return 资料列表
     */
    List<Profile> getProfilesByCompany(String company);
    
    /**
     * 根据地区查找资料
     * @param location 地区
     * @return 资料列表
     */
    List<Profile> getProfilesByLocation(String location);
    
    /**
     * 搜索用户资料
     * @param keyword 关键词
     * @return 搜索结果
     */
    List<Profile> searchProfiles(String keyword);
    
    /**
     * 获取按经验年限排序的资料
     * @param limit 限制数量
     * @return 资料列表
     */
    List<Profile> getProfilesByExperience(int limit);
    
    /**
     * 获取按项目数量排序的资料
     * @param limit 限制数量
     * @return 资料列表
     */
    List<Profile> getProfilesByProjectCount(int limit);
    
    /**
     * 获取按文章数量排序的资料
     * @param limit 限制数量
     * @return 资料列表
     */
    List<Profile> getProfilesByArticleCount(int limit);
    
    /**
     * 更新文章计数
     * @param userId 用户ID
     * @param count 文章数量
     * @return 更新结果
     */
    String updateArticleCount(Long userId, Integer count);
    
    /**
     * 统计资料总数
     * @return 资料数量
     */
    Long countProfiles();
    
    /**
     * 删除用户资料
     * @param userId 用户ID
     * @return 删除结果
     */
    String deleteProfile(Long userId);
} 