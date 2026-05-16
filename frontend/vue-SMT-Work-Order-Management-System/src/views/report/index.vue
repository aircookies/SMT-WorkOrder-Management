<template>
    <div class="container">
        <h1>生产报工</h1>

        <!-- 报工录入板块 -->
        <el-card class="box-card" header="报工录入" v-loading="messageLoading">
            <!-- 工单信息子板块 -->
            <el-card class="sub-card" header="工单信息" shadow="never">
                <el-form :model="workOrderInfo" :rules="workOrderRules" ref="workOrderFormRef" label-width="auto"
                    size="default" label-position="left" inline="true">
                    <el-form-item label="工单编号" prop="id">
                        <el-input v-model="workOrderInfo.id" placeholder="请输入工单编号" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="queryWorkOrder">查询</el-button>
                    </el-form-item>
                </el-form>
                <el-row :gutter="20">
                    <el-col :span="24">
                        <el-descriptions class="workorder-descriptions" title="" column="4" size="large">
                            <el-descriptions-item label="编号:">{{ currentOrderId }}</el-descriptions-item>
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
            <el-card class="sub-card" header="报工信息" style="margin-top: 20px" shadow="never" >
                <el-form :model="report" :rules="reportRules" ref="reportFormRef" label-width="auto" size="default">
                    <el-row :gutter="20">
                        <el-col :span="8">
                            <el-form-item label="工序" prop="processSeq">
                                <el-select v-model="report.processSeq" placeholder="请选择工序" style="width: 100%">
                                    <el-option label="印刷" :value="1"></el-option>
                                    <el-option label="贴片" :value="2"></el-option>
                                    <el-option label="回流焊" :value="3"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="合格数量" prop="qualifiedQuantity">
                                <el-input-number v-model="report.qualifiedQuantity" :min="0" style="width: 100%" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="不良数量" prop="badQuantity">
                                <el-input-number v-model="report.badQuantity" :min="0" style="width: 100%" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row :gutter="20">
                        <el-col :span="8">
                            <el-form-item label="完成时间" prop="finishTime">
                                <el-date-picker v-model="report.finishTime" type="datetime" placeholder="选择完成时间"
                                    format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DDTHH:mm:ss" 
                                    style="width: 100%" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="备注" prop="remarks">
                                <el-input v-model="report.remarks" type="textarea" :rows="3" placeholder="请输入备注信息"
                                    maxlength="500" show-word-limit />
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
            <el-table v-loading="tableLoading" :data="reportList" style="width: 100%" stripe border>
                <el-table-column prop="id" label="报工单ID" width="120" />
                <el-table-column prop="orderId" label="工单ID" width="120" />
                <el-table-column prop="processSeq" label="工序" width="100">
                    <template #default="{ row }">
                        {{ getProcessName(row.processSeq) }}
                    </template>
                </el-table-column>
                <el-table-column prop="qualifiedQuantity" label="合格数量" min-width="120" />
                <el-table-column prop="badQuantity" label="不合格数量" min-width="120" />
                <el-table-column prop="operatorId" label="操作员" min-width="120" />
                <el-table-column prop="finishTime" label="完成时间" min-width="160" />
                <el-table-column prop="remarks" label="备注" />
                <!-- <el-table-column label="操作" width="150">
                    <template #default="{ row }">
                        <el-button size="small" type="primary" @click="editReport(row)">编辑</el-button>
                        <el-button size="small" type="danger" @click="deleteReport(row.id)">删除</el-button>
                    </template>
                </el-table-column> -->
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

// 清空工单信息表单
const clearWorkOrderFormRule = () => {
    if (!workOrderFormRef) return
    workOrderFormRef.value.resetFields()
}

// 查询工单信息
const queryWorkOrder = async () => {
    // 校验表单
    try {
        await workOrderFormRef.value.validate()
    } catch (error) {
        ElMessage.warning('请完善表单信息')
        return
    }

    messageLoading.value = true
    try {
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
        } else {
            ElMessage.error('工单信息获取失败')
            clearWorkOrder()
        }
    } catch (error) {
        console.log(error || "发生错误")
        ElMessage.error('发生错误: ' + error.message)
    } finally {
        messageLoading.value = false
    }
}

// 提交报工
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
        return;
    }

    messageLoading.value = true

    try {
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
        const result = await addReportApi(report.value)
        if (result.code === 200) {
            ElMessage.success('报工记录添加成功')
        } else {
            ElMessage.error('报工记录添加失败: ' + result.message)
        }

        resetForm()
        fetchReportList()
    } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败: ' + error.message)
    } finally {
        messageLoading.value = false
    }
}

// 重置表单
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
    tableLoading.value = true
    try {
        const response = await getReportListApi(pagination.value.currentPage, pagination.value.pageSize)
        if (response.code === 200) {
            reportList.value = response.data.list
            pagination.value.total = response.data.total
        }
    } catch (error) {
        console.error('获取报工记录失败:', error)
        ElMessage.error('获取报工记录失败: ' + error.message)
    } finally {
        tableLoading.value = false
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