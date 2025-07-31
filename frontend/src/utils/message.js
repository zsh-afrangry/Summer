// 统一消息提示工具
// 可以根据使用的UI框架进行调整（Element UI、Ant Design Vue等）

class MessageHelper {
  // 成功消息
  success(message, duration = 3000) {
    this.showMessage(message, 'success', duration)
  }

  // 错误消息
  error(message, duration = 5000) {
    this.showMessage(message, 'error', duration)
  }

  // 警告消息
  warning(message, duration = 4000) {
    this.showMessage(message, 'warning', duration)
  }

  // 信息消息
  info(message, duration = 3000) {
    this.showMessage(message, 'info', duration)
  }

  // 显示消息的基础方法
  showMessage(message, type) {
    // 这里可以根据实际使用的UI框架进行调整
    // 例如使用Element UI：
    // this.$message({ message, type, duration })
    
    // 暂时使用原生alert和console.log
    if (type === 'error') {
      console.error('❌', message)
      alert(`错误: ${message}`)
    } else if (type === 'success') {
      console.log('✅', message)
      // 可以使用更友好的提示方式
    } else if (type === 'warning') {
      console.warn('⚠️', message)
    } else {
      console.info('ℹ️', message)
    }
  }

  // 确认对话框
  confirm(message, title = '确认') {
    return new Promise((resolve) => {
      // 暂时使用原生confirm
      const result = window.confirm(`${title}\n${message}`)
      resolve(result)
    })
  }

  // 加载提示
  loading(message = '加载中...') {
    console.log('🔄', message)
    // 这里可以显示loading组件
    return {
      close: () => {
        console.log('✅ 加载完成')
        // 关闭loading
      }
    }
  }
}

// 创建单例实例
const message = new MessageHelper()

export default message

// 导出类供需要时使用
export { MessageHelper } 