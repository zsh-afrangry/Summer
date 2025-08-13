<template>
  <div class="data-sender">
    <div class="header">
      <h2>📤 数据发送组件 (DataSender)</h2>
      <p class="subtitle">演示各种Vue数据传递方式</p>
    </div>

    <!-- 方式1: Props传递 -->
    <div class="demo-section">
      <h3>1️⃣ Props 传递 (父→子)</h3>
      <div class="controls">
        <label>输入要传递的消息:</label>
        <input v-model="messageForProps" placeholder="通过props传递的消息" class="input-field">
      </div>
      <div class="info">
        <span class="label">当前值:</span>
        <span class="value">{{ messageForProps }}</span>
      </div>
    </div>

    <!-- 方式2: Emit传递 -->
    <div class="demo-section">
      <h3>2️⃣ Emit 事件 (子→父)</h3>
      <div class="controls">
        <button @click="sendEmitMessage" class="btn primary">触发Emit事件</button>
      </div>
      <div class="info">
        <span class="label">从子组件接收到:</span>
        <span class="value">{{ receivedFromChild }}</span>
      </div>
    </div>

    <!-- 方式3: sessionStorage -->
    <div class="demo-section">
      <h3>3️⃣ sessionStorage 传递</h3>
      <div class="controls">
        <input v-model="sessionData" placeholder="存储到sessionStorage" class="input-field">
        <button @click="saveToSessionStorage" class="btn success">保存到Session</button>
        <button @click="loadFromSessionStorage" class="btn info">从Session读取</button>
      </div>
      <div class="info">
        <span class="label">Session中的值:</span>
        <span class="value">{{ sessionStorageValue }}</span>
      </div>
    </div>

    <!-- 方式4: localStorage -->
    <div class="demo-section">
      <h3>4️⃣ localStorage 传递</h3>
      <div class="controls">
        <input v-model="localData" placeholder="存储到localStorage" class="input-field">
        <button @click="saveToLocalStorage" class="btn success">保存到Local</button>
        <button @click="loadFromLocalStorage" class="btn info">从Local读取</button>
        <button @click="clearLocalStorage" class="btn danger">清除Local</button>
      </div>
      <div class="info">
        <span class="label">Local中的值:</span>
        <span class="value">{{ localStorageValue }}</span>
      </div>
    </div>

    <!-- 方式5: URL参数 -->
    <div class="demo-section">
      <h3>5️⃣ URL 参数传递</h3>
      <div class="controls">
        <input v-model="urlParam" placeholder="URL参数值" class="input-field">
        <button @click="updateUrlParams" class="btn warning">更新URL参数</button>
        <button @click="readUrlParams" class="btn info">读取URL参数</button>
      </div>
      <div class="info">
        <span class="label">URL参数值:</span>
        <span class="value">{{ urlParameterValue }}</span>
      </div>
    </div>

    <!-- 方式6: Vuex/Pinia 状态管理 -->
    <div class="demo-section">
      <h3>6️⃣ 全局状态管理 (模拟)</h3>
      <div class="controls">
        <input v-model="globalStateData" placeholder="全局状态数据" class="input-field">
        <button @click="updateGlobalState" class="btn primary">更新全局状态</button>
      </div>
      <div class="info">
        <span class="label">全局状态值:</span>
        <span class="value">{{ $globalState.message }}</span>
      </div>
    </div>

    <!-- 方式7: provide/inject -->
    <div class="demo-section">
      <h3>7️⃣ Provide/Inject 传递</h3>
      <div class="controls">
        <input v-model="providedData" placeholder="通过provide传递的数据" class="input-field">
        <button @click="updateProvidedData" class="btn success">更新Provide数据</button>
      </div>
      <div class="info">
        <span class="label">Provide的值:</span>
        <span class="value">{{ providedMessage }}</span>
      </div>
    </div>

    <!-- 嵌入子组件 -->
    <div class="child-component-wrapper">
      <h3>🎯 子组件演示区域</h3>
      <DataReceiver 
        :propsMessage="messageForProps"
        :sessionKey="'demo-session-key'"
        :localKey="'demo-local-key'"
        @childMessage="handleChildMessage"
        @childData="handleChildData"
      />
    </div>

    <!-- 数据传递总结 -->
    <div class="summary-section">
      <h3>📋 数据传递方式总结</h3>
      <div class="summary-grid">
        <div class="summary-item">
          <div class="method">Props</div>
          <div class="description">父组件向子组件传递数据</div>
          <div class="usage">单向数据流，响应式</div>
        </div>
        <div class="summary-item">
          <div class="method">Emit</div>
          <div class="description">子组件向父组件传递事件</div>
          <div class="usage">事件驱动，解耦合</div>
        </div>
        <div class="summary-item">
          <div class="method">sessionStorage</div>
          <div class="description">会话级别的本地存储</div>
          <div class="usage">页面关闭后清除</div>
        </div>
        <div class="summary-item">
          <div class="method">localStorage</div>
          <div class="description">持久化的本地存储</div>
          <div class="usage">手动清除或过期</div>
        </div>
        <div class="summary-item">
          <div class="method">URL参数</div>
          <div class="description">通过URL传递简单数据</div>
          <div class="usage">可分享、可收藏</div>
        </div>
        <div class="summary-item">
          <div class="method">全局状态</div>
          <div class="description">应用级别的状态管理</div>
          <div class="usage">跨组件共享状态</div>
        </div>
        <div class="summary-item">
          <div class="method">Provide/Inject</div>
          <div class="description">祖先向后代组件传递</div>
          <div class="usage">跨层级组件通信</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import DataReceiver from './DataReceiver.vue'

// 模拟全局状态管理
const globalState = {
  message: '初始全局状态',
  updateMessage(newMessage) {
    this.message = newMessage
  }
}

export default {
  name: 'DataSender',
  components: {
    DataReceiver
  },
  provide() {
    return {
      providedMessage: () => this.providedMessage,
      updateProvidedMessage: this.updateProvidedMessage
    }
  },
  data() {
    return {
      // Props相关
      messageForProps: 'Hello from Parent!',
      
      // Emit相关
      receivedFromChild: '等待子组件发送消息...',
      
      // Storage相关
      sessionData: '',
      localData: '',
      sessionStorageValue: '',
      localStorageValue: '',
      
      // URL参数相关
      urlParam: '',
      urlParameterValue: '',
      
      // 全局状态相关
      globalStateData: '',
      
      // Provide/Inject相关
      providedData: '',
      providedMessage: '通过provide传递的初始消息'
    }
  },
  mounted() {
    // 组件挂载时读取存储的值
    this.loadFromSessionStorage()
    this.loadFromLocalStorage()
    this.readUrlParams()
    
    // 设置全局状态
    this.$globalState = globalState
  },
  methods: {
    // Emit事件处理
    sendEmitMessage() {
      // 这里模拟发送消息，实际上会在子组件中触发
      console.log('准备接收子组件的emit事件...')
    },
    
    handleChildMessage(message) {
      this.receivedFromChild = message
      console.log('收到子组件消息:', message)
    },
    
    handleChildData(data) {
      console.log('收到子组件数据:', data)
    },
    
    // sessionStorage操作
    saveToSessionStorage() {
      if (this.sessionData.trim()) {
        sessionStorage.setItem('demo-session-key', this.sessionData)
        this.loadFromSessionStorage()
        console.log('数据已保存到sessionStorage:', this.sessionData)
      }
    },
    
    loadFromSessionStorage() {
      this.sessionStorageValue = sessionStorage.getItem('demo-session-key') || '暂无数据'
    },
    
    // localStorage操作
    saveToLocalStorage() {
      if (this.localData.trim()) {
        localStorage.setItem('demo-local-key', this.localData)
        this.loadFromLocalStorage()
        console.log('数据已保存到localStorage:', this.localData)
      }
    },
    
    loadFromLocalStorage() {
      this.localStorageValue = localStorage.getItem('demo-local-key') || '暂无数据'
    },
    
    clearLocalStorage() {
      localStorage.removeItem('demo-local-key')
      this.loadFromLocalStorage()
      console.log('localStorage数据已清除')
    },
    
    // URL参数操作
    updateUrlParams() {
      if (this.urlParam.trim()) {
        const url = new URL(window.location)
        url.searchParams.set('demoParam', this.urlParam)
        window.history.pushState({}, '', url)
        this.readUrlParams()
        console.log('URL参数已更新:', this.urlParam)
      }
    },
    
    readUrlParams() {
      const urlParams = new URLSearchParams(window.location.search)
      this.urlParameterValue = urlParams.get('demoParam') || '无URL参数'
    },
    
    // 全局状态管理
    updateGlobalState() {
      if (this.globalStateData.trim()) {
        this.$globalState.updateMessage(this.globalStateData)
        console.log('全局状态已更新:', this.globalStateData)
      }
    },
    
    // Provide/Inject操作
    updateProvidedData() {
      if (this.providedData.trim()) {
        this.providedMessage = this.providedData
        console.log('Provide数据已更新:', this.providedData)
      }
    },
    
    updateProvidedMessage(newMessage) {
      this.providedMessage = newMessage
    }
  }
}
</script>

<style scoped>
.data-sender {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

.header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.08);
}

.header h2 {
  color: #2c3e50;
  margin: 0 0 10px 0;
  font-size: 2em;
}

.subtitle {
  color: #7f8c8d;
  margin: 0;
  font-size: 1.1em;
}

.demo-section {
  background: white;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 10px;
  box-shadow: 0 3px 10px rgba(0,0,0,0.1);
  border-left: 4px solid #3498db;
}

.demo-section h3 {
  color: #2c3e50;
  margin: 0 0 15px 0;
  font-size: 1.3em;
}

.controls {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.input-field {
  flex: 1;
  min-width: 200px;
  padding: 10px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.input-field:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.2);
}

.btn.primary {
  background: #3498db;
  color: white;
}

.btn.success {
  background: #2ecc71;
  color: white;
}

.btn.info {
  background: #17a2b8;
  color: white;
}

.btn.warning {
  background: #f39c12;
  color: white;
}

.btn.danger {
  background: #e74c3c;
  color: white;
}

.info {
  background: #f8f9fa;
  padding: 12px 15px;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.label {
  font-weight: 600;
  color: #495057;
  margin-right: 10px;
}

.value {
  color: #28a745;
  font-family: 'Courier New', monospace;
  background: #e8f5e8;
  padding: 2px 8px;
  border-radius: 4px;
}

.child-component-wrapper {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  margin: 30px 0;
  border: 2px dashed #dee2e6;
}

.child-component-wrapper h3 {
  color: #495057;
  margin: 0 0 20px 0;
  text-align: center;
}

.summary-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 25px;
  border-radius: 15px;
  margin-top: 30px;
  color: white;
}

.summary-section h3 {
  color: white;
  margin: 0 0 20px 0;
  text-align: center;
  font-size: 1.5em;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 15px;
}

.summary-item {
  background: rgba(255,255,255,0.1);
  padding: 15px;
  border-radius: 10px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.2);
}

.method {
  font-weight: bold;
  font-size: 1.1em;
  margin-bottom: 8px;
  color: #fff3cd;
}

.description {
  font-size: 0.9em;
  margin-bottom: 5px;
  opacity: 0.9;
}

.usage {
  font-size: 0.8em;
  opacity: 0.7;
  font-style: italic;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .data-sender {
    padding: 15px;
    margin: 10px;
  }
  
  .controls {
    flex-direction: column;
  }
  
  .input-field {
    min-width: unset;
  }
  
  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>