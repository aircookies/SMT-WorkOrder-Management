<template>
    <div class="container">
        <h1>生产报工</h1>

        <!-- 报工录入板块 -->
        <el-card class="box-card" header="报工录入">
            <!-- 工单信息子板块 -->
            <el-card class="sub-card" header="工单信息">
                <el-form :model="workOrderInfo" label-width="120px" size="default">
                    <el-row :gutter="20">
                        <el-col :span="8">
                            <el-form-item label="工单编号">
                                <el-input v-model="workOrderInfo.orderNo" placeholder="请输入工单编号" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="产品名称">
                                <el-input v-model="workOrderInfo.productName" placeholder="请输入产品名称" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="计划数量">
                                <el-input-number v-model="workOrderInfo.planQuantity" :min="0" style="width: 100%" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
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
                            <el-form-item label="生产状态">
                                <el-select v-model="report.status" placeholder="请选择生产状态">
                                    <el-option label="未开始" :value="0"></el-option>
                                    <el-option label="进行中" :value="1"></el-option>
                                    <el-option label="已完成" :value="2"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="合格数量">
                                <el-input-number v-model="report.qualifiedQuantity" :min="0" style="width: 100%" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row :gutter="20">
                        <el-col :span="8">
                            <el-form-item label="不合格数量">
                                <el-input-number v-model="report.badQuantity" :min="0" style="width: 100%" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="操作员">
                                <el-input v-model="report.operatorId" placeholder="请输入操作员ID" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="完成时间">
                                <el-date-picker
                                    v-model="report.finishTime"
                                    type="datetime"
                                    placeholder="选择完成时间"
                                    format="YYYY-MM-DD HH:mm:ss"
                                    value-format="YYYY-MM-DD HH:mm:ss"
                                />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row :gutter="20">
                        <el-col :span="24">
                            <el-form-item label="备注">
                                <el-input 
                                    v-model="report.remarks" 
                                    type="textarea" 
                                    :rows="3"
                                    placeholder="请输入备注信息" 
                                />
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
                <el-table-column prop="status" label="生产状态" width="100">
                    <template #default="{ row }">
                        {{ getStatusName(row.status) }}
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
            <el-pagination
                v-model:current-page="pagination.currentPage"
                v-model:page-size="pagination.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="pagination.total"
                layout="total, sizes, prev, pager, next, jumper"
                style="margin-top: 20px; text-align: right"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue"
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReportListApi, addReportApi } from "@/api/report";

// 工单信息数据模型
const workOrderInfo = reactive({
    orderNo: '',
    productName: '',
    planQuantity: null,
})

// 报工信息数据模型
const report = reactive({
    id: '', // 报工单id
    orderId: '', // 工单id
    processSeq: '', // 丹铅工序:1印刷 2贴片 3回流焊
    status: '', // 生产状态：0未开始 1进行中 2已完成
    qualifiedQuantity: '', // 合格数量
    badQuantity: '', // 不合格数量
    operatorId: '', // 操作员id
    remarks: '', // 备注
    finishTime: '', // 完成时间
})

// 报工记录列表
const reportList = ref([])

// 分页信息
const pagination = reactive({
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

// 获取状态名称
const getStatusName = (status) => {
    const statuses = {
        0: '未开始',
        1: '进行中',
        2: '已完成'
    }
    return statuses[status] || '未知'
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
    report.status = ''
    report.qualifiedQuantity = ''
    report.badQuantity = ''
    report.operatorId = ''
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
    margin-bottom: 20px;
}

.sub-card {
    margin-bottom: 10px;
}
</style>