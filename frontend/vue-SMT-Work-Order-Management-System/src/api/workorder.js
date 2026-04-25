import request from "@/utils/request";

export const getWorkOrderListApi = () => request.get("/workorder/findAll");

export const queryWorkOrderApi = (pageNum, pageSize, id, status, priority) => {
    return request.get("/workorder/query?pageNum=" + pageNum
        + "&pageSize=" + pageSize + "&id=" + (id != null ? id : '')
        + "&status=" + (status != null ? status : '')
        + "&priority=" + (priority != null ? priority : ''));
}

