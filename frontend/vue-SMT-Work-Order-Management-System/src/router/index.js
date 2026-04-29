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
          path: "data-list",
          name: "data-list",
          component: () => import("@/views/datalist/index.vue"),
        },
        {
          path: "report",
          name: "report",
          component: () => import("@/views/report/index.vue"),
        },
        {
          path: "system",
          name: "system",
          component: () => import("@/views/system/index.vue"),
        }
      ],
    },
  ],
});

export default router