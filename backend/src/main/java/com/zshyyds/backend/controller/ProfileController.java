package com.zshyyds.backend.controller;

import com.zshyyds.backend.common.ApiResponse;
import com.zshyyds.backend.entity.Profile;
import com.zshyyds.backend.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "*")
public class ProfileController {
    
    private final ProfileService profileService;
    
    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }
    
    @GetMapping("/user/{userId}")
    public Map<String, Object> getProfileByUserId(@PathVariable Long userId) {
        try {
            Optional<Profile> profile = profileService.getProfileByUserId(userId);
            if (profile.isPresent()) {
                return ApiResponse.success(profile.get());
            } else {
                return ApiResponse.notFound();
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PostMapping
    public Map<String, Object> createProfile(@RequestBody Profile profile) {
        try {
            String result = profileService.saveOrUpdateProfile(profile);
            
            if ("success".equals(result)) {
                return ApiResponse.success("资料创建成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/user/{userId}")
    public Map<String, Object> updateProfile(@PathVariable Long userId, @RequestBody Profile profile) {
        try {
            profile.setUserId(userId);
            String result = profileService.saveOrUpdateProfile(profile);
            
            if ("success".equals(result)) {
                return ApiResponse.success("资料更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/nickname/{nickname}")
    public Map<String, Object> getProfileByNickname(@PathVariable String nickname) {
        try {
            Optional<Profile> profile = profileService.getProfileByNickname(nickname);
            if (profile.isPresent()) {
                return ApiResponse.success(profile.get());
            } else {
                return ApiResponse.notFound();
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/check/nickname")
    public Map<String, Object> checkNickname(@RequestParam String nickname, 
                                           @RequestParam(required = false) Long excludeUserId) {
        try {
            boolean available = profileService.isNicknameAvailable(nickname, excludeUserId);
            return ApiResponse.success("检查完成", available);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/job/{jobTitle}")
    public Map<String, Object> getProfilesByJobTitle(@PathVariable String jobTitle) {
        try {
            List<Profile> profiles = profileService.getProfilesByJobTitle(jobTitle);
            return ApiResponse.success(profiles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/company/{company}")
    public Map<String, Object> getProfilesByCompany(@PathVariable String company) {
        try {
            List<Profile> profiles = profileService.getProfilesByCompany(company);
            return ApiResponse.success(profiles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/location/{location}")
    public Map<String, Object> getProfilesByLocation(@PathVariable String location) {
        try {
            List<Profile> profiles = profileService.getProfilesByLocation(location);
            return ApiResponse.success(profiles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/search")
    public Map<String, Object> searchProfiles(@RequestParam String keyword) {
        try {
            List<Profile> profiles = profileService.searchProfiles(keyword);
            return ApiResponse.success(profiles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/top/experience")
    public Map<String, Object> getTopProfilesByExperience(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Profile> profiles = profileService.getProfilesByExperience(limit);
            return ApiResponse.success(profiles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/top/projects")
    public Map<String, Object> getTopProfilesByProjectCount(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Profile> profiles = profileService.getProfilesByProjectCount(limit);
            return ApiResponse.success(profiles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/top/articles")
    public Map<String, Object> getTopProfilesByArticleCount(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<Profile> profiles = profileService.getProfilesByArticleCount(limit);
            return ApiResponse.success(profiles);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @PutMapping("/user/{userId}/article-count")
    public Map<String, Object> updateArticleCount(@PathVariable Long userId, @RequestParam Integer count) {
        try {
            String result = profileService.updateArticleCount(userId, count);
            
            if ("success".equals(result)) {
                return ApiResponse.success("文章计数更新成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @GetMapping("/count")
    public Map<String, Object> countProfiles() {
        try {
            Long count = profileService.countProfiles();
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
    
    @DeleteMapping("/user/{userId}")
    public Map<String, Object> deleteProfile(@PathVariable Long userId) {
        try {
            String result = profileService.deleteProfile(userId);
            
            if ("success".equals(result)) {
                return ApiResponse.success("资料删除成功");
            } else {
                return ApiResponse.error(result);
            }
        } catch (Exception e) {
            return ApiResponse.serverError();
        }
    }
} 