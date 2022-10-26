package com.fzh.crm.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzh.crm.common.Constants;
import com.fzh.crm.common.Result;
import com.fzh.crm.entity.Course;
import com.fzh.crm.entity.User;
import com.fzh.crm.service.ICourseService;
import com.fzh.crm.service.IUserService;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private ICourseService courseService;

    @Resource
    private IUserService userService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Course course) {
        courseService.saveOrUpdate(course);
        return Result.success();
    }

    @PostMapping("/studentCourse/{courseId}/{studentId}/{xuanke}")
    public synchronized Result studentCourse(@PathVariable Integer courseId, @PathVariable Integer studentId, @PathVariable Integer xuanke) throws InterruptedException {
        if (xuanke == 1) {
            if (courseService.selCourse(courseId,studentId) != null){
                return Result.success(Constants.CODE_200,"该课程已选");
            }else {

                Thread.sleep(100);

                if (courseService.getById(courseId).getQuota() != 0) {
                    courseService.setStudentCourse(courseId, studentId);
                    return Result.success(Constants.CODE_200,"选课成功");
                }else {
                    return Result.success(Constants.CODE_200,"该课程已满");
                }
            }
        }else if (xuanke == 0) {
            if (courseService.selCourse(courseId,studentId) != null) {
                courseService.delStudentCourse(courseId, studentId);
                return Result.success(Constants.CODE_200,"退课成功");
            }else {
                return Result.success(Constants.CODE_200,"尚未选择该课程");
            }
        }

        return Result.error();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        courseService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        courseService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(courseService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(courseService.getById(id));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam String name,
                           @RequestParam Integer pageNum,
                                @RequestParam Integer pageSize) {

        Page<Course> page = courseService.findPage(new Page<>(pageNum, pageSize), name);


        return Result.success(page);
    }

}

