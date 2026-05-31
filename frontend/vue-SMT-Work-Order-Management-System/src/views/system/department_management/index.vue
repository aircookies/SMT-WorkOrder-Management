<template>
    <div class="container">
        <h1>部门管理</h1>
        <!-- 工具栏 -->
        <div class="toolbar">
            <div class="buttons">
                <el-button type="primary" @click="handleAdd" plain>新建部门</el-button>
            </div>
            <!-- 搜索栏 -->
            <div class="search">
                <el-form :inline="true" :model="queryFormModel" class="search-form">
                    <el-form-item label="部门名称:">
                        <el-input v-model="queryFormModel.name" placeholder="请输入部门名称" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="queryDepartment">查询</el-button>
                        <el-button @click="clearQueryForm">清空</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <!-- 表格 -->
        <div class="table">
            <el-table v-loading="loading" :data="tableData" style="width: 100%">
                <el-table-column property="id" label="ID" width="80" />
                <el-table-column property="name" label="部门名称" show-overflow-tooltip />
                <el-table-column property="createTime" label="创建时间"  />
                <el-table-column property="updateTime" label="更新时间"  />
                <!-- 操作按钮 -->
                <el-table-column label="操作" width="150">
                    <template #default="scope">
                        <el-button size="small" type="primary" :icon="Edit" @click="handleEdit(scope.row)" circle />
                        <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(scope.row.id)"
                            circle />
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <!-- dialog对话框 -->
        <el-dialog v-if="dialogFormVisible" v-model="dialogFormVisible" :title="isEdit ? '编辑部门' : '新建部门'" width="500" center>
            <el-form :model="departmentDTO" :rules="formRules" ref="formRef" label-width="auto" label-position="right">
                <el-form-item label="部门名称" prop="name">
                    <el-input v-model="departmentDTO.name" placeholder="请输入部门名称" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="handleCancel">取消</el-button>
                    <el-button type="primary" @click="handleSubmit">
                        提交
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { queryDepartmentApi, addDepartmentApi, updateDepartmentApi, deleteDepartmentApi } from '@/api/departmentManagement';
import { Edit, Delete } from '@element-plus/icons-vue';

// 表格数据
const tableData = ref([])

// 控制加载动画
const loading = ref(false)

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

    await formRef.value.validate(async (valid) => {
        if (!valid) {
            ElMessage.error('请完善表单信息')
            return
        }
        
        try {
            let res
            if (!isEdit.value) {
                // 新建部门
                res = await addDepartmentApi(departmentDTO.value)
                if (res.code === 200) {
                    ElMessage.success('部门创建成功')
                    dialogFormVisible.value = false
                    resetForm()
                    queryDepartment()
                } else {
                    ElMessage.error(res.message || '部门创建失败')
                }
            } else {
                // 编辑部门
                res = await updateDepartmentApi(departmentDTO.value)
                if (res.code === 200) {
                    ElMessage.success('部门修改成功')
                    dialogFormVisible.value = false
                    resetForm()
                    queryDepartment()
                } else {
                    ElMessage.error(res.message || '部门修改失败')
                }
            }
        } catch (error) {
            ElMessage.error(error.message || '操作失败')
        }
    })
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
    try {
        const params = {}
        if (queryFormModel.value.name) {
            params.name = queryFormModel.value.name
        }
        const res = await queryDepartmentApi(params)
        if (res.code === 200) {
            tableData.value = res.data
        } else {
            ElMessage.error(res.message || '查询部门失败')
        }
    } catch (error) {
        ElMessage.error(error.message || '查询部门失败')
    } finally {
        loading.value = false
    }
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
        try {
            const res = await deleteDepartmentApi(id)
            if (res.code === 200) {
                ElMessage.success('删除部门成功')
                queryDepartment()
            } else {
                ElMessage.error(res.message || '删除部门失败')
            }
        } catch (error) {
            ElMessage.error(error.message || '删除部门失败')
        }
    }).catch(() => {
        ElMessage.info('取消操作')
    })
}

/**
 * 组件挂载时初始化数据
 */
onMounted(async () => {
    loading.value = true
    try {
        // 获取部门列表
        await queryDepartment()
    } catch (error) {
        ElMessage.error(error.message || '初始化数据失败')
    } finally {
        loading.value = false
    }
})
</script>

<style scoped>
.container {
    padding: 10px 20px;
    width: 100%;
}

.toolbar {
    display: grid;
    grid-template-columns: 20% auto;
    padding: 20px 0;
}

.search {
    display: flex;
    flex-direction: row;
}

.search-form {
    width: 100%;
}

.pagination {
    padding: 20px 0;
}

:deep(.el-dialog) {
    .el-form {
        display: grid;
        grid-template-columns: auto;
        gap: 20px 0;
        padding: 0 20px;
    }
}
</style>
