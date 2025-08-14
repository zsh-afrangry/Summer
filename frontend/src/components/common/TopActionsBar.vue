<template>
  <div class="top-actions">
    <button v-if="showBackHome" class="nav-btn" @click="go(leftTo)">{{ leftText }}</button>
    <h1 class="page-title">{{ title }}</h1>
    <button v-if="showHelp" class="nav-btn" @click="go(rightTo)">{{ rightText }}</button>
  </div>
</template>

<script>
export default {
  name: 'TopActionsBar',
  props: {
    title: { type: String, required: true },
    leftText: { type: String, default: '← 返回首页' },
    rightText: { type: String, default: '操作说明' },
    leftTo: { type: String, default: '/' },
    rightTo: { type: String, default: '/op-guide' },
    showBackHome: { type: Boolean, default: true },
    showHelp: { type: Boolean, default: true }
  },
  methods: {
    go(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.top-actions {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 10px;
}
.nav-btn {
  justify-self: start;
  padding: 6px 12px;
  border-radius: 16px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}
.top-actions .page-title { margin: 0 auto; }
.top-actions .nav-btn:last-child { justify-self: end; }
</style>

