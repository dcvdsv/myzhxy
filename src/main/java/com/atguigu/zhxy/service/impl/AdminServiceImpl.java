package com.atguigu.zhxy.service.impl;

import com.atguigu.zhxy.mapper.AdminMapper;
import com.atguigu.zhxy.pojo.Admin;
import com.atguigu.zhxy.pojo.Grade;
import com.atguigu.zhxy.pojo.LoginForm;
import com.atguigu.zhxy.pojo.Teacher;
import com.atguigu.zhxy.service.AdminService;
import com.atguigu.zhxy.util.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("adminServiceImpl")
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Override
    public Admin login(LoginForm loginForm) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",loginForm.getUsername());
        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));
        Admin admin = baseMapper.selectOne(queryWrapper);
        return admin;
    }

    @Override
    public Admin getAdminById(Long userId) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<Admin>();
        queryWrapper.eq("id",userId);

        return  baseMapper.selectOne(queryWrapper);
    }

    @Override
    public IPage<Admin> getAdminsByOpr(Page<Admin> pageParam, String adminName) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(adminName)){
            //根据名字模糊查询
            queryWrapper.like("name",adminName);
        }
        //id倒序排列
        queryWrapper.orderByDesc("id");
        Page<Admin> page = baseMapper.selectPage(pageParam, queryWrapper);
        return page;
    }
}