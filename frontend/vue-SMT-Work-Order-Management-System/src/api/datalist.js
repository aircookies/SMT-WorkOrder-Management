import request from "@/utils/request";

/**
 * 获取指定时间内工单详细信息
 * @param {*} startTime 开始时间
 * @param {*} endTime 结束时间
 * @returns {Object} {
    "code": 200,
    "message": "success",
    "data": [
        {
            "date": "2025-04-11",
            "id": 45,
            "productId": 5,
            "LineId": 1,
            "quantity": 1800,
            "status": 1,
            "statusName": "生产中",
            "priority": 1,
            "planningTime": "2025-04-19",
            "creatorId": 1,
            "remarks": "常规生产中",
            "createTime": "2025-04-11T10:30:00",
            "updateTime": "2025-04-15T07:45:00"
        },
        {
            "date": "2025-04-11",
            "id": 61,
            "productId": 10,
            "LineId": 2,
            "quantity": 980,
            "status": 2,
            "statusName": "已完成",
            "priority": 1,
            "planningTime": "2025-04-15",
            "creatorId": 2,
            "remarks": "刚刚完成",
            "createTime": "2025-04-11T09:00:00",
            "updateTime": "2025-04-15T11:30:00"
        },
        ...
    ]
}
 */
export const getWorkOrderDetailApi = (startTime, endTime) => request.get("/workorder/detailed?startTime=" + startTime + "&endTime=" + endTime);

/**
 * 查询指定时间内的良品数和不良数统计
 * @param {*} startTime 开始时间
 * @param {*} endTime 结束时间
 * @returns {Object} {
    "code": 200,
    "message": "success",
    "data": [
        {
            "date": "2025-04-12",
            "qualifiedQuantity": 7462,
            "badQuantity": 25,
            "totalQuantity": 7487,
            "passRate": 99.67
        },
        {
            "date": "2025-04-10",
            "qualifiedQuantity": 2392,
            "badQuantity": 9,
            "totalQuantity": 2401,
            "passRate": 99.63
        },
        {
            "date": "2025-04-09",
            "qualifiedQuantity": 1796,
            "badQuantity": 4,
            "totalQuantity": 1800,
            "passRate": 99.78
        },
        ...
    ]
}
 */ 
export const getstatisticsProductionQualityApi = (startTime, endTime) => request.get("/workorder/process/statistics?startTime=" + startTime + "&endTime=" + endTime);

/**
 * 统计每条产线在指定日期范围内的计划数量和完成数量
 * @param {*} startTime 开始时间
 * @param {*} endTime 结束时间
 * @returns {Object} {
    "code": 200,
    "message": "success",
    "data": [
        {
            "lineId": 2,
            "lineName": "SMT贴片产线A",
            "totalPlanQuantity": 11830,
            "completedQuantity": 3680,
            "producingQuantity": 3200,
            "pendingQuantity": 4950,
            "closedQuantity": 0
        },
        {
            "lineId": 3,
            "lineName": "SMT贴片产线B",
            "totalPlanQuantity": 13100,
            "completedQuantity": 3300,
            "producingQuantity": 1600,
            "pendingQuantity": 6900,
            "closedQuantity": 1300
        },
        {
            "lineId": 4,
            "lineName": "DIP插件产线C",
            "totalPlanQuantity": 0,
            "completedQuantity": 0,
            "producingQuantity": 0,
            "pendingQuantity": 0,
            "closedQuantity": 0
        },
        ...
    ]
}
 */
export const getStatisticsLineProductionApi = (startTime, endTime) => request.get("/line/statistics?startTime=" + startTime + "&endTime=" + endTime);

/**
 * 查询指定日期内各个产品的产量统计
 * @param {string} startTime 开始时间
 * @param {string} endTime 结束时间
 * @returns {Object} {
    "code": 200,
    "message": "success",
    "data": [
        {
            "productId": 2,
            "productCode": "PROD-2024-001",
            "productName": "STM32F103C8T6开发板",
            "productSpec": "尺寸:50x50mm, 主控:STM32F103C8T6, 接口:USB-C",
            "totalPlanQuantity": 3900,
            "completedQuantity": 3400,
            "producingQuantity": 500,
            "pendingQuantity": 0,
            "closedQuantity": 0
        },
        {
            "productId": 3,
            "productCode": "PROD-2024-002",
            "productName": "ESP32-WROOM模组",
            "productSpec": "尺寸:25x18mm, WiFi+蓝牙双模, 工作电压:3.3V",
            "totalPlanQuantity": 5200,
            "completedQuantity": 2000,
            "producingQuantity": 1200,
            "pendingQuantity": 2000,
            "closedQuantity": 0
        },
        ...
    ]
}
 */
export const getStatisticsProductProductionApi = (startTime, endTime) => request.get("/product/statistics?startTime=" + startTime + "&endTime=" + endTime);
