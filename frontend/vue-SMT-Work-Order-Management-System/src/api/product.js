import request from "@/utils/request";

export const getProductListApi = (pageNum, pageSize) => request.get("/product/findAll?pageNum=" + pageNum + "&pageSize=" + pageSize);

export const getProductByIdApi = (id) => request.get("/product/find/" + id);

export const queryProductApi = (dataModel) => request.post("/product/query", dataModel);
