<template>
    <div class="login-container">
        <el-card class="login-card">
            <template #header>
                <div class="card-header">
                    <h2>SMT工单管理系统</h2>
                </div>
            </template>
            
            <el-form 
                ref="loginFormRef"
                :model="loginForm"
                :rules="loginRules"
                label-width="80px"
                size="large"
            >
                <el-form-item label="用户名" prop="username">
                    <el-input 
                        v-model="loginForm.username"
                        placeholder="请输入用户名"
                        clearable
                        prefix-icon="User"
                    />
                </el-form-item>
                
                <el-form-item label="密码" prop="password">
                    <el-input 
                        v-model="loginForm.password"
                        type="password"
                        placeholder="请输入密码"
                        show-password
                        prefix-icon="Lock"
                        @keyup.enter="handleLogin"
                    />
                </el-form-item>
                
                <el-form-item>
                    <el-button 
                        type="primary" 
                        size="large"
                        style="width: 100%"
                        :loading="loading"
                        @click="handleLogin"
                    >
                        {{ loading ? '登录中...' : '登 录' }}
                    </el-button>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginApi } from '@/api/login'

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
        { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
    ]
}

/**
 * 处理登录
 * 
 * @async
 * @returns {Promise<void>}
 */
const handleLogin = async () => {
    // 表单验证
    if (!loginFormRef.value) return
    
    try {
        await loginFormRef.value.validate()
    } catch (error) {
        ElMessage.warning('请完善表单信息')
        return
    }
    
    // 开始登录
    loading.value = true
    
    try {
        const res = await loginApi(loginForm.username, loginForm.password)
        
        if (res.code === 200) {
            // 清空缓存
            localStorage.clear()

            // 保存token和用户信息到localStorage
            localStorage.setItem('token', res.data.token)
            localStorage.setItem('username', res.data.username)
            localStorage.setItem('name', res.data.name)
            localStorage.setItem('role', res.data.role)
            
            ElMessage.success('登录成功')
            
            // 跳转到首页
            setTimeout(() => {
                router.push('/home')
            }, 500)
        } else {
            ElMessage.error(res.message || '登录失败')
        }
    } catch (error) {
        console.error('登录错误:', error)
        ElMessage.error(error.response?.data?.message || '登录失败，请稍后重试')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
    width: 450px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    border-radius: 12px;
}

.card-header {
    text-align: center;
}

.card-header h2 {
    margin: 0;
    color: #303133;
    font-size: 24px;
    font-weight: 600;
}

:deep(.el-form-item__label) {
    font-weight: 500;
}

:deep(.el-input__wrapper) {
    box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px #c0c4cc inset;
}

:deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 1px #409eff inset;
}
</style>
