<template>
    <div class="container">
        <h1>工单管理</h1>
        <!-- 工具栏 -->
        <div class="toolbar">
            <div class="buttons">
                <el-button type="primary" plain>新建表单</el-button>
                <el-button type="danger" plain>批量删除</el-button>
            </div>
            <!-- 搜索栏 -->
            <div class="search">
                <el-form :inline="true" :model="workOrderDTO" label-width="auto" class="search-form">
                    <el-form-item label="工单号:">
                        <el-input v-model="workOrderDTO.id" />
                    </el-form-item>
                    <el-form-item label="工单状态:">
                        <el-select v-model="workOrderDTO.status" placeholder="未选择" style="min-width: 100px;">
                            <el-option label="未选择" value="" />
                            <el-option label="待生产" value="0" />
                            <el-option label="生产中" value="1" />
                            <el-option label="生产完成" value="2" />
                            <!-- <el-option label="已关闭" value="3" /> -->
                        </el-select>
                    </el-form-item>
                    <el-form-item label="优先级:">
                        <el-select v-model="workOrderDTO.priority" placeholder="未选择" style="min-width: 100px;">
                            <el-option label="未选择" value="" />
                            <el-option label="低" value="0" />
                            <el-option label="中" value="1" />
                            <el-option label="高" value="2" />
                            <el-option label="紧急" value="3" />
                            <!-- <el-option label="已关闭" value="3" /> -->
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="">查询</el-button>
                        <el-button>清空</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <!--  表格 -->
        <div class="table">
            <el-table :data="tableData" style="width: 100%">
                <el-table-column type="selection" width="55" />
                <el-table-column property="id" label="工单号" width="120" />
                <el-table-column property="productName" label="产品名称" max-width="120" />
                <el-table-column property="lineName" label="产线名称" max-width="240" />
                <el-table-column property="priority" label="优先级" max-width="120" />
                <el-table-column property="quantity" label="计划生产数量" max-width="120" />
                <el-table-column property="status" label="状态" max-width="120" />
                <el-table-column label="操作">
                    <template #default="scope">
                        <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
                            编辑
                        </el-button>
                        <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <!-- 分页 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper" :total="400" @size-change="handleSizeChange"
            @current-change="handleCurrentChange" />
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElForm, ElFormItem, ElInput, ElSelect, ElButton } from 'element-plus';

// 数据模型
const workOrderDTO = ref({
    pageNum: 1, // 当前页码
    pageSize: 10, // 每页显示的记录数
    id: '', // 工单ID/工单号
    productId: '',  // 产品ID
    productName: '', // 产品名称
    lineId: '', // 产线ID
    lineName: '',   // 产线名称
    quantity: '',   // 计划生产数量
    status: '', // 工单状态(0:待生产, 1:生产中, 2:生产完成, 3:已关闭)
    priority: '',   // 优先级(0:低, 1:中, 2:高, 3:紧急)
    finishTime: '', // 工单完成时间
    creatorId: '',  // 工单创建人ID
    creatorName: '', // 工单创建人名称
    remarks: '',    // 工单备注
    createTime: '', // 工单创建时间
    updateTime: ''  // 工单更新时间
})

// 表格数据
const tableData = ref([
    {
        id: '1',
        productId: '1',
        productName: '产品1',
        lineId: '1',
        lineName: '产线1',
        quantity: '100',
        status: '0',
        priority: '0',
        finishTime: '2021-09-01 00:00:00',
        creatorId: '1',
    }
])

// 分页相关
const pageNum = ref()
const pageSize = ref()
const total = ref()
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
</style>