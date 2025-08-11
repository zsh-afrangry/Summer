<template>
  <div class="demo-container">
    <h1>Vue.js 核心概念演示</h1>
    
    <!-- 控制面板 -->
    <div class="control-panel">
      <h3>演示控制面板</h3>
      
      <div class="control-group">
        <label>页面标题 (props):</label>
        <input v-model="title" placeholder="输入自定义标题" />
      </div>
      
      <div class="control-group">
        <label>主题 (props):</label>
        <select v-model="theme">
          <option value="light">明亮主题</option>
          <option value="dark">深色主题</option>
        </select>
      </div>
      
      <div class="control-group">
        <label>显示调试信息 (props):</label>
        <input type="checkbox" v-model="showDebugInfo" />
      </div>
      
      <div class="control-group">
        <label>最大用户名长度 (props):</label>
        <input type="number" v-model.number="maxUsernameLength" min="5" max="50" />
      </div>
    </div>
    
    <!-- Vue.js概念说明 -->
    <div class="concept-explanation">
      <h3>Vue.js 核心概念说明</h3>
      
      <div class="concept-item">
        <h4>📛 name</h4>
        <p>组件名称，用于调试和递归组件。当前组件名: <code>LoginPage</code></p>
      </div>
      
      <div class="concept-item">
        <h4>📊 data()</h4>
        <p>组件的响应式数据。包括表单数据、状态标志、UI状态等。</p>
        <p>示例: <code>currentMode</code>, <code>loginForm</code>, <code>loading</code></p>
      </div>
      
      <div class="concept-item">
        <h4>⬇️ props</h4>
        <p>父组件传递给子组件的数据。支持类型验证、默认值、自定义验证器。</p>
        <p>尝试修改上面的控制面板来看props的效果！</p>
      </div>
      
      <div class="concept-item">
        <h4>🔧 methods</h4>
        <p>组件的方法集合。处理用户交互、业务逻辑等。</p>
        <p>示例: <code>handleLogin</code>, <code>handleRegister</code>, <code>switchMode</code></p>
      </div>
      
      <div class="concept-item">
        <h4>⚡ computed</h4>
        <p>基于依赖进行缓存的计算属性。依赖改变时自动重新计算。</p>
        <p>示例: <code>backgroundTheme</code>, <code>passwordStrengthText</code>, <code>formStatusText</code></p>
      </div>
      
      <div class="concept-item">
        <h4>👁️ watch</h4>
        <p>监听数据变化的观察者。可以执行异步操作或复杂逻辑。</p>
        <p>打开浏览器控制台，输入用户名看watch的效果！</p>
      </div>
      
      <div class="concept-item">
        <h4>🔄 Lifecycle Hooks</h4>
        <p>组件生命周期钩子。在特定时机执行代码。</p>
        <p>打开控制台查看生命周期钩子的执行顺序！</p>
        <ul>
          <li><code>beforeCreate</code> → <code>created</code> → <code>beforeMount</code> → <code>mounted</code></li>
          <li><code>beforeUpdate</code> → <code>updated</code> (数据变化时)</li>
          <li><code>beforeDestroy</code> → <code>destroyed</code> (组件销毁时)</li>
        </ul>
      </div>
    </div>
    
    <!-- 登录组件演示 -->
    <div class="login-demo">
      <h3>LoginPage 组件演示</h3>
      <LoginPage 
        :title="title"
        :theme="theme"
        :show-debug-info="showDebugInfo"
        :max-username-length="maxUsernameLength"
      />
    </div>
  </div>
</template>

<script>
import LoginPage from './LoginPage.vue'

export default {
  name: 'LoginPageDemo',
  components: {
    LoginPage
  },
  data() {
    return {
      // 控制LoginPage组件的props
      title: '用户中心',
      theme: 'light',
      showDebugInfo: true,
      maxUsernameLength: 15
    }
  },
  mounted() {
    console.log('🎉 演示页面已加载！请查看控制台输出来观察Vue.js的各种概念！')
    console.log('📝 提示：')
    console.log('1. 修改控制面板中的值来看props的效果')
    console.log('2. 在登录表单中输入内容来看watch和computed的效果')
    console.log('3. 切换登录/注册模式来看生命周期钩子')
    console.log('4. 查看状态信息区域来了解computed的实时计算')
  }
}
</script>

<style scoped>
.demo-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.demo-container h1 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
}

.control-panel {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  border: 1px solid #e9ecef;
}

.control-panel h3 {
  margin-top: 0;
  color: #495057;
}

.control-group {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.control-group label {
  min-width: 200px;
  font-weight: bold;
  color: #555;
}

.control-group input,
.control-group select {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.control-group input[type="checkbox"] {
  width: auto;
}

.concept-explanation {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.concept-explanation h3 {
  margin-top: 0;
  color: #2c3e50;
  border-bottom: 2px solid #4CAF50;
  padding-bottom: 10px;
}

.concept-item {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #4CAF50;
}

.concept-item h4 {
  margin: 0 0 10px 0;
  color: #2c3e50;
}

.concept-item p {
  margin: 5px 0;
  line-height: 1.6;
  color: #555;
}

.concept-item code {
  background: #e9ecef;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  color: #d63384;
}

.concept-item ul {
  margin: 10px 0;
  padding-left: 20px;
}

.concept-item li {
  margin: 5px 0;
  color: #555;
}

.login-demo {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.login-demo h3 {
  margin-top: 0;
  text-align: center;
  color: #2c3e50;
  border-bottom: 2px solid #4CAF50;
  padding-bottom: 10px;
}
</style>