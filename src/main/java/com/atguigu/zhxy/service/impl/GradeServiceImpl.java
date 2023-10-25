package com.atguigu.zhxy.service.impl;

import com.atguigu.zhxy.mapper.GradeMapper;
import com.atguigu.zhxy.pojo.Grade;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service("gradeServiceImpl")
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements com.atguigu.zhxy.service.GradeService {
    @Override
    public IPage<Grade> getGradeByOpr(Page<Grade> pageParam, String gradeName) {
        QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(gradeName)){
            queryWrapper.like("name",gradeName);
        }
        //id倒序排列
        queryWrapper.orderByDesc("id");
        Page<Grade> page = baseMapper.selectPage(pageParam, queryWrapper);
        return page;
    }

    @Override
    public List<Grade> getGrades() {
        return   baseMapper.selectList(null);

    }
}
