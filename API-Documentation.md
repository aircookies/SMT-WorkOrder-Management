# SMT 工单管理系统 - 接口文档

## 一、项目概述

SMT 工单管理系统（smt-workorder-management）是一个基于 Spring Boot 的后端服务，用于管理 SMT（表面贴装技术）生产工厂的工单全生命周期。系统涵盖部门管理、产线管理、产品管理、用户管理、角色管理、工单管理及工序报工等核心功能。

- **基础路径**：`http://localhost:8080`
- **认证方式**：JWT 令牌（通过 HttpOnly Cookie 传递，Cookie 名 `JWT_TOKEN`）
- **数据格式**：所有接口统一使用 `application/json` 格式，返回统一响应体 `Result`

---

## 二、通用说明

### 2.1 统一响应格式

所有接口返回统一 JSON 结构：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

| 字段    | 类型    | 说明                                       |
| ------- | ------- | ------------------------------------------ |
| code    | int     | 状态码：200 成功，4xx 客户端错误，5xx 服务端错误 |
| message | string  | 提示信息                                   |
| data    | object  | 返回数据（可能为 null）                    |

### 2.2 分页响应格式

分页查询接口的 `data` 字段格式为：

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "total": 100,
  "list": []
}
```

| 字段     | 类型    | 说明           |
| -------- | ------- | -------------- |
| pageNum  | int     | 当前页码       |
| pageSize | int     | 每页记录数     |
| total    | long    | 总记录数       |
| list     | array   | 当前页数据列表 |

### 2.3 认证说明

1. 登录成功后，JWT 令牌通过 `Set-Cookie` 响应头写入 HttpOnly Cookie（`JWT_TOKEN`），前端无需手动管理令牌
2. 后续请求浏览器会自动携带 Cookie，后端 `JwtAuthenticationFilter` 自动解析验证
3. 退出登录时，后端会清除 Cookie 并将令牌在 Redis 中标记为失效

### 2.4 角色权限说明

| 角色 ID | 角色名称     | 权限说明                                         |
| ------- | ------------ | ------------------------------------------------ |
| 1       | 系统管理员   | 全部权限：用户管理、部门管理、产线管理、产品管理、角色管理、工单管理、工序报工 |
| 2       | 生产计划员   | 工单管理、工序报工                               |
| 3       | 车间操作员   | 仅工序报工                                       |
| 4       | 企业管理层   | 查看权限（具体视业务实现）                       |

---

## 三、认证接口

### 3.1 获取 RSA 公钥

> 无需认证。前端在登录前调用此接口获取公钥，用于加密密码后发送登录请求。

**请求**

```
GET /publickey
```

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
}
```

| 字段 | 类型   | 说明                           |
| ---- | ------ | ------------------------------ |
| data | string | Base64 编码的 RSA 公钥字符串   |

---

### 3.2 用户登录

> 无需认证。用户名和密码需使用 RSA 公钥加密后传输。

**请求**

```
POST /login
Content-Type: application/json
```

**请求体**

```json
{
  "username": "RSA加密后的Base64字符串",
  "password": "RSA加密后的Base64字符串"
}
```

| 字段     | 类型   | 必填 | 说明                             |
| -------- | ------ | ---- | -------------------------------- |
| username | string | 是   | RSA 公钥加密后的用户名（Base64） |
| password | string | 是   | RSA 公钥加密后的密码（Base64）   |

**响应示例**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "admin1",
    "name": "管理员1",
    "roleId": "1",
    "roleName": "系统管理员"
  }
}
```

| 字段              | 类型    | 说明                               |
| ----------------- | ------- | ---------------------------------- |
| data.userId       | long    | 用户 ID                            |
| data.username     | string  | 登录用户名                         |
| data.name         | string  | 真实姓名                           |
| data.roleId       | string  | 角色 ID                            |
| data.roleName     | string  | 角色名称                           |

> **安全说明**：JWT 令牌通过 `Set-Cookie` 响应头以 HttpOnly Cookie 形式下发，不在响应体中返回，防止 XSS 攻击窃取令牌。

---

### 3.3 用户退出登录

> 需要认证。

**请求**

```
POST /logout
```

**响应示例**

```json
{
  "code": 200,
  "message": "退出登录成功",
  "data": null
}
```

> 退出后，后端会清除客户端 `JWT_TOKEN` Cookie 并在 Redis 中标记令牌失效。

---

## 四、用户管理接口

> 基础路径：`/user`
> 除 `POST /user/list`、`GET /user/findAll`、`GET /user/find/{id}` 外，其余接口均需管理员权限（角色 ID=1）。

### 4.1 添加用户

```
POST /user/add
```

**请求体**

```json
{
  "username": "newuser",
  "password": "123456",
  "name": "张三",
  "gender": 1,
  "roleId": 3,
  "departmentId": 4,
  "status": 1
}
```

| 字段         | 类型    | 必填 | 说明                       |
| ------------ | ------- | ---- | -------------------------- |
| username     | string  | 是   | 登录用户名（唯一）         |
| password     | string  | 否   | 密码，为空时使用默认密码   |
| name         | string  | 是   | 真实姓名                   |
| gender       | int     | 是   | 性别：1=男，0=女           |
| roleId       | long    | 是   | 角色 ID                    |
| departmentId | long    | 否   | 部门 ID                    |
| status       | int     | 否   | 状态：1=正常，0=禁用（默认1） |

---

### 4.2 批量删除用户

```
DELETE /user/delete
```

**请求体**

```json
[1, 2, 3]
```

| 字段 | 类型         | 说明         |
| ---- | ------------ | ------------ |
| -    | array\<long\> | 用户 ID 列表 |

---

### 4.3 条件查询用户（分页）

```
POST /user/list
```

**请求体**

```json
{
  "name": "张三",
  "gender": 1,
  "roleId": 3,
  "departmentId": 4,
  "pageNum": 1,
  "pageSize": 10
}
```

| 字段         | 类型    | 必填 | 说明                     |
| ------------ | ------- | ---- | ------------------------ |
| name         | string  | 否   | 姓名（模糊匹配）         |
| gender       | int     | 否   | 性别：1=男，0=女         |
| roleId       | long    | 否   | 角色 ID                  |
| departmentId | long    | 否   | 部门 ID                  |
| pageNum      | int     | 否   | 页码，默认 1             |
| pageSize     | int     | 否   | 每页数量，默认 10        |

**响应 data.list 元素**

```json
{
  "id": 1,
  "username": "admin1",
  "name": "管理员1",
  "gender": 1,
  "roleId": 1,
  "roleName": "系统管理员",
  "departmentId": 5,
  "status": 1,
  "createTime": "2026-06-08T21:38:49",
  "updateTime": "2026-06-08T21:38:49"
}
```

---

### 4.4 查询所有用户（分页）

```
GET /user/findAll?pageNum=1&pageSize=10
```

| 参数     | 类型 | 必填 | 默认值 | 说明      |
| -------- | ---- | ---- | ------ | --------- |
| pageNum  | int  | 否   | 1      | 页码      |
| pageSize | int  | 否   | 10     | 每页数量  |

---

### 4.5 根据 ID 查询用户

```
GET /user/find/{id}
```

| 参数 | 类型 | 说明    |
| ---- | ---- | ------- |
| id   | long | 用户 ID |

---

### 4.6 修改用户

```
PUT /user/update
```

**请求体**

```json
{
  "id": 1,
  "username": "admin1",
  "name": "管理员1",
  "gender": 1,
  "roleId": 1,
  "departmentId": 5,
  "status": 1
}
```

| 字段         | 类型    | 必填 | 说明                       |
| ------------ | ------- | ---- | -------------------------- |
| id           | long    | 是   | 用户 ID                    |
| username     | string  | 否   | 登录用户名                 |
| password     | string  | 否   | 密码（修改时需重新加密）   |
| name         | string  | 否   | 真实姓名                   |
| gender       | int     | 否   | 性别                       |
| roleId       | long    | 否   | 角色 ID                    |
| departmentId | long    | 否   | 部门 ID                    |
| status       | int     | 否   | 状态                       |

---

## 五、部门管理接口

> 基础路径：`/department`
> 除 `POST /department/find` 外，其余接口均需管理员权限（角色 ID=1）。

### 5.1 添加部门

```
POST /department/add
```

**请求体**

```json
{
  "name": "生产部"
}
```

| 字段 | 类型   | 必填 | 说明               |
| ---- | ------ | ---- | ------------------ |
| name | string | 是   | 部门名称（唯一）   |

---

### 5.2 删除部门

```
DELETE /department/delete/{id}
```

| 参数 | 类型 | 说明    |
| ---- | ---- | ------- |
| id   | int  | 部门 ID |

---

### 5.3 条件查询部门

```
POST /department/find
```

**请求体**（所有字段可选，不传则返回全部）

```json
{
  "name": "生产部"
}
```

| 字段 | 类型   | 必填 | 说明         |
| ---- | ------ | ---- | ------------ |
| name | string | 否   | 部门名称     |

**响应 data 示例**

```json
[
  {
    "id": 1,
    "name": "研发部",
    "createTime": "2026-05-27T02:33:48",
    "updateTime": "2026-05-27T02:33:48"
  }
]
```

---

### 5.4 修改部门

```
PUT /department/update
```

**请求体**

```json
{
  "id": 1,
  "name": "研发中心"
}
```

| 字段 | 类型   | 必填 | 说明       |
| ---- | ------ | ---- | ---------- |
| id   | int    | 是   | 部门 ID    |
| name | string | 是   | 部门名称   |

---

## 六、产线管理接口

> 基础路径：`/line`
> 除 `GET /line/find/{id}`、`GET /line/findAll`、`GET /line/statistics` 外，其余接口均需管理员权限（角色 ID=1）。

### 6.1 添加产线

```
POST /line/add
```

**请求体**

```json
{
  "name": "SMT贴片线-C",
  "description": "新增高速贴片产线"
}
```

| 字段        | 类型   | 必填 | 说明               |
| ----------- | ------ | ---- | ------------------ |
| name        | string | 是   | 产线名称（唯一）   |
| description | string | 否   | 产线描述           |

---

### 6.2 删除产线

```
DELETE /line/delete/{id}
```

| 参数 | 类型 | 说明    |
| ---- | ---- | ------- |
| id   | long | 产线 ID |

---

### 6.3 更新产线

```
PUT /line/update
```

**请求体**

```json
{
  "id": 1,
  "name": "SMT贴片线-A",
  "description": "高速贴片生产线（升级版）"
}
```

| 字段        | 类型   | 必填 | 说明     |
| ----------- | ------ | ---- | -------- |
| id          | long   | 是   | 产线 ID  |
| name        | string | 否   | 产线名称 |
| description | string | 否   | 产线描述 |

---

### 6.4 根据 ID 查询产线

```
GET /line/find/{id}
```

| 参数 | 类型 | 说明    |
| ---- | ---- | ------- |
| id   | long | 产线 ID |

---

### 6.5 查询所有产线

```
GET /line/findAll
```

**响应 data 示例**

```json
[
  {
    "id": 1,
    "name": "SMT贴片线-A",
    "description": "高速贴片生产线",
    "createTime": "2026-05-25T05:16:31",
    "updateTime": "2026-05-25T05:16:31"
  }
]
```

---

### 6.6 产线产量统计

> 统计每条产线在指定日期范围内的计划数量和完成数量。

```
GET /line/statistics?startTime=2026-06-01&endTime=2026-06-17
```

| 参数      | 类型   | 必填 | 说明       |
| --------- | ------ | ---- | ---------- |
| startTime | string | 是   | 开始日期（yyyy-MM-dd） |
| endTime   | string | 是   | 结束日期（yyyy-MM-dd） |

**响应 data 示例**

```json
[
  {
    "lineId": 1,
    "lineName": "SMT贴片线-A",
    "totalPlanQuantity": 5000,
    "completedQuantity": 3200,
    "producingQuantity": 1000,
    "pendingQuantity": 500,
    "closedQuantity": 300
  }
]
```

| 字段                | 类型    | 说明           |
| ------------------- | ------- | -------------- |
| lineId              | int     | 产线 ID        |
| lineName            | string  | 产线名称       |
| totalPlanQuantity   | int     | 总计划生产数量 |
| completedQuantity   | int     | 已完成数量     |
| producingQuantity   | int     | 生产中数量     |
| pendingQuantity     | int     | 待生产数量     |
| closedQuantity      | int     | 已关闭数量     |

---

## 七、产品管理接口

> 基础路径：`/product`
> 除 `GET /product/find/{id}`、`POST /product/query`、`GET /product/findAll`、`GET /product/statistics` 外，其余接口均需管理员权限（角色 ID=1）。

### 7.1 添加产品

```
POST /product/add
```

**请求体**

```json
{
  "code": "PRD0101",
  "name": "控制板-101",
  "spec": "V2.0",
  "image": "/images/prd0101.png"
}
```

| 字段  | 类型   | 必填 | 说明                 |
| ----- | ------ | ---- | -------------------- |
| code  | string | 是   | 产品编码（唯一）     |
| name  | string | 是   | 产品名称             |
| spec  | string | 是   | 产品规格             |
| image | string | 否   | 产品图片路径         |

---

### 7.2 根据 ID 删除产品

```
DELETE /product/delete/{id}
```

---

### 7.3 批量删除产品

```
DELETE /product/deleteBatch
```

**请求体**

```json
[1, 2, 3]
```

---

### 7.4 更新产品

```
PUT /product/update
```

**请求体**

```json
{
  "id": 1,
  "code": "PRD0001",
  "name": "控制板-1",
  "spec": "V3.0",
  "image": "/images/prd0001_v3.png"
}
```

| 字段  | 类型   | 必填 | 说明         |
| ----- | ------ | ---- | ------------ |
| id    | long   | 是   | 产品 ID      |
| code  | string | 否   | 产品编码     |
| name  | string | 否   | 产品名称     |
| spec  | string | 否   | 产品规格     |
| image | string | 否   | 产品图片路径 |

---

### 7.5 根据 ID 查询产品

```
GET /product/find/{id}
```

---

### 7.6 条件查询产品（分页）

```
POST /product/query
```

**请求体**

```json
{
  "name": "控制板",
  "code": "PRD",
  "createTime": "2026-06-01",
  "pageNum": 1,
  "pageSize": 10
}
```

| 字段       | 类型   | 必填 | 说明                     |
| ---------- | ------ | ---- | ------------------------ |
| name       | string | 否   | 产品名称（模糊匹配）     |
| code       | string | 否   | 产品编码（模糊匹配）     |
| createTime | string | 否   | 创建日期（yyyy-MM-dd）   |
| pageNum    | int    | 否   | 页码                     |
| pageSize   | int    | 否   | 每页数量                 |

---

### 7.7 查询所有产品（分页）

```
GET /product/findAll?pageNum=1&pageSize=10
```

---

### 7.8 产品产量统计

> 统计指定日期范围内各产品的产量分布。

```
GET /product/statistics?startTime=2026-06-01&endTime=2026-06-17
```

**响应 data 示例**

```json
[
  {
    "productId": 1,
    "productCode": "PRD0001",
    "productName": "控制板-1",
    "productSpec": "V2.8",
    "totalPlanQuantity": 3000,
    "completedQuantity": 2000,
    "producingQuantity": 500,
    "pendingQuantity": 300,
    "closedQuantity": 200
  }
]
```

| 字段                | 类型    | 说明           |
| ------------------- | ------- | -------------- |
| productId           | int     | 产品 ID        |
| productCode         | string  | 产品编码       |
| productName         | string  | 产品名称       |
| productSpec         | string  | 产品规格       |
| totalPlanQuantity   | int     | 总计划生产数量 |
| completedQuantity   | int     | 已完成数量     |
| producingQuantity   | int     | 生产中数量     |
| pendingQuantity     | int     | 待生产数量     |
| closedQuantity      | int     | 已关闭数量     |

---

## 八、角色管理接口

> 基础路径：`/sysRole`
> 除 `GET /sysRole/find/{id}`、`GET /sysRole/list`、`GET /sysRole/search` 外，其余接口均需管理员权限（角色 ID=1）。

### 8.1 添加角色

```
POST /sysRole/add
```

**请求体**

```json
{
  "name": "质检员",
  "description": "负责产品质量检验"
}
```

| 字段        | 类型   | 必填 | 说明               |
| ----------- | ------ | ---- | ------------------ |
| name        | string | 是   | 角色名称（唯一）   |
| description | string | 否   | 角色描述           |

---

### 8.2 更新角色

```
PUT /sysRole/update
```

**请求体**

```json
{
  "id": 5,
  "name": "质检员",
  "description": "负责产品质量检验与报告"
}
```

---

### 8.3 删除角色

```
DELETE /sysRole/delete/{id}
```

---

### 8.4 根据 ID 查询角色

```
GET /sysRole/find/{id}
```

---

### 8.5 查询所有角色

```
GET /sysRole/list
```

**响应 data 示例**

```json
[
  {
    "id": 1,
    "name": "系统管理员",
    "description": "系统配置与权限管理",
    "createTime": "2026-05-19T01:42:56",
    "updateTime": "2026-05-19T01:42:56"
  }
]
```

---

### 8.6 条件查询角色

```
GET /sysRole/search
```

**请求体**

```json
{
  "name": "管理员"
}
```

---

## 九、工单管理接口

> 基础路径：`/workorder`

### 9.1 添加工单

> 需要角色：管理员（1）或生产计划员（2）

```
POST /workorder/add
```

**请求体**

```json
{
  "productId": 1,
  "lineId": 1,
  "quantity": 1000,
  "priority": 2,
  "planningTime": "2026-06-30",
  "creatorId": 1,
  "remarks": "紧急订单"
}
```

| 字段         | 类型   | 必填 | 说明                                 |
| ------------ | ------ | ---- | ------------------------------------ |
| productId    | long   | 是   | 产品 ID                              |
| lineId       | long   | 是   | 产线 ID                              |
| quantity     | int    | 是   | 计划生产数量                         |
| priority     | int    | 否   | 优先级：0=低，1=中，2=高，3=紧急（默认0） |
| planningTime | string | 否   | 计划完成时间（yyyy-MM-dd）           |
| creatorId    | int    | 是   | 创建人 ID                            |
| remarks      | string | 否   | 备注                                 |

---

### 9.2 批量删除工单

> 需要角色：管理员（1）或生产计划员（2）

```
DELETE /workorder/delete
```

**请求体**

```json
[1, 2, 3]
```

---

### 9.3 更新工单

> 需要角色：管理员（1）或生产计划员（2）

```
PUT /workorder/update
```

**请求体**

```json
{
  "id": 1,
  "productId": 1,
  "lineId": 2,
  "quantity": 1200,
  "status": 1,
  "priority": 3,
  "planningTime": "2026-07-15",
  "remarks": "优先级提升"
}
```

| 字段         | 类型   | 必填 | 说明                                 |
| ------------ | ------ | ---- | ------------------------------------ |
| id           | long   | 是   | 工单 ID                              |
| productId    | long   | 否   | 产品 ID                              |
| lineId       | long   | 否   | 产线 ID                              |
| quantity     | int    | 否   | 计划生产数量                         |
| status       | int    | 否   | 状态：0=待生产，1=生产中，2=已完成，3=已关闭 |
| priority     | int    | 否   | 优先级：0=低，1=中，2=高，3=紧急     |
| planningTime | string | 否   | 计划完成时间                         |
| remarks      | string | 否   | 备注                                 |

---

### 9.4 根据 ID 查询工单

```
GET /workorder/find/{id}
```

**响应 data 示例**

```json
{
  "id": 1,
  "productId": 1,
  "productName": "控制板-1",
  "lineId": 1,
  "lineName": "SMT贴片线-A",
  "quantity": 1000,
  "status": 1,
  "priority": 2,
  "planningTime": "2026-06-30",
  "creatorId": 1,
  "creatorName": "管理员1",
  "remarks": "紧急订单",
  "createTime": "2026-06-15T10:00:00",
  "updateTime": "2026-06-16T08:00:00"
}
```

---

### 9.5 分页查询所有工单

```
GET /workorder/findAll?pageNum=1&pageSize=10
```

---

### 9.6 条件查询工单

```
POST /workorder/query
```

**请求体**

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "id": 1,
  "productId": 1,
  "status": 1
}
```

| 字段     | 类型 | 必填 | 说明         |
| -------- | ---- | ---- | ------------ |
| pageNum  | int  | 否   | 页码         |
| pageSize | int  | 否   | 每页数量     |
| 其他字段 | -    | 否   | 工单各字段均可作为查询条件 |

---

### 9.7 工单详细信息统计

> 统计指定时间范围内的工单详细数据。

```
GET /workorder/detailed?startTime=2026-06-01&endTime=2026-06-17
```

**响应 data 示例**

```json
[
  {
    "id": 1,
    "productId": 1,
    "lineId": 1,
    "quantity": 1000,
    "status": 1,
    "statusName": "生产中",
    "priority": 2,
    "planningTime": "2026-06-30",
    "creatorId": 1,
    "remarks": "紧急订单",
    "createTime": "2026-06-15T10:00:00",
    "updateTime": "2026-06-16T08:00:00"
  }
]
```

---

## 十、工序报工接口

> 基础路径：`/workorder/process`
> 查询接口无需特别权限，写操作需角色 1/2/3。

### 10.1 添加工序报工

> 需要角色：管理员（1）、生产计划员（2）、车间操作员（3）

```
POST /workorder/process/add
```

**请求体**

```json
{
  "orderId": 1,
  "processSeq": 1,
  "qualifiedQuantity": 950,
  "badQuantity": 50,
  "operatorId": 10,
  "remarks": "印刷工序完成",
  "startTime": "2026-06-16T08:00:00",
  "finishTime": "2026-06-16T12:00:00"
}
```

| 字段              | 类型   | 必填 | 说明                                 |
| ----------------- | ------ | ---- | ------------------------------------ |
| orderId           | long   | 是   | 工单 ID                              |
| processSeq        | int    | 是   | 工序序号：1=印刷，2=贴片，3=回流焊   |
| qualifiedQuantity | int    | 否   | 合格数量（默认0）                    |
| badQuantity       | int    | 否   | 不良数量（默认0）                    |
| operatorId        | long   | 是   | 操作员 ID                            |
| remarks           | string | 否   | 备注                                 |
| startTime         | string | 否   | 工序开始时间（ISO 8601）             |
| finishTime        | string | 否   | 工序完成时间（ISO 8601）             |

---

### 10.2 删除工序报工

> 需要角色：管理员（1）、生产计划员（2）、车间操作员（3）

```
DELETE /workorder/process/delete/{Id}
```

| 参数 | 类型 | 说明        |
| ---- | ---- | ----------- |
| Id   | long | 报工记录 ID |

---

### 10.3 更新工序报工

> 需要角色：管理员（1）、生产计划员（2）、车间操作员（3）

```
PUT /workorder/process/update
```

**请求体**

```json
{
  "id": 1,
  "qualifiedQuantity": 980,
  "badQuantity": 20,
  "remarks": "更新印刷数据",
  "finishTime": "2026-06-16T14:00:00"
}
```

| 字段              | 类型   | 必填 | 说明                                 |
| ----------------- | ------ | ---- | ------------------------------------ |
| id                | long   | 是   | 报工记录 ID                          |
| qualifiedQuantity | int    | 否   | 合格数量                             |
| badQuantity       | int    | 否   | 不良数量                             |
| operatorId        | long   | 否   | 操作员 ID                            |
| remarks           | string | 否   | 备注                                 |
| startTime         | string | 否   | 开始时间                             |
| finishTime        | string | 否   | 完成时间                             |

---

### 10.4 根据工单 ID 查询工序报工

```
GET /workorder/process/find/{orderId}
```

| 参数    | 类型 | 说明    |
| ------- | ---- | ------- |
| orderId | long | 工单 ID |

**响应 data 示例**

```json
[
  {
    "id": 1,
    "orderId": 1,
    "processSeq": 1,
    "qualifiedQuantity": 950,
    "badQuantity": 50,
    "operatorId": 10,
    "remarks": "印刷工序完成",
    "startTime": "2026-06-16T08:00:00",
    "finishTime": "2026-06-16T12:00:00",
    "createTime": "2026-06-16T12:05:00",
    "updateTime": "2026-06-16T12:05:00"
  }
]
```

---

### 10.5 查询所有工序报工（分页）

```
GET /workorder/process/findAll?pageNum=1&pageSize=10
```

---

### 10.6 生产质量统计

> 统计指定时间范围内的良品数和不良数。

```
GET /workorder/process/statistics?startTime=2026-06-01&endTime=2026-06-17
```

**响应 data 示例**

```json
[
  {
    "date": "2026-06-16",
    "qualifiedQuantity": 9500,
    "badQuantity": 500,
    "totalQuantity": 10000,
    "passRate": 0.95
  }
]
```

| 字段              | 类型   | 说明                     |
| ----------------- | ------ | ------------------------ |
| date              | string | 统计日期（yyyy-MM-dd）   |
| qualifiedQuantity | int    | 良品数量                 |
| badQuantity       | int    | 不良数量                 |
| totalQuantity     | int    | 总数量                   |
| passRate          | double | 通过率（良品数/总数）    |

---

## 十一、接口汇总

| 模块     | 方法   | 路径                                  | 认证 | 权限    | 说明               |
| -------- | ------ | ------------------------------------- | ---- | ------- | ------------------ |
| 认证     | GET    | `/publickey`                          | 否   | -       | 获取 RSA 公钥      |
| 认证     | POST   | `/login`                              | 否   | -       | 用户登录           |
| 认证     | POST   | `/logout`                             | 是   | -       | 用户退出登录       |
| 用户管理 | POST   | `/user/add`                           | 是   | 1       | 添加用户           |
| 用户管理 | DELETE | `/user/delete`                        | 是   | 1       | 批量删除用户       |
| 用户管理 | POST   | `/user/list`                          | 是   | -       | 条件查询用户       |
| 用户管理 | GET    | `/user/findAll`                       | 是   | -       | 查询所有用户       |
| 用户管理 | GET    | `/user/find/{id}`                     | 是   | -       | 根据 ID 查询用户   |
| 用户管理 | PUT    | `/user/update`                        | 是   | 1       | 修改用户           |
| 部门管理 | POST   | `/department/add`                     | 是   | 1       | 添加部门           |
| 部门管理 | DELETE | `/department/delete/{id}`             | 是   | 1       | 删除部门           |
| 部门管理 | POST   | `/department/find`                    | 是   | -       | 条件查询部门       |
| 部门管理 | PUT    | `/department/update`                  | 是   | 1       | 修改部门           |
| 产线管理 | POST   | `/line/add`                           | 是   | 1       | 添加产线           |
| 产线管理 | DELETE | `/line/delete/{id}`                   | 是   | 1       | 删除产线           |
| 产线管理 | PUT    | `/line/update`                        | 是   | 1       | 更新产线           |
| 产线管理 | GET    | `/line/find/{id}`                     | 是   | -       | 根据 ID 查询产线   |
| 产线管理 | GET    | `/line/findAll`                       | 是   | -       | 查询所有产线       |
| 产线管理 | GET    | `/line/statistics`                    | 是   | -       | 产线产量统计       |
| 产品管理 | POST   | `/product/add`                        | 是   | 1       | 添加产品           |
| 产品管理 | DELETE | `/product/delete/{id}`                | 是   | 1       | 删除产品           |
| 产品管理 | DELETE | `/product/deleteBatch`                | 是   | 1       | 批量删除产品       |
| 产品管理 | PUT    | `/product/update`                     | 是   | 1       | 更新产品           |
| 产品管理 | GET    | `/product/find/{id}`                  | 是   | -       | 根据 ID 查询产品   |
| 产品管理 | POST   | `/product/query`                      | 是   | -       | 条件查询产品       |
| 产品管理 | GET    | `/product/findAll`                    | 是   | -       | 查询所有产品       |
| 产品管理 | GET    | `/product/statistics`                 | 是   | -       | 产品产量统计       |
| 角色管理 | POST   | `/sysRole/add`                        | 是   | 1       | 添加角色           |
| 角色管理 | PUT    | `/sysRole/update`                     | 是   | 1       | 更新角色           |
| 角色管理 | DELETE | `/sysRole/delete/{id}`                | 是   | 1       | 删除角色           |
| 角色管理 | GET    | `/sysRole/find/{id}`                  | 是   | -       | 根据 ID 查询角色   |
| 角色管理 | GET    | `/sysRole/list`                       | 是   | -       | 查询所有角色       |
| 角色管理 | GET    | `/sysRole/search`                     | 是   | -       | 条件查询角色       |
| 工单管理 | POST   | `/workorder/add`                      | 是   | 1,2     | 添加工单           |
| 工单管理 | DELETE | `/workorder/delete`                   | 是   | 1,2     | 批量删除工单       |
| 工单管理 | PUT    | `/workorder/update`                   | 是   | 1,2     | 更新工单           |
| 工单管理 | GET    | `/workorder/find/{id}`                | 是   | -       | 根据 ID 查询工单   |
| 工单管理 | GET    | `/workorder/findAll`                  | 是   | -       | 查询所有工单       |
| 工单管理 | POST   | `/workorder/query`                    | 是   | -       | 条件查询工单       |
| 工单管理 | GET    | `/workorder/detailed`                 | 是   | -       | 工单详细统计       |
| 工序报工 | POST   | `/workorder/process/add`              | 是   | 1,2,3   | 添加工序报工       |
| 工序报工 | DELETE | `/workorder/process/delete/{Id}`      | 是   | 1,2,3   | 删除工序报工       |
| 工序报工 | PUT    | `/workorder/process/update`           | 是   | 1,2,3   | 更新工序报工       |
| 工序报工 | GET    | `/workorder/process/find/{orderId}`   | 是   | -       | 根据工单查询工序   |
| 工序报工 | GET    | `/workorder/process/findAll`          | 是   | -       | 查询所有工序报工   |
| 工序报工 | GET    | `/workorder/process/statistics`       | 是   | -       | 生产质量统计       |

---

## 十二、数据字典

### 12.1 工单状态

| 值 | 含义     |
| -- | -------- |
| 0  | 待生产   |
| 1  | 生产中   |
| 2  | 已完成   |
| 3  | 已关闭   |

### 12.2 工单优先级

| 值 | 含义 |
| -- | ---- |
| 0  | 低   |
| 1  | 中   |
| 2  | 高   |
| 3  | 紧急 |

### 12.3 工序序号

| 值 | 含义   |
| -- | ------ |
| 1  | 印刷   |
| 2  | 贴片   |
| 3  | 回流焊 |

### 12.4 用户状态

| 值 | 含义 |
| -- | ---- |
| 0  | 禁用 |
| 1  | 正常 |

### 12.5 性别

| 值 | 含义 |
| -- | ---- |
| 0  | 女   |
| 1  | 男   |

### 12.6 角色权限

| 角色 ID | 角色名称     | 说明               |
| ------- | ------------ | ------------------ |
| 1       | 系统管理员   | 全部权限           |
| 2       | 生产计划员   | 工单管理、工序报工 |
| 3       | 车间操作员   | 仅工序报工         |
| 4       | 企业管理层   | 查看权限           |