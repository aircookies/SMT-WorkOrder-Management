import {createApp} from 'vue'
import router from './router'
// Element Plus 采用 unplugin-vue-components 按需导入，无需全局注册
// import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

// 全局注册 Element Plus 图标库，避免在每个组件中重复导入图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 遍历注册所有 Element Plus 图标为全局组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 全局错误处理器，捕获未处理的组件异常
app.config.errorHandler = (err, instance, info) => {
    console.error('Vue 全局错误:', err)
    console.error('错误组件实例:', instance)
    console.error('错误信息:', info)
}

// 注册路由插件
app.use(router)

// 挂载应用到 #app 根节点
app.mount('#app')