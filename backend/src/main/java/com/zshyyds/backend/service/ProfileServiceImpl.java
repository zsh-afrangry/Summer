package com.zshyyds.backend.service;

import com.zshyyds.backend.entity.Profile;
import com.zshyyds.backend.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {
    
    private final ProfileRepository profileRepository;
    
    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }
    
    @Override
    public Optional<Profile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }
    
    @Override
    public String saveOrUpdateProfile(Profile profile) {
        if (profile.getUserId() == null) {
            return "用户ID不能为空";
        }
        
        // 验证昵称唯一性（如果提供）
        if (profile.getNickname() != null && !profile.getNickname().trim().isEmpty()) {
            if (!isNicknameAvailable(profile.getNickname(), profile.getUserId())) {
                return "昵称已被使用";
            }
        }
        
        // 验证字段长度
        if (profile.getBio() != null && profile.getBio().length() > 1000) {
            return "个人简介长度不能超过1000字符";
        }
        
        try {
            // 查找是否已存在资料
            Optional<Profile> existingProfile = profileRepository.findByUserId(profile.getUserId());
            
            if (existingProfile.isPresent()) {
                // 更新现有资料
                Profile profileToUpdate = existingProfile.get();
                updateProfileFields(profileToUpdate, profile);
                profileRepository.save(profileToUpdate);
            } else {
                // 创建新资料
                profileRepository.save(profile);
            }
            
            return "success";
        } catch (Exception e) {
            return "保存失败，请重试";
        }
    }
    
    @Override
    public Optional<Profile> getProfileByNickname(String nickname) {
        return profileRepository.findByNickname(nickname);
    }
    
    @Override
    public boolean isNicknameAvailable(String nickname, Long excludeUserId) {
        Optional<Profile> existingProfile = profileRepository.findByNickname(nickname);
        if (!existingProfile.isPresent()) {
            return true;
        }
        
        // 如果存在，检查是否是同一个用户（用于编辑时）
        return existingProfile.get().getUserId().equals(excludeUserId);
    }
    
    @Override
    public List<Profile> getProfilesByJobTitle(String jobTitle) {
        return profileRepository.findByJobTitle(jobTitle);
    }
    
    @Override
    public List<Profile> getProfilesByCompany(String company) {
        return profileRepository.findByCompany(company);
    }
    
    @Override
    public List<Profile> getProfilesByLocation(String location) {
        return profileRepository.findByLocation(location);
    }
    
    @Override
    public List<Profile> searchProfiles(String keyword) {
        return profileRepository.searchByKeyword(keyword);
    }
    
    @Override
    public List<Profile> getProfilesByExperience(int limit) {
        List<Profile> profiles = profileRepository.findByExperienceOrderByYearsDesc();
        return profiles.size() > limit ? profiles.subList(0, limit) : profiles;
    }
    
    @Override
    public List<Profile> getProfilesByProjectCount(int limit) {
        List<Profile> profiles = profileRepository.findByProjectCountOrderByCountDesc();
        return profiles.size() > limit ? profiles.subList(0, limit) : profiles;
    }
    
    @Override
    public List<Profile> getProfilesByArticleCount(int limit) {
        List<Profile> profiles = profileRepository.findByArticleCountOrderByCountDesc();
        return profiles.size() > limit ? profiles.subList(0, limit) : profiles;
    }
    
    @Override
    public String updateArticleCount(Long userId, Integer count) {
        Optional<Profile> profileOptional = profileRepository.findByUserId(userId);
        if (!profileOptional.isPresent()) {
            return "用户资料不存在";
        }
        
        try {
            Profile profile = profileOptional.get();
            profile.setArticleCount(count);
            profileRepository.save(profile);
            return "success";
        } catch (Exception e) {
            return "更新失败";
        }
    }
    
    @Override
    public Long countProfiles() {
        return profileRepository.countProfiles();
    }
    
    @Override
    public String deleteProfile(Long userId) {
        Optional<Profile> profileOptional = profileRepository.findByUserId(userId);
        if (!profileOptional.isPresent()) {
            return "用户资料不存在";
        }
        
        try {
            profileRepository.delete(profileOptional.get());
            return "success";
        } catch (Exception e) {
            return "删除失败";
        }
    }
    
    // 私有辅助方法
    private void updateProfileFields(Profile target, Profile source) {
        if (source.getNickname() != null) {
            target.setNickname(source.getNickname());
        }
        if (source.getRealName() != null) {
            target.setRealName(source.getRealName());
        }
        if (source.getBio() != null) {
            target.setBio(source.getBio());
        }
        if (source.getJobTitle() != null) {
            target.setJobTitle(source.getJobTitle());
        }
        if (source.getCompany() != null) {
            target.setCompany(source.getCompany());
        }
        if (source.getLocation() != null) {
            target.setLocation(source.getLocation());
        }
        if (source.getWebsite() != null) {
            target.setWebsite(source.getWebsite());
        }
        if (source.getGithub() != null) {
            target.setGithub(source.getGithub());
        }
        if (source.getLinkedin() != null) {
            target.setLinkedin(source.getLinkedin());
        }
        if (source.getTwitter() != null) {
            target.setTwitter(source.getTwitter());
        }
        if (source.getMotto() != null) {
            target.setMotto(source.getMotto());
        }
        if (source.getYearsExperience() != null) {
            target.setYearsExperience(source.getYearsExperience());
        }
        if (source.getProjectCount() != null) {
            target.setProjectCount(source.getProjectCount());
        }
        if (source.getCodeCommits() != null) {
            target.setCodeCommits(source.getCodeCommits());
        }
    }
} 