<script setup>
import index from './views/layout/index.vue';
import { useRouter, RouterView } from 'vue-router';

const router = useRouter();
</script>

<template>
  <RouterView></RouterView>
</template>

<style scoped></style>
