<template>
    <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
            <h1 class="page-title">
                <el-icon class="title-icon">
                    <Box />
                </el-icon>
                产品管理
            </h1>
            <p class="page-subtitle">管理和维护产品信息</p>
        </div>

        <!-- 工具栏 -->
        <el-card class="toolbar-card" shadow="hover">
            <div class="toolbar">
                <div class="toolbar-actions">
                    <el-button type="primary" @click="showAddDialog" plain>
                        <el-icon class="icon">
                            <Plus />
                        </el-icon>
                        添加产品</el-button>
                    <el-button type="danger" @click="deleteProductConfirm()" plain>
                        <el-icon class="icon">
                            <Delete />
                        </el-icon>
                        批量删除</el-button>
                </div>
                <el-form :inline="true" :model="productDTO" class="query-form">
                    <el-form-item label="产品名称">
                        <el-input v-model="productDTO.name" placeholder="请输入产品名称" clearable prefix-icon="Search"
                            style="width: 180px" />
                    </el-form-item>
                    <el-form-item label="产品编号">
                        <el-input v-model="productDTO.code" placeholder="请输入产品编号" clearable prefix-icon="Document"
                            style="width: 180px" />
                    </el-form-item>
                    <el-form-item label="创建日期">
                        <el-date-picker v-model="productDTO.createTime" type="date" placeholder="请选择日期"
                            format="YYYY-MM-DD" value-format="YYYY-MM-DD" clearable style="width: 180px" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="queryProduct" :loading="loadingBtn" plain>
                            <el-icon class="icon">
                                <Search />
                            </el-icon>
                            查询</el-button>
                        <el-button @click="clearQuery" plain>
                            <el-icon class="icon">
                                <Refresh />
                            </el-icon>
                            重置</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </el-card>

        <!-- 数据表格 -->
        <el-card class="table-card" shadow="hover">
            <div class="table-wrapper">
                <el-table v-loading="tableLoading" :data="tableData" row-key="id" style="width: 100%"
                    @selection-change="handleSelectionChange" stripe border class="custom-table">
                    <el-table-column type="selection" width="55" align="center" />
                    <el-table-column property="code" label="产品编号" min-width="150" align="center">
                        <template #default="{ row }">
                            <el-tag type="info" effect="plain">{{ row.code }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column property="name" label="产品名称" min-width="150" align="center" />
                    <el-table-column property="spec" label="产品规格" min-width="200" show-overflow-tooltip
                        align="center" />
                    <el-table-column property="updateTime" label="更新时间" min-width="180" align="center">
                        <template #default="{ row }">
                            <el-icon>
                                <Clock />
                            </el-icon>
                            {{ row.updateTime }}
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="150" align="center" fixed="right">
                        <template #default="scope">
                            <el-button type="primary" size="small" @click="showEditDialog(scope.row.id)" link><el-icon
                                    class="icon">
                                    <Edit />
                                </el-icon>编辑</el-button>
                            <el-button type="danger" size="small" @click="deleteProductConfirm(scope.row.id)"
                                link><el-icon class="icon">
                                    <Delete />
                                </el-icon>删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页 -->
            <div class="pagination-wrapper">
                <el-pagination :current-page="productDTO.pageNum" :page-size="productDTO.pageSize"
                    :page-sizes="[10, 25, 50, 100, 500]" layout="total, sizes, prev, pager, next, jumper"
                    :total="productDTO.total" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    background />
            </div>
        </el-card>

        <!-- 添加/编辑产品对话框 -->
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑产品' : '添加产品'" width="600px" center destroy-on-close
            class="product-dialog">
            <el-form :model="currentProduct" :rules="productFormRules" ref="productFormRef" label-width="100px"
                label-position="right" class="product-form">
                <el-form-item label="产品编号" prop="code">
                    <el-input v-model="currentProduct.code" placeholder="请输入产品编号" prefix-icon="Document" clearable />
                </el-form-item>
                <el-form-item label="产品名称" prop="name">
                    <el-input v-model="currentProduct.name" placeholder="请输入产品名称" prefix-icon="Goods" clearable />
                </el-form-item>
                <el-form-item label="产品规格" prop="spec">
                    <el-input type="textarea" v-model="currentProduct.spec" placeholder="请输入产品规格" :rows="4"
                        maxlength="500" show-word-limit />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="dialogVisible = false" plain><el-icon class="icon">
                            <Close />
                        </el-icon>取消</el-button>
                    <el-button type="primary" @click="submit" :loading="loadingBtn" plain><el-icon class="icon">
                            <Check />
                        </el-icon>确定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { onBeforeMount, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import {
    addProductApi,
    deleteProductApi,
    deleteProductByIdApi,
    editProductApi,
    getProductByIdApi,
    getProductListApi,
    queryProductApi
} from '@/api/product';

defineOptions({
    name: 'ProductManagement'
})

// ==================== 数据模型 ====================

// 产品数据传输对象（查询条件 + 分页）
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

// 控制加载动画
const tableLoading = ref(false)

// 控制按钮加载状态
const loadingBtn = ref(false)

// 当前操作的产品
const currentProduct = ref({
    id: '',
    code: '',
    name: '',
    spec: '',
    image: ''  // 图片字段
})

// 选择的行（存储选中行的 ID 数组）
const selectedRows = ref([])
const handleSelectionChange = (val) => {
    selectedRows.value = val.map(item => item.id)
}

// ==================== 表单验证规则 ====================
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

/**
 * 分页大小改变时的回调
 * @param {number} val - 新的每页条数
 */
const handleSizeChange = (val) => {
    productDTO.value.pageSize = val
    queryProduct()
}

/**
 * 当前页码改变时的回调
 * @param {number} val - 新的页码
 */
const handleCurrentChange = (val) => {
    productDTO.value.pageNum = val
    queryProduct()
}

// ==================== CRUD 操作 ====================

/**
 * 获取产品分页列表
 */
const getProductList = async () => {
    tableLoading.value = true
    await getProductListApi(productDTO.value.pageNum, productDTO.value.pageSize).then(res => {
        if (res.code === 200) {
            tableData.value = res.data.list
            productDTO.value.pageNum = res.data.pageNum
            productDTO.value.pageSize = res.data.pageSize
            productDTO.value.total = res.data.total
        }
    }).finally(() => {
        tableLoading.value = false
    })
}

/**
 * 条件查询产品
 */
const queryProduct = async () => {
    // 查询产品列表
    tableLoading.value = true
    loadingBtn.value = true

    await queryProductApi(productDTO.value).then(res => {
        // 判断响应结果是否正确
        if (res.code === 200) {
            tableData.value = res.data.list
            productDTO.value.pageNum = res.data.pageNum
            productDTO.value.pageSize = res.data.pageSize
            productDTO.value.total = res.data.total
        }
    }).finally(() => {
        tableLoading.value = false
        loadingBtn.value = false
    })

}

/**
 * 批量删除产品
 */
const deleteProduct = async () => {
    if (selectedRows.value.length === 0) {
        ElMessage.warning('请选择要删除的产品')
        return
    }
    const res = await deleteProductApi(selectedRows.value)
    if (res.code === 200) {
        ElMessage.success('删除产品成功')
        queryProduct()
    }
}

/**
 * 根据 ID 删除单个产品
 * @param {number} id - 产品 ID
 */
const deleteProductById = async (id) => {
    const res = await deleteProductByIdApi(id)
    if (res.code === 200) {
        ElMessage.success('删除产品成功')
        queryProduct()
    }
}

/**
 * 删除确认弹窗
 * @param {number} [id] - 可选的产品 ID，不传则执行批量删除
 */
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
    })
}

/**
 * 显示添加产品对话框
 */
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

/**
 * 显示编辑产品对话框，并回显产品数据
 * @param {number} id - 产品 ID
 */
const showEditDialog = async (id) => {
    isEdit.value = true
    clearForm()
    currentProduct.value = {
        id: '',
        code: '',
        name: '',
        spec: '',
        image: ''
    }
    currentProduct.value.id = id;
    const res = await getProductByIdApi(id)
    if (res.code === 200) {
        currentProduct.value = res.data
    }
    dialogVisible.value = true
}

/**
 * 新增产品
 */
const addProduct = async () => {
    loadingBtn.value = true
    await addProductApi(currentProduct.value).then(res => {
        if (res.code === 200) {
            ElMessage.success('添加产品成功')
            dialogVisible.value = false
            queryProduct()
        }
    }).finally(() => {
        loadingBtn.value = false
    })

}

/**
 * 修改产品
 */
const editProduct = async () => {
    loadingBtn.value = true
    await editProductApi(currentProduct.value).then(res => {
        if (res.code === 200) {
            ElMessage.success('修改产品成功')
            dialogVisible.value = false
        }
    }).finally(() => {
        loadingBtn.value = false
    })


    queryProduct()
}

/**
 * 提交表单（根据 isEdit 状态判断新增或修改）
 */
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

/**
 * 重置查询条件并重新查询
 */
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

/**
 * 组件挂载时获取产品列表
 */
onBeforeMount(async () => {
    await getProductList()
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
    border-radius: 4px;
    background: linear-gradient(60deg, #4c9cdd 0%, #c2e59c 100%);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
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

.query-form {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
}

.query-form :deep(.el-form-item) {
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
    background: #00BCD4;
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
.product-dialog :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px 24px;
    margin: 0;
}

.product-dialog :deep(.el-dialog__title) {
    color: #ffffff;
    font-size: 18px;
    font-weight: 600;
}

.product-dialog :deep(.el-dialog__close) {
    color: #ffffff;
}

.product-dialog :deep(.el-dialog__close:hover) {
    color: rgba(255, 255, 255, 0.8);
}

.product-dialog :deep(.el-dialog__body) {
    padding: 32px 24px;
}

.product-form {
    padding: 0 12px;
}

.product-form :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
}

.product-form :deep(.el-input__wrapper),
.product-form :deep(.el-textarea__inner) {
    border-radius: 4px;
    transition: all 0.3s ease;
}

.product-form :deep(.el-input__wrapper:hover),
.product-form :deep(.el-textarea__inner:hover) {
    box-shadow: 0 0 0 1px #667eea inset;
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

    .query-form {
        flex-direction: column;
        align-items: stretch;
    }

    .query-form :deep(.el-form-item) {
        width: 100%;
    }

    .query-form :deep(.el-input) {
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