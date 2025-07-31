import { request } from './config'

// 用户API服务
export const userApi = {
  // 用户登录
  login(userData) {
    return request.post('/api/users/login', userData)
  },

  // 用户注册
  register(userData) {
    return request.post('/api/users/register', userData)
  },

  // 根据ID获取用户信息
  getUserById(id) {
    return request.get(`/api/users/${id}`)
  },

  // 根据用户名获取用户信息
  getUserByUsername(username) {
    return request.get(`/api/users/username/${username}`)
  },

  // 更新用户信息
  updateUser(id, userData) {
    return request.put(`/api/users/${id}`, userData)
  },

  // 更新用户状态
  updateUserStatus(id, status) {
    return request.put(`/api/users/${id}/status`, null, { params: { status } })
  },

  // 修改密码
  changePassword(id, oldPassword, newPassword) {
    return request.put(`/api/users/${id}/password`, null, {
      params: { oldPassword, newPassword }
    })
  },

  // 检查用户名是否可用
  checkUsername(username) {
    return request.get('/api/users/check/username', { username })
  },

  // 检查邮箱是否可用
  checkEmail(email) {
    return request.get('/api/users/check/email', { email })
  },

  // 获取活跃用户列表
  getActiveUsers() {
    return request.get('/api/users/active')
  },

  // 获取最近注册用户
  getRecentUsers(limit = 10) {
    return request.get('/api/users/recent', { limit })
  },

  // 统计活跃用户数量
  countActiveUsers() {
    return request.get('/api/users/count')
  },

  // 删除用户
  deleteUser(id) {
    return request.delete(`/api/users/${id}`)
  }
}

export default userApi 