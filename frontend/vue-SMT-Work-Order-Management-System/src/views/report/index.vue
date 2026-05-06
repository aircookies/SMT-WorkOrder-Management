<template>
    <div class="container">
        <h1>生产报工</h1>

        <!-- 报工录入板块 -->
        <el-card class="box-card" header="报工录入">
            <!-- 工单信息子板块 -->
            <el-card class="sub-card" header="工单信息">
                <el-form :model="workOrderInfo" label-width="120px" size="default" label-position="left" inline="true">
                    <el-form-item label="工单编号">
                        <el-input v-model="orderId" placeholder="请输入工单编号" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="queryWorkOrder">查询</el-button>
                    </el-form-item>
                </el-form>
                <el-row :gutter="20">
                    <el-col :span="24">
                        <el-descriptions class="workorder-descriptions" title="" column="4" size="large">
                            <el-descriptions-item label="编号:">{{ workOrderInfo.id }}</el-descriptions-item>
                            <el-descriptions-item label="产品:">{{ workOrderInfo.productName }}</el-descriptions-item>
                            <el-descriptions-item label="产线:">{{ workOrderInfo.lineName }}</el-descriptions-item>
                            <el-descriptions-item label="计划生产数量:">{{ workOrderInfo.quantity }}</el-descriptions-item>
                            <el-descriptions-item label="创建时间:">{{ workOrderInfo.createTime }}</el-descriptions-item>
                            <el-descriptions-item label="优先级:">
                                <template #default>
                                    <el-tag v-if="workOrderInfo.priority === 0" type="info" size="small">低</el-tag>
                                    <el-tag v-if="workOrderInfo.priority === 1" type="primary" size="small">中</el-tag>
                                    <el-tag v-if="workOrderInfo.priority === 2" type="warning" size="small">高</el-tag>
                                    <el-tag v-if="workOrderInfo.priority === 3" type="danger" size="small">紧急</el-tag>
                                </template>
                            </el-descriptions-item>
                            <el-descriptions-item label="状态:">
                                <template #default>
                                    <el-tag v-if="workOrderInfo.status === 0" type="info" size="small">待生产</el-tag>
                                    <el-tag v-if="workOrderInfo.status === 1" type="primary" size="small">生产中</el-tag>
                                    <el-tag v-if="workOrderInfo.status === 2" type="warning" size="small">已完成</el-tag>
                                    <el-tag v-if="workOrderInfo.status === 3" type="danger" size="small">已关闭</el-tag>
                                </template>
                            </el-descriptions-item>
                        </el-descriptions>
                    </el-col>
                </el-row>
            </el-card>

            <!-- 报工信息子板块 -->
            <el-card class="sub-card" header="报工信息" style="margin-top: 20px">
                <el-form :model="report" label-width="120px" size="default">
                    <el-row :gutter="20">
                        <el-col :span="8">
                            <el-form-item label="工序">
                                <el-select v-model="report.processSeq" placeholder="请选择工序">
                                    <el-option label="印刷" :value="1"></el-option>
                                    <el-option label="贴片" :value="2"></el-option>
                                    <el-option label="回流焊" :value="3"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="合格数量">
                                <el-input-number v-model="report.qualifiedQuantity" :min="0" style="width: 100%" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="不良数量">
                                <el-input-number v-model="report.badQuantity" :min="0" style="width: 100%" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row :gutter="20">
                        <el-col :span="8">
                            <el-form-item label="完成时间">
                                <el-date-picker v-model="report.finishTime" type="datetime" placeholder="选择完成时间"
                                    format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="备注">
                                <el-input v-model="report.remarks" type="textarea" :rows="3" placeholder="请输入备注信息" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-form-item>
                        <el-button type="primary" @click="submitReport">提交报工</el-button>
                        <el-button @click="resetForm">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
        </el-card>

        <!-- 报工记录板块 -->
        <el-card class="box-card" header="报工记录" style="margin-top: 20px">
            <el-table :data="reportList" style="width: 100%" stripe border>
                <el-table-column prop="id" label="报工单ID" width="120" />
                <el-table-column prop="orderId" label="工单ID" width="120" />
                <el-table-column prop="processSeq" label="工序" width="100">
                    <template #default="{ row }">
                        {{ getProcessName(row.processSeq) }}
                    </template>
                </el-table-column>
                <el-table-column prop="qualifiedQuantity" label="合格数量" width="100" />
                <el-table-column prop="badQuantity" label="不合格数量" width="120" />
                <el-table-column prop="operatorId" label="操作员" width="100" />
                <el-table-column prop="finishTime" label="完成时间" width="160" />
                <el-table-column prop="remarks" label="备注" />
                <el-table-column label="操作" width="150">
                    <template #default="{ row }">
                        <el-button size="small" type="primary" @click="editReport(row)">编辑</el-button>
                        <el-button size="small" type="danger" @click="deleteReport(row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页组件 -->
            <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
                :page-sizes="[10, 20, 50, 100]" :total="pagination.total"
                layout="total, sizes, prev, pager, next, jumper" style="margin-top: 20px; text-align: right"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { ElMessage, ElMessageBox, ElDescriptions } from 'element-plus'
import { getReportListApi, addReportApi } from "@/api/report";
import { getWorkOrderByIdApi } from "@/api/workorder";

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

// 用于记录待查询的工单ID
let orderId = ref('')

// 报工信息数据模型
const report = ref({
    id: '', // 报工单id
    orderId: '', // 工单id
    processSeq: '', // 工序:1印刷 2贴片 3回流焊
    qualifiedQuantity: '', // 合格数量
    badQuantity: '', // 不合格数量
    operatorId: '1', // 操作员id 先用1
    remarks: '', // 备注
    finishTime: '', // 完成时间
})

// 报工记录列表
const reportList = ref([])

// 分页信息
const pagination = ref({
    currentPage: 1,
    pageSize: 10,
    total: 0
})

// 获取工序名称
const getProcessName = (processSeq) => {
    const processes = {
        1: '印刷',
        2: '贴片',
        3: '回流焊'
    }
    return processes[processSeq] || '未知'
}

// 清空工单信息
const clearWorkOrder = () => {
    workOrderInfo.value = {}
}

// 查询工单信息
const queryWorkOrder = async () => {
    // 判断用户是否输入了工单ID
    if (!orderId.value) {
        ElMessage.warning('请输入工单编号')
        return
    }

    try {
        const data = await getWorkOrderByIdApi(orderId.value)
        if (data.code === 200) {
            clearWorkOrder()
            workOrderInfo.value = data.data
        }
    } catch (error) {
        console.log(error || "发生错误")
        ElMessage.error('发生错误: ' + error.message)
    }
}

// 提交报工
const submitReport = async () => {
    if (!validateReport()) {
        return
    }

    try {
        if (report.id) {
            // 更新报工记录
            await updateReportApi(report)
            ElMessage.success('报工记录更新成功')
        } else {
            // 添加新报工记录
            await addReportApi(report)
            ElMessage.success('报工记录添加成功')
        }

        resetForm()
        fetchReportList()
    } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败: ' + error.message)
    }
}

// 验证报工数据
const validateReport = () => {
    if (!report.processSeq) {
        ElMessage.warning('请选择工序')
        return false
    }
    if (!report.status) {
        ElMessage.warning('请选择生产状态')
        return false
    }
    if (report.qualifiedQuantity === '') {
        ElMessage.warning('请输入合格数量')
        return false
    }
    if (report.badQuantity === '') {
        ElMessage.warning('请输入不合格数量')
        return false
    }
    if (!report.operatorId) {
        ElMessage.warning('请输入操作员ID')
        return false
    }

    return true
}

// 重置表单
const resetForm = () => {
    report.id = ''
    report.orderId = ''
    report.processSeq = ''
    report.qualifiedQuantity = ''
    report.badQuantity = ''
    report.operatorId = '1'
    report.remarks = ''
    report.finishTime = ''
}

// 编辑报工记录
const editReport = (row) => {
    Object.assign(report, { ...row })
}

// 删除报工记录
// const deleteReport = async (id) => {
//     try {
//         await ElMessageBox.confirm('确定要删除这条报工记录吗？', '警告', {
//             confirmButtonText: '确定',
//             cancelButtonText: '取消',
//             type: 'warning'
//         })

//         await deleteReportApi(id)
//         ElMessage.success('删除成功')
//         fetchReportList()
//     } catch (error) {
//         if (error !== 'cancel') {
//             console.error('删除失败:', error)
//             ElMessage.error('删除失败: ' + error.message)
//         }
//     }
// }

// 获取报工记录列表
const fetchReportList = async () => {
    try {
        const params = {
            page: pagination.currentPage,
            pageSize: pagination.pageSize
        }
        const response = await getReportListApi(params)
        reportList.value = response.data.list
        pagination.total = response.data.total
    } catch (error) {
        console.error('获取报工记录失败:', error)
        ElMessage.error('获取报工记录失败: ' + error.message)
    }
}

// 分页大小改变
const handleSizeChange = (val) => {
    pagination.pageSize = val
    fetchReportList()
}

// 当前页改变
const handleCurrentChange = (val) => {
    pagination.currentPage = val
    fetchReportList()
}

// 页面加载时获取报工记录
onMounted(() => {
    fetchReportList()
})
</script>

<style scoped>
.container {
    padding: 10px 20px;
    width: 100%;
}

.box-card {
    margin: 10px 0;
}

.sub-card {
    margin-bottom: 10px;
}
</style>