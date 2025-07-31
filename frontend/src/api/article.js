import { request } from './config'

// 文章API服务
export const articleApi = {
  // 根据ID获取文章
  getArticleById(id) {
    return request.get(`/api/articles/${id}`)
  },

  // 创建文章
  createArticle(articleData) {
    return request.post('/api/articles', articleData)
  },

  // 更新文章
  updateArticle(id, articleData) {
    return request.put(`/api/articles/${id}`, articleData)
  },

  // 发布文章
  publishArticle(id) {
    return request.put(`/api/articles/${id}/publish`)
  },

  // 撤回文章
  unpublishArticle(id) {
    return request.put(`/api/articles/${id}/unpublish`)
  },

  // 删除文章
  deleteArticle(id) {
    return request.delete(`/api/articles/${id}`)
  },

  // 获取作者的文章
  getArticlesByAuthor(authorId) {
    return request.get(`/api/articles/author/${authorId}`)
  },

  // 根据状态获取文章（分页）
  getArticlesByStatus(status, page = 0, size = 10) {
    return request.get(`/api/articles/status/${status}`, { page, size })
  },

  // 获取已发布文章（分页）
  getPublishedArticles(page = 0, size = 10) {
    return request.get('/api/articles/published', { page, size })
  },

  // 获取置顶文章
  getTopArticles() {
    return request.get('/api/articles/top')
  },

  // 设置文章置顶
  setArticleTop(id, isTop) {
    return request.put(`/api/articles/${id}/top`, null, { params: { isTop } })
  },

  // 搜索文章
  searchArticles(keyword, page = 0, size = 10) {
    return request.get('/api/articles/search', { keyword, page, size })
  },

  // 根据标签获取文章
  getArticlesByTag(tagId, page = 0, size = 10) {
    return request.get(`/api/articles/tag/${tagId}`, { page, size })
  },

  // 根据标签名获取文章
  getArticlesByTagName(tagName) {
    return request.get(`/api/articles/tag-name/${tagName}`)
  },

  // 获取最新文章
  getLatestArticles(limit = 5) {
    return request.get('/api/articles/latest', { limit })
  },

  // 获取推荐文章
  getRecommendedArticles(limit = 5) {
    return request.get('/api/articles/recommended', { limit })
  },

  // 获取热门文章
  getPopularArticles(limit = 5) {
    return request.get('/api/articles/popular', { limit })
  },

  // 获取相关文章
  getRelatedArticles(id, limit = 5) {
    return request.get(`/api/articles/${id}/related`, { limit })
  },

  // 增加文章浏览量
  incrementViewCount(id) {
    return request.post(`/api/articles/${id}/view`)
  },

  // 统计已发布文章数量
  countPublishedArticles() {
    return request.get('/api/articles/count/published')
  },

  // 统计作者文章数量
  countArticlesByAuthor(authorId) {
    return request.get(`/api/articles/count/author/${authorId}`)
  },

  // 获取月度文章统计
  getMonthlyArticleStats() {
    return request.get('/api/articles/stats/monthly')
  },

  // 获取作者文章统计
  getAuthorArticleStats() {
    return request.get('/api/articles/stats/author')
  }
}

export default articleApi 