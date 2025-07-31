import { request } from './config'

// 标签API服务
export const tagApi = {
  // 根据ID获取标签
  getTagById(id) {
    return request.get(`/api/tags/${id}`)
  },

  // 根据名称获取标签
  getTagByName(name) {
    return request.get(`/api/tags/name/${name}`)
  },

  // 创建标签
  createTag(tagData) {
    return request.post('/api/tags', tagData)
  },

  // 更新标签
  updateTag(id, tagData) {
    return request.put(`/api/tags/${id}`, tagData)
  },

  // 删除标签
  deleteTag(id) {
    return request.delete(`/api/tags/${id}`)
  },

  // 获取所有标签
  getAllTags() {
    return request.get('/api/tags')
  },

  // 获取所有标签（按名称排序）
  getAllTagsOrderByName() {
    return request.get('/api/tags/ordered/name')
  },

  // 获取所有标签（按排序顺序）
  getAllTagsOrderBySortOrder() {
    return request.get('/api/tags/ordered/sort')
  },

  // 获取热门标签
  getPopularTags(limit = 10) {
    return request.get('/api/tags/popular', { limit })
  },

  // 根据文章数量获取标签
  getTagsByArticleCount(minCount) {
    return request.get(`/api/tags/article-count/${minCount}`)
  },

  // 搜索标签
  searchTags(keyword) {
    return request.get('/api/tags/search', { keyword })
  },

  // 根据文章ID获取标签
  getTagsByArticleId(articleId) {
    return request.get(`/api/tags/article/${articleId}`)
  },

  // 获取标签云数据
  getTagCloudData() {
    return request.get('/api/tags/cloud')
  },

  // 更新标签的文章数量
  updateTagArticleCount(id) {
    return request.put(`/api/tags/${id}/article-count`)
  },

  // 批量更新所有标签的文章数量
  updateAllTagArticleCounts() {
    return request.put('/api/tags/article-count/all')
  },

  // 检查标签名是否可用
  checkTagName(name, excludeId = null) {
    const params = { name }
    if (excludeId) params.excludeId = excludeId
    return request.get('/api/tags/check/name', params)
  },

  // 统计标签总数
  countTags() {
    return request.get('/api/tags/count')
  },

  // 统计有文章的标签数量
  countTagsWithArticles() {
    return request.get('/api/tags/count/with-articles')
  },

  // 获取未使用的标签
  getUnusedTags() {
    return request.get('/api/tags/unused')
  },

  // 删除未使用的标签
  deleteUnusedTags() {
    return request.delete('/api/tags/unused')
  },

  // 根据颜色获取标签
  getTagsByColor(color) {
    return request.get(`/api/tags/color/${color}`)
  },

  // 创建标签（如果不存在）
  createTagIfNotExists(name, color = '#667eea', description = '') {
    return request.post('/api/tags/create-if-not-exists', null, {
      params: { name, color, description }
    })
  }
}

export default tagApi 