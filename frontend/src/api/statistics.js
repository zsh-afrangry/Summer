import { request } from './config'

// 统计API服务
export const statisticsApi = {
  // 获取概览统计
  getOverviewStatistics() {
    return request.get('/api/statistics/overview')
  },

  // 获取仪表板数据
  getDashboardData() {
    return request.get('/api/statistics/dashboard')
  },

  // 获取文章统计
  getArticleStatistics() {
    return request.get('/api/statistics/articles')
  },

  // 获取标签统计
  getTagStatistics() {
    return request.get('/api/statistics/tags')
  },

  // 获取用户统计
  getUserStatistics() {
    return request.get('/api/statistics/users')
  },

  // 获取系统统计
  getSystemStatistics() {
    return request.get('/api/statistics/system')
  },

  // 获取用户技能统计
  getUserSkillStatistics(userId) {
    return request.get(`/api/statistics/skills/user/${userId}`)
  },

  // 获取站点信息
  getSiteInfo() {
    return request.get('/api/statistics/site-info')
  },

  // 获取最近活动
  getRecentActivities() {
    return request.get('/api/statistics/recent-activities')
  }
}

export default statisticsApi 