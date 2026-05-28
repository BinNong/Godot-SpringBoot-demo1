<template>
  <div class="config-page">
    <!-- Header -->
    <header class="header pixel-box">
      <div class="header-left">
        <router-link to="/home" class="pixel-nav-btn">← 返回</router-link>
      </div>
      <div class="header-center">
        <div class="title-icon">⚙️</div>
        <div class="page-title">游戏配置</div>
      </div>
      <div class="header-right">
        <button @click="handleLogout" class="pixel-btn pixel-btn--danger pixel-btn--small">退出</button>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Terminal Block -->
      <div class="terminal-section">
        <div class="terminal-block">
          <pre>{{ configContent }}</pre>
        </div>
        <button @click="copyConfig" class="pixel-btn pixel-btn--gold copy-btn">
          {{ copied ? '✓ 已复制!' : '📋 复制' }}
        </button>
      </div>

      <!-- Instructions -->
      <div class="instructions-section">
        <div class="instruction-box pixel-box">
          <div class="instruction-header">
            <span class="blink">▶</span> 配置说明
          </div>
          <div class="instruction-steps">
            <div class="step">
              <div class="step-num">1</div>
              <div class="step-text">打开 Godot 引擎</div>
            </div>
            <div class="step">
              <div class="step-num">2</div>
              <div class="step-text">找到或创建 player_config.cfg 文件</div>
            </div>
            <div class="step">
              <div class="step-num">3</div>
              <div class="step-text">粘贴上方配置内容并保存</div>
            </div>
            <div class="step">
              <div class="step-num">4</div>
              <div class="step-text">重新启动游戏使配置生效</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Warning Box -->
      <div class="warning-box pixel-box">
        <div class="warning-icon">⚠️</div>
        <div class="warning-text">
          请勿泄露你的认证令牌！<br>
          <span class="text-dim">像保护密码一样保护它</span>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useLeaderboardStore } from '../stores/leaderboard'

const router = useRouter()
const store = useLeaderboardStore()
const copied = ref(false)

const playerId = computed(() => store.userId || 1)
const authToken = computed(() => store.token || '')

const configContent = computed(() => {
  return `[player]
player_id=${playerId.value}
auth_token="${authToken.value}"`
})

function handleLogout() {
  store.setAuth('', 0, '')
  router.push('/login')
}

function copyConfig() {
  navigator.clipboard.writeText(configContent.value)
  copied.value = true
  setTimeout(() => { copied.value = false }, 2000)
}
</script>

<style scoped>
.config-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Header */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  margin: 16px;
  background: var(--color-bg-panel);
}

.header-center {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 20px;
}

.page-title {
  font-size: 18px;
  color: var(--color-secondary);
  text-shadow: 2px 2px 0 rgba(0,0,0,0.5);
}

/* Main Content */
.main-content {
  flex: 1;
  padding: 16px 24px;
  max-width: 700px;
  margin: 0 auto;
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* Terminal Section */
.terminal-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.terminal-block {
  background: #050505;
  box-shadow:
    inset 0 0 0 2px #222,
    0 0 30px rgba(0, 255, 100, 0.1);
  padding: 24px;
  position: relative;
}

.terminal-block::before {
  content: '● ● ●';
  position: absolute;
  top: 10px;
  left: 14px;
  font-size: 8px;
  color: #444;
  letter-spacing: 4px;
}

.terminal-block pre {
  font-family: 'Press Start 2P', monospace;
  font-size: 9px;
  color: #00ff00;
  background: transparent;
  margin: 0;
  padding: 0;
  padding-top: 20px;
  overflow-x: auto;
  line-height: 2.2;
  text-shadow: 0 0 6px rgba(0, 255, 0, 0.6);
}

.copy-btn {
  align-self: flex-start;
}

/* Instructions */
.instructions-section {
}

.instruction-box {
  background: var(--color-bg-panel);
  padding: 20px;
}

.instruction-header {
  font-size: 13px;
  color: var(--color-gold);
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.blink {
  animation: blink 1s infinite;
}

.instruction-steps {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.step {
  display: flex;
  align-items: center;
  gap: 16px;
}

.step-num {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-secondary);
  color: var(--color-bg-dark);
  font-size: 12px;
  font-family: 'Press Start 2P', monospace;
  flex-shrink: 0;
}

.step-text {
  font-size: 11px;
  color: var(--color-text);
}

/* Warning Box */
.warning-box {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: rgba(255, 107, 107, 0.1);
  border-left: 4px solid var(--color-primary);
}

.warning-icon {
  font-size: 24px;
}

.warning-text {
  font-size: 11px;
  color: var(--color-primary);
  line-height: 2;
}

.text-dim {
  color: var(--color-text-dim);
  font-size: 10px;
}
</style>
