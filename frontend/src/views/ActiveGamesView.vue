<template>
  <div class="active-page">
    <!-- Header -->
    <header class="header pixel-box">
      <div class="header-left">
        <router-link to="/home" class="pixel-nav-btn">← 返回</router-link>
      </div>
      <div class="header-center">
        <div class="title-icon">👾</div>
        <div class="page-title">当前玩家</div>
      </div>
      <div class="header-right">
        <button @click="handleLogout" class="pixel-btn pixel-btn--danger pixel-btn--small">退出</button>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Online Counter -->
      <div class="online-counter pixel-box">
        <div class="counter-icon">
          <span class="pulse-dot"></span>
        </div>
        <div class="counter-text">
          <span class="counter-value">{{ store.games.length }}</span>
          <span class="counter-label">位玩家正在游戏</span>
        </div>
      </div>

      <!-- Players Grid -->
      <div class="players-grid" v-if="store.games.length > 0">
        <div
          v-for="game in store.games"
          :key="game.sessionId"
          class="player-card pixel-card"
          :class="{ 'score-pop': scorePopKey === game.sessionId }"
        >
          <div class="player-card__header">
            <div class="player-avatar">{{ getPlayerAvatar(game.sessionId) }}</div>
            <div class="player-info">
              <div class="player-name">{{ game.username }}</div>
              <div class="player-status">
                <span class="pulse-dot"></span>
                游戏中
              </div>
            </div>
          </div>

          <div class="player-card__stats">
            <div class="stat">
              <div class="stat-icon">🪙</div>
              <div class="stat-value" :key="game.currentScore">{{ game.currentScore }}</div>
              <div class="stat-label">当前分数</div>
            </div>
            <div class="stat">
              <div class="stat-icon">⏱️</div>
              <div class="stat-value">{{ formatDuration(game.durationSeconds) }}</div>
              <div class="stat-label">游戏时间</div>
            </div>
          </div>

          <div class="player-card__decoration">
            <span class="coin coin--spinning"></span>
            <span class="coin coin--spinning" style="animation-delay: 0.2s"></span>
            <span class="coin coin--spinning" style="animation-delay: 0.4s"></span>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="empty-state pixel-box">
        <div class="empty-icon">🎮</div>
        <div class="empty-title">暂无玩家</div>
        <div class="empty-sub">快来开始游戏吧！</div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useActiveGamesStore } from '../stores/activeGames'
import { connectActiveGamesWebSocket, disconnectActiveGamesWebSocket } from '../services/websocket'

const API_BASE = 'http://localhost:8080/api'
const router = useRouter()
const store = useActiveGamesStore()

const avatars = ['🎮', '🚀', '⭐', '🔥', '💎', '🌟', '⚡', '🎯']
const avatarMap = new Map<string, string>()
const previousScores = new Map<string, number>()
const scorePopKey = ref<string | null>(null)

function getPlayerAvatar(sessionId: string): string {
  if (!avatarMap.has(sessionId)) {
    avatarMap.set(sessionId, avatars[Math.floor(Math.random() * avatars.length)])
  }
  return avatarMap.get(sessionId)!
}

function formatDuration(seconds: number): string {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

function handleLogout() {
  router.push('/login')
}

function checkScoreChanges(data: any[]) {
  for (const game of data) {
    const prevScore = previousScores.get(game.sessionId)
    if (prevScore !== undefined && game.currentScore > prevScore) {
      scorePopKey.value = game.sessionId
      setTimeout(() => {
        scorePopKey.value = null
      }, 500)
      break
    }
    previousScores.set(game.sessionId, game.currentScore)
  }
  // Remove games that no longer exist
  const currentIds = new Set(data.map(g => g.sessionId))
  for (const id of previousScores.keys()) {
    if (!currentIds.has(id)) {
      previousScores.delete(id)
      avatarMap.delete(id)
    }
  }
}

async function fetchInitialData() {
  try {
    const res = await fetch(`${API_BASE}/game/active`)
    if (res.ok) {
      const data = await res.json()
      store.setGames(data)
      // Initialize previous scores
      for (const game of data) {
        previousScores.set(game.sessionId, game.currentScore)
      }
    }
  } catch (e) {
    console.error('Failed to fetch active games:', e)
  }
}

onMounted(() => {
  fetchInitialData()
  connectActiveGamesWebSocket((data) => {
    checkScoreChanges(data)
    store.setGames(data)
  })
})

onUnmounted(() => {
  disconnectActiveGamesWebSocket()
})
</script>

<style scoped>
.active-page {
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
  color: var(--color-green);
  text-shadow: 2px 2px 0 rgba(0,0,0,0.5);
}

/* Main Content */
.main-content {
  flex: 1;
  padding: 16px 24px;
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
}

/* Online Counter */
.online-counter {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 24px;
  background: var(--color-bg-panel);
  margin-bottom: 24px;
}

.counter-icon {
  font-size: 24px;
}

.counter-text {
  display: flex;
  flex-direction: column;
}

.counter-value {
  font-size: 30px;
  color: var(--color-green);
  text-shadow: 2px 2px 0 rgba(0,0,0,0.5);
}

.counter-label {
  font-size: 11px;
  color: var(--color-text-dim);
}

/* Players Grid */
.players-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.player-card {
  background: var(--color-bg-card);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.player-card__header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.player-avatar {
  font-size: 32px;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-panel);
  box-shadow: inset 0 0 0 2px var(--color-border-dark);
}

.player-info {
  flex: 1;
}

.player-name {
  font-size: 13px;
  color: var(--color-gold);
  text-shadow: 1px 1px 0 rgba(0,0,0,0.5);
  margin-bottom: 4px;
}

.player-status {
  font-size: 10px;
  color: var(--color-green);
  display: flex;
  align-items: center;
  gap: 6px;
}

.player-card__stats {
  display: flex;
  gap: 16px;
}

.stat {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px;
  background: var(--color-bg-panel);
}

.stat-icon {
  font-size: 16px;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 15px;
  color: var(--color-secondary);
  text-shadow: 1px 1px 0 rgba(0,0,0,0.5);
}

.stat-label {
  font-size: 9px;
  color: var(--color-text-dim);
  margin-top: 4px;
}

.player-card__decoration {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  gap: 4px;
}

/* Score Pop Animation */
.score-pop {
  animation: score-pop 0.5s ease-out;
}

.score-pop .stat-value {
  animation: score-glow 0.5s ease-out;
}

@keyframes score-pop {
  0% { transform: scale(1); }
  30% { transform: scale(1.08); }
  100% { transform: scale(1); }
}

@keyframes score-glow {
  0% {
    color: #ffcc00;
    text-shadow: 0 0 10px #ffcc00, 2px 2px 0 rgba(0,0,0,0.5);
    transform: scale(1.3);
  }
  100% {
    color: var(--color-secondary);
    text-shadow: 1px 1px 0 rgba(0,0,0,0.5);
    transform: scale(1);
  }
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 64px 24px;
  background: var(--color-bg-panel);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-title {
  font-size: 15px;
  color: var(--color-text-dim);
  margin-bottom: 8px;
}

.empty-sub {
  font-size: 13px;
  color: var(--color-secondary);
}
</style>
