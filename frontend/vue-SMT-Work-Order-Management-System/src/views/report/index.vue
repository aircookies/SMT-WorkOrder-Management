<template>
    <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
            <h1 class="page-title">
                <el-icon class="title-icon">
                    <TrendCharts />
                </el-icon>
                生产报工
            </h1>
            <p class="page-subtitle">录入和管理生产工序报工信息</p>
        </div>

        <!-- 报工录入板块 -->
        <el-card class="input-card" shadow="hover">
            <template #header>
                <div class="card-header">
                    <el-icon>
                        <EditPen />
                    </el-icon>
                    <span>报工录入</span>
                </div>
            </template>

            <!-- 工单信息子板块 -->
            <el-card class="sub-card" shadow="never">
                <template #header>
                    <div class="card-header">
                        <el-icon>
                            <Document />
                        </el-icon>
                        <span>工单信息</span>
                    </div>
                </template>

                <el-form :model="workOrderInfo" :rules="workOrderRules" ref="workOrderFormRef" label-width="auto"
                    size="default" label-position="left" inline class="query-form">
                    <el-form-item label="工单编号" prop="id">
                        <el-input v-model="workOrderInfo.id" placeholder="请输入工单编号" prefix-icon="Search"
                            style="width: 240px" clearable />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="queryWorkOrder" :loading="loadingBtn">
                            <el-icon>
                                <Search />
                            </el-icon>
                            查询
                        </el-button>
                        <el-button @click="clearWorkOrderAndForm">
                            <el-icon>
                                <Refresh />
                            </el-icon>
                            清空</el-button>
                    </el-form-item>
                </el-form>

                <el-row :gutter="20" v-if="currentOrderId">
                    <el-col :span="24">
                        <el-descriptions class="workorder-descriptions" column="4" size="large" border>
                            <el-descriptions-item label-align="right" label-class-name="desc-label">
                                <template #label>
                                    <el-icon>
                                        <Ticket />
                                    </el-icon>
                                    编号
                                </template>
                                <el-tag type="primary" effect="plain">{{ currentOrderId }}</el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label-align="right" label-class-name="desc-label">
                                <template #label>
                                    <el-icon>
                                        <Goods />
                                    </el-icon>
                                    产品
                                </template>
                                {{ workOrderInfo.productName }}
                            </el-descriptions-item>
                            <el-descriptions-item label-align="right" label-class-name="desc-label">
                                <template #label>
                                    <el-icon>
                                        <Connection />
                                    </el-icon>
                                    产线
                                </template>
                                {{ workOrderInfo.lineName }}
                            </el-descriptions-item>
                            <el-descriptions-item label-align="right" label-class-name="desc-label">
                                <template #label>
                                    <el-icon>
                                        <Box />
                                    </el-icon>
                                    计划生产数量
                                </template>
                                <strong>{{ workOrderInfo.quantity }}</strong>
                            </el-descriptions-item>
                            <el-descriptions-item label-align="right" label-class-name="desc-label">
                                <template #label>
                                    <el-icon>
                                        <Clock />
                                    </el-icon>
                                    创建时间
                                </template>
                                {{ workOrderInfo.createTime }}
                            </el-descriptions-item>
                            <el-descriptions-item label-align="right" label-class-name="desc-label">
                                <template #label>
                                    <el-icon>
                                        <Flag />
                                    </el-icon>
                                    优先级
                                </template>
                                <el-tag v-if="workOrderInfo.priority === 0" type="info" effect="dark"
                                    size="small">低</el-tag>
                                <el-tag v-if="workOrderInfo.priority === 1" type="primary" effect="dark"
                                    size="small">中</el-tag>
                                <el-tag v-if="workOrderInfo.priority === 2" type="warning" effect="dark"
                                    size="small">高</el-tag>
                                <el-tag v-if="workOrderInfo.priority === 3" type="danger" effect="dark"
                                    size="small">紧急</el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label-align="right" label-class-name="desc-label" :span="2">
                                <template #label>
                                    <el-icon>
                                        <CircleCheck />
                                    </el-icon>
                                    状态
                                </template>
                                <el-tag v-if="workOrderInfo.status === 0" type="warning" effect="dark"
                                    size="small">待生产</el-tag>
                                <el-tag v-if="workOrderInfo.status === 1" type="primary" effect="dark"
                                    size="small">生产中</el-tag>
                                <el-tag v-if="workOrderInfo.status === 2" type="success" effect="dark"
                                    size="small">生产完成</el-tag>
                                <el-tag v-if="workOrderInfo.status === 3" type="info" effect="dark"
                                    size="small">已关闭</el-tag>
                            </el-descriptions-item>
                        </el-descriptions>
                    </el-col>
                </el-row>
            </el-card>

            <!-- 报工信息子板块 -->
            <el-card class="sub-card report-form-card" shadow="never">
                <template #header>
                    <div class="card-header">
                        <el-icon>
                            <List />
                        </el-icon>
                        <span>报工信息</span>
                    </div>
                </template>

                <el-form :model="report" :rules="reportRules" ref="reportFormRef" label-width="120px" size="default"
                    class="report-form">
                    <el-row :gutter="20">
                        <el-col :span="8">
                            <el-form-item label="工序" prop="processSeq">
                                <el-select v-model="report.processSeq" placeholder="请选择工序" style="width: 100%">
                                    <el-option label="印刷" :value="1">
                                        <el-tag type="info" effect="dark">印刷</el-tag>
                                    </el-option>
                                    <el-option label="贴片" :value="2">
                                        <el-tag type="primary" effect="dark">贴片</el-tag>
                                    </el-option>
                                    <el-option label="回流焊" :value="3">
                                        <el-tag type="warning" effect="dark">回流焊</el-tag>
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="合格数量" prop="qualifiedQuantity">
                                <el-input-number v-model="report.qualifiedQuantity" :min="0" :max="999999"
                                    style="width: 100%" controls-position="right" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="不良数量" prop="badQuantity">
                                <el-input-number v-model="report.badQuantity" :min="0" :max="999999" style="width: 100%"
                                    controls-position="right" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row :gutter="20">
                        <el-col :span="8">
                            <el-form-item label="完成时间" prop="finishTime">
                                <el-date-picker v-model="report.finishTime" type="datetime" placeholder="选择完成时间"
                                    format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%"
                                    prefix-icon="Calendar" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="16">
                            <el-form-item label="备注" prop="remarks">
                                <el-input v-model="report.remarks" type="textarea" :rows="3" placeholder="请输入备注信息（可选）"
                                    maxlength="500" show-word-limit />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-form-item class="form-actions">
                        <el-button type="primary" @click="submitReport" :loading="loadingBtn"
                            size="large">
                            <el-icon>
                                <Check />
                            </el-icon>提交报工</el-button>
                        <el-button @click="resetForm" size="large">
                            <el-icon>
                                <Refresh />
                            </el-icon>重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
        </el-card>

        <!-- 报工记录板块 -->
        <el-card class="table-card" shadow="hover">
            <template #header>
                <div class="card-header">
                    <el-icon>
                        <DocumentChecked />
                    </el-icon>
                    <span>报工记录</span>
                </div>
            </template>

            <div class="table-wrapper">
                <el-table v-loading="tableLoading" :data="reportList" style="width: 100%" stripe border
                    class="custom-table">
                    <el-table-column prop="id" label="报工单ID" min-width="120" align="center">
                        <template #default="{ row }">
                            <el-tag type="info" effect="plain">{{ row.id }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="orderId" label="工单ID" min-width="120" align="center">
                        <template #default="{ row }">
                            <el-tag type="primary" effect="plain">{{ row.orderId }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="processSeq" label="工序" min-width="120" align="center">
                        <template #default="{ row }">
                            <el-tag v-if="row.processSeq === 1" type="info" effect="dark">印刷</el-tag>
                            <el-tag v-if="row.processSeq === 2" type="primary" effect="dark">贴片</el-tag>
                            <el-tag v-if="row.processSeq === 3" type="warning" effect="dark">回流焊</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="qualifiedQuantity" label="合格数量" min-width="120" align="center">
                        <template #default="{ row }">
                            <el-icon color="#67c23a">
                                <CircleCheck />
                            </el-icon>
                            <strong>{{ row.qualifiedQuantity }}</strong>
                        </template>
                    </el-table-column>
                    <el-table-column prop="badQuantity" label="不合格数量" min-width="120" align="center">
                        <template #default="{ row }">
                            <el-icon color="#f56c6c">
                                <CircleClose />
                            </el-icon>
                            <strong>{{ row.badQuantity }}</strong>
                        </template>
                    </el-table-column>
                    <el-table-column prop="operatorId" label="操作员" min-width="120" align="center">
                        <template #default="{ row }">
                            <el-icon>
                                <User />
                            </el-icon>
                            {{ row.operatorId }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="finishTime" label="完成时间" min-width="180" align="center">
                        <template #default="{ row }">
                            <el-icon>
                                <Calendar />
                            </el-icon>
                            {{ row.finishTime }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="remarks" label="备注" min-width="200" show-overflow-tooltip align="center" />
                    <el-table-column label="操作" min-width="80" align="center">
                        <template #default="scope">
                            <el-button type="danger" size="small" @click="deleteReport(scope.row.id)" link>
                                <el-icon>
                                    <Delete />
                                </el-icon> 删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页组件 -->
            <div class="pagination-wrapper">
                <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
                    :page-sizes="[10, 20, 50, 100]" :total="pagination.total"
                    layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange" background />
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { onBeforeMount, ref } from "vue"
import { ElMessage, ElMessageBox } from 'element-plus';
import { addReportApi, deleteReportApi, getReportListApi } from "@/api/report";
import { getWorkOrderByIdApi } from "@/api/workorder";

defineOptions({
    name: 'ProductionReport'
})

// ==================== 数据模型 ====================

// 工单信息数据模型
const workOrderInfo = ref({
    id: '',    // 工单ID
    productName: '',    // 产品名称
    lineName: '',   // 产线名称
    quantity: null, // 计划生产数量
    createTime: '', // 工单创建日期
    priority: '', // 工单优先级
    status: '' // 工单状态
})

// 用于记录查询的工单ID
const currentOrderId = ref()

// 表单引用
const workOrderFormRef = ref()
const reportFormRef = ref()

// 工单信息表单校验规则
const workOrderRules = ref({
    id: [
        { required: true, message: '请输入工单编号', trigger: 'blur' },
        { min: 1, max: 50, message: '工单编号长度应在1-50之间', trigger: 'blur' },
        { pattern: /^[0-9]+$/, message: '工单编号只能是数字', trigger: 'blur' }
    ]
})

// 报工信息表单校验规则
const reportRules = ref({
    processSeq: [
        { required: true, message: '请选择工序', trigger: 'change' },
        { type: 'number', min: 1, max: 3, message: '工序必须是1-3之间的数字', trigger: 'change' }
    ],
    qualifiedQuantity: [
        { required: true, message: '请输入合格数量', trigger: 'blur' },
        { type: 'number', min: 0, message: '合格数量不能小于0', trigger: 'blur' },
        { validator: validateTotalQuantity, trigger: 'blur' }
    ],
    badQuantity: [
        { required: true, message: '请输入不良数量', trigger: 'blur' },
        { type: 'number', min: 0, message: '不良数量不能小于0', trigger: 'blur' },
        { validator: validateTotalQuantity, trigger: 'blur' }
    ],
    finishTime: [
        { required: true, message: '请选择完成时间', trigger: 'change' },
        { type: 'string', message: '请选择正确的完成时间', trigger: 'change' }
    ],
    remarks: [
        { max: 500, message: '备注长度不能超过500个字符', trigger: 'blur' }
    ]
})

// 验证总数量的自定义校验器
function validateTotalQuantity(rule, value, callback) {
    // 检查合格数量和不良数量之和是否超过工单计划数量
    const total = Number(report.value.qualifiedQuantity || 0) + Number(report.value.badQuantity || 0);
    const maxAllowed = Number(workOrderInfo.value.quantity || Infinity);

    if (total > maxAllowed) {
        callback(new Error(`合格数量与不良数量之和(${total})不能超过工单计划数量(${maxAllowed})`));
    } else {
        callback();
    }
}

// 报工信息数据模型
const report = ref({
    id: '', // 报工单id
    orderId: '', // 工单id
    processSeq: '', // 工序:1印刷 2贴片 3回流焊
    qualifiedQuantity: 0, // 合格数量
    badQuantity: 0, // 不合格数量
    operatorId: '1', // 操作员id 先用1
    remarks: '', // 备注
    finishTime: '', // 完成时间
})

// 控制报工录入板块的加载动画
const messageLoading = ref(false)

// 控制表格的加载动画
const tableLoading = ref(false)

// 控制按钮加载状态
const loadingBtn = ref(false)

// 报工记录列表
const reportList = ref([])

// ==================== 分页信息 ====================

// 分页信息
const pagination = ref({
    currentPage: 1,
    pageSize: 10,
    total: 0
})

// 清空工单信息
const clearWorkOrder = () => {
    workOrderInfo.value = {
        id: '',    // 工单ID
        productName: '',    // 产品名称
        lineName: '',   // 产线名称
        quantity: null, // 计划生产数量
        createTime: '', // 工单创建日期
        priority: '', // 工单优先级
        status: '' // 工单状态
    }
    currentOrderId.value = ''
}

/**
 * 清空工单信息表单的校验状态
 */
const clearWorkOrderFormRule = () => {
    if (!workOrderFormRef.value) return
    workOrderFormRef.value.resetFields()
}

/**
 * 同时清空工单信息和表单校验
 */
const clearWorkOrderAndForm = () => {
    clearWorkOrder()
    clearWorkOrderFormRule()
}

/**
 * 根据工单编号查询工单信息
 */
const queryWorkOrder = async () => {
    // 校验表单
    try {
        await workOrderFormRef.value.validate()
    } catch (error) {
        ElMessage.warning('请完善表单信息')
        console.warn(error)
        return
    }

    messageLoading.value = true
    const data = await getWorkOrderByIdApi(workOrderInfo.value.id)
    if (data.code === 200) {
        if (data.data == null) {
            ElMessage.error('工单不存在')
            return
        }
        clearWorkOrder()
        workOrderInfo.value = data.data
        currentOrderId.value = data.data.id
        workOrderInfo.value.id = ''
        clearWorkOrderFormRule()
    }
    messageLoading.value = false
}

// ==================== 报工操作 ====================

/**
 * 提交报工记录
 */
const submitReport = async () => {
    // 确认工单信息是否存在
    if (!currentOrderId.value) {
        ElMessage.warning('请获取工单信息')
        return
    }
    report.value.orderId = currentOrderId.value

    // 验证表单
    try {
        await reportFormRef.value.validate();
    } catch (error) {
        ElMessage.warning('请完善表单信息')
        console.warn(error)
        return;
    }

    messageLoading.value = true
    loadingBtn.value = true

    // if (report.value.id) {
    //     // 更新报工记录
    //     // await updateReportApi(report.value)
    //     ElMessage.success('报工记录更新成功')
    // } else {
    //     // 添加新报工记录
    //     await addReportApi(report.value)
    //     ElMessage.success('报工记录添加成功')
    // }
    // 添加新报工记录
    await addReportApi(report.value).then(result => {
        if (result.code === 200) {
            ElMessage.success('报工记录添加成功')
        }
        resetForm()
        fetchReportList()
    }).finally(() => {
        messageLoading.value = false
        loadingBtn.value = false
    })
}

/**
 * 重置报工表单
 */
const resetForm = () => {
    if (reportFormRef.value) {
        reportFormRef.value.resetFields();
    }

    report.value = {
        id: '',
        orderId: '',
        processSeq: '',
        qualifiedQuantity: 0,
        badQuantity: 0,
        operatorId: '1',
        remarks: '',
        finishTime: ''
    }
}

// 编辑报工记录
// const editReport = (row) => {
//     Object.assign(report, { ...row })
// }

/**
 * 删除报工记录（带确认弹窗）
 * @param {number} id - 报工记录 ID
 */
const deleteReport = async (id) => {
    await ElMessageBox.confirm('确定要删除这条报工记录吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    })

    await deleteReportApi(id)
    ElMessage.success('删除成功')
    fetchReportList()
}

/**
 * 获取报工记录分页列表
 */
const fetchReportList = async () => {
    tableLoading.value = true
    await getReportListApi(pagination.value.currentPage, pagination.value.pageSize).then(response => {
        if (response.code === 200) {
            reportList.value = response.data.list
            pagination.value.total = response.data.total
        }
    }).finally(() => {
        tableLoading.value = false
    })
}

/**
 * 分页大小改变时的回调
 * @param {number} val - 新的每页条数
 */
const handleSizeChange = (val) => {
    pagination.value.pageSize = val
    fetchReportList()
}

/**
 * 当前页码改变时的回调
 * @param {number} val - 新的页码
 */
const handleCurrentChange = (val) => {
    pagination.value.currentPage = val
    fetchReportList()
}

/**
 * 页面加载时获取报工记录列表
 */
onBeforeMount(async () => {
    await fetchReportList()
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
    background: linear-gradient(135deg, #fa709a 10%, #faeda5 100%);
    border-radius: 4px;
    box-shadow: 0 4px 12px rgba(250, 112, 154, 0.3);
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
.input-card,
.table-card {
    margin-bottom: 20px;
    border-radius: 4px;
    border: none;
    transition: all 0.3s ease;
}

.input-card:hover,
.table-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
    font-size: 16px;
    color: #303133;
}

.card-header .el-icon {
    font-size: 20px;
    color: #fa709a;
}

/* 子卡片样式 */
.sub-card {
    margin-bottom: 20px;
    border-radius: 4px;
    border: 1px solid #ebeef5;
}

.sub-card :deep(.el-card__header) {
    background-color: #fafafa;
    border-bottom: 1px solid #ebeef5;
    padding: 12px 20px;
}

/* 查询表单样式 */
.query-form {
    padding: 20px;
    border-radius: 4px;
    margin-bottom: 20px;
}

.query-form :deep(.el-form-item) {
    margin-bottom: 0;
}

/* 工单描述样式 */
.workorder-descriptions {
    margin-top: 16px;
}

.workorder-descriptions :deep(.el-descriptions__label) {
    font-weight: 500;
    color: #606266;
}

.desc-label {
    display: flex;
    align-items: center;
    gap: 4px;
}

.desc-label .el-icon {
    font-size: 16px;
    color: #fa709a;
}

/* 报工表单样式 */
/* .report-form-card {
} */

.report-form {
    padding: 20px;
}

.report-form :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
}

.report-form :deep(.el-input__wrapper),
.report-form :deep(.el-textarea__inner),
.report-form :deep(.el-select__wrapper) {
    border-radius: 4px;
    transition: all 0.3s ease;
}

.report-form :deep(.el-input__wrapper:hover),
.report-form :deep(.el-textarea__inner:hover),
.report-form :deep(.el-select__wrapper:hover) {
    box-shadow: 0 0 0 1px #fa709a inset;
}

.form-actions {
    text-align: center;
    margin-top: 24px;
    padding-top: 20px;
    border-top: 1px solid #ebeef5;
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
    background: #ff77a4;
    color: #ffffff;
    font-weight: 600;
    font-size: 14px;
    padding: 16px 0;
}

.custom-table :deep(.el-table__row) {
    transition: all 0.2s ease;
}

.custom-table :deep(.el-table__row:hover) {
    background-color: #fff5f5 !important;
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

    .query-form {
        flex-direction: column;
    }

    .query-form :deep(.el-form-item) {
        width: 100%;
    }

    .query-form :deep(.el-input) {
        width: 100% !important;
    }

    .workorder-descriptions {
        :deep(.el-descriptions__body) {
            grid-template-columns: repeat(2, 1fr) !important;
        }
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

.input-card {
    animation: fadeInUp 0.6s ease-out 0.1s both;
}

.table-card {
    animation: fadeInUp 0.6s ease-out 0.2s both;
}
</style>