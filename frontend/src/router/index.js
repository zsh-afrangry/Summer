import { createRouter, createWebHistory } from 'vue-router'
import ElegantPortalPage from '../components/ElegantPortalPage.vue'
import LoginPage from '../components/LoginPage.vue'
import LoginPageDemo from '../components/LoginPageDemo.vue'
import MainLayout from '../components/MainLayout.vue'
import BlogHome from '../components/BlogHome.vue'
import BlogList from '../components/BlogList.vue'
import About from '../components/About.vue'
import TradingPage from '../components/trading/TradingPage.vue'
import ChartVisualization from '../components/trading/ChartVisualization.vue'
import DataSender from '../components/study/数据结构传递/DataSender.vue'

const routes = [
  {
    path: '/',
    name: 'ElegantPortal',
    component: ElegantPortalPage,
    meta: { title: 'Summer Portal - 欢迎来到智能系统' }
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: LoginPage
  },
  {
    path: '/demo',
    name: 'LoginPageDemo',
    component: LoginPageDemo,
    meta: { title: 'Vue.js 核心概念演示' }
  },
  // 网格交易策略分析系统
  {
    path: '/trading-page',
    name: 'TradingPageDirect',
    component: TradingPage,
    meta: { title: '网格交易策略分析系统' }
  },
  {
    path: '/chart-visualization',
    name: 'ChartVisualization',
    component: ChartVisualization,
    meta: { title: '可视化分析中心 - 专业图表分析' }
  },
  // Vue数据传递演示 - 学习用途
  {
    path: '/data-transfer-demo',
    name: 'DataTransferDemo',
    component: DataSender,
    meta: { title: 'Vue数据传递演示 - 学习用途' }
  },
  {
    path: '/main',
    component: MainLayout,
    children: [
      {
        path: '',
        redirect: '/main/home'
      },
      {
        path: 'home',
        name: 'BlogHome',
        component: BlogHome,
        meta: { title: '首页' }
      },
      {
        path: 'articles',
        name: 'BlogList',
        component: BlogList,
        meta: { title: '文章列表' }
      },
      {
        path: 'about',
        name: 'About',
        component: About,
        meta: { title: '关于我' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
  console.log('🔀 路由导航:', from.path, '->', to.path)
  const isLoggedIn = localStorage.getItem('userToken') || sessionStorage.getItem('currentUser')
  
  // 如果访问主界面但未登录，重定向到登录页
  // 门户页面(/)、登录页面(/login)等公共页面无需验证
  if (to.path.startsWith('/main') && !isLoggedIn) {
    console.log('🚫 未登录，重定向到登录页')
    next('/login')
  } else {
    console.log('✅ 允许访问:', to.path)
    next()
  }
})

export default router 