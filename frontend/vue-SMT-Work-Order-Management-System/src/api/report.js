import request from "@/utils/request";

// 获取报工列表
export const getReportListApi = () => request.get("/process/findAll");
// 添加报工
export const addReportApi = (report) => request.post("/process/add", report);
