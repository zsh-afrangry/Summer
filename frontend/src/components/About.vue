<template>
  <div class="about-page">
    <!-- 个人简介卡片 -->
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar">
          <div class="avatar-img">👨‍💻</div>
        </div>
        <div class="profile-info">
          <h1>张斯涵</h1>
          <p class="title">全栈开发工程师</p>
          <p class="motto">💡 "代码改变世界，技术创造未来"</p>
        </div>
      </div>
      
      <div class="profile-stats">
        <div class="stat-item">
          <strong>3+</strong>
          <span>年开发经验</span>
        </div>
        <div class="stat-item">
          <strong>50+</strong>
          <span>项目经验</span>
        </div>
        <div class="stat-item">
          <strong>12</strong>
          <span>技术文章</span>
        </div>
        <div class="stat-item">
          <strong>1000+</strong>
          <span>代码提交</span>
        </div>
      </div>
    </div>

    <!-- 技能专长 -->
    <section class="section">
      <h2 class="section-title">🚀 技能专长</h2>
      <div class="skills-container">
        <div class="skill-category">
          <h3>前端开发</h3>
          <div class="skills">
            <div class="skill-item">
              <span class="skill-name">Vue.js</span>
              <div class="skill-bar">
                <div class="skill-progress" style="width: 90%"></div>
              </div>
              <span class="skill-level">90%</span>
            </div>
            <div class="skill-item">
              <span class="skill-name">JavaScript</span>
              <div class="skill-bar">
                <div class="skill-progress" style="width: 85%"></div>
              </div>
              <span class="skill-level">85%</span>
            </div>
            <div class="skill-item">
              <span class="skill-name">HTML/CSS</span>
              <div class="skill-bar">
                <div class="skill-progress" style="width: 80%"></div>
              </div>
              <span class="skill-level">80%</span>
            </div>
          </div>
        </div>

        <div class="skill-category">
          <h3>后端开发</h3>
          <div class="skills">
            <div class="skill-item">
              <span class="skill-name">Spring Boot</span>
              <div class="skill-bar">
                <div class="skill-progress" style="width: 88%"></div>
              </div>
              <span class="skill-level">88%</span>
            </div>
            <div class="skill-item">
              <span class="skill-name">Java</span>
              <div class="skill-bar">
                <div class="skill-progress" style="width: 82%"></div>
              </div>
              <span class="skill-level">82%</span>
            </div>
            <div class="skill-item">
              <span class="skill-name">MySQL</span>
              <div class="skill-bar">
                <div class="skill-progress" style="width: 75%"></div>
              </div>
              <span class="skill-level">75%</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 联系方式 -->
    <section class="section">
      <h2 class="section-title">📞 联系我</h2>
      <div class="contact-info">
        <div class="contact-item">
          <span class="contact-icon">📧</span>
          <span class="contact-label">邮箱:</span>
          <a href="mailto:zhangsiwei@example.com" class="contact-value">zhangsiwei@example.com</a>
        </div>
        <div class="contact-item">
          <span class="contact-icon">🔗</span>
          <span class="contact-label">GitHub:</span>
          <a href="https://github.com/zhangsiwei" class="contact-value">github.com/zhangsiwei</a>
        </div>
        <div class="contact-item">
          <span class="contact-icon">💼</span>
          <span class="contact-label">LinkedIn:</span>
          <a href="https://linkedin.com/in/zhangsiwei" class="contact-value">linkedin.com/in/zhangsiwei</a>
        </div>
      </div>
    </section>

    <!-- 呼吁行动 -->
    <div class="cta-section">
      <h2>💡 让我们一起创造不凡！</h2>
      <p>如果你对技术充满热情，或者有有趣的项目想要合作，欢迎随时联系我！</p>
      <div class="cta-buttons">
        <router-link to="/main/articles" class="cta-btn primary">查看我的文章</router-link>
        <a href="mailto:zhangsiwei@example.com" class="cta-btn secondary">联系合作</a>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AboutPage'
}
</script>

<style scoped>
.about-page {
  max-width: 100%;
}

/* 个人简介卡片 */
.profile-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 30px;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  gap: 25px;
}

.avatar {
  flex-shrink: 0;
}

.avatar-img {
  width: 80px;
  height: 80px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  border: 3px solid rgba(255,255,255,0.3);
}

.profile-info h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.title {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.motto {
  font-size: 16px;
  opacity: 0.8;
  font-style: italic;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: rgba(255,255,255,0.1);
  border-radius: 8px;
  border: 1px solid rgba(255,255,255,0.2);
}

.stat-item strong {
  display: block;
  font-size: 24px;
  margin-bottom: 5px;
  color: #ffd700;
}

.stat-item span {
  font-size: 14px;
  opacity: 0.9;
}

/* 章节样式 */
.section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 24px;
  color: #495057;
  margin-bottom: 25px;
  font-weight: bold;
}

/* 技能展示 */
.skills-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.skill-category {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #e9ecef;
}

.skill-category h3 {
  color: #495057;
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
}

.skill-item {
  display: grid;
  grid-template-columns: 1fr 2fr auto;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.skill-name {
  font-weight: 500;
  color: #495057;
}

.skill-bar {
  height: 8px;
  background-color: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.skill-progress {
  height: 100%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 4px;
  transition: width 1s ease;
}

.skill-level {
  font-size: 12px;
  color: #6c757d;
  font-weight: 500;
}

/* 联系信息 */
.contact-info {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #e9ecef;
}

.contact-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  gap: 15px;
}

.contact-item:last-child {
  margin-bottom: 0;
}

.contact-icon {
  font-size: 18px;
  width: 24px;
}

.contact-label {
  font-weight: 500;
  color: #495057;
  min-width: 80px;
}

.contact-value {
  color: #667eea;
  text-decoration: none;
  transition: color 0.3s ease;
}

.contact-value:hover {
  color: #5a6fd8;
  text-decoration: underline;
}

/* 呼吁行动 */
.cta-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  margin-top: 40px;
}

.cta-section h2 {
  font-size: 24px;
  margin-bottom: 15px;
}

.cta-section p {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 25px;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
}

.cta-btn {
  padding: 12px 24px;
  border-radius: 6px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  display: inline-block;
}

.cta-btn.primary {
  background-color: white;
  color: #667eea;
}

.cta-btn.primary:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
}

.cta-btn.secondary {
  background-color: rgba(255,255,255,0.2);
  color: white;
  border: 1px solid rgba(255,255,255,0.3);
}

.cta-btn.secondary:hover {
  background-color: rgba(255,255,255,0.3);
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
  }
  
  .profile-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .skills-container {
    grid-template-columns: 1fr;
  }
  
  .cta-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .contact-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style> 