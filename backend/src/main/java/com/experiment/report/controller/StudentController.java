package com.experiment.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.experiment.report.common.Result;
import com.experiment.report.entity.*;
import com.experiment.report.mapper.*;
import com.experiment.report.service.FileService;
import com.experiment.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 学生控制器
 */
@RestController
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    @Autowired
    private ExperimentTaskMapper taskMapper;
    @Autowired
    private ReportService reportService;
    @Autowired
    private FileService fileService;
    @Autowired
    private SysUserMapper userMapper;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // =================== 实验任务 ===================

    /**
     * 获取我的实验任务列表
     */
    @GetMapping("/tasks")
    public Result<?> getMyTasks(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer size) {
        Long studentId = getCurrentUserId();
        // 获取学生班级
        SysUser student = userMapper.selectById(studentId);
        if (student == null || student.getClassId() == null) {
            return Result.success(new Page<>());
        }

        LambdaQueryWrapper<ExperimentTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExperimentTask::getClassId, student.getClassId());
        wrapper.eq(ExperimentTask::getStatus, 1);
        wrapper.orderByDesc(ExperimentTask::getDeadline);
        return Result.success(taskMapper.selectPage(new Page<>(page, size), wrapper));
    }

    // =================== 报告提交 ===================

    /**
     * 提交实验报告
     */
    @PostMapping("/report/submit")
    public Result<?> submitReport(@RequestBody ExperimentReport report) {
        report.setStudentId(getCurrentUserId());
        return reportService.submitReport(report);
    }

    /**
     * 保存草稿
     */
    @PostMapping("/report/draft")
    public Result<?> saveDraft(@RequestBody ExperimentReport report) {
        report.setStudentId(getCurrentUserId());
        return reportService.saveDraft(report);
    }

    /**
     * 查看我的报告（针对某个任务）
     */
    @GetMapping("/report/task/{taskId}")
    public Result<?> getMyReport(@PathVariable Long taskId) {
        Long studentId = getCurrentUserId();
        ExperimentReport report = reportService.getStudentReport(taskId, studentId);
        if (report != null) {
            java.util.List<ReportAttachment> attachments = reportService.getAttachments(report.getId());
            java.util.List<ReportScoreDetail> scoreDetails = reportService.getScoreDetails(report.getId());
            java.util.HashMap<String, Object> result = new java.util.HashMap<>();
            result.put("report", report);
            result.put("attachments", attachments);
            result.put("scoreDetails", scoreDetails);
            return Result.success(result);
        }
        return Result.success(null);
    }

    /**
     * 查看我的所有报告（历史记录）
     */
    @GetMapping("/reports")
    public Result<?> getMyReports(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reportService.getStudentReports(
                new Page<>(page, size), getCurrentUserId()));
    }

    // =================== 文件上传 ===================

    /**
     * 上传附件
     */
    @PostMapping("/upload")
    public Result<?> uploadFile(@RequestParam("file") MultipartFile file,
                                 @RequestParam(required = false) Long reportId) {
        return fileService.uploadFile(file, reportId);
    }

    /**
     * 删除附件
     */
    @DeleteMapping("/attachment/{id}")
    public Result<?> deleteAttachment(@PathVariable Long id) {
        return fileService.deleteAttachment(id);
    }
}
