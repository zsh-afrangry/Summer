<template>
  <div class="login-container">
    <div class="login-form">
      <!-- 切换标签 -->
      <div class="tabs">
        <button 
          :class="{ active: currentMode === 'login' }"
          @click="switchMode('login')"
          type="button"
        >
          用户登录
        </button>
        <button 
          :class="{ active: currentMode === 'register' }"
          @click="switchMode('register')"
          type="button"
        >
          用户注册
        </button>
      </div>

      <!-- 登录表单 -->
      <form v-if="currentMode === 'login'" @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名:</label>
          <input 
            type="text" 
            v-model="loginForm.username" 
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="form-group">
          <label>密码:</label>
          <div class="password-input">
            <input 
              :type="showLoginPassword ? 'text' : 'password'"
              v-model="loginForm.password"
              placeholder="请输入密码"
              required
            />
            <span 
              class="eye-icon"
              @click="toggleLoginPasswordVisibility"
            >
              {{ showLoginPassword ? '👁️' : '🙈' }}
            </span>
          </div>
        </div>
        <button type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <!-- 注册表单 -->
      <form v-if="currentMode === 'register'" @submit.prevent="handleRegister">
        <div class="form-group">
          <label>用户名:</label>
          <input 
            type="text" 
            v-model="registerForm.username" 
            placeholder="请输入用户名（至少3个字符）"
            required
            minlength="3"
          />
        </div>
        <div class="form-group">
          <label>密码:</label>
          <div class="password-input">
            <input 
              :type="showRegisterPassword ? 'text' : 'password'"
              v-model="registerForm.password"
              placeholder="请输入密码（至少6个字符）"
              required
              minlength="6"
            />
            <span 
              class="eye-icon"
              @click="toggleRegisterPasswordVisibility"
            >
              {{ showRegisterPassword ? '👁️' : '🙈' }}
            </span>
          </div>
        </div>
        <div class="form-group">
          <label>邮箱 (可选):</label>
          <input 
            type="email" 
            v-model="registerForm.email" 
            placeholder="请输入邮箱地址"
          />
        </div>
        <div class="form-group">
          <label>确认密码:</label>
          <div class="password-input">
            <input 
              :type="showConfirmPassword ? 'text' : 'password'"
              v-model="registerForm.confirmPassword"
              placeholder="请再次输入密码"
              required
            />
            <span 
              class="eye-icon"
              @click="toggleConfirmPasswordVisibility"
            >
              {{ showConfirmPassword ? '👁️' : '🙈' }}
            </span>
          </div>
        </div>
        <button type="submit" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      

    </div>
  </div>
</template>

<script>
import { userApi } from '@/api'
import message from '@/utils/message'

export default {
  name: 'LoginPage',
  data() {
    return {
      currentMode: 'login', // 'login' 或 'register'
      loginForm: {
        username: '',
        password: ''
      },
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        email: '' // 添加邮箱字段
      },
      loading: false,
      // 密码显示状态
      showLoginPassword: false,
      showRegisterPassword: false,
      showConfirmPassword: false
    }
  },
  methods: {
    // 切换模式
    switchMode(mode) {
      this.currentMode = mode
      this.clearForms()
      this.resetPasswordVisibility()
    },

    // 切换登录密码显示状态
    toggleLoginPasswordVisibility() {
      this.showLoginPassword = !this.showLoginPassword
    },

    // 切换注册密码显示状态
    toggleRegisterPasswordVisibility() {
      this.showRegisterPassword = !this.showRegisterPassword
    },

    // 切换确认密码显示状态
    toggleConfirmPasswordVisibility() {
      this.showConfirmPassword = !this.showConfirmPassword
    },

    // 重置密码显示状态
    resetPasswordVisibility() {
      this.showLoginPassword = false
      this.showRegisterPassword = false
      this.showConfirmPassword = false
    },

    // 处理登录
    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        message.error('请填写用户名和密码')
        return
      }
      
      this.loading = true
      
      try {
        const result = await userApi.login(this.loginForm)
        
        if (result.success) {
          message.success(`欢迎 ${result.data.username}！登录成功`)
          
          // 保存用户登录状态
          sessionStorage.setItem('currentUser', result.data.username)
          localStorage.setItem('userToken', result.data.token || 'login-token-' + Date.now())
          localStorage.setItem('userId', result.data.id)
          
          // 跳转到主界面
          setTimeout(() => {
            this.$router.push('/main/home')
          }, 1000)
        } else {
          message.error(result.error || '登录失败')
        }
      } catch (error) {
        console.error('登录错误:', error)
        message.error('网络错误，请检查后端服务是否启动')
      } finally {
        this.loading = false
      }
    },

    // 处理注册
    async handleRegister() {
      // 前端验证
      if (!this.validateRegisterForm()) {
        return
      }
      
      this.loading = true
      
      try {
        const registerData = {
          username: this.registerForm.username,
          password: this.registerForm.password,
          email: this.registerForm.email
        }
        
        const result = await userApi.register(registerData)
        
        if (result.success) {
          message.success(`用户 ${result.data.username} 注册成功！请切换到登录页面`)
          // 3秒后自动切换到登录模式
          setTimeout(() => {
            this.switchMode('login')
          }, 3000)
        } else {
          message.error(result.error || '注册失败')
        }
      } catch (error) {
        console.error('注册错误:', error)
        message.error('网络错误，请检查后端服务是否启动')
      } finally {
        this.loading = false
      }
    },

    // 验证注册表单
    validateRegisterForm() {
      const { username, password, confirmPassword, email } = this.registerForm

      if (!username || !password || !confirmPassword) {
        message.error('请填写所有必填字段')
        return false
      }

      if (username.trim().length < 3) {
        message.error('用户名长度不能少于3个字符')
        return false
      }

      if (password.length < 6) {
        message.error('密码长度不能少于6个字符')
        return false
      }

      if (password !== confirmPassword) {
        message.error('两次输入的密码不一致')
        return false
      }

      // 简单的邮箱格式验证
      if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        message.error('邮箱格式不正确')
        return false
      }

      return true
    },

    // 清空表单
    clearForms() {
      this.loginForm = {
        username: '',
        password: ''
      }
      this.registerForm = {
        username: '',
        password: '',
        confirmPassword: '',
        email: ''
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-form {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

/* 标签切换样式 */
.tabs {
  display: flex;
  margin-bottom: 30px;
  border-bottom: 1px solid #ddd;
}

.tabs button {
  flex: 1;
  padding: 12px;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  transition: all 0.3s ease;
}

.tabs button.active {
  color: #4CAF50;
  border-bottom-color: #4CAF50;
  font-weight: bold;
}

.tabs button:hover {
  color: #4CAF50;
  background-color: #f9f9f9;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  color: #555;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 14px;
}

input:focus {
  outline: none;
  border-color: #4CAF50;
}

/* 密码输入框容器样式 */
.password-input {
  position: relative;
  display: flex;
  align-items: center;
}

.password-input input {
  padding-right: 45px; /* 为眼睛图标留出空间 */
}

/* 眼睛图标样式 */
.eye-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  font-size: 18px;
  user-select: none;
  padding: 2px;
  border-radius: 2px;
  transition: background-color 0.2s ease;
}

.eye-icon:hover {
  background-color: #f0f0f0;
}

button[type="submit"] {
  width: 100%;
  padding: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 10px;
}

button[type="submit"]:hover:not(:disabled) {
  background-color: #45a049;
}

button[type="submit"]:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.success {
  color: #4CAF50;
  background-color: #dff0d8;
  border: 1px solid #d6e9c6;
  padding: 10px;
  border-radius: 4px;
  margin-top: 15px;
}

.error {
  color: #721c24;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  padding: 10px;
  border-radius: 4px;
  margin-top: 15px;
}
</style> 