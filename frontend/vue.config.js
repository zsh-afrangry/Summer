const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    host: '0.0.0.0',  // Docker环境中需要监听所有接口
    port: 3000,
    allowedHosts: 'all',
    proxy: {
      '/api': {
        target: 'http://backend-dev:8080',  // Docker容器间通信使用服务名
        changeOrigin: true
      }
    }
  }
})
