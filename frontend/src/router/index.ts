import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import HomeView from '../views/HomeView.vue'
import GodotConfigView from '../views/GodotConfigView.vue'
import LeaderboardView from '../views/LeaderboardView.vue'
import ActiveGamesView from '../views/ActiveGamesView.vue'
import { useLeaderboardStore } from '../stores/leaderboard'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/home',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/godot-config',
      name: 'godot-config',
      component: GodotConfigView,
      meta: { requiresAuth: true }
    },
    {
      path: '/leaderboard',
      name: 'leaderboard',
      component: LeaderboardView,
      meta: { requiresAuth: true }
    },
    {
      path: '/active-games',
      name: 'active-games',
      component: ActiveGamesView,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const store = useLeaderboardStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !store.token) {
    next('/login')
  } else if (to.path === '/login' && store.token) {
    next('/home')
  } else {
    next()
  }
})

export default router
