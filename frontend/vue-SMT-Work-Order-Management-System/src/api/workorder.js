import request from "@/utils/request";

export const getWorkOrderListApi = () => request.get("/workorder/findAll");

export const getWorkOrderByIdApi = (id) => request.get("/workorder/find/" + id);

export const queryWorkOrderApi = (pageNum, pageSize, id, status, priority) => {
    return request.get("/workorder/query?pageNum=" + pageNum
        + "&pageSize=" + pageSize + "&id=" + (id != null ? id : '')
        + "&status=" + (status != null ? status : '')
        + "&priority=" + (priority != null ? priority : ''));
}

export const addWorkOrderApi = (workOrder) => request.post("/workorder/add", workOrder);

export const editWorkOrderApi = (workOrder) => request.put("/workorder/update", workOrder);

export const deleteWorkOrderApi = (ids) => request.delete("/workorder/delete", { data: ids });
