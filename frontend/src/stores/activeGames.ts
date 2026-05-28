import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface ActiveGame {
  sessionId: string
  playerId: number
  username: string
  currentScore: number
  startTime: number
  durationSeconds: number
}

export const useActiveGamesStore = defineStore('activeGames', () => {
  const games = ref<ActiveGame[]>([])

  function setGames(g: ActiveGame[]) {
    games.value = g
  }

  return { games, setGames }
})
