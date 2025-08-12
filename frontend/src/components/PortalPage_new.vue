<template>
  <div class="portal-page">
    <!-- Header -->
    <header class="header">
      <div class="container flex space-between align-center">
        <a href="#" class="logo">Summer<span class="logo-accent">智能系统</span></a>
        <nav>
          <ul class="nav-list">
            <li><a href="#home" class="nav-link">首页</a></li>
            <li><a href="#features" class="nav-link">功能</a></li>
            <li><a href="#about" class="nav-link">关于</a></li>
            <li><a href="#contact" class="nav-link">联系</a></li>
          </ul>
        </nav>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="hero" id="home">
      <div class="hero-content">
        <p class="hero-subtitle fade-in">智能科技</p>
        <h1 class="hero-title fade-in delay-1">Summer 智能系统<br>现代化数字解决方案</h1>
        <p class="hero-description fade-in delay-2">
          一个集成了多种功能的现代化平台，为您提供博客管理、网格交易分析等多种智能服务，
          让科技赋能您的数字生活。
        </p>
        <a href="#" @click="navigateToLogin" class="btn fade-in delay-3">开始探索</a>
      </div>
    </section>

    <!-- Features Section -->
    <section class="section" id="features">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">核心功能</h2>
          <p class="section-description">探索我们为您精心打造的功能模块，体验现代化的数字解决方案</p>
        </div>
        
        <div class="features-grid">
          <article class="feature-card" v-for="(feature, index) in features" :key="index">
            <div class="feature-icon">
              <i :class="feature.icon"></i>
            </div>
            <p class="feature-category">{{ feature.category }}</p>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
            <div class="feature-meta">
              <span>{{ feature.status }}</span>
            </div>
          </article>
        </div>
      </div>
    </section>

    <!-- About Section -->
    <section class="section" id="about">
      <div class="container">
        <div class="about">
          <div class="about-image">
            <img src="https://images.unsplash.com/photo-1551434678-e076c223a692?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80" alt="团队工作">
          </div>
          <div class="about-content">
            <p class="about-subtitle">关于我们</p>
            <h2 class="about-title">专注于创新与用户体验</h2>
            <p class="about-description">Summer 系统致力于为用户提供优质的数字化解决方案。我们专注于技术创新，追求卓越的用户体验。</p>
            <p class="about-description">经过多年在快节奏创意行业的深耕，我们发现了更慢、更有意图的生活方式的价值。这个平台既是创意的出口，也是让想法和体验得以呼吸和发展的圣地。</p>
            <a href="#" @click="navigateToLogin" class="btn">了解更多</a>
            <div class="social-links">
              <a href="#" class="social-link">
                <i class="fab fa-github"></i>
              </a>
              <a href="#" class="social-link">
                <i class="fab fa-linkedin"></i>
              </a>
              <a href="#" class="social-link">
                <i class="fab fa-twitter"></i>
              </a>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Newsletter -->
    <section class="newsletter">
      <div class="container">
        <h2 class="newsletter-title">加入我们的社区</h2>
        <p class="newsletter-description">订阅我们的资讯，获取最新的技术动态和产品更新，与我们一起探索数字化的未来。</p>
        <form class="newsletter-form" @submit.prevent="handleNewsletter">
          <input type="email" class="newsletter-input" placeholder="请输入您的邮箱地址" v-model="email">
          <button type="submit" class="newsletter-btn">订阅</button>
        </form>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-about">
            <h3 class="footer-logo">Summer Portal</h3>
            <p class="footer-description">现代化的智能系统平台，探索技术、设计和数字生活的美好。</p>
            <div class="social-links">
              <a href="#" class="social-link">
                <i class="fab fa-github"></i>
              </a>
              <a href="#" class="social-link">
                <i class="fab fa-linkedin"></i>
              </a>
              <a href="#" class="social-link">
                <i class="fab fa-twitter"></i>
              </a>
            </div>
          </div>

          <div class="footer-links-column">
            <h4 class="footer-title">导航</h4>
            <ul class="footer-links">
              <li><a href="#">首页</a></li>
              <li><a href="#">功能</a></li>
              <li><a href="#">关于</a></li>
              <li><a href="#">联系</a></li>
            </ul>
          </div>

          <div class="footer-links-column">
            <h4 class="footer-title">功能</h4>
            <ul class="footer-links">
              <li><a href="#">博客管理</a></li>
              <li><a href="#">交易分析</a></li>
              <li><a href="#">个人中心</a></li>
              <li><a href="#">数据统计</a></li>
            </ul>
          </div>

          <div class="footer-links-column">
            <h4 class="footer-title">信息</h4>
            <ul class="footer-links">
              <li><a href="#">隐私政策</a></li>
              <li><a href="#">使用条款</a></li>
              <li><a href="#">帮助中心</a></li>
              <li><a href="#">技术支持</a></li>
            </ul>
          </div>
        </div>

        <div class="footer-bottom">
          <p>&copy; 2025 Summer Portal. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'PortalPage',
  data() {
    return {
      email: '',
      features: [
        {
          icon: 'fas fa-blog',
          category: '内容管理',
          title: '博客管理系统',
          description: '强大的博客系统，支持文章编写、分类管理和评论互动，让您的想法得以完美呈现。',
          status: '已上线'
        },
        {
          icon: 'fas fa-chart-line',
          category: '数据分析',
          title: '网格交易分析',
          description: '专业的网格交易分析工具，通过智能算法助您做出明智的投资决策。',
          status: '已上线'
        },
        {
          icon: 'fas fa-user-circle',
          category: '用户体验',
          title: '个人中心',
          description: '完善的用户管理系统，个性化设置和数据统计，打造专属的数字空间。',
          status: '持续优化'
        }
      ]
    }
  },
  mounted() {
    this.initAnimations()
  },
  methods: {
    navigateToLogin() {
      this.$router.push('/login')
    },
    scrollToFeatures() {
      const featuresSection = document.getElementById('features')
      if (featuresSection) {
        featuresSection.scrollIntoView({ behavior: 'smooth' })
      }
    },
    handleNewsletter() {
      if (this.email) {
        // 这里可以添加实际的订阅逻辑
        alert(`感谢订阅！邮箱：${this.email}`)
        this.email = ''
      }
    },
    initAnimations() {
      // 使用 Intersection Observer 来触发动画
      const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
      }

      const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add('animate')
          }
        })
      }, observerOptions)

      // 观察所有需要动画的元素
      const animatedElements = document.querySelectorAll('.fade-in')
      animatedElements.forEach(el => observer.observe(el))
    }
  }
}
</script>

<style scoped>
/* Font imports */
@import url('https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@300;400;500;600&family=Montserrat:wght@300;400;500&display=swap');
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

/* CSS Variables */
:root {
  --primary: #1a1a1a;
  --secondary: #f8f5f2;
  --accent: #d4b8a0;
  --text: #333333;
  --light-text: #777777;
  --white: #ffffff;
  --transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
}

/* Reset and base styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.portal-page {
  font-family: 'Montserrat', sans-serif;
  color: var(--text);
  background-color: var(--secondary);
  line-height: 1.6;
  overflow-x: hidden;
}

h1, h2, h3, h4, h5 {
  font-family: 'Cormorant Garamond', serif;
  font-weight: 500;
  letter-spacing: 0.5px;
}

a {
  text-decoration: none;
  color: inherit;
  transition: var(--transition);
}

img {
  max-width: 100%;
  height: auto;
  display: block;
}

/* Layout */
.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 2rem;
}

.flex {
  display: flex;
}

.space-between {
  justify-content: space-between;
}

.align-center {
  align-items: center;
}

/* Header */
.header {
  padding: 2rem 0;
  position: sticky;
  top: 0;
  background-color: rgba(248, 245, 242, 0.98);
  z-index: 100;
  backdrop-filter: blur(10px);
}

.logo {
  font-family: 'Cormorant Garamond', serif;
  font-size: 2rem;
  font-weight: 500;
  letter-spacing: 3px;
}

.logo-accent {
  color: var(--accent);
}

.nav-list {
  list-style: none;
  display: flex;
  gap: 2.5rem;
}

.nav-link {
  font-size: 0.9rem;
  font-weight: 400;
  text-transform: uppercase;
  letter-spacing: 1px;
  position: relative;
}

.nav-link::after {
  content: '';
  position: absolute;
  width: 0;
  height: 1px;
  bottom: -4px;
  left: 0;
  background-color: var(--accent);
  transition: var(--transition);
}

.nav-link:hover::after {
  width: 100%;
}

/* Hero section */
.hero {
  height: calc(100vh - 100px);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 0 2rem;
  position: relative;
  overflow: hidden;
}

.hero-content {
  max-width: 800px;
  z-index: 1;
}

.hero-subtitle {
  font-size: 1.1rem;
  color: var(--accent);
  margin-bottom: 1.5rem;
  letter-spacing: 3px;
  text-transform: uppercase;
  font-weight: 300;
}

.hero-title {
  font-size: 4rem;
  line-height: 1.1;
  margin-bottom: 2rem;
}

.hero-description {
  font-size: 1.2rem;
  max-width: 600px;
  margin: 0 auto 3rem;
  color: var(--light-text);
}

.btn {
  display: inline-block;
  padding: 0.8rem 2.5rem;
  border: 1px solid var(--primary);
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 2px;
  position: relative;
  overflow: hidden;
  transition: var(--transition);
  background: transparent;
  cursor: pointer;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background-color: var(--primary);
  transition: var(--transition);
  z-index: -1;
}

.btn:hover {
  color: var(--secondary);
}

.btn:hover::before {
  left: 0;
}

/* Sections */
.section {
  padding: 8rem 0;
}

.section-header {
  text-align: center;
  margin-bottom: 5rem;
}

.section-title {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.section-description {
  font-size: 1.1rem;
  max-width: 600px;
  margin: 0 auto;
  color: var(--light-text);
}

/* Features */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 3rem;
}

.feature-card {
  position: relative;
  transition: var(--transition);
  padding: 2rem;
  background: var(--white);
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.feature-icon {
  width: 60px;
  height: 60px;
  background: var(--accent);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
  color: var(--white);
}

.feature-category {
  font-size: 0.85rem;
  color: var(--accent);
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.feature-title {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  line-height: 1.3;
}

.feature-description {
  font-size: 1rem;
  color: var(--light-text);
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.feature-meta {
  font-size: 0.85rem;
  color: var(--light-text);
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
}

/* About section */
.about {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 5rem;
  align-items: center;
}

.about-image {
  position: relative;
}

.about-image img {
  width: 100%;
  height: 600px;
  object-fit: cover;
}

.about-image::before {
  content: '';
  position: absolute;
  top: -30px;
  left: -30px;
  width: 100%;
  height: 100%;
  border: 1px solid var(--accent);
  z-index: -1;
}

.about-content {
  padding-right: 2rem;
}

.about-subtitle {
  font-size: 1.1rem;
  color: var(--accent);
  margin-bottom: 1.5rem;
  letter-spacing: 3px;
  text-transform: uppercase;
  font-weight: 300;
}

.about-title {
  font-size: 3rem;
  margin-bottom: 2rem;
  line-height: 1.2;
}

.about-description {
  font-size: 1.1rem;
  margin-bottom: 2rem;
  color: var(--light-text);
}

.social-links {
  display: flex;
  gap: 1.5rem;
  margin-top: 2rem;
}

.social-link {
  display: inline-block;
  width: 40px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  border: 1px solid var(--accent);
  border-radius: 50%;
  transition: var(--transition);
}

.social-link:hover {
  background-color: var(--accent);
  color: var(--primary);
}

/* Newsletter */
.newsletter {
  background-color: var(--primary);
  color: var(--secondary);
  padding: 8rem 0;
  text-align: center;
}

.newsletter-title {
  font-size: 3rem;
  margin-bottom: 1.5rem;
}

.newsletter-description {
  font-size: 1.1rem;
  max-width: 600px;
  margin: 0 auto 3rem;
  color: rgba(255, 255, 255, 0.7);
}

.newsletter-form {
  display: flex;
  max-width: 600px;
  margin: 0 auto;
}

.newsletter-input {
  flex: 1;
  padding: 1rem 1.5rem;
  font-family: 'Montserrat', sans-serif;
  font-size: 1rem;
  border: none;
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--secondary);
  border-radius: 0;
}

.newsletter-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.newsletter-btn {
  padding: 0 2rem;
  background-color: var(--accent);
  color: var(--primary);
  font-family: 'Montserrat', sans-serif;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 2px;
  border: none;
  cursor: pointer;
  transition: var(--transition);
}

.newsletter-btn:hover {
  background-color: var(--secondary);
}

/* Footer */
.footer {
  background-color: var(--primary);
  color: var(--secondary);
  padding: 5rem 0 2rem;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 5rem;
  margin-bottom: 5rem;
}

.footer-logo {
  font-family: 'Cormorant Garamond', serif;
  font-size: 2rem;
  font-weight: 500;
  letter-spacing: 3px;
  margin-bottom: 1.5rem;
}

.footer-description {
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 2rem;
}

.footer-title {
  font-size: 1.2rem;
  margin-bottom: 1.5rem;
  position: relative;
  padding-bottom: 0.7rem;
}

.footer-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 30px;
  height: 1px;
  background-color: var(--accent);
}

.footer-links {
  list-style: none;
}

.footer-links li {
  margin-bottom: 1rem;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.7);
  transition: var(--transition);
}

.footer-links a:hover {
  color: var(--accent);
  padding-left: 5px;
}

.footer-bottom {
  padding-top: 2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.9rem;
}

/* Animations */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-in {
  opacity: 0;
  animation: fadeIn 1s ease forwards;
}

.delay-1 {
  animation-delay: 0.2s;
}

.delay-2 {
  animation-delay: 0.4s;
}

.delay-3 {
  animation-delay: 0.6s;
}

/* Responsive */
@media (max-width: 992px) {
  .hero-title {
    font-size: 3rem;
  }

  .about {
    grid-template-columns: 1fr;
    gap: 3rem;
  }

  .about-image::before {
    display: none;
  }

  .footer-content {
    grid-template-columns: 1fr 1fr;
    gap: 3rem;
  }
}

@media (max-width: 768px) {
  .features-grid {
    grid-template-columns: 1fr;
  }

  .newsletter-form {
    flex-direction: column;
  }

  .newsletter-input {
    margin-bottom: 1rem;
  }

  .footer-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .hero-title {
    font-size: 2.5rem;
  }

  .section-title {
    font-size: 2rem;
  }

  .nav-list {
    display: none;
  }
}
</style>