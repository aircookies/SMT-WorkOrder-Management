<template>
    <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
            <h1 class="page-title">
                <el-icon class="title-icon">
                    <Document />
                </el-icon>
                工单管理
            </h1>
            <p class="page-subtitle">创建、跟踪和管理生产工单</p>
        </div>

        <!-- 工具栏 -->
        <el-card class="toolbar-card" shadow="hover">
            <div class="toolbar">
                <div class="toolbar-actions">
                    <el-button type="primary" @click="handleAdd" plain><el-icon class="icon-plus">
                            <Plus />
                        </el-icon>新建工单</el-button>
                    <el-button type="danger" @click="handleDelete(selectedRows)" plain><el-icon class="icon-delete">
                            <Delete />
                        </el-icon>批量删除</el-button>
                </div>
                <!-- 搜索栏 -->
                <el-form :inline="true" :model="queryFormModel" class="search-form">
                    <el-form-item label="工单号">
                        <el-input v-model="queryFormModel.id" placeholder="请输入工单号" clearable prefix-icon="Search"
                            style="width: 180px" />
                    </el-form-item>
                    <el-form-item label="工单状态">
                        <el-select v-model="queryFormModel.status" placeholder="请选择状态" style="width: 140px" clearable>
                            <el-option label="待生产" value="0" />
                            <el-option label="生产中" value="1" />
                            <el-option label="生产完成" value="2" />
                            <el-option label="已关闭" value="3" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="优先级">
                        <el-select v-model="queryFormModel.priority" placeholder="请选择优先级" style="width: 140px"
                            clearable>
                            <el-option label="低" value="0" />
                            <el-option label="中" value="1" />
                            <el-option label="高" value="2" />
                            <el-option label="紧急" value="3" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="创建日期">
                        <el-date-picker v-model="queryFormModel.createTime" type="date" placeholder="请选择日期" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="queryWorkOrder" :loading="btnLoading"><el-icon
                                class="icon-search">
                                <Search />
                            </el-icon>查询</el-button>
                        <el-button @click="clearQueryForm" plain><el-icon class="icon-refresh">
                                <Refresh />
                            </el-icon>重置</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </el-card>

        <!-- 数据表格 -->
        <el-card class="table-card" shadow="hover">
            <div class="table-wrapper">
                <el-table v-loading="loading" :data="tableData" @selection-change="handleSelectionChange"
                    style="width: 100%" stripe border class="custom-table">
                    <el-table-column type="selection" width="55" align="center" />
                    <el-table-column property="id" label="工单号" min-width="80" align="center">
                        <template #default="{ row }">
                            <el-tag type="info" effect="plain">{{ row.id }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column property="creatorName" label="创建者" min-width="120" align="center">
                        <template #default="{ row }">
                            <el-icon>
                                <User />
                            </el-icon>
                            {{ row.creatorName }}
                        </template>
                    </el-table-column>
                    <el-table-column property="productName" label="产品名称" min-width="150" show-overflow-tooltip
                        align="center" />
                    <el-table-column property="lineName" label="产线名称" min-width="150" show-overflow-tooltip
                        align="center" />
                    <el-table-column property="priority" label="优先级" min-width="100" align="center">
                        <template #default="scope">
                            <el-tag v-if="scope.row.priority === 0" type="info" effect="dark">低</el-tag>
                            <el-tag v-if="scope.row.priority === 1" type="primary" effect="dark">中</el-tag>
                            <el-tag v-if="scope.row.priority === 2" type="warning" effect="dark">高</el-tag>
                            <el-tag v-if="scope.row.priority === 3" type="danger" effect="dark">紧急</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column property="quantity" label="计划数量" min-width="120" align="center">
                        <template #default="{ row }">
                            <el-icon>
                                <Box />
                            </el-icon>
                            {{ row.quantity }}
                        </template>
                    </el-table-column>
                    <el-table-column property="planningTime" label="计划完成时间" min-width="150" align="center">
                        <template #default="{ row }">
                            <el-icon>
                                <Calendar />
                            </el-icon>
                            {{ row.planningTime }}
                        </template>
                    </el-table-column>
                    <el-table-column property="status" label="状态" min-width="120" align="center">
                        <template #default="scope">
                            <el-tag v-if="scope.row.status === 0" type="warning" effect="dark">待生产</el-tag>
                            <el-tag v-if="scope.row.status === 1" type="primary" effect="dark">生产中</el-tag>
                            <el-tag v-if="scope.row.status === 2" type="success" effect="dark">已完成</el-tag>
                            <el-tag v-if="scope.row.status === 3" type="info" effect="dark">已关闭</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="150" align="center" fixed="right">
                        <template #default="scope">
                            <el-button size="small" type="primary" @click="handleEdit(scope.row.id)" link><el-icon>
                                    <Edit />
                                </el-icon>编辑</el-button>
                            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)" link><el-icon>
                                    <Delete />
                                </el-icon>删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页 -->
            <div class="pagination-wrapper">
                <el-pagination :current-page="queryFormModel.pageNum" :page-size="queryFormModel.pageSize" :page-sizes="[10, 25, 50, 100, 500]"
                    layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange" background />
            </div>
        </el-card>

        <!-- 添加/编辑工单对话框 -->
        <el-dialog v-model="dialogFormVisible" :title="isEdit ? '编辑工单' : '新建工单'" width="650px" center destroy-on-close
            class="workorder-dialog">
            <el-form :model="workOrderDTO" :rules="formRules" ref="formRef" label-width="120px" label-position="right"
                class="workorder-form">
                <el-form-item label="产品名称" prop="productId">
                    <el-select v-model="workOrderDTO.productId" placeholder="请选择产品" filterable style="width: 100%;"
                        prefix-icon="Goods">
                        <el-option v-for="item in productList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="产线" prop="lineId">
                    <el-select v-model="workOrderDTO.lineId" placeholder="请选择产线" style="width: 100%;"
                        prefix-icon="Connection">
                        <el-option v-for="item in lineList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="优先级" prop="priority">
                    <el-select v-model="workOrderDTO.priority" placeholder="请选择工单优先级" style="width: 100%;">
                        <el-option label="低" :value="0">
                            <el-tag type="info" effect="dark">低</el-tag>
                        </el-option>
                        <el-option label="中" :value="1">
                            <el-tag type="primary" effect="dark">中</el-tag>
                        </el-option>
                        <el-option label="高" :value="2">
                            <el-tag type="warning" effect="dark">高</el-tag>
                        </el-option>
                        <el-option label="紧急" :value="3">
                            <el-tag type="danger" effect="dark">紧急</el-tag>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="计划生产数量" prop="quantity">
                    <el-input-number v-model="workOrderDTO.quantity" :min="1" :max="999999" style="width: 100%;"
                        controls-position="right" />
                </el-form-item>
                <el-form-item label="计划完成时间" prop="planningTime">
                    <el-date-picker v-model="workOrderDTO.planningTime" type="date" placeholder="选择日期"
                        style="width: 100%;" value-format="YYYY-MM-DD" prefix-icon="Calendar" />
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="workOrderDTO.status" placeholder="请选择工单状态" style="width: 100%;">
                        <el-option label="待生产" :value="0">
                            <el-tag type="warning" effect="dark">待生产</el-tag>
                        </el-option>
                        <el-option label="生产中" :value="1">
                            <el-tag type="primary" effect="dark">生产中</el-tag>
                        </el-option>
                        <el-option label="生产完成" :value="2">
                            <el-tag type="success" effect="dark">生产完成</el-tag>
                        </el-option>
                        <el-option label="已关闭" :value="3">
                            <el-tag type="info" effect="dark">已关闭</el-tag>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="备注" prop="remarks">
                    <el-input v-model="workOrderDTO.remarks" type="textarea" :rows="3" placeholder="请输入工单备注信息（可选）"
                        maxlength="500" show-word-limit />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="handleCancel">
                        <el-icon>
                            <Close />
                        </el-icon>取消</el-button>
                    <el-button type="primary" @click="handleSubmit">
                        <el-icon>
                            <Check />
                        </el-icon>提交</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { onBeforeMount, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import {
    addWorkOrderApi,
    deleteWorkOrderApi,
    editWorkOrderApi,
    getWorkOrderByIdApi,
    getWorkOrderListApi,
    queryWorkOrderApi
} from '@/api/workorder';
import { getLineListApi } from '@/api/line';
import { getProductListApi } from '@/api/product';
import { isEmpty } from 'element-plus/es/utils/types.mjs';

defineOptions({
    name: 'WorkOrderManagement'
})

// 产线列表
const lineList = ref([])

// 产品列表
const productList = ref([])

// 表格数据
const tableData = ref([])

// 分页相关
// const pageNum = ref(1)
// const pageSize = ref(10)
const total = ref(0)

// 控制加载动画
const loading = ref(false)

// 控制按钮加载状态
const btnLoading = ref(false)

// 显隐dialog对话框
const dialogFormVisible = ref(false)

// 是否是编辑状态
const isEdit = ref(false)

// 勾选的行
const selectedRows = ref([])

// 从本地存储中获取用户信息
const userId = ref(localStorage.getItem('userId'))

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
    creatorId: userId,  // // 工单创建人ID
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
    createTime: '', // 工单创建时间
})

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
    queryFormModel.value = {
        pageNum: 1, // 当前页码
        pageSize: 10, // 每页显示的记录数
        id: '', // 工单ID/工单号
        status: '', // 工单状态(0:待生产, 1:生产中, 2:生产完成, 3:已关闭)
        priority: '',   // 优先级(0:低, 1:中, 2:高, 3:紧急)
        createTime: '', // 工单创建时间
    }
    queryWorkOrder()
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
        creatorId: userId  // 工单创建人ID
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
            const res = await addWorkOrderApi(workOrderDTO.value)
            if (res.code === 200) {
                ElMessage.success('工单创建成功')
                dialogFormVisible.value = false
                resetForm()
                queryWorkOrder()
            } else {
                ElMessage.error(res.message || '工单创建失败')
            }

        }
        else if (isEdit.value) {
            const res = await editWorkOrderApi(workOrderDTO.value)
            if (res.code === 200) {
                ElMessage.success('工单修改成功')
                dialogFormVisible.value = false
                resetForm()
                queryWorkOrder()
            } else {
                ElMessage.error(res.message || '工单修改失败')
            }

        }
    })
}

// 编辑工单
const handleEdit = async (row) => {
    isEdit.value = true
    resetForm()
    // 获取工单数据
    const res = await getWorkOrderByIdApi(toArray(row))
    if (res.code === 200) {
        workOrderDTO.value = res.data
    } else {
        ElMessage.error(res.message || '获取工单数据失败')
        return
    }

    // 显示编辑工单对话框
    dialogFormVisible.value = true
}

// 查询工单
const queryWorkOrder = async () => {
    loading.value = true
    btnLoading.value = true
    await queryWorkOrderApi(queryFormModel.value).then(res => {
            if (res.code === 200) {
                tableData.value = res.data.list
                queryFormModel.value.pageNum = res.data.pageNum
                queryFormModel.value.pageSize = res.data.pageSize
                total.value = res.data.total
                console.log(res.data)
            }
        }).finally(() => {
            loading.value = false
            btnLoading.value = false
        })
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
        const res = await deleteWorkOrderApi(toArray(row))
        if (res.code === 200) {
            ElMessage.success('删除工单成功')
            queryWorkOrder()
        }

    })
}

// 获取工单列表
const getWorkOrderList = async () => {
    const res = await getWorkOrderListApi(queryFormModel.value.pageNum, queryFormModel.value.pageSize)
    if (res.code === 200) {
        tableData.value = res.data.list
        queryFormModel.value.pageNum = res.data.pageNum
        queryFormModel.value.pageSize = res.data.pageSize
        total.value = res.data.total
    }
}

// 获取产线列表
const getLineList = async () => {
    const lineRes = await getLineListApi()
    if (lineRes.code === 200) {
        lineList.value = lineRes.data
    }
}

// 获取产品列表
const getProductList = async () => {
    const productRes = await getProductListApi(1, 1000)
    if (productRes.code === 200) {
        productList.value = productRes.data.list
    }
}

// 分页大小改变
const handleSizeChange = (val) => {
    queryFormModel.value.pageSize = val
    queryWorkOrder()
}

// 分页页码改变
const handleCurrentChange = (val) => {
    queryFormModel.value.pageNum = val
    queryWorkOrder()
}

onBeforeMount(async () => {
    loading.value = true

    // 并行发送网络请求
    await Promise.allSettled([
        getWorkOrderList(),
        getLineList(),
        getProductList()
    ]).catch(error => { throw error })

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
    background: linear-gradient(90deg, #513e84 0.000%, #6545b1 20.000%, #7a5cc9 40.000%, #9044cc 60.000%, #a53fb9 80.000%, #bb5b91 100.000%);
    border-radius: 4px;
    box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
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
    background: #448AFF;
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

/* 分页样式 */
.pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
}

/* 对话框样式 */
.workorder-dialog :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    padding: 20px 24px;
    margin: 0;
}

.workorder-dialog :deep(.el-dialog__title) {
    color: #ffffff;
    font-size: 18px;
    font-weight: 600;
}

.workorder-dialog :deep(.el-dialog__close) {
    color: #ffffff;
}

.workorder-dialog :deep(.el-dialog__close:hover) {
    color: rgba(255, 255, 255, 0.8);
}

.workorder-dialog :deep(.el-dialog__body) {
    padding: 32px 24px;
}

.workorder-form {
    padding: 0 12px;
}

.workorder-form :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
}

.workorder-form :deep(.el-input__wrapper),
.workorder-form :deep(.el-textarea__inner),
.workorder-form :deep(.el-select__wrapper) {
    border-radius: 4px;
    transition: all 0.3s ease;
}

.workorder-form :deep(.el-input__wrapper:hover),
.workorder-form :deep(.el-textarea__inner:hover),
.workorder-form :deep(.el-select__wrapper:hover) {
    box-shadow: 0 0 0 1px #4facfe inset;
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

    .search-form :deep(.el-input),
    .search-form :deep(.el-select) {
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