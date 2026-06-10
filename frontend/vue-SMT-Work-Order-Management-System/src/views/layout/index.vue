<template>
    <el-container class="layout-container" style="height: 100vh">
        <!-- 顶部导航栏 -->
        <el-header class="layout-header">
            <div class="header-left">
                <div class="logo">
                    <el-icon :size="28">
                        <Platform />
                    </el-icon>
                    <span>SMT工单管理系统</span>
                </div>
            </div>
            <div class="header-right">
                <div class="user-info">
                    <el-icon>
                        <UserFilled />
                    </el-icon>
                    <span class="user-name">{{ currentUserName() }}</span>
                    <el-tag size="small" type="primary" effect="plain" class="role-tag">{{ currentRoleName() }}</el-tag>
                </div>
                <el-divider direction="vertical" />
                <el-button type="danger" link @click="logoutConfirm" class="logout-btn">
                    <el-icon>
                        <SwitchButton />
                    </el-icon>
                    退出登录
                </el-button>
            </div>
        </el-header>

        <el-container class="main-container">
            <!-- 侧边栏菜单 -->
            <el-aside width="240px" class="layout-aside">
                <el-scrollbar>
                    <el-menu :default-openeds="[]" :default-active="$route.path" router class="sidebar-menu">
                        <el-menu-item index="/home" class="menu-item">
                            <el-icon>
                                <House />
                            </el-icon>
                            <span>主页</span>
                        </el-menu-item>

                        <el-menu-item index="/product" class="menu-item">
                            <el-icon>
                                <Box />
                            </el-icon>
                            <span>产品管理</span>
                        </el-menu-item>

                        <el-menu-item index="/line" class="menu-item">
                            <el-icon>
                                <Connection />
                            </el-icon>
                            <span>产线管理</span>
                        </el-menu-item>

                        <el-menu-item index="/workorder" class="menu-item">
                            <el-icon>
                                <Tickets />
                            </el-icon>
                            <span>工单管理</span>
                        </el-menu-item>

                        <el-menu-item index="/report" class="menu-item">
                            <el-icon>
                                <EditPen />
                            </el-icon>
                            <span>生产报工</span>
                        </el-menu-item>

                        <el-menu-item index="/data-list" class="menu-item">
                            <el-icon>
                                <DataAnalysis />
                            </el-icon>
                            <span>数据报表</span>
                        </el-menu-item>

                        <el-sub-menu index="/system-management" class="submenu">
                            <template #title>
                                <el-icon>
                                    <Setting />
                                </el-icon>
                                <span>系统管理</span>
                            </template>
                            <el-menu-item index="/system/user-management">
                                <el-icon>
                                    <User />
                                </el-icon>
                                <span>用户管理</span>
                            </el-menu-item>
                            <el-menu-item index="/system/department_management">
                                <el-icon>
                                    <OfficeBuilding />
                                </el-icon>
                                <span>部门管理</span>
                            </el-menu-item>
                        </el-sub-menu>
                    </el-menu>
                </el-scrollbar>
            </el-aside>

            <!-- 主内容区域 -->
            <el-main class="layout-main">
                <RouterView></RouterView>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { RouterView, useRouter } from 'vue-router'
import {
    Platform, House, Box, Connection, Tickets, EditPen, DataAnalysis,
    Setting, User, OfficeBuilding, UserFilled, SwitchButton
} from '@element-plus/icons-vue'
import { logoutApi } from '@/api/login'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

defineOptions({
    name: 'LayoutView'
})

// 获取当前用户姓名
const currentUserName = () => localStorage.getItem('name') ? localStorage.getItem('name') : '访客'

// 获取当前角色名
const currentRoleName = () => localStorage.getItem('roleName') ? localStorage.getItem('roleName') : ''

// 退出登录
const logout = async () => {
    const result = await logoutApi()

    if (result.code === 200) {
        ElMessage.success(result.message || '登出成功')
        // 登出成功后，重定向到登录页面
        setTimeout(() => {
            localStorage.clear()
            router.push('/login')
        }, 1000)
    } else {
        ElMessage.error(result.message || '登出失败')
    }
}

// 登出确认
const logoutConfirm = () => {
    ElMessageBox.confirm(
        '确定要退出登录吗？',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(() => {
            logout()
        })
}
</script>

<style scoped>
/* 整体容器 */
.layout-container {
    background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
}

/* 顶部导航栏 */
.layout-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
    background: linear-gradient(45deg, #232c5c 0.000%, #232c5c 20.000%, #574b92 calc(20.000% + 1px), #574b92 40.000%, #8966be calc(40.000% + 1px), #8966be 60.000%, #b87bdf calc(60.000% + 1px), #b87bdf 80.000%, #e289f4 calc(80.000% + 1px) 100.000%);
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
    height: 60px !important;
    line-height: 60px;
    z-index: 1000;
}

.header-left {
    display: flex;
    align-items: center;
}

.logo {
    display: flex;
    align-items: center;
    gap: 12px;
    color: #ffffff;
    font-size: 20px;
    font-weight: 600;
}

.logo .el-icon {
    font-size: 28px;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 16px;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #ffffff;
    font-size: 14px;
}

.user-info .el-icon {
    font-size: 18px;
}

.user-name {
    font-weight: 500;
}

.role-tag {
    background-color: rgba(255, 255, 255, 0.2) !important;
    border-color: rgba(255, 255, 255, 0.3) !important;
    color: #ffffff !important;
}

.logout-btn {
    color: #ffffff !important;
    font-size: 14px;
    transition: all 0.3s ease;
}

.logout-btn:hover {
    color: #ffd04b !important;
    transform: scale(1.05);
}

.logout-btn .el-icon {
    margin-right: 4px;
}

/* 主容器 */
.main-container {
    height: calc(100vh - 60px);
}

/* 侧边栏 */
.layout-aside {
    background: linear-gradient(180deg, #2d3748 0%, #1a202c 100%);
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
    overflow-y: auto;
}

.layout-aside::-webkit-scrollbar {
    width: 6px;
}

.layout-aside::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    /* border-radius: 3px; */
}

.layout-aside::-webkit-scrollbar-thumb:hover {
    background: rgba(255, 255, 255, 0.3);
}

/* 菜单样式 */
.sidebar-menu {
    border-right: none;
    background: transparent;
}

.menu-item {
    color: rgba(255, 255, 255, 0.8);
    border-left: 3px solid transparent;
    transition: all 0.3s ease;
    margin: 4px 8px;
    /* border-radius: 8px; */
}

.menu-item:hover {
    background-color: rgba(255, 255, 255, 0.1) !important;
    color: #ffffff !important;
    /* border-left-color: #667eea; */
}

.menu-item.is-active {
    background: linear-gradient(90deg, rgba(102, 126, 234, 0.3) 0%, rgba(118, 75, 162, 0.3) 100%) !important;
    color: #ffd04b !important;
    border-left-color: #ffd04b;
}

.menu-item .el-icon {
    font-size: 18px;
    margin-right: 12px;
}

.submenu {
    color: rgba(255, 255, 255, 0.8);
}

.submenu :deep(.el-sub-menu__title) {
    color: rgba(255, 255, 255, 0.8) !important;
    border-left: 3px solid transparent;
    transition: all 0.3s ease;
    margin: 4px 8px;
    /* border-radius: 8px; */
}

.submenu :deep(.el-sub-menu__title:hover) {
    background-color: rgba(255, 255, 255, 0.1) !important;
    color: #ffffff !important;
    /* border-left-color: #667eea; */
}

.submenu :deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
    background-color: rgba(255, 255, 255, 0.05) !important;
    color: #ffffff !important;
}

/* 子菜单容器样式 */
.submenu :deep(.el-menu--inline) {
    background-color: rgba(0, 0, 0, 0.15) !important;
}

/* 子菜单项样式 */
.submenu :deep(.el-menu-item) {
    background-color: transparent !important;
    color: rgba(255, 255, 255, 0.75) !important;
    padding-left: 56px !important;
    margin: 2px 8px;
    /* border-radius: 6px; */
    min-height: 48px;
    line-height: 48px;
    font-size: 14px;
    display: flex;
    align-items: center;
}

.submenu :deep(.el-menu-item:hover) {
    background-color: rgba(255, 255, 255, 0.1) !important;
    color: #ffffff !important;
}

.submenu :deep(.el-menu-item.is-active) {
    background: linear-gradient(90deg, rgba(102, 126, 234, 0.3) 0%, rgba(118, 75, 162, 0.3) 100%) !important;
    color: #ffd04b !important;
    border-left: 3px solid #ffd04b;
    margin-left: 5px;
}

.submenu :deep(.el-menu-item .el-icon) {
    font-size: 16px;
    margin-right: 10px;
    color: inherit !important;
}

.submenu :deep(.el-menu-item span) {
    color: inherit !important;
}

/* 主内容区域 */
.layout-main {
    padding: 0;
    background: transparent;
    overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .layout-header {
        padding: 0 16px;
    }

    .logo span {
        display: none;
    }

    .user-name {
        display: none;
    }

    .layout-aside {
        width: 64px !important;
    }

    .menu-item span,
    .submenu .el-sub-menu__title span {
        display: none;
    }

    .menu-item .el-icon,
    .submenu .el-icon {
        margin-right: 0;
        font-size: 20px;
    }

    .submenu-item {
        padding-left: 0 !important;
    }
}
</style>