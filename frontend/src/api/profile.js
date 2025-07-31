import { request } from './config'

// 用户资料API服务
export const profileApi = {
  // 根据用户ID获取资料
  getProfileByUserId(userId) {
    return request.get(`/api/profiles/user/${userId}`)
  },

  // 创建用户资料
  createProfile(profileData) {
    return request.post('/api/profiles', profileData)
  },

  // 更新用户资料
  updateProfile(userId, profileData) {
    return request.put(`/api/profiles/user/${userId}`, profileData)
  },

  // 根据昵称获取资料
  getProfileByNickname(nickname) {
    return request.get(`/api/profiles/nickname/${nickname}`)
  },

  // 检查昵称是否可用
  checkNickname(nickname, excludeUserId = null) {
    const params = { nickname }
    if (excludeUserId) params.excludeUserId = excludeUserId
    return request.get('/api/profiles/check/nickname', params)
  },

  // 根据职位获取资料
  getProfilesByJobTitle(jobTitle) {
    return request.get(`/api/profiles/job/${jobTitle}`)
  },

  // 根据公司获取资料
  getProfilesByCompany(company) {
    return request.get(`/api/profiles/company/${company}`)
  },

  // 根据地区获取资料
  getProfilesByLocation(location) {
    return request.get(`/api/profiles/location/${location}`)
  },

  // 搜索用户资料
  searchProfiles(keyword) {
    return request.get('/api/profiles/search', { keyword })
  },

  // 获取按经验年限排序的资料
  getTopProfilesByExperience(limit = 10) {
    return request.get('/api/profiles/top/experience', { limit })
  },

  // 获取按项目数量排序的资料
  getTopProfilesByProjectCount(limit = 10) {
    return request.get('/api/profiles/top/projects', { limit })
  },

  // 获取按文章数量排序的资料
  getTopProfilesByArticleCount(limit = 10) {
    return request.get('/api/profiles/top/articles', { limit })
  },

  // 更新文章计数
  updateArticleCount(userId, count) {
    return request.put(`/api/profiles/user/${userId}/article-count`, null, {
      params: { count }
    })
  },

  // 统计资料总数
  countProfiles() {
    return request.get('/api/profiles/count')
  },

  // 删除用户资料
  deleteProfile(userId) {
    return request.delete(`/api/profiles/user/${userId}`)
  }
}

export default profileApi 