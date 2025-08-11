<template>
  <div class="login-container" :style="{ backgroundColor: backgroundTheme }">
    <div class="login-form">
      <!-- 页面标题 (使用props) -->
      <h2 class="page-title">{{ title }}</h2>
      
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
      
      <!-- 状态信息显示 (使用computed) -->
      <div class="status-info" v-if="showDebugInfo">
        <p>当前模式: {{ currentMode }}</p>
        <p>表单状态: {{ formStatusText }}</p>
        <p>用户名长度: {{ currentUsernameLength }}</p>
        <p>密码强度: {{ passwordStrengthText }}</p>
        <p>组件挂载时间: {{ mountTime }}</p>
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
      
      <div v-if="message" :class="messageType">
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  // ========== name 属性演示 ==========
  name: 'LoginPage',
  
  // ========== props 属性演示 ==========
  props: {
    // 页面标题
    title: {
      type: String,
      default: '用户中心'
    },
    // 背景主题色
    theme: {
      type: String,
      default: 'light',
      validator(value) {
        return ['light', 'dark'].includes(value)
      }
    },
    // 是否显示调试信息
    showDebugInfo: {
      type: Boolean,
      default: false
    },
    // 最大用户名长度
    maxUsernameLength: {
      type: Number,
      default: 20
    }
  },
  
  // ========== data() 数据属性演示 ==========
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
        confirmPassword: ''
      },
      loading: false,
      message: '',
      messageType: '',
      // 密码显示状态
      showLoginPassword: false,
      showRegisterPassword: false,
      showConfirmPassword: false,
      // 用于演示的额外数据
      mountTime: '',
      userInputCount: 0,
      lastInputTime: null
    }
  },
  
  // ========== computed 计算属性演示 ==========
  computed: {
    // 根据主题返回背景色
    backgroundTheme() {
      return this.theme === 'dark' ? '#2c3e50' : '#f5f5f5'
    },
    
    // 当前用户名长度（根据模式动态计算）
    currentUsernameLength() {
      const username = this.currentMode === 'login' 
        ? this.loginForm.username 
        : this.registerForm.username
      return username.length
    },
    
    // 表单状态文本
    formStatusText() {
      if (this.loading) {
        return '处理中...'
      }
      if (this.currentMode === 'login') {
        return this.loginForm.username && this.loginForm.password ? '可以登录' : '请填写完整'
      } else {
        return this.isRegisterFormValid ? '可以注册' : '请填写完整'
      }
    },
    
    // 注册表单是否有效
    isRegisterFormValid() {
      const { username, password, confirmPassword } = this.registerForm
      return username.length >= 3 && 
             password.length >= 6 && 
             password === confirmPassword
    },
    
    // 密码强度计算
    passwordStrengthText() {
      const password = this.currentMode === 'login' 
        ? this.loginForm.password 
        : this.registerForm.password
      
      if (password.length === 0) return '未输入'
      if (password.length < 6) return '弱'
      if (password.length < 10) return '中等'
      return '强'
    },
    
    // 用户名是否超长
    isUsernameTooLong() {
      return this.currentUsernameLength > this.maxUsernameLength
    }
  },
  
  // ========== watch 监听器演示 ==========
  watch: {
    // 监听当前模式变化
    currentMode: {
      handler(newMode, oldMode) {
        console.log(`模式从 ${oldMode} 切换到 ${newMode}`)
        this.userInputCount = 0 // 重置输入计数
      },
      immediate: false
    },
    
    // 监听登录表单用户名变化
    'loginForm.username': {
      handler(newUsername, oldUsername) {
        console.log(`登录用户名变化: ${oldUsername} -> ${newUsername}`)
        this.userInputCount++
        this.lastInputTime = new Date().toLocaleTimeString()
        
        // 用户名长度检查
        if (newUsername.length > this.maxUsernameLength) {
          this.showMessage(`用户名不能超过${this.maxUsernameLength}个字符`, 'error')
        }
      },
      immediate: false
    },
    
    // 监听注册表单用户名变化
    'registerForm.username': {
      handler(newUsername) {
        console.log(`注册用户名变化: ${newUsername}`)
        this.userInputCount++
        this.lastInputTime = new Date().toLocaleTimeString()
        
        if (newUsername.length > this.maxUsernameLength) {
          this.showMessage(`用户名不能超过${this.maxUsernameLength}个字符`, 'error')
        }
      }
    },
    
    // 监听密码强度变化
    passwordStrengthText: {
      handler(newStrength) {
        if (newStrength === '强') {
          console.log('密码强度很好！')
        } else if (newStrength === '弱') {
          console.log('建议使用更强的密码')
        }
      }
    },
    
    // 监听loading状态变化
    loading: {
      handler(newLoading) {
        console.log(`加载状态变化: ${newLoading}`)
        if (newLoading) {
          console.log('开始处理请求...')
        } else {
          console.log('请求处理完成')
        }
      }
    }
  },
  
  // ========== methods 方法演示 ==========
  methods: {
    // 切换模式
    switchMode(mode) {
      this.currentMode = mode
      this.clearMessage()
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
        this.showMessage('请填写用户名和密码', 'error')
        return
      }
      
      this.loading = true
      this.message = ''
      
      try {
        const response = await axios.post('/api/login', this.loginForm)
        const result = response.data
        
        if (result.code === 200) {
          this.showMessage(`欢迎 ${result.data}！登录成功`, 'success')
          
          // 保存用户登录状态
          sessionStorage.setItem('currentUser', result.data)
          localStorage.setItem('userToken', 'login-token-' + Date.now())
          
          // 跳转到主界面
          setTimeout(() => {
            this.$router.push('/main/home')
          }, 1000)
        } else {
          this.showMessage(result.message || '登录失败', 'error')
        }
      } catch (error) {
        console.error('登录错误:', error)
        this.showMessage('网络错误，请检查后端服务是否启动', 'error')
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
      this.message = ''
      
      try {
        const response = await axios.post('/api/register', {
          username: this.registerForm.username,
          password: this.registerForm.password
        })
        const result = response.data
        
        if (result.code === 200) {
          this.showMessage(`用户 ${result.data} 注册成功！请切换到登录页面`, 'success')
          // 3秒后自动切换到登录模式
          setTimeout(() => {
            this.switchMode('login')
          }, 3000)
        } else {
          this.showMessage(result.message || '注册失败', 'error')
        }
      } catch (error) {
        console.error('注册错误:', error)
        this.showMessage('网络错误，请检查后端服务是否启动', 'error')
      } finally {
        this.loading = false
      }
    },

    // 验证注册表单
    validateRegisterForm() {
      const { username, password, confirmPassword } = this.registerForm

      if (!username || !password || !confirmPassword) {
        this.showMessage('请填写所有字段', 'error')
        return false
      }

      if (username.trim().length < 3) {
        this.showMessage('用户名长度不能少于3个字符', 'error')
        return false
      }

      if (password.length < 6) {
        this.showMessage('密码长度不能少于6个字符', 'error')
        return false
      }

      if (password !== confirmPassword) {
        this.showMessage('两次输入的密码不一致', 'error')
        return false
      }

      return true
    },

    // 显示消息
    showMessage(text, type) {
      this.message = text
      this.messageType = type
      // 3秒后清除消息
      setTimeout(() => {
        this.message = ''
      }, 3000)
    },

    // 清除消息
    clearMessage() {
      this.message = ''
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
        confirmPassword: ''
      }
    }
  },
  
  // ========== Lifecycle Hooks 生命周期钩子演示 ==========
  
  // 创建前
  beforeCreate() {
    console.log('🔄 beforeCreate: 组件实例刚被创建，数据观测和事件配置都还没有')
  },
  
  // 创建完成
  created() {
    console.log('✅ created: 组件实例创建完成，数据观测、属性计算、方法、事件回调都已设置')
    console.log('此时还没有挂载到DOM，$el属性还不存在')
    
    // 可以在这里进行数据初始化
    this.mountTime = new Date().toLocaleString()
    console.log('设置挂载时间:', this.mountTime)
  },
  
  // 挂载前
  beforeMount() {
    console.log('🔄 beforeMount: 模板编译完成，即将挂载到DOM')
    console.log('此时虚拟DOM已经创建完成，即将替换真实DOM')
  },
  
  // 挂载完成
  mounted() {
    console.log('✅ mounted: 组件已挂载到DOM')
    console.log('此时可以访问DOM元素，进行DOM操作')
    console.log('组件的$el属性可用:', !!this.$el)
    
    // 挂载后的操作示例
    const formElement = this.$el.querySelector('.login-form')
    if (formElement) {
      console.log('找到表单元素，宽度:', formElement.offsetWidth + 'px')
    }
    
    // 模拟一些挂载后的初始化工作
    setTimeout(() => {
      console.log('模拟异步初始化工作完成')
    }, 1000)
  },
  
  // 更新前
  beforeUpdate() {
    console.log('🔄 beforeUpdate: 数据发生变化，DOM即将重新渲染')
    console.log('此时数据已经更新，但DOM还没有重新渲染')
  },
  
  // 更新完成
  updated() {
    console.log('✅ updated: DOM重新渲染完成')
    console.log('此时数据和DOM都已经更新')
    console.log('当前模式:', this.currentMode)
  },
  
  // 激活 (用于keep-alive组件)
  activated() {
    console.log('✅ activated: 组件被激活 (keep-alive)')
    console.log('当组件从缓存中恢复时触发')
  },
  
  // 停用 (用于keep-alive组件)  
  deactivated() {
    console.log('⏸️ deactivated: 组件被停用 (keep-alive)')
    console.log('当组件被缓存时触发')
  },
  
  // 卸载前 (Vue 3: beforeUnmount, Vue 2: beforeDestroy)
  beforeUnmount() {
    console.log('🔄 beforeUnmount: 组件即将被卸载')
    console.log('此时实例仍然完全可用')
    
    // 清理工作：移除事件监听器、清除定时器等
    console.log('执行清理工作...')
  },
  
  // 卸载完成 (Vue 3: unmounted, Vue 2: destroyed)
  unmounted() {
    console.log('💀 unmounted: 组件已被卸载')
    console.log('所有事件监听器被移除，子组件也被卸载')
  },
  
  // 错误捕获 (Vue 2.5+)
  errorCaptured(err, instance, info) {
    console.error('❌ errorCaptured: 捕获到子组件错误')
    console.error('错误:', err)
    console.error('错误信息:', info)
    // 返回false可以阻止错误继续向上传播
    return false
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

/* 页面标题样式 */
.page-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 24px;
  font-weight: bold;
}

/* 状态信息样式 */
.status-info {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 20px;
  font-size: 12px;
  color: #666;
}

.status-info p {
  margin: 5px 0;
  display: flex;
  justify-content: space-between;
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