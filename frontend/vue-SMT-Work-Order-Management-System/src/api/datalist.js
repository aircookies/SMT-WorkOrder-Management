import request from "@/utils/request";

/**
 * 获取指定时间内工单详细信息
 * @param {*} startTime 
 * @param {*} endTime 
 * @example {
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
        }
    ]
}
 */
export const getWorkOrderDetailApi = (startTime, endTime) => request.get("/workorder/detailed?startTime=" + startTime + "&endTime=" + endTime);

/**
 * 查询指定时间内的良品数和不良数统计
 * @param {*} startTime 
 * @param {*} endTime 
 * @example {
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
        {
            "date": "2025-04-08",
            "qualifiedQuantity": 2987,
            "badQuantity": 10,
            "totalQuantity": 2997,
            "passRate": 99.67
        }
    ]
}
 */ 
export const getstatisticsProductionQualityApi = (startTime, endTime) => request.get("/workorder/process/statistics?startTime=" + startTime + "&endTime=" + endTime);