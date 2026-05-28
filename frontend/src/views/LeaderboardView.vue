<template>
  <div class="leaderboard-page">
    <!-- Header -->
    <header class="header pixel-box">
      <div class="header-left">
        <router-link to="/home" class="pixel-nav-btn">← 返回</router-link>
      </div>
      <div class="header-center">
        <div class="title-icon">🏆</div>
        <div class="page-title">最高分榜</div>
      </div>
      <div class="header-right">
        <button @click="handleLogout" class="pixel-btn pixel-btn--danger pixel-btn--small">退出</button>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <!-- Arcade Cabinet Top -->
      <div class="cabinet-top">
        <div class="marquee-text">★ 街机冠军 ★</div>
      </div>

      <!-- Score Table -->
      <div class="score-table pixel-box">
        <div class="table-header">
          <div class="col-rank">排名</div>
          <div class="col-player">玩家</div>
          <div class="col-score">分数</div>
        </div>

        <div class="table-body">
          <div
            v-for="(entry, index) in store.entries"
            :key="entry.playerId"
            class="score-row"
            :class="{
              'score-row--gold': entry.rank === 1,
              'score-row--silver': entry.rank === 2,
              'score-row--bronze': entry.rank === 3,
              'animate-flash': recentlyUpdated.includes(entry.playerId)
            }"
            :style="{ animationDelay: `${index * 0.05}s` }"
          >
            <div class="col-rank">
              <span v-if="entry.rank <= 3" class="rank-badge" :class="`rank-badge--${entry.rank}`">
                {{ entry.rank }}
              </span>
              <span v-else class="rank-number">{{ entry.rank }}</span>
            </div>
            <div class="col-player">
              <span class="player-icon">{{ getPlayerIcon(entry.rank) }}</span>
              {{ entry.username }}
            </div>
            <div class="col-score">
              <span class="coin"></span>
              {{ formatScore(entry.totalScore) }}
            </div>
          </div>
        </div>

        <div v-if="store.entries.length === 0" class="no-data pixel-box-inner">
          <div class="no-data-text">暂无分数</div>
          <div class="no-data-sub">开始游戏吧！</div>
        </div>
      </div>

      <!-- Live Indicator -->
      <div class="live-indicator">
        <span class="pulse-dot"></span>
        <span class="live-text">实时</span>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useLeaderboardStore } from '../stores/leaderboard'
import { connectWebSocket, disconnectWebSocket } from '../services/websocket'

const API_BASE = 'http://localhost:8080/api'
const router = useRouter()
const store = useLeaderboardStore()
const recentlyUpdated = ref<number[]>([])

function handleLogout() {
  store.setAuth('', 0, '')
  router.push('/login')
}

function formatScore(score: number): string {
  return score.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function getPlayerIcon(rank: number): string {
  if (rank === 1) return '👑'
  if (rank === 2) return '🥈'
  if (rank === 3) return '🥉'
  return '🎮'
}

async function fetchInitialData() {
  try {
    const res = await fetch(`${API_BASE}/leaderboard`)
    if (res.ok) {
      const data = await res.json()
      store.setEntries(data)
    }
  } catch (e) {
    console.error('Failed to fetch initial leaderboard:', e)
  }
}

function handleWebSocketUpdate(data: any) {
  const newIds = data.map((e: any) => e.playerId)
  recentlyUpdated.value = newIds
  store.setEntries(data)
  setTimeout(() => {
    recentlyUpdated.value = []
  }, 600)
}

onMounted(() => {
  fetchInitialData()
  connectWebSocket(handleWebSocketUpdate)
})

onUnmounted(() => {
  disconnectWebSocket()
})
</script>

<style scoped>
.leaderboard-page {
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
  color: var(--color-gold);
  text-shadow: 2px 2px 0 rgba(0,0,0,0.5);
  animation: glow 2s ease-in-out infinite;
}

/* Main Content */
.main-content {
  flex: 1;
  padding: 16px 24px;
  max-width: 700px;
  margin: 0 auto;
  width: 100%;
}

/* Cabinet Top */
.cabinet-top {
  text-align: center;
  padding: 12px;
  margin-bottom: 16px;
}

.marquee-text {
  font-size: 12px;
  color: var(--color-secondary);
  animation: blink 2s infinite;
}

/* Score Table */
.score-table {
  background: var(--color-bg-panel);
  overflow: hidden;
}

.table-header {
  display: flex;
  padding: 12px 16px;
  background: linear-gradient(180deg, #333355 0%, #222244 100%);
  font-size: 11px;
  color: var(--color-gold);
  text-shadow: 1px 1px 0 rgba(0,0,0,0.5);
}

.table-body {
  padding: 8px;
}

.score-row {
  display: flex;
  padding: 12px 16px;
  margin-bottom: 4px;
  background: var(--color-bg-card);
  align-items: center;
  transition: background 0.2s;
}

.score-row:hover {
  background: rgba(78, 205, 196, 0.1);
}

.score-row--gold {
  background: linear-gradient(90deg, rgba(255, 215, 0, 0.15) 0%, var(--color-bg-card) 100%);
  border-left: 4px solid var(--color-rank-gold);
}

.score-row--silver {
  background: linear-gradient(90deg, rgba(192, 192, 192, 0.1) 0%, var(--color-bg-card) 100%);
  border-left: 4px solid var(--color-rank-silver);
}

.score-row--bronze {
  background: linear-gradient(90deg, rgba(205, 127, 50, 0.1) 0%, var(--color-bg-card) 100%);
  border-left: 4px solid var(--color-rank-bronze);
}

.col-rank {
  width: 80px;
  font-size: 12px;
}

.col-player {
  flex: 1;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.col-score {
  width: 150px;
  font-size: 12px;
  color: var(--color-gold);
  display: flex;
  align-items: center;
  gap: 8px;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  font-size: 12px;
  font-weight: bold;
  border-radius: 4px;
}

.rank-badge--1 {
  background: linear-gradient(135deg, #ffd700 0%, #ffaa00 100%);
  color: #000;
  box-shadow: 0 0 8px rgba(255, 215, 0, 0.5);
}

.rank-badge--2 {
  background: linear-gradient(135deg, #e0e0e0 0%, #a0a0a0 100%);
  color: #000;
  box-shadow: 0 0 8px rgba(192, 192, 192, 0.5);
}

.rank-badge--3 {
  background: linear-gradient(135deg, #cd7f32 0%, #a05a20 100%);
  color: #fff;
  box-shadow: 0 0 8px rgba(205, 127, 50, 0.5);
}

.rank-number {
  color: var(--color-text-dim);
  font-size: 11px;
}

.player-icon {
  font-size: 14px;
}

/* No Data */
.no-data {
  text-align: center;
  padding: 48px 24px;
  background: var(--color-bg-card);
  margin: 8px;
}

.no-data-text {
  font-size: 16px;
  color: var(--color-text-dim);
  margin-bottom: 8px;
}

.no-data-sub {
  font-size: 13px;
  color: var(--color-primary);
}

/* Live Indicator */
.live-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 24px;
}

.live-text {
  font-size: 11px;
  color: var(--color-green);
  animation: blink 1.5s infinite;
}
</style>
