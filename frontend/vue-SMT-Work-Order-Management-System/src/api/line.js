import request from "@/utils/request";

export const getLineListApi = () => request.get("/line/findAll");

export const getLineByIdApi = (id) => request.get("/line/find/" + id);

export const deleteLineApi = (id) => request.delete("/line/delete/" + id);

export const addLineApi = (dataModel) => request.post("/line/add", dataModel);

export const editLineApi = (dataModel) => request.put("/line/update", dataModel);
