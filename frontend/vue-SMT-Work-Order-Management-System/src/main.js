import {createApp} from 'vue'
import router from './router'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import App from './App.vue'
// import { ElMessage } from 'element-plus'

const app = createApp(App)

// 配置全局错误处理器
app.config.errorHandler = (err, instance, info) => {
    console.error('Vue 全局错误:', err)
    console.error('错误组件实例:', instance)
    console.error('错误信息:', info)

    // 向用户显示提示信息
    // ElMessage.error('系统发生错误，请稍后重试')
}

app.use(router)
app.use(ElementPlus, {locale: zhCn})
app.mount('#app')