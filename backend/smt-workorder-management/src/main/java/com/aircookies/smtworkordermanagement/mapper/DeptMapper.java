package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeptMapper {

    int addDept(Dept dept);

    void deleteDept(int id);

    List<Dept> findDept(Dept dept);

    Dept findDeptByName(String name);

    int updateDept(Dept dept);
}
