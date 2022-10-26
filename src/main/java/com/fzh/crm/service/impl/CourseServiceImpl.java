package com.fzh.crm.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzh.crm.entity.Course;
import com.fzh.crm.mapper.CourseMapper;
import com.fzh.crm.service.ICourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private ICourseService courseService;

    @Override
    public Page<Course> findPage(Page<Course> page, String name) {
        return courseMapper.findPage(page, name);
    }

    @Transactional
    @Override
    public void setStudentCourse(Integer courseId, Integer studentId) {
        courseMapper.deleteStudentCourse(courseId, studentId);
        courseMapper.setStudentCourse(courseId, studentId);
        Course byId = courseService.getById(courseId);
        byId.setQuota(byId.getQuota() - 1);
        courseService.updateById(byId);
    }

    @Override
    public void delStudentCourse(Integer courseId, Integer studentId) {
        courseMapper.deleteStudentCourse(courseId, studentId);
        Course byId = courseService.getById(courseId);
        byId.setQuota(byId.getQuota() + 1);
        courseService.updateById(byId);
    }

    @Override
    public String selCourse(Integer courseId, Integer studentId) {
        return courseMapper.selCourse(courseId, studentId);
    }

}
