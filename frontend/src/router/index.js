import { createRouter, createWebHistory } from 'vue-router'
import PortalPage from '../components/PortalPage.vue'
import LoginPage from '../components/LoginPage.vue'
import LoginPageDemo from '../components/LoginPageDemo.vue'
import MainLayout from '../components/MainLayout.vue'
import BlogHome from '../components/BlogHome.vue'
import BlogList from '../components/BlogList.vue'
import About from '../components/About.vue'
import GridTradingAnalyzer from '../components/GridTradingAnalyzer.vue'

const routes = [
  {
    path: '/',
    name: 'Portal',
    component: PortalPage
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
  {
    path: '/grid-trading',
    name: 'GridTradingDirect',
    component: GridTradingAnalyzer,
    meta: { title: '网格交易分析' }
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
      },
      {
        path: 'trading',
        name: 'GridTrading',
        component: GridTradingAnalyzer,
        meta: { title: '网格交易分析' }
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
  const isLoggedIn = localStorage.getItem('userToken') || sessionStorage.getItem('currentUser')
  
  // 如果访问主界面但未登录，重定向到登录页
  if (to.path.startsWith('/main') && !isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router 