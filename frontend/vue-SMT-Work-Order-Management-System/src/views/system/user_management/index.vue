<template>
    <div class="container">
        <!-- 页面标题 -->
        <div class="page-header">
            <h1 class="page-title">
                <el-icon class="title-icon">
                    <User />
                </el-icon>
                用户管理
            </h1>
            <p class="page-subtitle">管理系统用户账号和权限信息</p>
        </div>

        <!-- 工具栏 -->
        <el-card class="toolbar-card" shadow="hover">
            <div class="toolbar">
                <div class="toolbar-actions">
                    <el-button type="primary" @click="handleAdd" :icon="Plus" plain>新建用户</el-button>
                    <el-button type="danger" @click="handleDelete(selectedRows)" :icon="Delete" plain>批量删除</el-button>
                </div>
                <!-- 搜索栏 -->
                <el-form :inline="true" :model="queryFormModel" class="search-form">
                    <el-form-item label="姓名">
                        <el-input v-model="queryFormModel.name" placeholder="请输入姓名" clearable prefix-icon="Search"
                            style="width: 160px" />
                    </el-form-item>
                    <el-form-item label="性别">
                        <el-select v-model="queryFormModel.gender" placeholder="请选择" clearable style="width: 120px">
                            <el-option label="男" value="1" />
                            <el-option label="女" value="0" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="角色">
                        <el-select v-model="queryFormModel.roleId" placeholder="请选择" clearable filterable
                            style="width: 140px">
                            <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="部门">
                        <el-select v-model="queryFormModel.departmentId" placeholder="请选择" clearable filterable
                            style="width: 140px">
                            <el-option v-for="item in departmentList" :key="item.id" :label="item.name"
                                :value="item.id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="queryUser" :icon="Search">查询</el-button>
                        <el-button @click="clearQueryForm" :icon="Refresh">重置</el-button>
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
                    <el-table-column property="id" label="ID" min-width="100" align="center">
                        <template #default="{ row }">
                            <el-tag type="info" effect="plain">{{ row.id }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column property="username" label="用户名" min-width="140" align="center">
                        <template #default="{ row }">
                            <el-icon>
                                <User />
                            </el-icon>
                            {{ row.username }}
                        </template>
                    </el-table-column>
                    <el-table-column property="name" label="姓名" min-width="120" align="center" />
                    <el-table-column property="gender" label="性别" min-width="100" align="center">
                        <template #default="scope">
                            <el-tag v-if="scope.row.gender === 1" type="primary" effect="dark">男</el-tag>
                            <el-tag v-else-if="scope.row.gender === 0" type="danger" effect="dark">女</el-tag>
                            <span v-else>-</span>
                        </template>
                    </el-table-column>
                    <el-table-column property="roleId" label="角色" min-width="140" show-overflow-tooltip align="center">
                        <template #default="scope">
                            <el-icon>
                                <Avatar />
                            </el-icon>
                            {{ getRoleName(scope.row.roleId) }}
                        </template>
                    </el-table-column>
                    <el-table-column property="departmentName" label="部门" min-width="140" show-overflow-tooltip
                        align="center">
                        <template #default="scope">
                            <el-icon>
                                <OfficeBuilding />
                            </el-icon>
                            {{ getDepartmentName(scope.row.departmentId) }}
                        </template>
                    </el-table-column>
                    <el-table-column property="status" label="状态" min-width="100" align="center">
                        <template #default="scope">
                            <el-tag v-if="scope.row.status === 1" type="success" effect="dark">启用</el-tag>
                            <el-tag v-else-if="scope.row.status === 0" type="info" effect="dark">禁用</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column property="createTime" label="创建时间" min-width="180" align="center">
                        <template #default="{ row }">
                            <el-icon>
                                <Clock />
                            </el-icon>
                            {{ row.createTime }}
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="150" align="center" fixed="right">
                        <template #default="scope">
                            <el-button size="small" type="primary" :icon="Edit" @click="handleEdit(scope.row.id)"
                                link>编辑</el-button>
                            <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(scope.row.id)"
                                link>删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>

            <!-- 分页 -->
            <div class="pagination-wrapper">
                <el-pagination :current-page="pageNum" :page-size="pageSize" :page-sizes="[10, 25, 50, 100, 500]"
                    layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
                    @current-change="handleCurrentChange" background />
            </div>
        </el-card>

        <!-- 添加/编辑用户对话框 -->
        <el-dialog v-if="dialogFormVisible" v-model="dialogFormVisible" :title="isEdit ? '编辑用户' : '新建用户'" width="650px"
            center destroy-on-close class="user-dialog">
            <el-form :model="userDTO" :rules="formRules" ref="formRef" label-width="100px" label-position="right"
                class="user-form">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="userDTO.username" placeholder="请输入用户名" prefix-icon="User" clearable />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="userDTO.password" type="password" placeholder="请输入密码" prefix-icon="Lock"
                        show-password clearable />
                </el-form-item>
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="userDTO.name" placeholder="请输入姓名" prefix-icon="UserFilled" clearable />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                    <el-select v-model="userDTO.gender" placeholder="请选择性别" style="width: 100%;">
                        <el-option label="男" :value="1">
                            <el-tag type="primary" effect="dark">男</el-tag>
                        </el-option>
                        <el-option label="女" :value="0">
                            <el-tag type="danger" effect="dark">女</el-tag>
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="角色" prop="roleId">
                    <el-select v-model="userDTO.roleId" placeholder="请选择角色" style="width: 100%;" filterable>
                        <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.id">
                            <el-icon>
                                <Avatar />
                            </el-icon>
                            {{ item.name }}
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="部门" prop="departmentId">
                    <el-select v-model="userDTO.departmentId" placeholder="请选择部门" style="width: 100%;" filterable>
                        <el-option v-for="item in departmentList" :key="item.id" :label="item.name" :value="item.id">
                            <el-icon>
                                <OfficeBuilding />
                            </el-icon>
                            {{ item.name }}
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="userDTO.status" placeholder="请选择状态" style="width: 100%;">
                        <el-option label="启用" :value="1">
                            <el-tag type="success" effect="dark">启用</el-tag>
                        </el-option>
                        <el-option label="禁用" :value="0">
                            <el-tag type="info" effect="dark">禁用</el-tag>
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="handleCancel" :icon="Close">取消</el-button>
                    <el-button type="primary" @click="handleSubmit" :icon="Check">提交</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox, ElTag} from 'element-plus';
import {addUserApi, deleteUserApi, getUserByIdApi, queryUserApi, updateUserApi} from '@/api/userManagement';
import {queryDepartmentApi} from '@/api/departmentManagement';
import {getSystemRoleListApi} from '@/api/role';
import {
  Avatar,
  Check,
  Clock,
  Close,
  Delete,
  Edit,
  OfficeBuilding,
  Plus,
  Refresh,
  Search,
  User
} from '@element-plus/icons-vue';
import {isEmpty} from 'element-plus/es/utils/types.mjs';

defineOptions({
    name: 'UserManagement'
})

// 角色列表
const roleList = ref([])

// 部门列表
const departmentList = ref([])

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

// 用户数据模型
const userDTO = ref({
    id: '',
    username: '',
    password: '',
    name: '',
    gender: '',
    roleId: '',
    departmentId: '',
    status: 1
})

// 查询表单数据模型
const queryFormModel = ref({
    pageNum: 1,
    pageSize: 10,
    name: '',
    gender: '',
    roleId: '',
    departmentId: ''
})

// 表单验证规则
const formRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    password: [
        { required: false, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    name: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
    ],
    gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
    ],
    roleId: [
        { required: true, message: '请选择角色', trigger: 'change' }
    ],
    departmentId: [
        { required: true, message: '请选择部门', trigger: 'change' }
    ],
    status: [
        { required: true, message: '请选择状态', trigger: 'change' }
    ]
}

// 表单引用
const formRef = ref(null)

/**
 * 清空查询表单
 */
const clearQueryForm = () => {
    queryFormModel.value = {
        pageNum: 1,
        pageSize: 10,
        name: '',
        gender: '',
        roleId: '',
        departmentId: ''
    }
}

/**
 * 重置表单
 */
const resetForm = () => {
    userDTO.value = {
        id: '',
        username: '',
        password: '',
        name: '',
        gender: '',
        roleId: '',
        departmentId: '',
        status: 1
    }
    if (formRef.value) {
        formRef.value.clearValidate()
    }
}

/**
 * 处理选中的行
 * @param {Array} val - 选中的行数据
 */
const handleSelectionChange = (val) => {
    selectedRows.value = val.map(item => item.id)
}

/**
 * 通用转换函数，将单个值或数组统一转换为数组
 * @param {*} item - 需要转换的值
 * @returns {Array} 转换后的数组
 */
const toArray = (item) => {
    if (Array.isArray(item)) {
        return item;
    }
    return [item];
};

/**
 * 新建用户
 */
const handleAdd = () => {
    isEdit.value = false
    resetForm()
    dialogFormVisible.value = true
}

/**
 * 取消操作
 */
const handleCancel = () => {
    dialogFormVisible.value = false
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
    if (!formRef.value) return

    await formRef.value.validate(async (valid) => {
        if (!valid) {
            ElMessage.error('请完善表单信息')
            return
        }

        let res
        if (!isEdit.value) {
            // 新建用户
            res = await addUserApi(userDTO.value)
            if (res.code === 200) {
                ElMessage.success('用户创建成功')
                dialogFormVisible.value = false
                resetForm()
                queryUser()
            }
        } else {
            // 修改用户
            res = await updateUserApi(userDTO.value)
            if (res.code === 200) {
                ElMessage.success('用户修改成功')
                dialogFormVisible.value = false
                resetForm()
                queryUser()
            }
        }
    })
}

/**
 * 根据id查询用户
 * @param {Number} id - 用户ID
 */
const handleEdit = async (id) => {
    isEdit.value = true
    resetForm()

    const res = await getUserByIdApi(id)
    if (res.code === 200) {
        userDTO.value = res.data
        dialogFormVisible.value = true
    }
}

/**
 * 查询用户列表
 */
const queryUser = async () => {
    loading.value = true
    const params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        ...queryFormModel.value
    }
    const res = await queryUserApi(params)
    if (res.code === 200) {
        tableData.value = res.data.list
        pageNum.value = res.data.pageNum
        pageSize.value = res.data.pageSize
        total.value = res.data.total
    }
    loading.value = false
}

/** 
 * 查询角色列表
*/
const queryRole = async () => {
    const res = await getSystemRoleListApi()
    if (res.code === 200) {
        roleList.value = res.data
    }
}

/**
 * 查询部门列表
 */
const queryDepartment = async () => {
    const deptRes = await queryDepartmentApi({})
    if (deptRes.code === 200) {
        departmentList.value = deptRes.data
    }
}

/**
 * 删除用户
 * @param {Number|Array} row - 用户ID或ID数组
 */
const handleDelete = async (row) => {
    if (isEmpty(row)) {
        ElMessage.error('请选择要删除的用户')
        return
    }

    ElMessageBox.confirm('确定要删除选中的用户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        const ids = toArray(row)
        const results = await deleteUserApi(ids)

        const successCount = results.data
        if (successCount > 0) {
            ElMessage.success(`成功删除 ${successCount} 个用户`)
            queryUser()
        }
    })
}

/**
 * 根据角色id获取角色名
 */
const getRoleName = (roleId) => {
    const roleName = roleList.value.find(item => item.id === roleId)?.name
    return isEmpty(roleName) ? '未知角色' : roleName
}

/**
 * 根据部门id获取部门名
 */
const getDepartmentName = (departmentId) => {
    const departmentName = departmentList.value.find(item => item.id === departmentId)?.name
    return isEmpty(departmentName) ? '未知部门' : departmentName
}

/**
 * 分页大小改变
 * @param {Number} val - 新的分页大小
 */
const handleSizeChange = (val) => {
    pageSize.value = val
    queryUser()
}

/**
 * 分页页码改变
 * @param {Number} val - 新的页码
 */
const handleCurrentChange = (val) => {
    pageNum.value = val
    queryUser()
}

/**
 * 组件挂载时初始化数据
 */
onMounted(async () => {
    loading.value = true
    // 发送异步请求
    Promise.allSettled([
        queryUser(),
        queryRole(),
        queryDepartment()
    ])

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

    background: linear-gradient(315deg, #856cff 0.000%, #856cff 10.000%, #8568ff calc(10.000% + 1px), #8568ff 20.000%, #846eff calc(20.000% + 1px), #846eff 30.000%, #847aff calc(30.000% + 1px), #847aff 40.000%, #8487ff calc(40.000% + 1px), #8487ff 50.000%, #838fff calc(50.000% + 1px), #838fff 60.000%, #8390ff calc(60.000% + 1px), #8390ff 70.000%, #8387ff calc(70.000% + 1px), #8387ff 80.000%, #827aff calc(80.000% + 1px), #827aff 90.000%, #826eff calc(90.000% + 1px) 100.000%);
    border-radius: 4px;
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
    background: #3F51B5;
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
.user-dialog :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px 24px;
    margin: 0;
}

.user-dialog :deep(.el-dialog__title) {
    color: #ffffff;
    font-size: 18px;
    font-weight: 600;
}

.user-dialog :deep(.el-dialog__close) {
    color: #ffffff;
}

.user-dialog :deep(.el-dialog__close:hover) {
    color: rgba(255, 255, 255, 0.8);
}

.user-dialog :deep(.el-dialog__body) {
    padding: 32px 24px;
}

.user-form {
    padding: 0 12px;
}

.user-form :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
}

.user-form :deep(.el-input__wrapper),
.user-form :deep(.el-select__wrapper) {
    border-radius: 4px;
    transition: all 0.3s ease;
}

.user-form :deep(.el-input__wrapper:hover),
.user-form :deep(.el-select__wrapper:hover) {
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
