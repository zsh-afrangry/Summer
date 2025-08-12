<template>
  <div class="portal-page">
    <!-- Header -->
    <header class="header">
      <div class="container header-content">
        <div class="logo">
          <span class="logo-text">Summer</span>
          <span class="logo-accent">Portal</span>
        </div>
        <nav class="nav">
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
        <p class="hero-subtitle fade-in">欢迎使用</p>
        <h1 class="hero-title fade-in delay-1">Summer 智能系统</h1>
        <p class="hero-description fade-in delay-2">
          一个集成了多种功能的现代化平台，为您提供博客管理、交易分析等多种服务
        </p>
        <div class="hero-buttons fade-in delay-3">
          <button @click="navigateToLogin" class="btn btn-primary">
            开始使用
          </button>
          <button @click="scrollToFeatures" class="btn btn-secondary">
            了解更多
          </button>
        </div>
      </div>
      <div class="hero-background">
        <div class="floating-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features section" id="features">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">核心功能</h2>
          <p class="section-description">
            探索我们为您精心打造的功能模块
          </p>
        </div>
        
        <div class="features-grid">
          <div class="feature-card fade-in" v-for="(feature, index) in features" :key="index">
            <div class="feature-icon">
              <i :class="feature.icon"></i>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- About Section -->
    <section class="about section" id="about">
      <div class="container">
        <div class="about-content">
          <div class="about-text">
            <p class="about-subtitle">关于我们</p>
            <h2 class="about-title">专注于创新与用户体验</h2>
            <p class="about-description">
              Summer 系统致力于为用户提供优质的数字化解决方案。
              我们专注于技术创新，追求卓越的用户体验，让每一个功能都能为用户创造价值。
            </p>
            <div class="about-stats">
              <div class="stat-item">
                <div class="stat-number">100+</div>
                <div class="stat-label">活跃用户</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">50+</div>
                <div class="stat-label">功能模块</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">99%</div>
                <div class="stat-label">用户满意度</div>
              </div>
            </div>
          </div>
          <div class="about-image">
            <div class="image-placeholder">
              <i class="fas fa-code"></i>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta">
      <div class="container">
        <div class="cta-content">
          <h2 class="cta-title">准备开始了吗？</h2>
          <p class="cta-description">
            立即登录或注册，开始您的 Summer 之旅
          </p>
          <button @click="navigateToLogin" class="btn btn-cta">
            立即开始
          </button>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3 class="footer-logo">Summer Portal</h3>
            <p class="footer-description">
              创新驱动，用户至上
            </p>
          </div>
          <div class="footer-section">
            <h4 class="footer-title">快速链接</h4>
            <ul class="footer-links">
              <li><a href="#home">首页</a></li>
              <li><a href="#features">功能</a></li>
              <li><a href="#about">关于</a></li>
            </ul>
          </div>
          <div class="footer-section">
            <h4 class="footer-title">联系我们</h4>
            <ul class="footer-links">
              <li>邮箱: hello@summer.com</li>
              <li>电话: +86 123 4567 8900</li>
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
      features: [
        {
          icon: 'fas fa-blog',
          title: '博客管理',
          description: '强大的博客系统，支持文章编写、分类管理和评论互动'
        },
        {
          icon: 'fas fa-chart-line',
          title: '交易分析',
          description: '专业的网格交易分析工具，助您做出明智的投资决策'
        },
        {
          icon: 'fas fa-user-circle',
          title: '个人中心',
          description: '完善的用户管理系统，个性化设置和数据统计'
        },
        {
          icon: 'fas fa-mobile-alt',
          title: '响应式设计',
          description: '完美适配各种设备，随时随地享受优质体验'
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
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

/* CSS Variables */
:root {
  --primary: #2563eb;
  --primary-dark: #1d4ed8;
  --secondary: #f8fafc;
  --accent: #06b6d4;
  --text: #1e293b;
  --text-light: #64748b;
  --white: #ffffff;
  --gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  --transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  --shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  --shadow-lg: 0 20px 40px rgba(0, 0, 0, 0.15);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.portal-page {
  font-family: 'Inter', sans-serif;
  line-height: 1.6;
  color: var(--text);
  overflow-x: hidden;
}

/* Header Styles */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  z-index: 1000;
  padding: 1rem 0;
  transition: var(--transition);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 1.5rem;
  font-weight: 700;
}

.logo-text {
  color: var(--text);
}

.logo-accent {
  color: var(--primary);
  margin-left: 0.25rem;
}

.nav-list {
  display: flex;
  list-style: none;
  gap: 2rem;
}

.nav-link {
  text-decoration: none;
  color: var(--text);
  font-weight: 500;
  transition: var(--transition);
  position: relative;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--primary);
  transition: var(--transition);
}

.nav-link:hover {
  color: var(--primary);
}

.nav-link:hover::after {
  width: 100%;
}

/* Hero Styles */
.hero {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: var(--white);
  overflow: hidden;
}

.hero-content {
  max-width: 800px;
  z-index: 2;
  position: relative;
}

.hero-subtitle {
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: 1rem;
  opacity: 0.9;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  line-height: 1.2;
}

.hero-description {
  font-size: 1.2rem;
  margin-bottom: 2.5rem;
  opacity: 0.9;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.hero-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  padding: 0.875rem 2rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: var(--transition);
  text-decoration: none;
  display: inline-block;
}

.btn-primary {
  background: var(--white);
  color: var(--primary);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow);
}

.btn-secondary {
  background: transparent;
  color: var(--white);
  border: 2px solid var(--white);
}

.btn-secondary:hover {
  background: var(--white);
  color: var(--primary);
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.floating-shapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* Section Styles */
.section {
  padding: 5rem 0;
}

.section-header {
  text-align: center;
  margin-bottom: 4rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: var(--text);
}

.section-description {
  font-size: 1.1rem;
  color: var(--text-light);
  max-width: 600px;
  margin: 0 auto;
}

/* Features Styles */
.features {
  background: var(--secondary);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: var(--white);
  padding: 2.5rem 2rem;
  border-radius: 12px;
  text-align: center;
  box-shadow: var(--shadow);
  transition: var(--transition);
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-lg);
}

.feature-icon {
  width: 80px;
  height: 80px;
  background: var(--gradient);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
  font-size: 2rem;
  color: var(--white);
}

.feature-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: var(--text);
}

.feature-description {
  color: var(--text-light);
  line-height: 1.6;
}

/* About Styles */
.about-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.about-subtitle {
  color: var(--primary);
  font-weight: 600;
  margin-bottom: 1rem;
}

.about-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  color: var(--text);
}

.about-description {
  font-size: 1.1rem;
  color: var(--text-light);
  margin-bottom: 2rem;
  line-height: 1.7;
}

.about-stats {
  display: flex;
  gap: 2rem;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 0.5rem;
}

.stat-label {
  color: var(--text-light);
  font-size: 0.9rem;
}

.about-image {
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-placeholder {
  width: 300px;
  height: 300px;
  background: var(--gradient);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 4rem;
  color: var(--white);
}

/* CTA Styles */
.cta {
  background: var(--gradient);
  color: var(--white);
  text-align: center;
  padding: 5rem 0;
}

.cta-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.cta-description {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.btn-cta {
  background: var(--white);
  color: var(--primary);
  font-size: 1.1rem;
  padding: 1rem 2.5rem;
}

.btn-cta:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

/* Footer Styles */
.footer {
  background: var(--text);
  color: var(--white);
  padding: 3rem 0 1rem;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-logo {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.footer-description {
  opacity: 0.8;
  margin-bottom: 1rem;
}

.footer-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.footer-links {
  list-style: none;
}

.footer-links li {
  margin-bottom: 0.5rem;
}

.footer-links a {
  color: var(--white);
  text-decoration: none;
  opacity: 0.8;
  transition: var(--transition);
}

.footer-links a:hover {
  opacity: 1;
  color: var(--accent);
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 1rem;
  text-align: center;
  opacity: 0.6;
}

/* Animations */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(30px);
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

.fade-in.animate {
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

/* Responsive Design */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .about-content {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .about-stats {
    justify-content: center;
  }
  
  .nav-list {
    display: none;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .image-placeholder {
    width: 200px;
    height: 200px;
    font-size: 3rem;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 1rem;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .about-stats {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>