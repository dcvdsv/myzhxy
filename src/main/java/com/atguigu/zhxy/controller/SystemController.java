package com.atguigu.zhxy.controller;

import com.atguigu.zhxy.pojo.Admin;
import com.atguigu.zhxy.pojo.LoginForm;
import com.atguigu.zhxy.pojo.Student;
import com.atguigu.zhxy.pojo.Teacher;
import com.atguigu.zhxy.service.AdminService;
import com.atguigu.zhxy.service.StudentService;
import com.atguigu.zhxy.service.TeacherService;
import com.atguigu.zhxy.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/sms/system")
public class SystemController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;

    // /sms/system/headerImgUpload
    @ApiOperation("文件上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(
         @ApiParam("头像文件") @RequestPart("multipartFile")MultipartFile multipartFile,
    HttpServletRequest request
    ){
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
       String newFileName= uuid+originalFilename.substring(i);
        //保存文件 将文件发送到第三方/独立的图片处理器上
       String portraitPath="D:/zhihuixm/zhihui/myzhxy/target/classes/public/upload/".concat(newFileName);
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //响应图片的路径/
        String path="upload/".concat(newFileName);
        return Result.ok(path);
    }





    @GetMapping("/getInfo")
    public Result getInfoByToken(@RequestHeader("token") String token){
        boolean expiration = JwtHelper.isExpiration(token);
        if (expiration){
            return Result.build(null, ResultCodeEnum.TOKEN_ERROR);
        }
        //从token中解析出用户id和用户类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        Map<String,Object> map=new LinkedHashMap<>();
        System.out.println(userType);
        System.out.println(userId);
        switch (userType){
            case 1:
             Admin admin=adminService.getAdminById(userId);
                map.put("userType",1);
                map.put("user",admin);
                 break;
            case 2:
             Student student=studentService.getStudentById(userId);
                map.put("userType",2);
                map.put("user",student);
                break;
            case 3:
                Teacher teacher=teacherService.getTeacherById(userId);
                map.put("userType",3);
                map.put("user",teacher);
                break;
        }
        return Result.ok(map);
    }
@PostMapping("/login")
public Result login(@RequestBody LoginForm loginForm,HttpServletRequest request){
    //验证码效验
    HttpSession session = request.getSession();
    String sessionVerifiCode = (String) session.getAttribute("verifiCode");
    String loginVerifiCode = loginForm.getVerifiCode();
    if ("".equals(sessionVerifiCode)|| null == sessionVerifiCode){
        return Result.fail().message("验证码失效，请刷新后重试");
    }
    if(!sessionVerifiCode.equalsIgnoreCase(loginVerifiCode)){
        return Result.fail() .message("验证码有误，请小心输入后重试");
    }
    //从session域中移除现有验证码
    session.removeAttribute("verifiCode");
    //分用户类型进行效验
        //准备一个map用户存放响应的数据
    Map<String,Object> map=new LinkedHashMap<>();
    switch (loginForm.getUserType()){
        case 1:
            try {


                Admin admin = adminService.login(loginForm);
                if (null != admin) {
                    //用户的类型和用户id转换成一个密文，以token的名称向客户端反馈
                    map.put("token", JwtHelper.createToken(admin.getId().longValue(), 1));

                } else {
                    throw new RuntimeException("用户名或密码有误");

                }
                return Result.ok(map);
            }catch (RuntimeException e){
                e.printStackTrace();
                return Result.fail().message(e.getMessage());
            }
        case 2:
            try {


                Student student = studentService.login(loginForm);
                if (null != student) {
                    //用户的类型和用户id转换成一个密文，以token的名称向客户端反馈
                    map.put("token", JwtHelper.createToken(student.getId().longValue(), 2));

                } else {
                    throw new RuntimeException("用户名或密码有误");

                }
                return Result.ok(map);
            }catch (RuntimeException e){
                e.printStackTrace();
                return Result.fail().message(e.getMessage());
            }
        case 3:
            try {


                Teacher teacher = teacherService.login(loginForm);
                if (null != teacher) {
                    //用户的类型和用户id转换成一个密文，以token的名称向客户端反馈
                    map.put("token", JwtHelper.createToken(teacher.getId().longValue(), 3));

                } else {
                    throw new RuntimeException("用户名或密码有误");

                }
                return Result.ok(map);
            }catch (RuntimeException e){
                e.printStackTrace();
                return Result.fail().message(e.getMessage());
            }

    }
return  Result.fail().message("查无此用户");
}



    @GetMapping("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpServletRequest request, HttpServletResponse response){

        //获取图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();

        //获取图片上的验证码
        String verifiCode=new String(CreateVerifiCodeImage.getVerifiCode());
        //将验证码文本放入session域，为下一次验证做准备
        HttpSession session = request.getSession();
        session.setAttribute("verifiCode",verifiCode);
        //将验证码的图片响应给浏览器
        try {
            ImageIO.write(verifiCodeImage,"JPEG",response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

   //  /sms/system/updatePwd/admin/123456
    @ApiOperation("更新用户名密码的处理器")
    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result updatePwd(
            @RequestHeader("token") String token,
            @PathVariable("oldPwd") String oldPwd,
            @PathVariable("newPwd") String newPwd

    ){
        boolean expiration = JwtHelper.isExpiration(token);//检测token
        System.out.println(oldPwd);
        System.out.println("---------");
        System.out.println(newPwd);
        if (expiration){
            //token过期
            return Result.fail().message("token失效，请更新");
        }
        //获取用户ID和用户类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        System.out.println("---------------");
        oldPwd= MD5.encrypt(oldPwd);//把密码加密

        newPwd=MD5.encrypt(newPwd);
       String adminPwd=MD5.encrypt("6cf82ee1020caef069e753c67a97a70d");
        System.out.println(adminPwd);
        switch (userType){
            case 1:
                QueryWrapper<Admin> adminQueryWrapper = new QueryWrapper<>();
                adminQueryWrapper.eq("id",userId.intValue());
                adminQueryWrapper.eq("password",oldPwd);
                Admin admin = adminService.getOne(adminQueryWrapper);
                if (admin!=null) {
                    //修改
                    admin.setPassword(newPwd);
                    adminService.saveOrUpdate(admin);
                }else {
                    return Result.fail().message("原密码有误！");
                }
                break;
            case 2:
                QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
                studentQueryWrapper.eq("id",userId.intValue());
                studentQueryWrapper.eq("password",oldPwd);
                Student student = studentService.getOne(studentQueryWrapper);
                if (student!=null) {
                    //修改
                    student.setPassword(newPwd);
                    studentService.saveOrUpdate(student);
                }else {
                    return Result.fail().message("原密码有误！");
                }
                break;
            case 3:
                QueryWrapper<Teacher> teacherQueryWrapper = new QueryWrapper<>();
                teacherQueryWrapper.eq("id",userId.intValue());
                teacherQueryWrapper.eq("password",oldPwd);
                Teacher teacher = teacherService.getOne(teacherQueryWrapper);
                if (teacher!=null) {
                    //修改
                    teacher.setPassword(newPwd);
                    teacherService.saveOrUpdate(teacher);
                }else {
                    return Result.fail().message("原密码有误！");
                }
                break;
        }
        return Result.ok();
    }



}
