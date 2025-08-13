<template>
  <div class="data-receiver">
    <div class="header">
      <h3>📥 数据接收组件 (DataReceiver)</h3>
      <p class="subtitle">演示各种方式接收和处理数据</p>
    </div>

    <!-- 方式1: 接收Props -->
    <div class="demo-section">
      <h4>1️⃣ 接收 Props 数据</h4>
      <div class="data-display">
        <span class="label">从父组件接收到:</span>
        <span class="value">{{ propsMessage }}</span>
      </div>
      <div class="props-info">
        <small>
          <strong>实时响应:</strong> 当父组件的messageForProps改变时，这里会自动更新
        </small>
      </div>
    </div>

    <!-- 方式2: 发送Emit事件 -->
    <div class="demo-section">
      <h4>2️⃣ 发送 Emit 事件</h4>
      <div class="controls">
        <input v-model="messageToParent" placeholder="发送给父组件的消息" class="input-field">
        <button @click="sendToParent" class="btn primary">发送消息</button>
        <button @click="sendComplexData" class="btn success">发送复杂数据</button>
      </div>
      <div class="emit-counter">
        <span class="label">已发送消息次数:</span>
        <span class="counter">{{ emitCount }}</span>
      </div>
    </div>

    <!-- 方式3: 读取sessionStorage -->
    <div class="demo-section">
      <h4>3️⃣ 读取 sessionStorage</h4>
      <div class="controls">
        <button @click="readSessionStorage" class="btn info">刷新读取</button>
        <button @click="modifySessionStorage" class="btn warning">修改Session数据</button>
      </div>
      <div class="data-display">
        <span class="label">Session存储的值:</span>
        <span class="value">{{ sessionValue }}</span>
      </div>
      <div class="storage-info">
        <small>
          <strong>生命周期:</strong> 标签页关闭后自动清除
        </small>
      </div>
    </div>

    <!-- 方式4: 读取localStorage -->
    <div class="demo-section">
      <h4>4️⃣ 读取 localStorage</h4>
      <div class="controls">
        <button @click="readLocalStorage" class="btn info">刷新读取</button>
        <button @click="modifyLocalStorage" class="btn warning">修改Local数据</button>
      </div>
      <div class="data-display">
        <span class="label">Local存储的值:</span>
        <span class="value">{{ localValue }}</span>
      </div>
      <div class="storage-info">
        <small>
          <strong>持久化:</strong> 手动清除或浏览器清理前一直存在
        </small>
      </div>
    </div>

    <!-- 方式5: 监听URL变化 -->
    <div class="demo-section">
      <h4>5️⃣ 监听 URL 参数</h4>
      <div class="controls">
        <button @click="readUrlParams" class="btn info">读取URL参数</button>
        <button @click="modifyUrlParams" class="btn warning">修改URL参数</button>
      </div>
      <div class="data-display">
        <span class="label">URL参数值:</span>
        <span class="value">{{ urlParamValue }}</span>
      </div>
      <div class="url-info">
        <small>
          <strong>特点:</strong> 可分享、可收藏、刷新页面后依然存在
        </small>
      </div>
    </div>

    <!-- 方式6: 全局状态读取 -->
    <div class="demo-section">
      <h4>6️⃣ 读取全局状态</h4>
      <div class="controls">
        <button @click="readGlobalState" class="btn info">刷新读取</button>
        <button @click="modifyGlobalState" class="btn warning">修改全局状态</button>
      </div>
      <div class="data-display">
        <span class="label">全局状态值:</span>
        <span class="value">{{ globalStateValue }}</span>
      </div>
      <div class="global-info">
        <small>
          <strong>共享性:</strong> 所有组件都可以访问和修改
        </small>
      </div>
    </div>

    <!-- 方式7: inject接收数据 -->
    <div class="demo-section">
      <h4>7️⃣ Inject 接收数据</h4>
      <div class="controls">
        <button @click="refreshInjectData" class="btn info">刷新数据</button>
        <button @click="updateInjectData" class="btn warning">更新Provide数据</button>
      </div>
      <div class="data-display">
        <span class="label">Inject接收到:</span>
        <span class="value">{{ injectedMessage }}</span>
      </div>
      <div class="inject-info">
        <small>
          <strong>跨层级:</strong> 可以跨越多层组件传递数据
        </small>
      </div>
    </div>

    <!-- 实时数据监控 -->
    <div class="monitor-section">
      <h4>📊 实时数据监控</h4>
      <div class="monitor-grid">
        <div class="monitor-item">
          <div class="monitor-label">Props变化次数</div>
          <div class="monitor-value">{{ propsChangeCount }}</div>
        </div>
        <div class="monitor-item">
          <div class="monitor-label">Emit发送次数</div>
          <div class="monitor-value">{{ emitCount }}</div>
        </div>
        <div class="monitor-item">
          <div class="monitor-label">Storage读取次数</div>
          <div class="monitor-value">{{ storageReadCount }}</div>
        </div>
        <div class="monitor-item">
          <div class="monitor-label">URL参数读取次数</div>
          <div class="monitor-value">{{ urlReadCount }}</div>
        </div>
      </div>
    </div>

    <!-- 数据处理演示 -->
    <div class="processing-section">
      <h4>⚙️ 数据处理演示</h4>
      <div class="processing-demo">
        <div class="input-data">
          <h5>输入数据:</h5>
          <textarea v-model="rawData" placeholder="输入JSON格式的数据" class="json-input"></textarea>
          <button @click="processData" class="btn primary">处理数据</button>
        </div>
        <div class="output-data">
          <h5>处理结果:</h5>
          <pre class="json-output">{{ processedData }}</pre>
        </div>
      </div>
    </div>

    <!-- 事件日志 -->
    <div class="log-section">
      <h4>📝 事件日志</h4>
      <div class="log-controls">
        <button @click="clearLogs" class="btn danger">清空日志</button>
        <button @click="exportLogs" class="btn success">导出日志</button>
      </div>
      <div class="log-container">
        <div v-for="(log, index) in eventLogs" :key="index" class="log-item">
          <span class="log-time">{{ log.time }}</span>
          <span class="log-type" :class="log.type">{{ log.type }}</span>
          <span class="log-message">{{ log.message }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DataReceiver',
  props: {
    propsMessage: {
      type: String,
      default: '默认Props消息'
    },
    sessionKey: {
      type: String,
      default: 'demo-session-key'
    },
    localKey: {
      type: String,
      default: 'demo-local-key'
    }
  },
  inject: {
    providedMessage: {
      default: () => () => '无Provide数据'
    },
    updateProvidedMessage: {
      default: () => () => {}
    }
  },
  data() {
    return {
      // Emit相关
      messageToParent: '',
      emitCount: 0,
      
      // Storage相关
      sessionValue: '',
      localValue: '',
      storageReadCount: 0,
      
      // URL相关
      urlParamValue: '',
      urlReadCount: 0,
      
      // 全局状态相关
      globalStateValue: '',
      
      // Inject相关
      injectedMessage: '',
      
      // 监控数据
      propsChangeCount: 0,
      
      // 数据处理
      rawData: '{\n  "name": "测试数据",\n  "value": 123,\n  "items": [1, 2, 3]\n}',
      processedData: '',
      
      // 事件日志
      eventLogs: []
    }
  },
  watch: {
    // 监听Props变化
    propsMessage: {
      handler(newVal, oldVal) {
        if (oldVal !== undefined) {
          this.propsChangeCount++
          this.addLog('PROPS', `Props消息变化: ${oldVal} → ${newVal}`)
        }
      },
      immediate: true
    }
  },
  mounted() {
    this.initializeData()
    this.addLog('INIT', '组件已挂载，开始初始化数据')
  },
  methods: {
    // 初始化数据
    initializeData() {
      this.readSessionStorage()
      this.readLocalStorage()
      this.readUrlParams()
      this.readGlobalState()
      this.refreshInjectData()
      this.addLog('INIT', '所有数据源初始化完成')
    },
    
    // Emit事件方法
    sendToParent() {
      if (this.messageToParent.trim()) {
        this.$emit('childMessage', this.messageToParent)
        this.emitCount++
        this.addLog('EMIT', `发送消息给父组件: ${this.messageToParent}`)
        this.messageToParent = ''
      }
    },
    
    sendComplexData() {
      const complexData = {
        timestamp: new Date().toISOString(),
        random: Math.random(),
        user: { id: 1, name: '测试用户' },
        items: ['item1', 'item2', 'item3']
      }
      this.$emit('childData', complexData)
      this.emitCount++
      this.addLog('EMIT', `发送复杂数据给父组件: ${JSON.stringify(complexData)}`)
    },
    
    // sessionStorage操作
    readSessionStorage() {
      this.sessionValue = sessionStorage.getItem(this.sessionKey) || '暂无数据'
      this.storageReadCount++
      this.addLog('SESSION', `读取sessionStorage: ${this.sessionValue}`)
    },
    
    modifySessionStorage() {
      const newValue = `子组件修改-${Date.now()}`
      sessionStorage.setItem(this.sessionKey, newValue)
      this.readSessionStorage()
      this.addLog('SESSION', `修改sessionStorage: ${newValue}`)
    },
    
    // localStorage操作
    readLocalStorage() {
      this.localValue = localStorage.getItem(this.localKey) || '暂无数据'
      this.storageReadCount++
      this.addLog('LOCAL', `读取localStorage: ${this.localValue}`)
    },
    
    modifyLocalStorage() {
      const newValue = `子组件修改-${Date.now()}`
      localStorage.setItem(this.localKey, newValue)
      this.readLocalStorage()
      this.addLog('LOCAL', `修改localStorage: ${newValue}`)
    },
    
    // URL参数操作
    readUrlParams() {
      const urlParams = new URLSearchParams(window.location.search)
      this.urlParamValue = urlParams.get('demoParam') || '无URL参数'
      this.urlReadCount++
      this.addLog('URL', `读取URL参数: ${this.urlParamValue}`)
    },
    
    modifyUrlParams() {
      const newValue = `子组件修改-${Date.now()}`
      const url = new URL(window.location)
      url.searchParams.set('demoParam', newValue)
      window.history.pushState({}, '', url)
      this.readUrlParams()
      this.addLog('URL', `修改URL参数: ${newValue}`)
    },
    
    // 全局状态操作
    readGlobalState() {
      this.globalStateValue = this.$parent.$globalState?.message || '无全局状态'
      this.addLog('GLOBAL', `读取全局状态: ${this.globalStateValue}`)
    },
    
    modifyGlobalState() {
      const newValue = `子组件修改-${Date.now()}`
      if (this.$parent.$globalState) {
        this.$parent.$globalState.updateMessage(newValue)
        this.readGlobalState()
        this.addLog('GLOBAL', `修改全局状态: ${newValue}`)
      }
    },
    
    // Inject操作
    refreshInjectData() {
      this.injectedMessage = this.providedMessage()
      this.addLog('INJECT', `刷新Inject数据: ${this.injectedMessage}`)
    },
    
    updateInjectData() {
      const newValue = `子组件通过Inject修改-${Date.now()}`
      this.updateProvidedMessage(newValue)
      this.refreshInjectData()
      this.addLog('INJECT', `通过Inject更新数据: ${newValue}`)
    },
    
    // 数据处理
    processData() {
      try {
        const data = JSON.parse(this.rawData)
        const processed = {
          ...data,
          processedAt: new Date().toISOString(),
          processedBy: 'DataReceiver组件',
          hash: btoa(JSON.stringify(data)).slice(0, 8),
          stats: {
            keys: Object.keys(data).length,
            stringValues: Object.values(data).filter(v => typeof v === 'string').length,
            numberValues: Object.values(data).filter(v => typeof v === 'number').length
          }
        }
        this.processedData = JSON.stringify(processed, null, 2)
        this.addLog('PROCESS', '数据处理成功')
      } catch (error) {
        this.processedData = `错误: ${error.message}`
        this.addLog('ERROR', `数据处理失败: ${error.message}`)
      }
    },
    
    // 日志相关
    addLog(type, message) {
      const log = {
        time: new Date().toLocaleTimeString(),
        type: type,
        message: message
      }
      this.eventLogs.unshift(log)
      // 保持最新50条日志
      if (this.eventLogs.length > 50) {
        this.eventLogs = this.eventLogs.slice(0, 50)
      }
    },
    
    clearLogs() {
      this.eventLogs = []
      this.addLog('SYSTEM', '日志已清空')
    },
    
    exportLogs() {
      const logsText = this.eventLogs.map(log => 
        `[${log.time}] ${log.type}: ${log.message}`
      ).join('\n')
      const blob = new Blob([logsText], { type: 'text/plain' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `data-transfer-logs-${Date.now()}.txt`
      a.click()
      URL.revokeObjectURL(url)
      this.addLog('EXPORT', '日志已导出')
    }
  }
}
</script>

<style scoped>
.data-receiver {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 20px rgba(0,0,0,0.1);
  border: 2px solid #e9ecef;
}

.header {
  text-align: center;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f8f9fa;
}

.header h3 {
  color: #495057;
  margin: 0 0 8px 0;
  font-size: 1.5em;
}

.subtitle {
  color: #6c757d;
  margin: 0;
  font-size: 1em;
}

.demo-section {
  background: #f8f9fa;
  padding: 18px;
  margin-bottom: 15px;
  border-radius: 8px;
  border-left: 4px solid #28a745;
}

.demo-section h4 {
  color: #495057;
  margin: 0 0 15px 0;
  font-size: 1.1em;
}

.controls {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.input-field {
  flex: 1;
  min-width: 180px;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 13px;
}

.input-field:focus {
  outline: none;
  border-color: #80bdff;
  box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(0,0,0,0.15);
}

.btn.primary { background: #007bff; color: white; }
.btn.success { background: #28a745; color: white; }
.btn.info { background: #17a2b8; color: white; }
.btn.warning { background: #ffc107; color: #212529; }
.btn.danger { background: #dc3545; color: white; }

.data-display {
  background: white;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #dee2e6;
  margin-bottom: 8px;
}

.label {
  font-weight: 600;
  color: #495057;
  margin-right: 8px;
}

.value {
  color: #28a745;
  font-family: 'Courier New', monospace;
  background: #d4edda;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 13px;
}

.props-info, .storage-info, .url-info, .global-info, .inject-info {
  color: #6c757d;
  font-size: 12px;
  font-style: italic;
}

.emit-counter {
  background: white;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #dee2e6;
}

.counter {
  background: #007bff;
  color: white;
  padding: 3px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.monitor-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  border-radius: 10px;
  margin: 20px 0;
  color: white;
}

.monitor-section h4 {
  color: white;
  margin: 0 0 15px 0;
  text-align: center;
}

.monitor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
}

.monitor-item {
  background: rgba(255,255,255,0.1);
  padding: 12px;
  border-radius: 8px;
  text-align: center;
  backdrop-filter: blur(10px);
}

.monitor-label {
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 5px;
}

.monitor-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff3cd;
}

.processing-section {
  background: #fff3cd;
  padding: 20px;
  border-radius: 10px;
  margin: 20px 0;
  border: 1px solid #ffeaa7;
}

.processing-section h4 {
  color: #856404;
  margin: 0 0 15px 0;
}

.processing-demo {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.input-data h5, .output-data h5 {
  color: #856404;
  margin: 0 0 10px 0;
  font-size: 14px;
}

.json-input {
  width: 100%;
  height: 120px;
  padding: 10px;
  border: 1px solid #ffeaa7;
  border-radius: 6px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  resize: vertical;
}

.json-output {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #dee2e6;
  font-size: 12px;
  max-height: 160px;
  overflow-y: auto;
  margin: 0;
}

.log-section {
  background: #e9ecef;
  padding: 20px;
  border-radius: 10px;
  margin-top: 20px;
}

.log-section h4 {
  color: #495057;
  margin: 0 0 15px 0;
}

.log-controls {
  margin-bottom: 15px;
}

.log-container {
  background: white;
  border-radius: 6px;
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #ced4da;
}

.log-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 1px solid #f8f9fa;
  font-size: 13px;
}

.log-item:last-child {
  border-bottom: none;
}

.log-time {
  color: #6c757d;
  width: 80px;
  font-family: 'Courier New', monospace;
  font-size: 11px;
}

.log-type {
  width: 80px;
  text-align: center;
  font-weight: bold;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  margin: 0 8px;
}

.log-type.PROPS { background: #d4edda; color: #155724; }
.log-type.EMIT { background: #cce5ff; color: #004085; }
.log-type.SESSION { background: #fff3cd; color: #856404; }
.log-type.LOCAL { background: #f8d7da; color: #721c24; }
.log-type.URL { background: #d1ecf1; color: #0c5460; }
.log-type.GLOBAL { background: #e2e3e5; color: #383d41; }
.log-type.INJECT { background: #f4cccc; color: #721c24; }
.log-type.PROCESS { background: #c3e6cb; color: #155724; }
.log-type.ERROR { background: #f8d7da; color: #721c24; }
.log-type.INIT { background: #bee5eb; color: #0c5460; }
.log-type.SYSTEM { background: #e2e3e5; color: #383d41; }
.log-type.EXPORT { background: #d4edda; color: #155724; }

.log-message {
  flex: 1;
  color: #495057;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .data-receiver {
    padding: 15px;
  }
  
  .controls {
    flex-direction: column;
  }
  
  .input-field {
    min-width: unset;
  }
  
  .processing-demo {
    grid-template-columns: 1fr;
  }
  
  .monitor-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>