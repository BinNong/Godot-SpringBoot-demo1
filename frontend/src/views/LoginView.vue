<template>
  <div class="login-page">
    <div class="stars-bg"></div>

    <div class="login-container">
      <!-- Title -->
      <div class="title-section animate-bounce-in">
        <div class="game-title">像素<br>跑酷</div>
        <div class="subtitle">街机版</div>
      </div>

      <!-- High Score Display -->
      <div class="highscore-box pixel-box">
        <div class="highscore-label">最高分</div>
        <div class="highscore-value">88880</div>
      </div>

      <!-- Form -->
      <div class="form-section pixel-card">
        <div class="form-title">
          <span class="blink">▶</span> 投入硬币
        </div>

        <div class="input-group">
          <input
            v-model="username"
            type="text"
            class="pixel-input"
            placeholder="玩家名称"
            maxlength="12"
            @keyup.enter="handleLogin"
          />
        </div>

        <div class="input-group">
          <input
            v-model="password"
            type="password"
            class="pixel-input"
            placeholder="密码"
            @keyup.enter="handleLogin"
          />
        </div>

        <p v-if="error" class="error-msg">{{ error }}</p>

        <div class="button-group">
          <button @click="handleLogin" class="pixel-btn pixel-btn--gold">
            开始游戏
          </button>
          <button @click="handleRegister" class="pixel-btn pixel-btn--secondary pixel-btn--small">
            注册
          </button>
        </div>
      </div>

      <!-- Credits -->
      <div class="credits">
        <span class="text-dim">2026 像素工作室</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '../services/auth'
import { useLeaderboardStore } from '../stores/leaderboard'

const router = useRouter()
const store = useLeaderboardStore()

const username = ref('')
const password = ref('')
const error = ref('')

async function handleLogin() {
  error.value = ''
  if (!username.value || !password.value) {
    error.value = '! 请填写所有字段'
    return
  }
  try {
    const res = await login(username.value, password.value)
    store.setAuth(res.token, res.userId, res.username)
    router.push('/home')
  } catch (e: any) {
    error.value = e.message || '! 登录失败'
  }
}

async function handleRegister() {
  error.value = ''
  if (!username.value || !password.value) {
    error.value = '! 请填写所有字段'
    return
  }
  try {
    const res = await register(username.value, password.value)
    store.setAuth(res.token, res.userId, res.username)
    router.push('/home')
  } catch (e: any) {
    error.value = e.message || '! 注册失败'
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
  padding: 32px;
  z-index: 1;
  width: 100%;
  max-width: 400px;
}

/* Title */
.title-section {
  text-align: center;
}

.game-title {
  font-size: 38px;
  color: var(--color-gold);
  text-shadow:
    4px 4px 0 var(--color-primary),
    8px 8px 0 rgba(0,0,0,0.3);
  line-height: 1.4;
  animation: glow 2s ease-in-out infinite;
  letter-spacing: 4px;
}

.subtitle {
  font-size: 13px;
  color: var(--color-secondary);
  margin-top: 12px;
  letter-spacing: 8px;
}

/* High Score Box */
.highscore-box {
  text-align: center;
  padding: 16px 32px;
  background: var(--color-bg-panel);
}

.highscore-label {
  font-size: 11px;
  color: var(--color-text-dim);
  margin-bottom: 8px;
}

.highscore-value {
  font-size: 24px;
  color: var(--color-gold);
  text-shadow: 2px 2px 0 rgba(0,0,0,0.5);
}

/* Form Section */
.form-section {
  width: 100%;
  padding: 24px;
}

.form-title {
  font-size: 13px;
  color: var(--color-secondary);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.blink {
  animation: blink 1s infinite;
}

.input-group {
  margin-bottom: 12px;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 20px;
}

.error-msg {
  font-size: 11px;
  color: var(--color-primary);
  margin-top: 12px;
  text-align: center;
  animation: blink 0.5s infinite;
}

/* Credits */
.credits {
  font-size: 9px;
  margin-top: 16px;
  letter-spacing: 2px;
}
</style>
