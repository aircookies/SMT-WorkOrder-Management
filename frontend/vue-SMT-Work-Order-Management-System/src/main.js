import {createApp} from 'vue'
import router from './router'
// import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'


import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}


app.config.errorHandler = (err, instance, info) => {
    console.error('Vue 全局错误:', err)
    console.error('错误组件实例:', instance)
    console.error('错误信息:', info)
}

// app.use(ElementPlus)
app.use(router)
app.mount('#app')