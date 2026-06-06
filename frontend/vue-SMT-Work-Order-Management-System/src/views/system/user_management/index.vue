<template>
    <div class="container">
        <h1>用户管理</h1>
        <!-- 工具栏 -->
        <div class="toolbar">
            <div class="buttons">
                <el-button type="primary" @click="handleAdd" plain>新建用户</el-button>
                <el-button type="danger" @click="handleDelete(selectedRows)" plain>批量删除</el-button>
            </div>
            <!-- 搜索栏 -->
            <div class="search">
                <el-form :inline="true" :model="queryFormModel" class="search-form">
                    <el-form-item label="姓名:">
                        <el-input v-model="queryFormModel.name" placeholder="请输入姓名" />
                    </el-form-item>
                    <el-form-item label="性别:">
                        <el-select v-model="queryFormModel.gender" placeholder="未选择" style="min-width: 100px;">
                            <el-option label="未选择" value="" />
                            <el-option label="男" value="1" />
                            <el-option label="女" value="0" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="角色:">
                        <el-select v-model="queryFormModel.roleId" placeholder="未选择" style="min-width: 100px;" >
                            <el-option label="未选择" value="" />
                            <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="部门:">
                        <el-select v-model="queryFormModel.departmentId" placeholder="未选择" style="min-width: 100px;">
                            <el-option label="未选择" value="" />
                            <el-option v-for="item in departmentList" :key="item.id" :label="item.name" :value="item.id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="queryUser">查询</el-button>
                        <el-button @click="clearQueryForm">清空</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <!-- 表格 -->
        <div class="table">
            <el-table v-loading="loading" :data="tableData" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" />
                <el-table-column property="id" label="ID" width="80" />
                <el-table-column property="username" label="用户名" />
                <el-table-column property="name" label="姓名" />
                <el-table-column property="gender" label="性别">
                    <template #default="scope">
                        <span v-if="scope.row.gender === 1">男</span>
                        <span v-else-if="scope.row.gender === 0">女</span>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column property="roleName" label="角色" show-overflow-tooltip />
                <el-table-column property="departmentName" label="部门" show-overflow-tooltip />
                <el-table-column property="status" label="状态">
                    <template #default="scope">
                        <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
                        <el-tag v-else-if="scope.row.status === 0" type="info">禁用</el-tag>
                    </template>
                </el-table-column>
                <el-table-column property="createTime" label="创建时间" />
                <!-- 操作按钮 -->
                <el-table-column label="操作">
                    <template #default="scope">
                        <el-button size="small" type="primary" :icon="Edit" @click="handleEdit(scope.row.id)" circle />
                        <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(scope.row.id)"
                            circle />
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <!-- 分页 -->
        <el-pagination class="pagination" :current-page="pageNum" :page-size="pageSize"
            :page-sizes="[10, 25, 50, 100, 500]" layout="total, sizes, prev, pager, next, jumper" :total="total"
            @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        <!-- dialog对话框 -->
        <el-dialog v-if="dialogFormVisible" v-model="dialogFormVisible" :title="isEdit ? '编辑用户' : '新建用户'" width="600" center>
            <el-form :model="userDTO" :rules="formRules" ref="formRef" label-width="auto" label-position="right">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="userDTO.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="userDTO.password" type="password" placeholder="请输入密码" show-password />
                </el-form-item>
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="userDTO.name" placeholder="请输入姓名" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                    <el-select v-model="userDTO.gender" placeholder="请选择性别" style="width: 100%;">
                        <el-option label="男" :value="1" />
                        <el-option label="女" :value="0" />
                    </el-select>
                </el-form-item>
                <el-form-item label="角色" prop="roleId">
                    <el-select v-model="userDTO.roleId" placeholder="请选择角色" style="width: 100%;" filterable>
                        <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="部门" prop="departmentId">
                    <el-select v-model="userDTO.departmentId" placeholder="请选择部门" style="width: 100%;" filterable>
                        <el-option v-for="item in departmentList" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-select v-model="userDTO.status" placeholder="请选择状态" style="width: 100%;">
                        <el-option label="启用" :value="1" />
                        <el-option label="禁用" :value="0" />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="handleCancel">取消</el-button>
                    <el-button type="primary" @click="handleSubmit">
                        提交
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus';
import { queryUserApi, getUserByIdApi, addUserApi, updateUserApi, deleteUserApi } from '@/api/userManagement';
import { queryDepartmentApi } from '@/api/departmentManagement';
import { getSystemRoleListApi } from '@/api/role';
import { Edit, Delete } from '@element-plus/icons-vue';
import { isEmpty } from 'element-plus/es/utils/types.mjs';

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
        { required: true, message: '请输入密码', trigger: 'blur' },
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
        
        try {
            let res
            if (!isEdit.value) {
                // 新建用户
                res = await addUserApi(userDTO.value)
                if (res.code === 200) {
                    ElMessage.success('用户创建成功')
                    dialogFormVisible.value = false
                    resetForm()
                    queryUser()
                } else {
                    ElMessage.error(res.message || '用户创建失败')
                }
            } else {
                // 编辑用户
                res = await updateUserApi(userDTO.value)
                if (res.code === 200) {
                    ElMessage.success('用户修改成功')
                    dialogFormVisible.value = false
                    resetForm()
                    queryUser()
                } else {
                    ElMessage.error(res.message || '用户修改失败')
                }
            }
        } catch (error) {
            ElMessage.error(error.message || '操作失败')
        }
    })
}

/**
 * 编辑用户
 * @param {Number} id - 用户ID
 */
const handleEdit = async (id) => {
    isEdit.value = true
    resetForm()
    
    try {
        const res = await getUserByIdApi(id)
        if (res.code === 200) {
            userDTO.value = res.data
            dialogFormVisible.value = true
        } else {
            ElMessage.error(res.message || '获取用户数据失败')
        }
    } catch (error) {
        ElMessage.error(error.message || '获取用户数据失败')
    }
}

/**
 * 查询用户列表
 */
const queryUser = async () => {
    loading.value = true
    try {
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
        } else {
            ElMessage.error(res.message || '查询用户失败')
        }
    } catch (error) {
        ElMessage.error(error.message || '查询用户失败')
    } finally {
        loading.value = false
    }
}

/** 
 * 查询角色列表
*/
const queryRole = async () => {
    try {
        const res = await getSystemRoleListApi()
        if (res.code === 200) {
            roleList.value = res.data
        } else {
            ElMessage.error(res.message || '查询角色列表失败')
        }
    } catch (error) {
        ElMessage.error(error.message || '查询角色列表失败')
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
        try {
            const ids = toArray(row)
            // const promises = ids.map(id => deleteUserApi(id))
            // const results = await Promise.all(promises)
            const results = await deleteUserApi(ids)
            
            const successCount = results.data
            if (successCount > 0) {
                ElMessage.success(`成功删除 ${successCount} 个用户`)
                queryUser()
            }
        } catch (error) {
            ElMessage.error(error.message || '删除用户失败')
        }
    }).catch(() => {
        ElMessage.info('取消操作')
    })
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
    try {
        // 获取部门列表
        const deptRes = await queryDepartmentApi()
        if (deptRes.code === 200) {
            departmentList.value = deptRes.data
        }
        
        // 获取用户列表
        await queryUser()

        // 获取角色列表
        await queryRole()
    } catch (error) {
        ElMessage.error(error.message || '初始化数据失败')
    } finally {
        loading.value = false
    }
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

.search {
    display: flex;
    flex-direction: row;
}

.search-form {
    width: 100%;
}

.pagination {
    padding: 20px 0;
}

:deep(.el-dialog) {
    .el-form {
        display: grid;
        grid-template-columns: auto;
        gap: 20px 0;
        padding: 0 20px;
    }
}
</style>
