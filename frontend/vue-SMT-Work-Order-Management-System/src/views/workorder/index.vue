<template>
    <div class="container">
        <h1>工单管理</h1>
        <!-- 工具栏 -->
        <div class="toolbar">
            <div class="buttons">
                <el-button type="primary" @click="handleAdd" plain>新建工单</el-button>
                <el-button type="danger" @click="handleDelete(selectedRows)" plain>批量删除</el-button>
            </div>
            <!-- 搜索栏 -->
            <div class="search">
                <el-form :inline="true" :model="queryFormModel" class="search-form">
                    <el-form-item label="工单号:">
                        <el-input v-model="queryFormModel.id" />
                    </el-form-item>
                    <el-form-item label="工单状态:">
                        <el-select v-model="queryFormModel.status" placeholder="未选择" style="min-width: 100px;">
                            <el-option label="未选择" value="" />
                            <el-option label="待生产" value="0" />
                            <el-option label="生产中" value="1" />
                            <el-option label="生产完成" value="2" />
                            <el-option label="已关闭" value="3" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="优先级:">
                        <el-select v-model="queryFormModel.priority" placeholder="未选择" style="min-width: 100px;">
                            <el-option label="未选择" value="" />
                            <el-option label="低" value="0" />
                            <el-option label="中" value="1" />
                            <el-option label="高" value="2" />
                            <el-option label="紧急" value="3" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="queryWorkOrder">查询</el-button>
                        <el-button @click="clearQueryForm">清空</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <!--  表格 -->
        <div class="table">
            <el-table v-loading="loading" :data="tableData" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" />
                <el-table-column property="id" label="工单号" width="120" />
                <el-table-column property="creatorName" label="创建者" width="120" />
                <el-table-column property="productName" label="产品名称" max-width="120" show-overflow-tooltip />
                <el-table-column property="lineName" label="产线名称" max-width="240" show-overflow-tooltip />
                <el-table-column property="priority" label="优先级" max-width="80">
                    <template #default="scope">
                        <el-tag v-if="scope.row.priority === 0" type="info">低</el-tag>
                        <el-tag v-if="scope.row.priority === 1" type="primary">中</el-tag>
                        <el-tag v-if="scope.row.priority === 2" type="warning">高</el-tag>
                        <el-tag v-if="scope.row.priority === 3" type="danger">紧急</el-tag>
                    </template>
                </el-table-column>
                <el-table-column property="quantity" label="计划生产数量" max-width="120" />
                <el-table-column property="planningTime" label="计划完成时间" max-width="120" />
                <el-table-column property="status" label="状态" max-width="80">
                    <template #default="scope">
                        <el-tag v-if="scope.row.status === 0" type="warning">待生产</el-tag>
                        <el-tag v-if="scope.row.status === 1" type="primary">生产中</el-tag>
                        <el-tag v-if="scope.row.status === 2" type="success">已完成</el-tag>
                        <el-tag v-if="scope.row.status === 3" type="info">已关闭</el-tag>
                    </template>
                </el-table-column>
                <!-- 操作按钮 -->
                <el-table-column label="操作">
                    <template #default="scope">
                        <el-button size="small" type="primary" :icon="Edit" @click="handleEdit(scope.row.id)" circle />
                        <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(scope.row.id)"
                            circle />
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <!-- 分页 -->
        <el-pagination class="pagination" :current-page="pageNum" :page-size="pageSize"
            :page-sizes="[10, 25, 50, 100, 500]" layout="total, sizes, prev, pager, next, jumper" :total="total"
            @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        <!-- dialog对话框 -->
        <el-dialog class="dialog" v-model="dialogFormVisible" :title="isEdit ? '编辑工单' : '新建工单'" width="600" center>
            <el-form :model="workOrderDTO" :rules="formRules" ref="formRef" label-width="auto" label-position="right">
                <el-form-item label="产品名称" prop="productId">
                    <el-select v-model="workOrderDTO.productId" placeholder="请选择产品" style="width: 100%;" filterable>
                        <el-option v-for="item in productList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="产线" prop="lineId">
                    <el-select v-model="workOrderDTO.lineId" placeholder="请选择产线" style="width: 100%;">
                        <el-option v-for="item in lineList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="优先级" prop="priority">
                    <el-select v-model="workOrderDTO.priority" placeholder="请选择工单优先级" style="width: 100%;">
                        <el-option label="低" :value="0" />
                        <el-option label="中" :value="1" />
                        <el-option label="高" :value="2" />
                        <el-option label="紧急" :value="3" />
                    </el-select>
                </el-form-item>
                <el-form-item label="计划生产数量" prop="quantity">
                    <el-input-number v-model="workOrderDTO.quantity" :min="1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="计划完成时间" prop="planningTime">
                    <el-date-picker v-model="workOrderDTO.planningTime" type="date" placeholder="选择日期"
                        style="width: 100%;" value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="workOrderDTO.status" placeholder="请选择工单状态" style="width: 100%;">
                        <el-option label="待生产" :value="0" />
                        <el-option label="生产中" :value="1" />
                        <el-option label="生产完成" :value="2" />
                        <el-option label="已关闭" :value="3" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="remarks">
                    <el-input v-model="workOrderDTO.remarks" type="textarea" :rows="3" placeholder="可选" />
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
import { ElForm, ElFormItem, ElInput, ElSelect, ElButton, ElNotification, ElMessage, ElDialog, ElMessageBox } from 'element-plus';
import { getWorkOrderListApi, queryWorkOrderApi, addWorkOrderApi, getWorkOrderByIdApi, editWorkOrderApi, deleteWorkOrderApi } from '@/api/workorder';
import { getLineListApi } from '@/api/line';
import { getProductListApi } from '@/api/product';
import { Edit, Delete, List } from '@element-plus/icons-vue';
import { isEmpty } from 'element-plus/es/utils/types.mjs';

// 产线列表
const lineList = ref([])

// 产品列表
const productList = ref([])

// 表格数据
const tableData = ref([])

// 分页相关
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 控制加载动画
const loading = ref(false)

// 显隐dialog对话框
const dialogFormVisible = ref(false)

// 是否是编辑状态
const isEdit = ref(false)

// 勾选的行
const selectedRows = ref([])

// 工单数据模型
const workOrderDTO = ref({
    id: '', // 工单ID
    productId: '',  // 产品ID
    lineId: '', // 产线ID
    priority: '',   // 优先级
    quantity: '1',   // 计划生产数量
    planningTime: '',   // 工单完成时间
    status: '0', // 工单状态(0:待生产, 1:生产中, 2:生产完成, 3:已关闭)
    remarks: '',    // 工单备注
    creatorId: '1',  // // 工单创建人ID 先用1
    createTime: '', // 工单创建时间
    updateTime: ''  // 工单更新时间
})

// 查询表单数据模型
const queryFormModel = ref({
    pageNum: 1, // 当前页码
    pageSize: 10, // 每页显示的记录数
    id: '', // 工单ID/工单号
    status: '', // 工单状态(0:待生产, 1:生产中, 2:生产完成, 3:已关闭)
    priority: '',   // 优先级(0:低, 1:中, 2:高, 3:紧急)
})

// 错误通知
const errorNotify = (message) => {
    ElNotification({
        title: '错误',
        message: message,
        type: 'error'
    })
}

// 表单验证规则
const formRules = {
    productId: [
        { required: true, message: '请选择产品', trigger: 'blur' }
    ],
    lineId: [
        { required: true, message: '请选择产线', trigger: 'blur' }
    ],
    priority: [
        { required: true, message: '请选择优先级', trigger: 'blur' }
    ],
    quantity: [
        { required: true, message: '请输入计划生产数量', trigger: 'blur' }
    ],
    planningTime: [
        { required: true, message: '请选择计划完成时间', trigger: 'blur' }
    ],
    status: [
        { required: true, message: '请选择状态', trigger: 'blur' }
    ]
}

// 表单引用
const formRef = ref(null)

// 清空查询表单
const clearQueryForm = () => {
    queryFormModel.value = {}
}


// 重置表单
const resetForm = () => {
    workOrderDTO.value = {
        productId: '',
        lineId: '',
        priority: '',
        quantity: 1,
        planningTime: '',
        status: 0,
        remarks: '',
        creatorId: '1'  // 工单创建人ID 先用1
    }
    if (formRef.value) {
        formRef.value.clearValidate()
    }
}

// 处理选中的行
const handleSelectionChange = (val) => {
    selectedRows.value = val.map(item => item.id)
}

// 通用转换函数
const toArray = (item) => {
    if (Array.isArray(item)) {
        return item;  // 如果已经是数组，直接返回
    }
    return [item];  // 否则包装成数组
};

// 新建工单
const handleAdd = () => {
    isEdit.value = false
    resetForm()
    dialogFormVisible.value = true
}

// 取消
const handleCancel = () => {
    dialogFormVisible.value = false
}

// 提交
const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
        if (!valid) {
            ElMessage.error('请完善表单信息')
            return
        }
        if (!isEdit.value) {
            try {
                const res = await addWorkOrderApi(workOrderDTO.value)
                if (res.code === 200) {
                    ElMessage.success('工单创建成功')
                    dialogFormVisible.value = false
                    resetForm()
                    queryWorkOrder()
                } else {
                    ElMessage.error(res.message || '工单创建失败')
                }
            } catch (error) {
                ElMessage.error(error.message || '工单创建失败')
            }
        }
        else if (isEdit.value) {
            try {
                const res = await editWorkOrderApi(workOrderDTO.value)
                if (res.code === 200) {
                    ElMessage.success('工单修改成功')
                    dialogFormVisible.value = false
                    resetForm()
                    queryWorkOrder()
                } else {
                    ElMessage.error(res.message || '工单修改失败')
                }
            } catch (error) {
                ElMessage.error(error.message || '工单修改失败')
            }
        }
    })
}

// 编辑工单
const handleEdit = async (row) => {
    isEdit.value = true
    resetForm()
    // 获取工单数据
    try {
        const res = await getWorkOrderByIdApi(toArray(row))
        if (res.code === 200) {
            workOrderDTO.value = res.data
        } else {
            ElMessage.error(res.message || '获取工单数据失败')
            return
        }
    } catch (error) {
        ElMessage.error(error.message || '获取工单数据失败')
        return
    }
    // 显示编辑工单对话框
    dialogFormVisible.value = true
}

// 查询工单
const queryWorkOrder = async () => {
    loading.value = true
    try {
        await queryWorkOrderApi(pageNum.value, pageSize.value, queryFormModel.value.id, queryFormModel.value.status, queryFormModel.value.priority).then(res => {
            if (res.code === 200) {
                tableData.value = res.data.list
                pageNum.value = res.data.pageNum
                pageSize.value = res.data.pageSize
                total.value = res.data.total
            }
        })
    } catch (error) {
        ElMessage.error(error.message || '查询工单失败')
    } finally {
        loading.value = false
    }
}

// 删除工单
const handleDelete = async (row) => {
    if (isEmpty(row)) {
        ElMessage.error('请选择要删除的工单')
        return
    }

    ElMessageBox.confirm('确定要删除工单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
            const res = await deleteWorkOrderApi(toArray(row))
            if (res.code === 200) {
                ElMessage.success('删除工单成功')
                queryWorkOrder()
            }
        } catch (error) {
            ElMessage.error(error.message || '删除工单失败')
        }
    }).catch(() => {
        ElMessage.info('取消操作')
    })
}

// 分页大小改变
const handleSizeChange = (val) => {
    pageSize.value = val
    queryWorkOrder()
}

// 分页页码改变
const handleCurrentChange = (val) => {
    pageNum.value = val
    queryWorkOrder()
}

onMounted(async () => {
    loading.value = true
    try {
        // 获取工单列表
        const res = await getWorkOrderListApi(pageNum.value, pageSize.value)
        if (res.code === 200) {
            tableData.value = res.data.list
            pageNum.value = res.data.pageNum
            pageSize.value = res.data.pageSize
            total.value = res.data.total
        }
        // 获取产线列表
        const lineRes = await getLineListApi()
        if (lineRes.code === 200) {
            lineList.value = lineRes.data
        }
        // 获取产品列表
        const productRes = await getProductListApi(1, 1000)
        if (productRes.code === 200) {
            productList.value = productRes.data.list
        }
    } catch (error) {
        errorNotify(error.message || "无法获取数据")
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

:deep(.dialog) {
    .el-form {
        display: grid;
        grid-template-columns: auto;
        gap: 30px 0;
        padding: 0 20px;
    }
}
</style>