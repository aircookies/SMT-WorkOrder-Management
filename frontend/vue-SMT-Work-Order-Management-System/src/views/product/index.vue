<template>
    <div class="container">
        <!-- 标题 -->
        <h1>产品管理</h1>
        <!-- 工具栏 -->
        <div class="toolbar">
            <div>
                <el-button type="primary" @click="showAddDialog" plain>添加产品</el-button>
                <el-button type="danger" @click="deleteProductConfirm()" plain>批量删除</el-button>
            </div>
            <el-form :inline="true" :model="productDTO" class="demo-form-inline" >
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
                    <el-button type="default" @click="clearQuery">清空</el-button>
                </el-form-item>
            </el-form>
        </div>
        <!-- 表格 -->
        <div class="table">
            <el-table v-loading="tableLoading" :data="tableData" row-key="id" style="width: 100%"
                @selection-change="handleSelectionChange" table-layout="fixed">
                <el-table-column type="selection" width="55" />
                <el-table-column property="code" label="产品编号" min-width="120" />
                <el-table-column property="name" label="产品名称" min-width="120" />
                <el-table-column property="spec" label="产品规格" min-width="150" show-overflow-tooltip />
                <el-table-column property="updateTime" label="更新时间" min-width="120" />
                <el-table-column label="操作">
                    <template #default="scope">
                        <el-button type="primary" :icon="Edit" size="small" @click="showEditDialog(scope.row.id)"
                            circle />
                        <el-button type="danger" :icon="Delete" size="small" @click="deleteProductConfirm(scope.row.id)"
                            circle />
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <!-- 分页 -->
        <el-pagination :current-page="productDTO.pageNum" :page-size="productDTO.pageSize"
            :page-sizes="[10, 25, 50, 100, 500]" layout="total, sizes, prev, pager, next, jumper"
            :total="productDTO.total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        <!-- 添加/编辑产品对话框 -->
        <el-dialog class="dialog" v-model="dialogVisible" :title="isEdit ? '编辑产品' : '添加产品'" width="500px" center>
            <el-form :model="currentProduct" :rules="productFormRules" ref="productFormRef" label-width="auto"
                label-position="right">
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
                    <el-upload class="avatar-uploader" action="#" :show-file-list="false"
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
                    <el-button type="primary" @click="submit">确定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import {
    ElButton, ElFormItem, ElInput, ElDatePicker, ElMessage, ElDialog, ElUpload,
    ElMessageBox, ElNotification, ElForm
} from 'element-plus';
import {
    getProductListApi, queryProductApi, addProductApi, getProductByIdApi, editProductApi,
    deleteProductApi, deleteProductByIdApi
} from '@/api/product';
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

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

// 控制按钮是否可用
// const disabledBtn = ref(false)

// 控制加载动画
const tableLoading = ref(false)

// 当前操作的产品
const currentProduct = ref({
    id: '',
    code: '',
    name: '',
    spec: '',
    image: ''  // 图片字段
})

// 错误通知
const errorNotify = (message) => {
    ElNotification({
        title: '错误',
        message: message,
        type: 'error'
    })
}

// 选择的行
const selectedRows = ref([])
const handleSelectionChange = (val) => {
    selectedRows.value = val.map(item => item.id)
}

// 上传头像相关配置
const uploadHeaders = ref({
    // 可以在这里添加认证token等头部信息
    // 'Authorization': 'Bearer ' + localStorage.getItem('token')
})

// 表单验证规则
const productFormRules = {
    code: [
        { required: true, message: '请输入产品编号', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
        {
            pattern: /^[A-Za-z0-9_-]+$/,
            message: '只能包含字母、数字、下划线和横线',
            trigger: 'blur'
        }
    ],
    name: [
        { required: true, message: '请输入产品名称', trigger: 'blur' },
    ],
    spec: [
        { required: true, message: '请输入产品规格', trigger: 'blur' },
    ]
}

// 清空表单
const clearForm = () => {
    if (!productFormRef.value) return
    productFormRef.value.resetFields()
}

// 分页大小改变
const handleSizeChange = (val) => {
    productDTO.value.pageSize = val
    queryProduct()
}

// 分页页码改变
const handleCurrentChange = (val) => {
    productDTO.value.pageNum = val
    queryProduct()
}

// 获取产品列表
const getProductList = async () => {
    tableLoading.value = true
    try {
        await getProductListApi(productDTO.value.pageNum, productDTO.value.pageSize).then(res => {
            if (res.code === 200) {
                tableData.value = res.data.list
                productDTO.value.pageNum = res.data.pageNum
                productDTO.value.pageSize = res.data.pageSize
                productDTO.value.total = res.data.total
            }
        })
    } catch (error) {
        errorNotify(error.message || "发生错误")
    } finally {
        tableLoading.value = false
    }
}

// 查询产品
const queryProduct = async () => {
    // 查询产品列表
    tableLoading.value = true
    try {
        const res = await queryProductApi(productDTO.value)
        // 判断响应结果是否正确
        if (res.code === 200) {
            tableData.value = res.data.list
            productDTO.value.pageNum = res.data.pageNum
            productDTO.value.pageSize = res.data.pageSize
            productDTO.value.total = res.data.total
        } else {
            ElMessage.error(res.message || '查询产品列表失败')
        }
        tableLoading.value = false
    } catch (error) {
        ElMessage.error('发生错误: ' + error.message)
    }
}

// 批量删除产品
const deleteProduct = async () => {
    if (selectedRows.value.length === 0) {
        ElMessage.warning('请选择要删除的产品')
        return
    }
    try {
        const res = await deleteProductApi(selectedRows.value)
        if (res.code === 200) {
            ElMessage.success('删除产品成功')
            queryProduct()
        } else {
            ElMessage.error(res.message || '删除产品失败')
        }
    } catch (error) {
        ElMessage.error('发生错误: ' + error.message)
    }
}

// 根据ID删除产品
const deleteProductById = async (id) => {
    try {
        const res = await deleteProductByIdApi(id)
        if (res.code === 200) {
            ElMessage.success('删除产品成功')
            queryProduct()
        } else {
            ElMessage.error(res.message || '删除产品失败')
        }
    } catch (error) {
        ElMessage.error('发生错误: ' + error.message)
    }
}

// 删除产品确认框
const deleteProductConfirm = (id) => {
    ElMessageBox.confirm('确定要删除所选产品吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        if (id !== undefined) {
            deleteProductById(id)
        } else {
            deleteProduct()
        }
    }).catch(() => {
        ElMessage.info('取消操作')
    })
}

// 显示添加产品对话框
const showAddDialog = () => {
    isEdit.value = false
    clearForm()
    // disabledBtn.value = false
    currentProduct.value = {
        id: '',
        code: '',
        name: '',
        spec: '',
        image: ''
    }
    dialogVisible.value = true
}

// 显示修改产品对话框并实现查询回显
const showEditDialog = async (id) => {
    isEdit.value = true
    clearForm()
    // disabledBtn.value = false
    currentProduct.value = {
        id: '',
        code: '',
        name: '',
        spec: '',
        image: ''
    }
    currentProduct.value.id = id;
    try {
        const res = await getProductByIdApi(id)
        if (res.code === 200) {
            currentProduct.value = res.data
        } else {
            ElMessage.error(res.message || '获取产品数据失败')
            return
        }
    } catch (error) {
        ElMessage.error('发生错误: ' + error.message)
    }
    dialogVisible.value = true
}

// 添加产品
const addProduct = async () => {
    try {
        const res = await addProductApi(currentProduct.value)
        if (res.code === 200) {
            ElMessage.success('添加产品成功')
            dialogVisible.value = false
            queryProduct()
        } else {
            ElMessage.error(res.message || '添加产品失败')
        }
    } catch (error) {
        ElMessage.error('发生错误: ' + error.message)
    }
}

// 修改产品
const editProduct = async () => {
    try {
        const res = await editProductApi(currentProduct.value)
        if (res.code === 200) {
            ElMessage.success('修改产品成功')
            dialogVisible.value = false
            queryProduct()
        } else {
            ElMessage.error(res.message || '修改产品失败')
        }
    } catch (error) {
        ElMessage.error('发生错误: ' + error.message)
    }
}

// 提交修改或添加操作
const submit = async () => {
    try {
        // 表单验证
        await productFormRef.value.validate()

        if (isEdit.value) {
            editProduct()
        } else {
            addProduct()
        }
    } catch (error) {
        ElMessage.error(error.message || '请完善表单信息')
    }
}

// 清空表单
const clearQuery = () => {
    productDTO.value = {
        pageNum: productDTO.value.pageNum,
        pageSize: productDTO.value.pageSize,
        total: productDTO.value.total,
        id: '',
        code: '',
        name: '',
        createTime: '',
    }
    queryProduct()
}

onMounted(async () => {
    // 获取产品列表
    getProductList()
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
