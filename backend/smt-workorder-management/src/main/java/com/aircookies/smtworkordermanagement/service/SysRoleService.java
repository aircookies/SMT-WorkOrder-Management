package com.aircookies.smtworkordermanagement.service;

import com.aircookies.smtworkordermanagement.common.Result;
import com.aircookies.smtworkordermanagement.entity.SysRole;

public interface SysRoleService {
    Result addRole(SysRole sysRole);
    
    Result updateRole(SysRole sysRole);
    
    Result deleteRole(Integer id);
    
    Result findById(Integer id);
    
    Result findAll();
    
    Result findRoles(SysRole sysRole);
}