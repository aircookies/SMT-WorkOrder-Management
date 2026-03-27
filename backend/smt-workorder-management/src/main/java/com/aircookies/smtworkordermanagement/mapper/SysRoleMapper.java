package com.aircookies.smtworkordermanagement.mapper;

import com.aircookies.smtworkordermanagement.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysRoleMapper {
    void addRole(SysRole sysRole);
    
    void updateRole(SysRole sysRole);
    
    void deleteRole(Integer id);
    
    SysRole findById(Integer id);
    
    List<SysRole> findAll();
    
    List<SysRole> findRoles(SysRole sysRole);
}