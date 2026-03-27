package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.Dept;

public interface DeptService {
    Result addDept(Dept dept);

    Result deleteDept(int id);

    Result findDept(Dept dept);

    Result updateDept(Dept dept);
}
