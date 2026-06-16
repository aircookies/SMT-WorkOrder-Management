package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 部门数据访问层（MyBatis Mapper）
 * <p>
 * 对应 XML 映射文件，提供部门表的 CRUD 操作。
 * </p>
 */
@Mapper
public interface DeptMapper {

    /** 添加部门 */
    int addDept(Dept dept);

    /** 根据 ID 删除部门 */
    void deleteDept(int id);

    /** 条件查询部门列表 */
    List<Dept> findDept(Dept dept);

    /** 根据名称查询部门（用于重复检查） */
    Dept findDeptByName(String name);

    /** 根据 ID 查询部门 */
    Dept findDeptById(int id);

    /** 更新部门 */
    int updateDept(Dept dept);
}