package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Grade;
import com.atguigu.zhxy.service.GradeService;
import com.atguigu.zhxy.util.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "年级控制器")//解释类
@RestController
@RequestMapping("/sms/gradeController")
public class GradeController {
    @Autowired
    private GradeService gradeService;

    //http://localhost:9001/sms/gradeController/getGrades
    @ApiOperation("获取全部年级")
    @GetMapping("/getGrades")
    public Result getGrades(){
      List<Grade> grades= gradeService.getGrades();
      return Result.ok(grades);
    }


    @ApiOperation("根据年级名称模糊查询，带分页")
@GetMapping("/getGrades/{pageNo}/{pageSize}")
    public Result getGrades(
        @ApiParam("分页查询的页码数")    @PathVariable("pageNo") Integer pageNo,
        @ApiParam("分页查询的页大小")    @PathVariable("pageSize") Integer pageSize,
        @ApiParam("分页查询模糊匹配的名称")    String gradeName
    ){
    //分页带条件查询
    Page<Grade> page = new Page<>(pageNo, pageSize);
    //通过服务器
   IPage<Grade> pageRs=gradeService.getGradeByOpr(page,gradeName);
    //封装Result对象并返回
    return Result.ok(pageRs);
    }
    @ApiOperation("删除Grade信息")//解释方法
    @DeleteMapping("/deleteGrade")
    public Result deleteGrade(
         @ApiParam("要删除的所有的grade的id的JSON集合") @RequestBody List<Integer> ids  //@ApiParam("") 解释属性

            ){
//删除或批量删除
        gradeService.removeByIds(ids);
        return Result.ok();
    }
    @ApiOperation("新增或修改grade,有id属性是修改没有则是增加")
    @PostMapping("/saveOrUpdateGrade")
    public Result saveOrUpdateGrade(
            //接收参数
        @ApiParam("JSON的Grade对象")   @RequestBody Grade grade

    ){
    //调用服务层方法完成增减或修改
        gradeService.saveOrUpdate(grade);
        return Result.ok();
    }
}
