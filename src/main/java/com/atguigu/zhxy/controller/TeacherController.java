package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Admin;
import com.atguigu.zhxy.pojo.Teacher;
import com.atguigu.zhxy.service.TeacherService;
import com.atguigu.zhxy.util.MD5;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sms/teacherController")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    // /getTeachers/1/3?clazzName=?&name=?
    @ApiOperation("查询教师的信息")
    @GetMapping("/getTeachers/{pageNo}/{pageSize}")
    public Result  getTeachers(
            @ApiParam("分页查询的页码数")@PathVariable Integer pageNo,
            @ApiParam("分页查询的页大小")@PathVariable Integer pageSize,
            Teacher teacher

    ){
        Page<Teacher> pageParam = new Page<>(pageNo, pageSize);
        IPage<Teacher> iPage=teacherService.getAdminsByOpr(pageParam,teacher);
        return Result.ok(iPage);

    }
    // /saveOrUpdateTeacher
    @ApiOperation("修改或添加教师的信息")
    @PostMapping("/saveOrUpdateTeacher")
    public Result saveOrUpdateTeacher(
            @ApiParam("JSON格式的Teacher对象") @RequestBody Teacher teacher
    ){
        Integer id = teacher.getId();
        if (id==null||0==id) {
            teacher.setPassword(MD5.encrypt(teacher.getPassword()));
        }
        teacherService.saveOrUpdate(teacher);
        return Result.ok();
    }
    // /deleteTeacher
    @ApiOperation("单个删除或批量删除教师信息")
    @DeleteMapping("/deleteTeacher")
    public Result deleteTeacher(
         @ApiParam("要删除的管理员多个ID的JSON集合") @RequestBody List<Integer> ids
    ){
      teacherService.removeByIds(ids);
      return Result.ok();
    }
}
