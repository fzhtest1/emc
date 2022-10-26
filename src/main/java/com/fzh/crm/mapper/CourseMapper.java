package com.fzh.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzh.crm.entity.Course;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CourseMapper extends BaseMapper<Course> {

    Page<Course> findPage(Page<Course> page, @Param("name") String name);

    void deleteStudentCourse(@Param("courseId")Integer courseId, @Param("studentId") Integer studentId);

    void setStudentCourse(@Param("courseId")Integer courseId, @Param("studentId") Integer studentId);

    @Select("SELECT * FROM student_course WHERE course_id = #{courseId} and student_id = #{studentId}")
    String selCourse(Integer courseId, Integer studentId);
}
