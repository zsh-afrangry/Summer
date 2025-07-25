<template>
  <div class="login-container">
    <div class="login-form">
      <h2>用户登录</h2>
      <form @submit.prevent="handleLogin">
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
          <input 
            type="text"
            v-model="loginForm.password"
            placeholder="请输入密码"
            required
          />
        </div>
        <button type="submit" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
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
  name: 'UserLogin',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loading: false,
      message: '',
      messageType: ''
    }
  },
  methods: {
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
          // 这里可以跳转到其他页面或保存用户状态
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
    
    showMessage(text, type) {
      this.message = text
      this.messageType = type
      // 3秒后清除消息
      setTimeout(() => {
        this.message = ''
      }, 3000)
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

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
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

button {
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

button:hover:not(:disabled) {
  background-color: #45a049;
}

button:disabled {
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