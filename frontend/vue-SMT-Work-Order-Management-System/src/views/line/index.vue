<template>
    <div class="container">
        <h1>产线管理</h1>
        <div class="toolbar">
            <el-button type="success" @click="showDialog()" plain>新增</el-button>
        </div>
        <!-- 表格 -->
        <el-table v-loading="loading" :data="tableData" style="width: 100%" row-key="id" table-layout="fixed">
            <el-table-column prop="name" label="产线名称" min-width="100" />
            <el-table-column prop="description" label="产线描述" min-width="220" />
            <el-table-column prop="updateTime" label="更新时间" min-width="180" />
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="primary" :icon="Edit" size="small" @click="showDialog(scope.row.id)" circle />
                    <el-button type="danger" :icon="Delete" size="small" @click="deleteLine(scope.row.id)" circle />
                </template>
            </el-table-column>
        </el-table>
        <!-- dialog表单 -->
        <el-dialog v-model="dialogFormVisible" class="dialog" :title="isEdit ? '编辑产线' : '新增产线'" center width="500">
            <el-form :model="line" :rules="rules" ref="ruleFormRef" label-width="auto">
                <el-form-item label="产线名称:" prop="name">
                    <el-input v-model="line.name" placeholder="请输入产线名称" autocomplete="off" />
                </el-form-item>
                <el-form-item label="产线描述:">
                    <el-input v-model="line.description" type="textarea" placeholder="可选" autocomplete="off" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="dialogFormVisible = false">取消</el-button>
                    <el-button type="primary" @click="submit()">
                        确定
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Edit, Delete, CaretRight } from '@element-plus/icons-vue'
import { getLineListApi, addLineApi, deleteLineApi, getLineByIdApi, editLineApi } from '@/api/line';
import { ElMessage, ElNotification, ElMessageBox } from 'element-plus'

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

// 显示dialog表单
const showDialog = async (id) => {
    resetForm()
    line.value.id = id
    if (line.value.id !== undefined) {
        isEdit.value = true
        // 获取产线信息
        try {
            const res = await getLineByIdApi(line.value.id)
            if (res.code === 200) {
                line.value.name = res.data.name
                line.value.description = res.data.description
            } else {
                throw new Error(res.message)
            }
        } catch (error) {
            errorMsg(error.message || '获取产线信息失败')
            return 
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
    line.value.id = ''
    line.value.name = ''
    line.value.description = ''
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
        } else {
            ElMessage.error(res.message || '删除失败')
        }
    })
}

// 添加产线
const addLine = async () => {
    const res = await addLineApi(line.value)
    if (res.code === 200) {
        ElMessage.success('添加产线成功')
        dialogFormVisible.value = false
        getLineList()
        resetForm()
    } else {
        ElMessage.error(res.message || '添加产线失败')
    }
}

// 修改产线
const editLine = async () => {
    const res = await editLineApi(line.value)
    if (res.code === 200) {
        ElMessage.success('修改产线成功')
        dialogFormVisible.value = false
        getLineList()
        resetForm()
    } else {
        ElMessage.error(res.message || '修改产线失败')
    }
}

// 提交添加或修改请求
const submit = async () => {
    // 校验表单
    try {
        await ruleFormRef.value.validate()
    } catch (error) {
        ElMessage.error('请完善表单信息')
        return
    }

    try {
        if (isEdit.value) {
            await editLine()
        } else {
            await addLine()
        }
    } catch (error) {
        ElMessage.error(error.message || '操作失败')
    }
}

// 错误通知
const errorMsg = (msg) => {
    ElNotification({
        title: '错误',
        message: msg,
        type: 'error',
    })
}

// 获取产线列表
const getLineList = async () => {
    loading.value = true
    try {
        const res = await getLineListApi()
        // 响应结果判断
        if (res.code === 200) {
            tableData.value = res.data
        } else {
            errorMsg(res.message || '无法获取产线列表')
        }
    } catch (error) {
        errorMsg(error.message || '获取产线列表失败')
    } finally {
        loading.value = false
    }
}

onMounted(async () => {
    // 获取产线列表
    getLineList()
})
</script>

<style scoped>
* {
    margin: 0;
}

.container {
    margin: 0 2cap;
    padding-top: 10px;
    width: 100%;
}

.toolbar {
    padding: 20px 0;
}

:deep(.dialog) {
    .el-form {
        display: grid;
        grid-template-columns: auto;
        gap: 30px 0;
        padding: 0 20px;
    }
}
</style>