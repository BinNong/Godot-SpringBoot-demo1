<template>
  <div id="app-root">
    <router-view />
    <div class="crt-overlay"></div>
  </div>
</template>

<script setup lang="ts">
</script>

<style>
#app-root {
  min-height: 100vh;
  background: linear-gradient(180deg, #0f0f23 0%, #1a1a2e 50%, #0f0f23 100%);
  position: relative;
}
</style>
