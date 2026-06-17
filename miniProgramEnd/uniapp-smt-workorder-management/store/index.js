/**
 * 简单的全局状态管理
 * 使用 Vue 3 reactive 实现响应式状态
 */

import { reactive } from 'vue'
import { getUserInfo, setUserInfo, removeUserInfo } from '../utils/auth'

// 全局状态
const state = reactive({
  // 当前登录用户信息
  user: null,

  // 角色名称映射（用于显示）
  roleNameMap: {
    '1': '系统管理员',
    '2': '生产计划员',
    '3': '车间操作员',
    '4': '企业管理层'
  },

  // 工单状态映射
  statusMap: {
    0: '待生产',
    1: '生产中',
    2: '已完工',
    3: '已关闭'
  },

  // 工单优先级映射
  priorityMap: {
    0: '低',
    1: '中',
    2: '高',
    3: '紧急'
  },

  // 工序名称映射
  processMap: {
    1: '印刷',
    2: '贴片',
    3: '回流焊'
  }
})

// ========== 用户相关操作 ==========

/** 从本地存储恢复用户信息到全局状态 */
const loadUser = () => {
  const info = getUserInfo()
  if (info) {
    state.user = info
  }
}

/** 设置用户信息（同时更新全局状态和本地存储） */
const setUser = (user) => {
  state.user = user
  setUserInfo(user)
}

/** 清除用户信息 */
const clearUser = () => {
  state.user = null
  removeUserInfo()
}

// 初始化时自动加载
loadUser()

export default {
  state,
  loadUser,
  setUser,
  clearUser
}
