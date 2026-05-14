package com.experiment.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.experiment.report.common.Result;
import com.experiment.report.entity.*;
import com.experiment.report.mapper.*;
import com.experiment.report.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private SysGradeMapper gradeMapper;
    @Autowired
    private SysClassMapper classMapper;
    @Autowired
    private SysSubjectMapper subjectMapper;
    @Autowired
    private ExperimentProjectMapper projectMapper;
    @Autowired
    private ScoringCriteriaMapper criteriaMapper;
    @Autowired
    private ExperimentTaskMapper taskMapper;
    @Autowired
    private ExperimentReportMapper reportMapper;
    @Autowired
    private SysUserMapper userMapper;

    // =================== 教师管理 ===================

    @GetMapping("/teachers")
    public Result<?> getTeachers(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(required = false) String keyword) {
        return Result.success(userService.getUserPage(new Page<>(page, size), "TEACHER", keyword));
    }

    @PostMapping("/teacher")
    public Result<?> addTeacher(@RequestBody SysUser user) {
        user.setRole("TEACHER");
        return userService.addUser(user);
    }

    @PutMapping("/teacher")
    public Result<?> updateTeacher(@RequestBody SysUser user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/teacher/{id}")
    public Result<?> deleteTeacher(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/teacher/reset-password/{id}")
    public Result<?> resetTeacherPassword(@PathVariable Long id) {
        return userService.resetPassword(id);
    }

    // =================== 学生管理 ===================

    @GetMapping("/students")
    public Result<?> getStudents(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Long classId) {
        Page<SysUser> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRole, "STUDENT");
        if (classId != null) {
            wrapper.eq(SysUser::getClassId, classId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getStudentNo, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = userMapper.selectPage(pageParam, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(result);
    }

    @PostMapping("/student")
    public Result<?> addStudent(@RequestBody SysUser user) {
        user.setRole("STUDENT");
        return userService.addUser(user);
    }

    @PutMapping("/student")
    public Result<?> updateStudent(@RequestBody SysUser user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/student/{id}")
    public Result<?> deleteStudent(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    // =================== 年级管理 ===================

    @GetMapping("/grades")
    public Result<?> getGrades() {
        return Result.success(gradeMapper.selectList(
                new LambdaQueryWrapper<SysGrade>().orderByAsc(SysGrade::getSortOrder)));
    }

    @PostMapping("/grade")
    public Result<?> addGrade(@RequestBody SysGrade grade) {
        gradeMapper.insert(grade);
        return Result.success("添加成功");
    }

    @PutMapping("/grade")
    public Result<?> updateGrade(@RequestBody SysGrade grade) {
        gradeMapper.updateById(grade);
        return Result.success("修改成功");
    }

    @DeleteMapping("/grade/{id}")
    public Result<?> deleteGrade(@PathVariable Long id) {
        gradeMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // =================== 班级管理 ===================

    @GetMapping("/classes")
    public Result<?> getClasses(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(classMapper.selectClassPage(new Page<>(page, size)));
    }

    @GetMapping("/classes/all")
    public Result<?> getAllClasses() {
        return Result.success(classMapper.selectList(new LambdaQueryWrapper<SysClass>().orderByAsc(SysClass::getGradeId)));
    }

    @PostMapping("/class")
    public Result<?> addClass(@RequestBody SysClass sysClass) {
        classMapper.insert(sysClass);
        return Result.success("添加成功");
    }

    @PutMapping("/class")
    public Result<?> updateClass(@RequestBody SysClass sysClass) {
        classMapper.updateById(sysClass);
        return Result.success("修改成功");
    }

    @DeleteMapping("/class/{id}")
    public Result<?> deleteClass(@PathVariable Long id) {
        classMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // =================== 学科管理 ===================

    @GetMapping("/subjects")
    public Result<?> getSubjects() {
        return Result.success(subjectMapper.selectList(
                new LambdaQueryWrapper<SysSubject>().orderByAsc(SysSubject::getSortOrder)));
    }

    @PostMapping("/subject")
    public Result<?> addSubject(@RequestBody SysSubject subject) {
        subjectMapper.insert(subject);
        return Result.success("添加成功");
    }

    @PutMapping("/subject")
    public Result<?> updateSubject(@RequestBody SysSubject subject) {
        subjectMapper.updateById(subject);
        return Result.success("修改成功");
    }

    @DeleteMapping("/subject/{id}")
    public Result<?> deleteSubject(@PathVariable Long id) {
        subjectMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // =================== 实验项目管理 ===================

    @GetMapping("/projects")
    public Result<?> getProjects(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(required = false) Long subjectId) {
        LambdaQueryWrapper<ExperimentProject> wrapper = new LambdaQueryWrapper<>();
        if (subjectId != null) {
            wrapper.eq(ExperimentProject::getSubjectId, subjectId);
        }
        wrapper.orderByDesc(ExperimentProject::getCreateTime);
        return Result.success(projectMapper.selectPage(new Page<>(page, size), wrapper));
    }

    @PostMapping("/project")
    public Result<?> addProject(@RequestBody ExperimentProject project) {
        projectMapper.insert(project);
        return Result.success("添加成功");
    }

    @PutMapping("/project")
    public Result<?> updateProject(@RequestBody ExperimentProject project) {
        projectMapper.updateById(project);
        return Result.success("修改成功");
    }

    @DeleteMapping("/project/{id}")
    public Result<?> deleteProject(@PathVariable Long id) {
        projectMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // =================== 评分标准管理 ===================

    @GetMapping("/criteria")
    public Result<?> getCriteria(@RequestParam(required = false) Long projectId) {
        LambdaQueryWrapper<ScoringCriteria> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(ScoringCriteria::getProjectId, projectId);
        } else {
            wrapper.isNull(ScoringCriteria::getProjectId);
        }
        wrapper.orderByAsc(ScoringCriteria::getSortOrder);
        return Result.success(criteriaMapper.selectList(wrapper));
    }

    @PostMapping("/criteria")
    public Result<?> addCriteria(@RequestBody ScoringCriteria criteria) {
        criteriaMapper.insert(criteria);
        return Result.success("添加成功");
    }

    @PutMapping("/criteria")
    public Result<?> updateCriteria(@RequestBody ScoringCriteria criteria) {
        criteriaMapper.updateById(criteria);
        return Result.success("修改成功");
    }

    @DeleteMapping("/criteria/{id}")
    public Result<?> deleteCriteria(@PathVariable Long id) {
        criteriaMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // =================== 数据统计 ===================

    @GetMapping("/statistics")
    public Result<?> getStatistics() {
        java.util.HashMap<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalStudents", userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "STUDENT")));
        stats.put("totalTeachers", userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getRole, "TEACHER")));
        stats.put("totalTasks", taskMapper.selectCount(null));
        stats.put("totalReports", reportMapper.selectCount(null));
        stats.put("reviewedReports", reportMapper.selectCount(
                new LambdaQueryWrapper<ExperimentReport>().eq(ExperimentReport::getStatus, 2)));
        return Result.success(stats);
    }
}
