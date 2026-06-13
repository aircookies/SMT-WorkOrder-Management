SET NAMES utf8mb4;
create database if not exists smt_workorder_management;
use smt_workorder_management;

set global slow_query_log = 1; -- 开启慢查询日志
set global long_query_time = 1; -- 记录1秒以上的慢查询

drop table if exists dept;
create table dept
(
    id          int auto_increment comment '部门ID'
        primary key,
    name        varchar(64) not null comment '部门名称',
    create_time datetime    not null comment '创建时间',
    update_time datetime    not null comment '更新时间',
    constraint name
        unique (name)
);

drop table if exists line;
create table line
(
    id          int auto_increment comment '产线ID'
        primary key,
    name        varchar(128) not null comment '产线名称',
    description varchar(512) null comment '产线描述',
    create_time datetime     not null comment '创建时间',
    update_time datetime     not null comment '更新时间',
    constraint name
        unique (name)
);

drop table if exists product;
create table product
(
    id          bigint auto_increment comment '产品ID'
        primary key,
    code        varchar(128) not null comment '产品编码',
    name        varchar(128) not null comment '产品名称',
    spec        varchar(512) not null comment '产品规格',
    image       varchar(512) null comment '产品图片',
    create_time datetime     not null comment '创建时间',
    update_time datetime     not null comment '更新时间',
    constraint code
        unique (code)
);

drop table if exists sys_role;
create table sys_role
(
    id          int auto_increment comment '角色ID'
        primary key,
    name        varchar(64)  null comment '角色名称',
    description varchar(255) null comment '角色描述',
    create_time datetime     not null comment '创建时间',
    update_time datetime     not null comment '更新时间',
    constraint name
        unique (name)
);

drop table if exists sys_user;
create table sys_user
(
    id            bigint auto_increment
        primary key,
    username      varchar(64)                                                                         not null comment '用户名',
    password      varchar(255) default '$2b$10$FMW1gm4OizgeJtEmwY1KIuOYKYP1oPIp14w9cTZhF9LuOc.b4DVey' not null comment '密码',
    name          varchar(64)                                                                         not null comment '姓名',
    gender        tinyint                                                                             not null comment '性别: 1男 0女',
    role_id       bigint                                                                              not null comment '角色ID',
    department_id bigint                                                                              null comment '部门ID',
    status        tinyint      default 1                                                              not null comment '状态：1-正常 0-禁用',
    create_time   datetime     default CURRENT_TIMESTAMP                                              not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP                                              not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint username
        unique (username)
);

create index idx_create_time
    on sys_user (create_time);

drop table if exists work_order;
create table work_order
(
    id            bigint auto_increment comment '工单ID'
        primary key,
    product_id    bigint            not null comment '产品ID',
    line_id       bigint            not null comment '产线ID',
    quantity      int               not null comment '计划生产',
    status        tinyint default 0 not null comment '工单状态: 0待生产 1生产中 2已完成 3已关闭',
    priority      tinyint default 0 not null comment '工单优先级: 0低 1中 2高 3紧急',
    planning_time date              null comment '计划完成时间',
    creator_id    bigint            not null comment '创建人ID',
    remarks       varchar(512)      null comment '备注',
    create_time   datetime          not null comment '创建时间',
    update_time   datetime          not null comment '更新时间'
);

create index idx_create_time
    on work_order (create_time);

drop table if exists work_process;
create table work_process
(
    id                 bigint auto_increment comment '工单工序ID'
        primary key,
    order_id           bigint        not null comment '工单ID',
    process_seq        int           not null comment '工序序号：1印刷，2贴片，3回流焊',
    qualified_quantity int default 0 null comment '合格数量',
    bad_quantity       int default 0 null comment '不良数量',
    operator_id        bigint        not null comment '操作员ID',
    remarks            varchar(512)  null comment '备注',
    start_time         datetime      null comment '开始时间',
    finish_time        datetime      null comment '完成时间',
    create_time        datetime      not null comment '创建时间',
    update_time        datetime      not null comment '更新时间',
    constraint uk_order_process_seq
        unique (order_id, process_seq)
);

create index idx_order_id
    on work_process (order_id);