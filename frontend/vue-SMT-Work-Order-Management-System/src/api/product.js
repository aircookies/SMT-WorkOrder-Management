import request from "@/utils/request";

export const getProductListApi = (pageNum, pageSize) => request.get("/product/findAll?pageNum=" + pageNum + "&pageSize=" + pageSize);

export const getProductByIdApi = (id) => request.get("/product/find/" + id);

export const queryProductApi = (dataModel) => request.post("/product/query", dataModel);

export const addProductApi = (dataModel) => request.post("/product/add", dataModel);

export const editProductApi = (dataModel) => request.put("/product/update", dataModel);

export const deleteProductApi = (ids) => request.delete("/product/deleteBatch", { data: ids });

export const deleteProductByIdApi = (id) => request.delete("/product/delete/" + id);
