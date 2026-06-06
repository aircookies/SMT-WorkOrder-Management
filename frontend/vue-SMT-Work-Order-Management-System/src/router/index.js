import { createRouter, createWebHistory } from "vue-router";

import layout from "@/views/layout/index.vue";  // 布局组件

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "layout",
      component: layout,
      redirect: "/home",
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
    {
      path: "/login",
      name: "login",
      component: () => import("@/views/login/index.vue"),
    }
  ],
});

export default router