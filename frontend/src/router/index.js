import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../components/LoginPage.vue'
import MainLayout from '../components/MainLayout.vue'
import BlogHome from '../components/BlogHome.vue'
import BlogList from '../components/BlogList.vue'
import About from '../components/About.vue'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: LoginPage
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: LoginPage
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
  const isLoggedIn = localStorage.getItem('userToken') || sessionStorage.getItem('currentUser')
  
  // 如果访问主界面但未登录，重定向到登录页
  if (to.path.startsWith('/main') && !isLoggedIn) {
    next('/login')
  } else {
    next()
  }
})

export default router 