<template>
    <div class="container">
        <!-- 标题 -->
        <h1>产品管理</h1>
        <!-- 工具栏 -->
        <div class="toolbar">
            <div>
                <el-button type="primary" @click="showAddDialog" plain>添加产品</el-button>
                <el-button type="danger" plain>批量删除</el-button>
            </div>
            <el-form :inline="true" :model="productDTO" class="demo-form-inline">
                <el-form-item label="产品名称:">
                    <el-input v-model="productDTO.name" placeholder="请输入" clearable />
                </el-form-item>
                <el-form-item label="产品编号:">
                    <el-input v-model="productDTO.code" placeholder="请输入" clearable />
                </el-form-item>
                <el-form-item label="创建日期:">
                    <!-- 设置日期格式为 YYYY-MM-DD -->
                    <el-date-picker v-model="productDTO.createTime" type="date" placeholder="请选择日期" format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" clearable />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="queryProduct">查询</el-button>
                    <el-button type="default" @click="clear">清空</el-button>
                </el-form-item>
            </el-form>
        </div>
        <!-- 表格 -->
        <div class="table">
            <el-table ref="multipleTableRef" :data="tableData" row-key="id" style="width: 100%">
                <el-table-column type="selection" width="55" />
                <el-table-column property="code" label="产品编号" min-width="120" />
                <el-table-column property="name" label="产品名称" min-width="120" />
                <el-table-column property="spec" label="产品规格" min-width="150" show-overflow-tooltip />
                <el-table-column property="updateTime" label="更新时间" min-width="120" />
                <el-table-column label="操作" min-width="150">
                    <template #default="scope">
                        <el-button type="primary" size="small" plain>修改</el-button>
                        <el-button type="danger" size="small" plain>删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <!-- 分页 -->
        <el-pagination v-model:current-page="productDTO.pageNum" v-model:page-size="productDTO.pageSize"
            :page-sizes="[10, 15, 25, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
            :total="productDTO.total" @size-change="queryProduct" @current-change="queryProduct" />
        <!-- 添加/编辑产品对话框 -->
        <el-dialog class="dialog" v-model="dialogVisible" :title="isEdit ? '编辑产品' : '添加产品'" width="500px" center="true">
            <el-form :model="currentProduct" :rules="productFormRules" ref="productFormRef" label-width="100px">
                <el-form-item label="产品编号" prop="code">
                    <el-input v-model="currentProduct.code" placeholder="请输入产品编号" />
                </el-form-item>
                <el-form-item label="产品名称" prop="name">
                    <el-input v-model="currentProduct.name" placeholder="请输入产品名称" />
                </el-form-item>
                <el-form-item label="产品规格" prop="spec">
                    <el-input type="textarea" v-model="currentProduct.spec" placeholder="请输入产品规格" />
                </el-form-item>
                <el-form-item label="产品图片">
                    <el-upload class="avatar-uploader"
                        action="#" :show-file-list="false"
                        :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                        <img v-if="imageUrl" :src="imageUrl" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="default" @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary">确定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElButton, ElFormItem, ElInput, ElDatePicker, ElMessage, ElDialog, ElUpload } from 'element-plus';
import { getProductListApi, queryProductApi } from '@/api/product';
import { Plus } from '@element-plus/icons-vue'

// 产品数据传输对象
const productDTO = ref({
    pageNum: 1,
    pageSize: 10,
    total: 0,
    id: '',
    code: '',
    name: '',
    createTime: '',
})

// 用于接收后端返回的列表数据
const tableData = ref([])

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const productFormRef = ref()

// 当前操作的产品
const currentProduct = ref({
    id: '',
    code: '',
    name: '',
    spec: '',
    image: ''  // 图片字段
})

// 上传头像相关配置
const uploadHeaders = ref({
    // 可以在这里添加认证token等头部信息
    // 'Authorization': 'Bearer ' + localStorage.getItem('token')
})

// 表单验证规则
const productFormRules = {
    code: [
        { required: true, message: '请输入产品编号', trigger: 'blur' },
    ],
    name: [
        { required: true, message: '请输入产品名称', trigger: 'blur' },
    ]
}

// 清空表单
const clearForm = () => {
    if (!productFormRef.value) return
    productFormRef.value.resetFields()
}

// 显示添加产品对话框
const showAddDialog = () => {
    isEdit.value = false
    clearForm()
    currentProduct.value = {
        id: '',
        code: '',
        name: '',
        spec: '',
        image: ''
    }
    dialogVisible.value = true
}

// 查询产品
const queryProduct = async () => {
    // 查询产品列表
    const res = await queryProductApi(productDTO.value)
    // 判断响应结果是否正确
    if (res.code === 200) {
        tableData.value = res.data.list
        if (res.data.total > 0) {
            productDTO.value.total = res.data.total
        }
    } else {
        ElMessage.error(res.message || '查询产品列表失败')
    }
}

// 清空表单
const clear = () => {
    productDTO.value = {
        pageNum: 1,
        pageSize: 10,
        total: 0,
        id: '',
        code: '',
        name: '',
        createTime: '',
    }
}

onMounted(async () => {
    // 获取产品列表
    const res = await getProductListApi(productDTO.value.pageNum, productDTO.value.pageSize)
    console.log(res)
    // 判断响应结果是否正确
    if (res.code === 200) {
        tableData.value = res.data.list
        if (res.data.total > 0) {
            productDTO.value.total = res.data.total
        }
    } else {
        ElMessage.error(res.message || '查询产品列表失败')
    }
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
    display: grid;
    grid-template-columns: 20% auto;
    padding: 20px 0;
}

.table {
    margin: 20px 0;
}

.dialog-footer {
    text-align: center;
}

.avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
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

<style>
.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
}
</style>
