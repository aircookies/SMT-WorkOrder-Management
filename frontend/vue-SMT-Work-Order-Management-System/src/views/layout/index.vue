JS

vue
<template>
    <el-container class="layout-container" style="height: auto">
        <el-header style="text-align: right; font-size: 12px">
            <div class="toolbar">
                <el-dropdown>
                    <el-icon style="margin-right: 8px; margin-top: 1px">
                        <setting />
                    </el-icon>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item>View</el-dropdown-item>
                            <el-dropdown-item>Add</el-dropdown-item>
                            <el-dropdown-item>Delete</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
                <span>Tom</span>
            </div>
        </el-header>

        <el-container>


            <el-aside width="250px">
                <el-scrollbar>
                    <el-menu :default-openeds="[]" :default-active="$route.path" router>
                        <el-menu-item index="/home">
                            <template #title>
                                <el-icon>
                                    <House />
                                </el-icon>
                                主页
                            </template>
                        </el-menu-item>
                        <el-menu-item index="/product">
                            <template #title>
                                <el-icon>
                                    <Cpu />
                                </el-icon>
                                产品管理
                            </template>
                        </el-menu-item>
                        <el-menu-item index="/line">
                            <template #title>
                                <el-icon>
                                    <Van />
                                </el-icon>
                                产线管理
                            </template>
                        </el-menu-item>
                        <el-menu-item index="/workorder">
                            <template #title>
                                <el-icon>
                                    <Document />
                                </el-icon>
                                工单管理
                            </template>
                        </el-menu-item>
                        <el-menu-item index="/report">
                            <template #title>
                                <el-icon>
                                    <Edit />
                                </el-icon>
                                生产报工
                            </template>
                        </el-menu-item>
                        <el-menu-item index="/data-list">
                            <template #title>
                                <el-icon>
                                    <DataAnalysis />
                                </el-icon>
                                数据报表
                            </template>
                        </el-menu-item>
                        <el-menu-item index="/system">
                            <template #title>
                                <el-icon>
                                    <Operation />
                                </el-icon>
                                系统管理
                            </template>
                        </el-menu-item>
                    </el-menu>
                </el-scrollbar>
            </el-aside>
            <el-main>
                <RouterView></RouterView>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { RouterView, useRouter } from 'vue-router'
import { Cpu, Van, Document, Edit, DataAnalysis, Setting, Operation, House } from '@element-plus/icons-vue'

const router = useRouter()
</script>

<style scoped>
.layout-container .el-header {
    position: relative;
    background: linear-gradient(45deg, #8ec5fc 0.000%, #8dd3ff 25.000%, #a1d8ff 50.000%, #c1d2ff 75.000%, #e0c3ff 100.000%);
    color: var(--el-text-color-primary);
}

.layout-container .el-aside {
    color: var(--el-text-color-primary);
    border-right: gainsboro 1px solid;
}

.layout-container .el-menu {
    border-right: none;
}

.layout-container .el-main {
    padding: 0;
}

.layout-container .toolbar {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    right: 20px;
}
</style>