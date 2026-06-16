<script setup>
import {RouterView} from 'vue-router'
// 引入 Element Plus 中文本地化语言包
import zhCn from 'element-plus/es/locale/lang/zh-cn'
</script>

<template>
  <!-- Element Plus 全局配置提供者，统一设置组件语言为中文 -->
  <el-config-provider :locale="zhCn">
    <!-- 路由视图，根据当前路由动态渲染对应页面组件 -->
    <RouterView></RouterView>
  </el-config-provider>
</template>

<style scoped></style>