// API模块统一导出
export { default as userApi } from './user'
export { default as articleApi } from './article'
export { default as tagApi } from './tag'
export { default as profileApi } from './profile'
export { default as skillApi } from './skill'
export { default as statisticsApi } from './statistics'
export { default as systemConfigApi } from './systemConfig'

// 导出通用配置
export { request, apiClient, API_BASE_URL } from './config'

// 便捷的统一导出
export const api = {
  user: require('./user').default,
  article: require('./article').default,
  tag: require('./tag').default,
  profile: require('./profile').default,
  skill: require('./skill').default,
  statistics: require('./statistics').default,
  systemConfig: require('./systemConfig').default
}

export default api 