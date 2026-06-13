<template>
    <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
            <h1 class="page-title">
                <el-icon class="title-icon">
                    <Connection />
                </el-icon>
                产线管理
            </h1>
            <p class="page-subtitle">管理和维护生产线信息</p>
        </div>

        <!-- 工具栏 -->
        <el-card class="toolbar-card" shadow="hover">
            <div class="toolbar">
                <div class="toolbar-actions">
                    <el-button type="success" @click="showDialog()" :icon="Plus" plain>新增产线</el-button>
                </div>
            </div>
        </el-card>

        <!-- 数据表格 -->
        <el-card class="table-card" shadow="hover">
            <div class="table-wrapper">
                <el-table v-loading="loading" :data="tableData" row-key="id" table-layout="fixed" style="width: 100%"
                    stripe border class="custom-table">
                    <el-table-column prop="name" label="产线名称" min-width="200" align="center">
                        <template #default="{ row }">
                            <el-tag type="info" effect="plain">{{ row.name }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="description" label="产线描述" min-width="300" show-overflow-tooltip
                        align="center" />
                    <el-table-column prop="updateTime" label="更新时间" min-width="180" align="center">
                        <template #default="{ row }">
                            <el-icon>
                                <Clock />
                            </el-icon>
                            {{ row.updateTime }}
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="150" align="center" fixed="right">
                        <template #default="scope">
                            <el-button type="primary" :icon="Edit" size="small" @click="showDialog(scope.row.id)"
                                link>编辑</el-button>
                            <el-button type="danger" :icon="Delete" size="small" @click="deleteLine(scope.row.id)"
                                link>删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </el-card>

        <!-- 添加/编辑产线对话框 -->
        <el-dialog v-model="dialogFormVisible" :title="isEdit ? '编辑产线' : '新增产线'" width="600px" center destroy-on-close
            class="line-dialog">
            <el-form :model="line" :rules="rules" ref="ruleFormRef" label-width="100px" label-position="right"
                class="line-form">
                <el-form-item label="产线名称" prop="name">
                    <el-input v-model="line.name" placeholder="请输入产线名称" prefix-icon="Connection" clearable
                        autocomplete="off" />
                </el-form-item>
                <el-form-item label="产线描述">
                    <el-input v-model="line.description" type="textarea" placeholder="请输入产线描述（可选）" :rows="4"
                        maxlength="500" show-word-limit autocomplete="off" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="dialogFormVisible = false" :icon="Close">取消</el-button>
                    <el-button type="primary" @click="submit()" :loading="loadingBtn" :icon="Check">确定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {Check, Clock, Close, Connection, Delete, Edit, Plus} from '@element-plus/icons-vue'
import {addLineApi, deleteLineApi, editLineApi, getLineByIdApi, getLineListApi} from '@/api/line';
import {ElDialog, ElForm, ElFormItem, ElMessage, ElMessageBox, ElTag} from 'element-plus'

defineOptions({
    name: 'LineManagement'
})

// 表格数据
const tableData = ref([])

// 数据模型
const line = ref({
    id: '',
    name: '',
    description: '',
})

// 控制dialog表单显隐
const dialogFormVisible = ref(false)

const isEdit = ref(false)

// 控制加载动画
const loading = ref(false)

// 控制按钮加载状态
const loadingBtn = ref(false)

// 显示dialog表单
const showDialog = async (id) => {
    resetForm()
    line.value.id = id
    if (line.value.id !== undefined) {
        isEdit.value = true
        // 获取产线信息
        const res = await getLineByIdApi(line.value.id)
        if (res.code === 200) {
            line.value.name = res.data.name
            line.value.description = res.data.description
        }
    } else {
        isEdit.value = false
    }
    dialogFormVisible.value = true
}

// 表单校验
const ruleFormRef = ref()
const rules = {
    name: [
        { required: true, message: '请输入产线名称', trigger: 'blur' }
    ]
}

// 重置表单
const resetForm = () => {
    line.value = {}
    if (!ruleFormRef.value) return
    ruleFormRef.value.resetFields()
}

// 删除产线
const deleteLine = async (id) => {
    // 显示确认框
    ElMessageBox.confirm(
        '确定要删除这条产线吗?',
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        const res = await deleteLineApi(id)
        if (res.code === 200) {
            ElMessage.success('删除成功')
            getLineList()
        }
    })
}

// 添加产线
const addLine = async () => {
    loadingBtn.value = true
    await addLineApi(line.value).then(res => {
        if (res.code === 200) {
            ElMessage.success('添加产线成功')
            dialogFormVisible.value = false
            getLineList()
            resetForm()
        }
    }).finally(() => loadingBtn.value = false)
}

// 修改产线
const editLine = async () => {
    await editLineApi(line.value).then(res => {
        if (res.code === 200) {
            ElMessage.success('修改产线成功')
            dialogFormVisible.value = false
            getLineList()
            resetForm()
        }
    }).finally(() => loadingBtn.value = false)
}

// 提交添加或修改请求
const submit = async () => {
    // 校验表单
    try {
        await ruleFormRef.value.validate()
    } catch (error) {
        ElMessage.error('请完善表单信息')
        console.warn(error)
        return
    }

    if (isEdit.value) {
        await editLine()
    } else {
        await addLine()
    }
}

// 获取产线列表
const getLineList = async () => {
    loading.value = true
    await getLineListApi().then(res => { 
        if (res.code === 200) {
            tableData.value = res.data
        }
    }).finally(() => {
        loading.value = false
    })
}

onMounted(async () => {
    // 获取产线列表
    getLineList()
})
</script>

<style scoped>
.container {
    padding: 20px;
    width: 100%;
    background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
    min-height: 100vh;
}

/* 页面标题区域 */
.page-header {
    margin-bottom: 24px;
    padding: 20px 24px;
    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    border-radius: 4px;
    box-shadow: 0 4px 12px rgba(17, 153, 142, 0.3);
}

.page-title {
    color: #ffffff;
    font-size: 28px;
    font-weight: 600;
    margin: 0 0 8px 0;
    display: flex;
    align-items: center;
    gap: 12px;
}

.title-icon {
    font-size: 32px;
}

.page-subtitle {
    color: rgba(255, 255, 255, 0.9);
    font-size: 14px;
    margin: 0;
}

/* 卡片样式 */
.toolbar-card,
.table-card {
    margin-bottom: 20px;
    border-radius: 4px;
    border: none;
    transition: all 0.3s ease;
}

.toolbar-card:hover,
.table-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* 工具栏样式 */
.toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20px;
}

.toolbar-actions {
    display: flex;
    gap: 12px;
}

/* 表格样式 */
.table-wrapper {
    margin: 16px 0;
}

.custom-table {
    border-radius: 4px;
    overflow: hidden;
}

.custom-table :deep(.el-table__header th) {
    background: #8BC34A;
    color: #ffffff;
    font-weight: 600;
    font-size: 14px;
    padding: 16px 0;
}

.custom-table :deep(.el-table__row) {
    transition: all 0.2s ease;
}

.custom-table :deep(.el-table__row:hover) {
    background-color: #f5f7fa !important;
    transform: scale(1.005);
}

.custom-table :deep(.el-table__cell) {
    padding: 14px 0;
}

/* 对话框样式 */
.line-dialog :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #11998e 0%, #85fdb3 100%);
    padding: 20px 24px;
    margin: 0;
}

.line-dialog :deep(.el-dialog__title) {
    color: #ffffff;
    font-size: 18px;
    font-weight: 600;
}

.line-dialog :deep(.el-dialog__close) {
    color: #ffffff;
}

.line-dialog :deep(.el-dialog__close:hover) {
    color: rgba(255, 255, 255, 0.8);
}

.line-dialog :deep(.el-dialog__body) {
    padding: 32px 24px;
}

.line-form {
    padding: 0 12px;
}

.line-form :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
}

.line-form :deep(.el-input__wrapper),
.line-form :deep(.el-textarea__inner) {
    border-radius: 4px;
    transition: all 0.3s ease;
}

.line-form :deep(.el-input__wrapper:hover),
.line-form :deep(.el-textarea__inner:hover) {
    box-shadow: 0 0 0 1px #11998e inset;
}

.dialog-footer {
    display: flex;
    justify-content: center;
    gap: 16px;
    padding-top: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .container {
        padding: 12px;
    }

    .page-header {
        padding: 16px;
    }

    .page-title {
        font-size: 22px;
    }

    .toolbar {
        flex-direction: column;
        align-items: stretch;
    }

    .toolbar-actions {
        justify-content: center;
    }
}

/* 动画效果 */
@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.page-header {
    animation: fadeInUp 0.6s ease-out;
}

.toolbar-card {
    animation: fadeInUp 0.6s ease-out 0.1s both;
}

.table-card {
    animation: fadeInUp 0.6s ease-out 0.2s both;
}
</style>