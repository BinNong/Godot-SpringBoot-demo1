<template>
  <div class="home-page">
    <!-- Header -->
    <header class="header pixel-box">
      <div class="header-left">
        <div class="logo">
          <span class="coin"></span>
          像素跑酷
        </div>
      </div>
      <div class="header-right">
        <span class="heart">♥</span>
        <span class="heart">♥</span>
        <span class="heart">♥</span>
        <span class="player-name">{{ username }}</span>
        <button @click="logout" class="pixel-btn pixel-btn--danger pixel-btn--small">
          退出
        </button>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Welcome Banner -->
      <div class="welcome-banner animate-slide-up">
        <div class="welcome-text">欢迎回来，</div>
        <div class="player-title">{{ username }}</div>
        <div class="welcome-sub">
          <span class="coin coin--spinning"></span>
          准备开始游戏了吗？
          <span class="coin coin--spinning"></span>
        </div>
      </div>

      <!-- Navigation Cards -->
      <div class="nav-grid">
        <router-link to="/godot-config" class="nav-card nav-card--orange animate-slide-up" style="animation-delay: 0.1s">
          <div class="nav-card__icon">⚙</div>
          <div class="nav-card__title">游戏配置</div>
          <div class="nav-card__desc">设置你的游戏</div>
          <div class="nav-card__decoration">●●●</div>
        </router-link>

        <router-link to="/leaderboard" class="nav-card nav-card--gold animate-slide-up" style="animation-delay: 0.2s">
          <div class="nav-card__icon">🏆</div>
          <div class="nav-card__title">排行榜</div>
          <div class="nav-card__desc">查看最高分</div>
          <div class="nav-card__decoration">●●●</div>
        </router-link>

        <router-link to="/active-games" class="nav-card nav-card--green animate-slide-up" style="animation-delay: 0.3s">
          <div class="nav-card__icon">👾</div>
          <div class="nav-card__title">活跃游戏</div>
          <div class="nav-card__desc">当前玩家</div>
          <div class="nav-card__decoration">●●●</div>
        </router-link>
      </div>

      <!-- Stats Row -->
      <div class="stats-row animate-slide-up" style="animation-delay: 0.4s">
        <div class="stat-box pixel-box-inner">
          <div class="stat-value">01</div>
          <div class="stat-label">等级</div>
        </div>
        <div class="stat-box pixel-box-inner">
          <div class="stat-value">🔥</div>
          <div class="stat-label">连胜</div>
        </div>
        <div class="stat-box pixel-box-inner">
          <div class="stat-value">⭐</div>
          <div class="stat-label">排名</div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
      <span class="text-dim">按开始键继续</span>
      <span class="blink">▌</span>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useLeaderboardStore } from '../stores/leaderboard'

const router = useRouter()
const store = useLeaderboardStore()

const username = computed(() => store.username || '玩家')

function logout() {
  store.setAuth('', 0, '')
  router.push('/login')
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Header */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  margin: 16px;
  background: var(--color-bg-panel);
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  font-size: 14px;
  color: var(--color-gold);
  display: flex;
  align-items: center;
  gap: 8px;
  text-shadow: 2px 2px 0 rgba(0,0,0,0.5);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.heart {
  animation: pulse 1.5s ease-in-out infinite;
}

.heart:nth-child(2) { animation-delay: 0.2s; }
.heart:nth-child(3) { animation-delay: 0.4s; }

.player-name {
  font-size: 11px;
  color: var(--color-secondary);
  margin-right: 8px;
}

/* Main Content */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 16px;
  gap: 32px;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

/* Welcome Banner */
.welcome-banner {
  text-align: center;
  padding: 24px;
}

.welcome-text {
  font-size: 13px;
  color: var(--color-text-dim);
}

.player-title {
  font-size: 26px;
  color: var(--color-gold);
  text-shadow: 3px 3px 0 rgba(0,0,0,0.3);
  margin: 8px 0;
}

.welcome-sub {
  font-size: 13px;
  color: var(--color-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

/* Nav Grid */
.nav-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  width: 100%;
}

@media (max-width: 600px) {
  .nav-grid {
    grid-template-columns: 1fr;
  }
}

.nav-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  text-align: center;
  text-decoration: none;
  position: relative;
  overflow: hidden;
  transition: transform 0.2s;
}

.nav-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.nav-card--orange {
  background: #ff9f43;
  box-shadow:
    0 0 0 2px var(--color-bg-dark),
    0 0 0 4px var(--color-border),
    6px 6px 0 4px rgba(0,0,0,0.3);
}

.nav-card--orange::before {
  background: #ffcc00;
}

.nav-card--gold {
  background: var(--color-gold);
  box-shadow:
    0 0 0 2px var(--color-bg-dark),
    0 0 0 4px var(--color-border),
    6px 6px 0 4px rgba(0,0,0,0.3);
}

.nav-card--gold::before {
  background: #fff9e6;
}

.nav-card--green {
  background: var(--color-green);
  box-shadow:
    0 0 0 2px var(--color-bg-dark),
    0 0 0 4px var(--color-border),
    6px 6px 0 4px rgba(0,0,0,0.3);
}

.nav-card--green::before {
  background: #a8e6b0;
}

.nav-card:hover {
  transform: translate(-4px, -4px);
}

.nav-card--orange:hover {
  box-shadow:
    0 0 0 2px var(--color-bg-dark),
    0 0 0 4px var(--color-border),
    10px 10px 0 4px rgba(0,0,0,0.3);
}

.nav-card--gold:hover {
  box-shadow:
    0 0 0 2px var(--color-bg-dark),
    0 0 0 4px var(--color-border),
    10px 10px 0 4px rgba(0,0,0,0.3);
}

.nav-card--green:hover {
  box-shadow:
    0 0 0 2px var(--color-bg-dark),
    0 0 0 4px var(--color-border),
    10px 10px 0 4px rgba(0,0,0,0.3);
}

.nav-card__icon {
  font-size: 40px;
  margin-bottom: 16px;
}

.nav-card__title {
  font-size: 22px;
  color: var(--color-bg-dark);
  text-shadow: 1px 1px 0 rgba(255,255,255,0.3);
  margin-bottom: 4px;
}

.nav-card__desc {
  font-size: 9px;
  color: rgba(0,0,0,0.6);
}

.nav-card__decoration {
  position: absolute;
  bottom: 8px;
  font-size: 8px;
  color: rgba(0,0,0,0.4);
  letter-spacing: 4px;
}

/* Stats Row */
.stats-row {
  display: flex;
  gap: 16px;
  width: 100%;
  justify-content: center;
}

.stat-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 24px;
  min-width: 100px;
  background: var(--color-bg-card);
}

.stat-value {
  font-size: 20px;
  color: var(--color-gold);
}

.stat-label {
  font-size: 9px;
  color: var(--color-text-dim);
  margin-top: 4px;
}

/* Footer */
.footer {
  padding: 16px;
  text-align: center;
  font-size: 11px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.blink {
  animation: blink 1s infinite;
  color: var(--color-secondary);
}
</style>
