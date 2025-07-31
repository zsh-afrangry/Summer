import { request } from './config'

// 系统配置API服务
export const systemConfigApi = {
  // 根据配置键获取配置
  getConfigByKey(configKey) {
    return request.get(`/api/system-config/${configKey}`)
  },

  // 获取配置值
  getConfigValue(configKey, defaultValue = null) {
    const params = defaultValue ? { defaultValue } : {}
    return request.get(`/api/system-config/${configKey}/value`, params)
  },

  // 获取字符串配置值
  getStringValue(configKey) {
    return request.get(`/api/system-config/${configKey}/string`)
  },

  // 获取整数配置值
  getIntValue(configKey) {
    return request.get(`/api/system-config/${configKey}/int`)
  },

  // 获取布尔配置值
  getBooleanValue(configKey) {
    return request.get(`/api/system-config/${configKey}/boolean`)
  },

  // 创建配置
  createConfig(configData) {
    return request.post('/api/system-config', configData)
  },

  // 更新配置
  updateConfig(configData) {
    return request.put('/api/system-config', configData)
  },

  // 更新配置值
  updateConfigValue(configKey, configValue) {
    return request.put(`/api/system-config/${configKey}`, null, {
      params: { configValue }
    })
  },

  // 批量更新配置
  batchUpdateConfigs(configs) {
    return request.put('/api/system-config/batch', configs)
  },

  // 删除配置
  deleteConfig(configKey) {
    return request.delete(`/api/system-config/${configKey}`)
  },

  // 根据分类获取配置
  getConfigsByCategory(category) {
    return request.get(`/api/system-config/category/${category}`)
  },

  // 根据类型获取配置
  getConfigsByType(configType) {
    return request.get(`/api/system-config/type/${configType}`)
  },

  // 获取可编辑的配置
  getEditableConfigs() {
    return request.get('/api/system-config/editable')
  },

  // 获取网站配置
  getSiteConfigs() {
    return request.get('/api/system-config/site')
  },

  // 获取功能配置
  getFeatureConfigs() {
    return request.get('/api/system-config/feature')
  },

  // 获取管理员配置
  getAdminConfigs() {
    return request.get('/api/system-config/admin')
  },

  // 获取所有配置分类
  getAllCategories() {
    return request.get('/api/system-config/categories')
  },

  // 获取所有配置类型
  getAllConfigTypes() {
    return request.get('/api/system-config/types')
  },

  // 搜索配置
  searchConfigs(keyword) {
    return request.get('/api/system-config/search', { keyword })
  },

  // 检查配置是否存在
  configExists(configKey) {
    return request.get(`/api/system-config/exists/${configKey}`)
  },

  // 统计配置总数
  countConfigs() {
    return request.get('/api/system-config/count')
  },

  // 统计可编辑配置数量
  countEditableConfigs() {
    return request.get('/api/system-config/count/editable')
  },

  // 根据分类统计配置数量
  countConfigsByCategory() {
    return request.get('/api/system-config/stats/category')
  },

  // 根据类型统计配置数量
  countConfigsByType() {
    return request.get('/api/system-config/stats/type')
  },

  // 初始化默认配置
  initializeDefaultConfigs() {
    return request.post('/api/system-config/initialize')
  },

  // 检查布尔配置是否启用
  isBooleanConfigEnabled(configKey) {
    return request.get(`/api/system-config/${configKey}/enabled`)
  }
}

export default systemConfigApi 