import { request } from './config'

// 技能API服务
export const skillApi = {
  // 根据用户ID获取技能列表
  getSkillsByUserId(userId) {
    return request.get(`/api/skills/user/${userId}`)
  },

  // 根据用户ID和分类获取技能
  getSkillsByUserIdAndCategory(userId, category) {
    return request.get(`/api/skills/user/${userId}/category/${category}`)
  },

  // 创建技能
  createSkill(skillData) {
    return request.post('/api/skills', skillData)
  },

  // 更新技能
  updateSkill(id, skillData) {
    return request.put(`/api/skills/${id}`, skillData)
  },

  // 批量保存技能
  batchSaveSkills(skills) {
    return request.post('/api/skills/batch', skills)
  },

  // 删除技能
  deleteSkill(id) {
    return request.delete(`/api/skills/${id}`)
  },

  // 删除用户的所有技能
  deleteSkillsByUserId(userId) {
    return request.delete(`/api/skills/user/${userId}`)
  },

  // 获取用户的技能分类列表
  getSkillCategoriesByUserId(userId) {
    return request.get(`/api/skills/user/${userId}/categories`)
  },

  // 获取所有技能分类
  getAllSkillCategories() {
    return request.get('/api/skills/categories')
  },

  // 获取用户技能平均等级
  getAverageSkillLevel(userId) {
    return request.get(`/api/skills/user/${userId}/average-level`)
  },

  // 获取用户最高技能等级
  getMaxSkillLevel(userId) {
    return request.get(`/api/skills/user/${userId}/max-level`)
  },

  // 根据分类统计用户技能数量
  getSkillCountByCategory(userId) {
    return request.get(`/api/skills/user/${userId}/count-by-category`)
  },

  // 检查用户是否已有该技能
  hasSkill(userId, skillName) {
    return request.get(`/api/skills/user/${userId}/has-skill`, { skillName })
  },

  // 获取热门技能统计
  getPopularSkills(limit = 10) {
    return request.get('/api/skills/popular', { limit })
  },

  // 根据技能等级范围查找技能
  getSkillsByLevelRange(userId, minLevel, maxLevel) {
    return request.get(`/api/skills/user/${userId}/level-range`, {
      minLevel,
      maxLevel
    })
  },

  // 更新技能排序
  updateSkillOrder(skillIds) {
    return request.put('/api/skills/order', skillIds)
  },

  // 根据技能名称搜索
  searchSkillsByName(skillName) {
    return request.get('/api/skills/search', { skillName })
  }
}

export default skillApi 