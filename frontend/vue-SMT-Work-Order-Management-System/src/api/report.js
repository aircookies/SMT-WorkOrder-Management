import request from "@/utils/request";

// 获取报工列表
export const getReportListApi = (pageNum, pageSize) => request.get("/workorder/process/findAll" + "?pageNum=" + pageNum + "&pageSize=" + pageSize);
// 添加报工
export const addReportApi = (report) => request.post("/workorder/process/add", report);
