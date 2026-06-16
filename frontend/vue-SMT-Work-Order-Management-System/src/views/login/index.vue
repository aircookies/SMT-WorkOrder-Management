<template>
    <div class="login-container">
        <!-- 背景装饰 -->
        <div class="bg-decoration">
            <div class="circle circle-1"></div>
            <div class="circle circle-2"></div>
            <div class="circle circle-3"></div>
        </div>

        <!-- 登录卡片 -->
        <el-card class="login-background">
            <el-card class="login-card" shadow="never">
                <div class="login-header">
                    <div class="logo-icon">
                        <img src="@/assets/image/LOGO.webp" alt="logo" class="logo-image" width="80px" height="80px">
                    </div>
                    <h2 class="title">SMT工单管理系统</h2>
                    <p class="subtitle">Intelligent Manufacturing Terminal Work Order Management</p>
                </div>

                <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form" size="large">
                    <el-form-item prop="username">
                        <el-input v-model="loginForm.username" placeholder="请输入用户名" clearable>
                            <template #prefix>
                                <el-icon>
                                    <User />
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>

                    <el-form-item prop="password">
                        <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password
                            @keyup.enter="handleLogin">
                            <template #prefix>
                                <el-icon>
                                    <Lock />
                                </el-icon>
                            </template>
                        </el-input>
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" size="large" class="login-btn" :loading="loading"
                            @click="handleLogin">
                            <span v-if="!loading">登 录</span>
                            <span v-else>登录中...</span>
                        </el-button>
                    </el-form-item>
                </el-form>

                <div class="login-footer">
                    <p>2026信息工程学院项目式教学作业: SMT工单管理系统</p>
                </div>
            </el-card>
        </el-card>
    </div>
</template>

<script setup>
import {onBeforeMount, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {loginApi} from '@/api/login'
import {getPublicKey} from '@/utils/RSAUtil'

defineOptions({
    name: 'LoginView'
})

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

/**
 * 登录表单数据
 */
const loginForm = reactive({
    username: '',
    password: ''
})

/**
 * 表单验证规则
 */
const loginRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 30, message: '密码长度在6到30个字符', trigger: 'blur' }
    ]
}

/**
 * 处理登录
 * 
 * @async
 * @returns {Promise<>}
 */
const handleLogin = async () => {
    // 表单验证
    if (!loginFormRef.value) return

    try {
        await loginFormRef.value.validate()
    } catch (error) {
        ElMessage.warning('请完善表单信息')
        console.error('表单验证失败' + error)
        return
    }

    // 开始登录
    loading.value = true

    const res = await loginApi(loginForm.username, loginForm.password).catch((error) => {
        loading.value = false
        throw error
    })

    if (res.code === 200) {
        // 清空缓存
        localStorage.clear()

        // 保存token和用户信息到localStorage
        localStorage.setItem('userId', res.data.userId)
        localStorage.setItem('username', res.data.username)
        localStorage.setItem('name', res.data.name)
        localStorage.setItem('roleId', res.data.roleId)
        localStorage.setItem('roleName', res.data.roleName)

        ElMessage.success('登录成功')

        // 跳转到首页
        setTimeout(() => {
            router.push('/home')
        }, 1000)
    } else {
        ElMessage.error(res.message || '登录失败')
    }

}

// 组件创建时预获取公钥
onBeforeMount(async () => {
    await getPublicKey()
})
</script>

<style scoped>
.login-container {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: #f2efff;
    overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
    position: absolute;
    width: 100%;
    height: 100%;
    overflow: hidden;
    pointer-events: none;
}

.circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    animation: float 20s infinite ease-in-out;
}

.circle-1 {
    width: 300px;
    height: 300px;
    top: -150px;
    left: -150px;
    animation-delay: 0s;
}

.circle-2 {
    width: 200px;
    height: 200px;
    bottom: -100px;
    right: -100px;
    animation-delay: 5s;
}

.circle-3 {
    width: 150px;
    height: 150px;
    top: 50%;
    right: 10%;
    animation-delay: 10s;
}

@keyframes float {

    0%,
    100% {
        transform: translate(0, 0) scale(1);
    }

    33% {
        transform: translate(30px, -30px) scale(1.1);
    }

    66% {
        transform: translate(-20px, 20px) scale(0.9);
    }
}

.login-background {
    width: 95vw;
    height: 95vh;
    background-image: url('@/assets/image/login_background_image.webp');
    background-position: left;
    background-size: contain;
    background-repeat: no-repeat;
    background-color: #f9faff;
}

/* 登录卡片 */
.login-card {
    position: relative;
    z-index: 1;
    left: 60%;
    top: 10%;
    width: 480px;
    padding: 50px 40px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 4px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 头部区域 */
.login-header {
    text-align: center;
    margin-bottom: 40px;
}

.logo-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 20px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
}

@keyframes pulse {

    0%,
    100% {
        transform: scale(1);
        box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
    }

    50% {
        transform: scale(1.05);
        box-shadow: 0 12px 32px rgba(102, 126, 234, 0.6);
    }
}

.title {
    margin: 0 0 8px 0;
    font-size: 28px;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
}

.subtitle {
    margin: 0;
    font-size: 13px;
    color: #909399;
}

/* 表单样式 */
.login-form {
    margin-top: 30px;
}

:deep(.el-form-item) {
    margin-bottom: 24px;
}

:deep(.el-input__wrapper) {
    padding: 12px 16px;
    border-radius: 4px;
    box-shadow: 0 0 0 1px #dcdfe6 inset;
    transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px #c0c4cc inset;
    transform: translateY(-2px);
}

:deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #667eea inset;
}

:deep(.el-input__inner) {
    font-size: 15px;
}

:deep(.el-input__prefix) {
    color: #909399;
    margin-right: 8px;
}

/* 登录按钮 */
.login-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 2px;
    border-radius: 4px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
    transition: all 0.3s ease;
}

.login-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.6);
}

.login-btn:active {
    transform: translateY(0);
}

/* 底部版权 */
.login-footer {
    margin-top: 30px;
    text-align: center;
}

.login-footer p {
    margin: 0;
    font-size: 12px;
    color: #c0c4cc;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .login-card {
        width: 90%;
        padding: 40px 30px;
    }

    .title {
        font-size: 24px;
    }

    .subtitle {
        font-size: 12px;
    }

    .logo-icon {
        width: 70px;
        height: 70px;
    }

    .logo-icon .el-icon {
        font-size: 40px !important;
    }
}

@media (max-width: 480px) {
    .login-card {
        padding: 30px 20px;
    }

    .title {
        font-size: 22px;
    }

    .login-btn {
        height: 44px;
        font-size: 15px;
    }
}
</style>
