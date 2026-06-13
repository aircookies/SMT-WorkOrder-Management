<template>
    <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
            <h1 class="page-title">
                <el-icon class="title-icon"><OfficeBuilding /></el-icon>
                部门管理
            </h1>
            <p class="page-subtitle">管理和维护组织架构信息</p>
        </div>
        
        <!-- 工具栏 -->
        <el-card class="toolbar-card" shadow="hover">
            <div class="toolbar">
                <div class="toolbar-actions">
                    <el-button type="primary" @click="handleAdd" :icon="Plus" plain>新建部门</el-button>
                </div>
                <!-- 搜索栏 -->
                <el-form :inline="true" :model="queryFormModel" class="search-form">
                    <el-form-item label="部门名称">
                        <el-input v-model="queryFormModel.name" placeholder="请输入部门名称" clearable 
                            prefix-icon="Search" style="width: 240px" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="queryDepartment" :loading="btnLoading" :icon="Search">查询</el-button>
                        <el-button @click="clearQueryForm" :icon="Refresh">重置</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </el-card>
        
        <!-- 数据表格 -->
        <el-card class="table-card" shadow="hover">
            <div class="table-wrapper">
                <el-table v-loading="loading" :data="tableData" style="width: 100%" stripe border class="custom-table">
                    <el-table-column property="id" label="ID" min-width="100" align="center">
                        <template #default="{ row }">
                            <el-tag type="info" effect="plain">{{ row.id }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column property="name" label="部门名称" min-width="200" show-overflow-tooltip align="center">
                        <template #default="{ row }">
                            <el-icon><OfficeBuilding /></el-icon>
                            {{ row.name }}
                        </template>
                    </el-table-column>
                    <el-table-column property="createTime" label="创建时间" min-width="180" align="center">
                        <template #default="{ row }">
                            <el-icon><Clock /></el-icon>
                            {{ row.createTime }}
                        </template>
                    </el-table-column>
                    <el-table-column property="updateTime" label="更新时间" min-width="180" align="center">
                        <template #default="{ row }">
                            <el-icon><Refresh /></el-icon>
                            {{ row.updateTime }}
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="150" align="center" fixed="right">
                        <template #default="scope">
                            <el-button size="small" type="primary" :icon="Edit" 
                                @click="handleEdit(scope.row)" link>编辑</el-button>
                            <el-button size="small" type="danger" :icon="Delete" 
                                @click="handleDelete(scope.row.id)" link>删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </el-card>
        
        <!-- 添加/编辑部门对话框 -->
        <el-dialog v-if="dialogFormVisible" v-model="dialogFormVisible" :title="isEdit ? '编辑部门' : '新建部门'" 
            width="600px" center destroy-on-close class="department-dialog">
            <el-form :model="departmentDTO" :rules="formRules" ref="formRef" label-width="100px" 
                label-position="right" class="department-form">
                <el-form-item label="部门名称" prop="name">
                    <el-input v-model="departmentDTO.name" placeholder="请输入部门名称" 
                        prefix-icon="OfficeBuilding" clearable />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="handleCancel" :icon="Close">取消</el-button>
                    <el-button type="primary" @click="handleSubmit" :loading="btnLoading" :icon="Check">提交</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus';
import {
  addDepartmentApi,
  deleteDepartmentApi,
  queryDepartmentApi,
  updateDepartmentApi
} from '@/api/departmentManagement';
import {Check, Clock, Close, Delete, Edit, OfficeBuilding, Plus, Refresh, Search} from '@element-plus/icons-vue';

defineOptions({
    name: 'DepartmentManagement'
})

// 表格数据
const tableData = ref([])

// 控制加载动画
const loading = ref(false)

// 控制按钮加载状态
const btnLoading = ref(false)

// 显隐dialog对话框
const dialogFormVisible = ref(false)

// 是否是编辑状态
const isEdit = ref(false)

// 部门数据模型
const departmentDTO = ref({
    id: '',
    name: ''
})

// 查询表单数据模型
const queryFormModel = ref({
    name: ''
})

// 表单验证规则
const formRules = {
    name: [
        { required: true, message: '请输入部门名称', trigger: 'blur' },
        { min: 2, max: 50, message: '部门名称长度在 2 到 50 个字符', trigger: 'blur' }
    ]
}

// 表单引用
const formRef = ref(null)

/**
 * 清空查询表单
 */
const clearQueryForm = () => {
    queryFormModel.value = {
        name: ''
    }

    queryDepartment()
}

/**
 * 重置表单
 */
const resetForm = () => {
    departmentDTO.value = {
        id: '',
        name: ''
    }
    if (formRef.value) {
        formRef.value.clearValidate()
    }
}

/**
 * 新建部门
 */
const handleAdd = () => {
    isEdit.value = false
    resetForm()
    dialogFormVisible.value = true
}

/**
 * 取消操作
 */
const handleCancel = () => {
    dialogFormVisible.value = false
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
    if (!formRef.value) return

    btnLoading.value = true

    await formRef.value.validate(async (valid) => {
        if (!valid) {
            ElMessage.error('请完善表单信息')
            return
        }

        let res
        if (!isEdit.value) {
            // 新建部门
            res = await addDepartmentApi(departmentDTO.value)
            if (res.code === 200) {
                ElMessage.success('部门创建成功')
                dialogFormVisible.value = false
                resetForm()
                queryDepartment()
            }
        } else {
            // 编辑部门
            res = await updateDepartmentApi(departmentDTO.value)
            if (res.code === 200) {
                ElMessage.success('部门修改成功')
                dialogFormVisible.value = false
                resetForm()
                queryDepartment()
            }
        }
    }).finally(() => btnLoading.value = false)
}

/**
 * 编辑部门
 * @param {Object} row - 部门行数据
 */
const handleEdit = (row) => {
    isEdit.value = true
    departmentDTO.value = {
        id: row.id,
        name: row.name
    }
    dialogFormVisible.value = true
}

/**
 * 查询部门列表
 */
const queryDepartment = async () => {
    loading.value = true
    btnLoading.value = true

    const params = {}
    if (queryFormModel.value.name) {
        params.name = queryFormModel.value.name
    }
    
    await queryDepartmentApi(params).then(res => { 
        if (res.code === 200) {
            tableData.value = res.data
        }
    }).finally(() => {
        btnLoading.value = false
        loading.value = false
    })
}

/**
 * 删除部门
 * @param {Number} id - 部门ID
 */
const handleDelete = async (id) => {
    ElMessageBox.confirm('确定要删除该部门吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        const res = await deleteDepartmentApi(id)
        if (res.code === 200) {
            ElMessage.success('删除部门成功')
            queryDepartment()
        }
    })
}

/**
 * 组件挂载时初始化数据
 */
onMounted(async () => {
    loading.value = true
    // 获取部门列表
    await queryDepartment()
    loading.value = false
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
    background: linear-gradient(315deg, #856cff 0.000%, #856cff 10.000%, #8568ff calc(10.000% + 1px), #8568ff 20.000%, #846eff calc(20.000% + 1px), #846eff 30.000%, #847aff calc(30.000% + 1px), #847aff 40.000%, #8487ff calc(40.000% + 1px), #8487ff 50.000%, #838fff calc(50.000% + 1px), #838fff 60.000%, #8390ff calc(60.000% + 1px), #8390ff 70.000%, #8387ff calc(70.000% + 1px), #8387ff 80.000%, #827aff calc(80.000% + 1px), #827aff 90.000%, #826eff calc(90.000% + 1px) 100.000%);
    border-radius: 4px;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
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
    flex-wrap: wrap;
}

.toolbar-actions {
    display: flex;
    gap: 12px;
}

.search-form {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
}

.search-form :deep(.el-form-item) {
    margin-bottom: 0;
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
    background: #3F51B5;
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
.department-dialog :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px 24px;
    margin: 0;
}

.department-dialog :deep(.el-dialog__title) {
    color: #ffffff;
    font-size: 18px;
    font-weight: 600;
}

.department-dialog :deep(.el-dialog__close) {
    color: #ffffff;
}

.department-dialog :deep(.el-dialog__close:hover) {
    color: rgba(255, 255, 255, 0.8);
}

.department-dialog :deep(.el-dialog__body) {
    padding: 32px 24px;
}

.department-form {
    padding: 0 12px;
}

.department-form :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
}

.department-form :deep(.el-input__wrapper) {
    border-radius: 4px;
    transition: all 0.3s ease;
}

.department-form :deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px #667eea inset;
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
    
    .search-form {
        flex-direction: column;
        align-items: stretch;
    }
    
    .search-form :deep(.el-form-item) {
        width: 100%;
    }
    
    .search-form :deep(.el-input) {
        width: 100% !important;
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
