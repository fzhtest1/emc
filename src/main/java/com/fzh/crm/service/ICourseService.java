package com.fzh.crm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fzh.crm.entity.Course;

public interface ICourseService extends IService<Course> {

    Page<Course> findPage(Page<Course> page, String name);

    void setStudentCourse(Integer courseId, Integer studentId);

    void delStudentCourse(Integer courseId, Integer studentId);

    String selCourse(Integer courseId, Integer studentId);
}
