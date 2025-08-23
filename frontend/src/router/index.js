import { createRouter, createWebHistory } from 'vue-router'
import ElegantPortalPage from '../components/ElegantPortalPage.vue'
import LoginPage from '../components/LoginPage.vue'
import LoginPageDemo from '../components/LoginPageDemo.vue'
import MainLayout from '../components/MainLayout.vue'
import TradingPage from '../components/trading/TradingPage.vue'
import ChartVisualization from '../components/trading/ChartVisualization.vue'
import OperationGuide from '../components/trading/OperationGuide.vue'
import DataSender from '../components/study/数据结构传递/DataSender.vue'
import Vivo50Home from '../components/vivo50/Vivo50Home.vue'

const routes = [
  // ==================== 主页面 ====================
  {
    path: '/',
    name: 'ElegantPortal',
    component: ElegantPortalPage,
    meta: { title: 'Summer Portal - 欢迎来到智能系统' }
  },

  // ==================== 用户认证 ====================
  {
    path: '/login',
    name: 'LoginPage',
    component: LoginPage,
    meta: { title: '用户登录' }
  },

  // ==================== Vivo50 学习记录中心 ====================
  {
    path: '/vivo50',
    name: 'Vivo50',
    children: [
      {
        path: '',
        name: 'Vivo50Home',
        component: Vivo50Home,
        meta: { title: 'Vivo50 学习记录中心' }
      },
      {
        path: 'algorithm',
        name: 'Algorithm',
        children: [
          {
            path: '',
            name: 'AlgorithmHome',
            component: () => import('../components/vivo50/pages/algorithm/AlgorithmHome.vue'),
            meta: { title: '算法与数据结构' }
          },
          {
            path: 'topic/:topicId',
            name: 'TopicDetail',
            component: () => import('../components/vivo50/pages/algorithm/TopicDetail.vue'),
            meta: { title: '知识点详情' }
          }
        ]
      }
      // 其他模块路由：
      // {
      //   path: 'study-records',
      //   name: 'StudyRecords',
      //   component: () => import('../components/vivo50/pages/StudyRecordList.vue'),
      //   meta: { title: '学习记录管理' }
      // }
    ]
  },

  // ==================== 网格交易策略分析系统 ====================
  {
    path: '/trading',
    name: 'Trading',
    children: [
      {
        path: '',
        name: 'TradingHome',
        component: TradingPage,
        meta: { title: '网格交易策略分析系统' }
      },
      {
        path: 'chart',
        name: 'ChartVisualization',
        component: ChartVisualization,
        meta: { title: '可视化分析中心 - 专业图表分析' }
      },
      {
        path: 'guide',
        name: 'OperationGuide',
        component: OperationGuide,
        meta: { title: '可视化分析操作说明' }
      }
    ]
  },

  // ==================== 学习演示模块 ====================
  {
    path: '/demo',
    name: 'Demo',
    children: [
      {
        path: '',
        name: 'LoginPageDemo',
        component: LoginPageDemo,
        meta: { title: 'Vue.js 核心概念演示' }
      },
      {
        path: 'data-transfer',
        name: 'DataTransferDemo',
        component: DataSender,
        meta: { title: 'Vue数据传递演示 - 学习用途' }
      }
    ]
  },

  // ==================== 主应用布局 ====================
  {
    path: '/main',
    component: MainLayout,
    children: [
      {
        path: '',
        redirect: '/main/home'
      }
    ]
  },

  // ==================== 兼容性路由 (向后兼容) ====================
  {
    path: '/trading-page',
    redirect: '/trading'
  },
  {
    path: '/chart-visualization',
    redirect: '/trading/chart'
  },
  {
    path: '/op-guide',
    redirect: '/trading/guide'
  },
  {
    path: '/data-transfer-demo',
    redirect: '/demo/data-transfer'
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