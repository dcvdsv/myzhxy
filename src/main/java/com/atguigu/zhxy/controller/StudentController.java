package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Student;
import com.atguigu.zhxy.service.StudentService;
import com.atguigu.zhxy.util.MD5;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sms/studentController")
public class StudentController {
    @Autowired
    private StudentService studentService;

    // /delStudentById
    @ApiOperation("删除单个或多个学生信息")
    @DeleteMapping("/delStudentById")
    public Result delStudentById(
        @ApiParam("要删除的学生编号的JSON数组")    @RequestBody List<Integer> ids

            ){
studentService.removeByIds(ids);

        return Result.ok();
    }



    // /addOrUpdateStudent
   @ApiOperation("学生信息的增加和修改")
   @PostMapping("addOrUpdateStudent")
   public Result addOrUpdateStudent(
           @RequestBody Student student

   ){
       Integer id = student.getId();
       if (null==id||0==id) {
           student.setPassword(MD5.encrypt(student.getPassword()));
       }
       studentService.saveOrUpdate(student);
       return Result.ok();
   }





    //  /getStudentByOpr/1/3
  @ApiOperation("分页带条件查询学生信息")
  @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
  public Result getStudentByOpr(
          @ApiParam("页码数") @PathVariable("pageNo") Integer pageNo,
          @ApiParam("页大小") @PathVariable("pageSize") Integer pageSize,
          @ApiParam("查新学生条件")Student student

          ){
      //分页信息封装Page对象
      Page<Student> pageParam = new Page<>(pageNo, pageSize);
      //进行查询
     IPage<Student> studentPage =studentService.getStudentByOpr(pageParam,student);
     //封装Result返回
      return Result.ok(studentPage);
  }
}
