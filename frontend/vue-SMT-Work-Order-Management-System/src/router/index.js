import {createRouter, createWebHistory} from "vue-router";

import layout from "@/views/layout/index.vue"; // 布局组件（静态导入，首屏必需）

/**
 * 创建路由实例
 * 使用 HTML5 History 模式，需要后端配合配置回退路由
 * 子路由均采用动态导入（懒加载），按需加载页面组件以优化首屏性能
 */
const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "layout",
      component: layout,
      redirect: "/home", // 根路径自动重定向到首页
      children: [
        {
            path: "/home",
            name: "home",
            component: () => import("@/views/home/index.vue"),
        },
        {
          path: "/product",
          name: "product",
          component: () => import("@/views/product/index.vue"),
        },
        {
          path: "/workorder",
          name: "workorder",
          component: () => import("@/views/workorder/index.vue"),
        },
        {
          path: "/line",
          name: "line",
          component: () => import("@/views/line/index.vue"),
        },
        {
          path: "/data-list",
          name: "data-list",
          component: () => import("@/views/datalist/index.vue"),
        },
        {
          path: "/report",
          name: "report",
          component: () => import("@/views/report/index.vue"),
        },
        // 系统管理模块
        {
          path: "/system/user-management",
          name: "user-management",
          component: () => import("@/views/system/user_management/index.vue"),
        },
        {
          path: "/system/role-management",
          name: "role-management",
          component: () => import("@/views/system/role_management/index.vue"),
        },
        {
          path: "/system/department_management",
          name: "department-management",
          component: () => import("@/views/system/department_management/index.vue"),
        }
      ],
    },
    // 登录页面独立于布局之外，不使用 layout 壳
    {
      path: "/login",
      name: "login",
      component: () => import("@/views/login/index.vue"),
    }
  ],
});

export default router