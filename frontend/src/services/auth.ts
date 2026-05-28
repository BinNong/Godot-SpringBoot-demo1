const API_BASE = 'http://localhost:8080/api'

export interface LoginResponse {
  token: string
  userId: number
  username: string
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  })
  if (!res.ok) throw new Error('Login failed')
  return res.json()
}

export async function register(username: string, password: string): Promise<LoginResponse> {
  const res = await fetch(`${API_BASE}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || 'Registration failed')
  }
  return res.json()
}
