import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface LeaderboardEntry {
  rank: number
  playerId: number
  username: string
  totalScore: number
}

export const useLeaderboardStore = defineStore('leaderboard', () => {
  const entries = ref<LeaderboardEntry[]>([])
  const token = ref<string>('')
  const userId = ref<number>(0)
  const username = ref<string>('')

  function setAuth(t: string, uid: number, uname: string) {
    token.value = t
    userId.value = uid
    username.value = uname
  }

  function setEntries(e: LeaderboardEntry[]) {
    entries.value = e
  }

  return { entries, token, userId, username, setAuth, setEntries }
})
