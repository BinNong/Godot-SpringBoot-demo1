import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'

const WS_URL = 'http://localhost:8080/ws'

let client: Client | null = null

export function connectWebSocket(onMessage: (data: any) => void): Client {
  if (client && client.connected) {
    return client
  }

  client = new Client({
    webSocketFactory: () => new SockJS(WS_URL) as any
  })

  client.onConnect = () => {
    client!.subscribe('/topic/leaderboard', (message) => {
      onMessage(JSON.parse(message.body))
    })
  }

  client.activate()
  return client
}

export function disconnectWebSocket() {
  if (client) {
    client.deactivate()
    client = null
  }
}

let activeGamesClient: Client | null = null

export function connectActiveGamesWebSocket(onMessage: (data: any) => void): Client {
  if (activeGamesClient && activeGamesClient.connected) {
    return activeGamesClient
  }

  activeGamesClient = new Client({
    webSocketFactory: () => new SockJS(WS_URL) as any
  })

  activeGamesClient.onConnect = () => {
    activeGamesClient!.subscribe('/topic/active-games', (message) => {
      onMessage(JSON.parse(message.body))
    })
  }

  activeGamesClient.activate()
  return activeGamesClient
}

export function disconnectActiveGamesWebSocket() {
  if (activeGamesClient) {
    activeGamesClient.deactivate()
    activeGamesClient = null
  }
}
