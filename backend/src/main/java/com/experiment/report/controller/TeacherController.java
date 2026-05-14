package com.experiment.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.experiment.report.common.Result;
import com.experiment.report.dto.ReviewDTO;
import com.experiment.report.entity.*;
import com.experiment.report.mapper.*;
import com.experiment.report.service.FileService;
import com.experiment.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    @Autowired
    private ExperimentTaskMapper taskMapper;
    @Autowired
    private ExperimentProjectMapper projectMapper;
    @Autowired
    private SysClassMapper classMapper;
    @Autowired
    private ScoringCriteriaMapper criteriaMapper;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ExperimentReportMapper reportMapper;
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private com.experiment.report.service.ReportAnalysisService reportAnalysisService;

    private Long getCurrentUserId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // =================== 实验任务管理 ===================

    /**
     * 获取我发布的实验任务
     */
    @GetMapping("/tasks")
    public Result<?> getMyTasks(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer size) {
        Long teacherId = getCurrentUserId();
        LambdaQueryWrapper<ExperimentTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExperimentTask::getTeacherId, teacherId);
        wrapper.orderByDesc(ExperimentTask::getCreateTime);
        return Result.success(taskMapper.selectPage(new Page<>(page, size), wrapper));
    }

    /**
     * 发布实验任务
     */
    @PostMapping("/task")
    public Result<?> publishTask(@RequestBody ExperimentTask task) {
        task.setTeacherId(getCurrentUserId());
        task.setStatus(1);
        taskMapper.insert(task);
        return Result.success("发布成功");
    }

    /**
     * 修改实验任务
     */
    @PutMapping("/task")
    public Result<?> updateTask(@RequestBody ExperimentTask task) {
        taskMapper.updateById(task);
        return Result.success("修改成功");
    }

    /**
     * 删除实验任务
     */
    @DeleteMapping("/task/{id}")
    public Result<?> deleteTask(@PathVariable Long id) {
        taskMapper.deleteById(id);
        return Result.success("删除成功");
    }

    // =================== 报告批阅 ===================

    /**
     * 查看某任务的学生报告列表
     */
    @GetMapping("/task/{taskId}/reports")
    public Result<?> getTaskReports(@PathVariable Long taskId,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) Integer status) {
        return Result.success(reportService.getTaskReports(new Page<>(page, size), taskId, status));
    }

    /**
     * 查看报告详情
     */
    @GetMapping("/report/{reportId}")
    public Result<?> getReportDetail(@PathVariable Long reportId) {
        ExperimentReport report = reportService.getReportDetail(reportId);
        java.util.List<ReportScoreDetail> details = reportService.getScoreDetails(reportId);
        java.util.List<ReportAttachment> attachments = reportService.getAttachments(reportId);
        java.util.HashMap<String, Object> result = new java.util.HashMap<>();
        result.put("report", report);
        result.put("scoreDetails", details);
        result.put("attachments", attachments);
        return Result.success(result);
    }

    /**
     * 批阅报告
     */
    @PostMapping("/report/review")
    public Result<?> reviewReport(@RequestBody ReviewDTO dto) {
        return reportService.reviewReport(dto, getCurrentUserId());
    }

    /**
     * 获取评分标准
     */
    @GetMapping("/criteria/{projectId}")
    public Result<?> getCriteria(@PathVariable Long projectId) {
        return Result.success(reportService.getCriteriaList(projectId));
    }

    // =================== 辅助接口 ===================

    /**
     * 一键分析实验报告（AI智能分析）
     */
    @PostMapping("/report/analyze/{reportId}")
    public Result<?> analyzeReport(@PathVariable Long reportId,
                                    @RequestParam(defaultValue = "0") Long projectId) {
        return reportAnalysisService.analyzeReport(reportId, projectId);
    }

    /**
     * 获取所有实验项目（下拉选择用）
     */
    @GetMapping("/projects")
    public Result<?> getProjects() {
        return Result.success(projectMapper.selectList(
                new LambdaQueryWrapper<ExperimentProject>().orderByDesc(ExperimentProject::getCreateTime)));
    }

    /**
     * 获取我教的班级
     */
    @GetMapping("/my-classes")
    public Result<?> getMyClasses() {
        // 简化处理：返回所有班级（实际应根据teacher_class关联表查询）
        return Result.success(classMapper.selectList(new LambdaQueryWrapper<SysClass>()));
    }

    /**
     * 班级统计
     */
    @GetMapping("/statistics/{taskId}")
    public Result<?> getTaskStatistics(@PathVariable Long taskId) {
        java.util.HashMap<String, Object> stats = new java.util.HashMap<>();
        stats.put("total", reportMapper.selectCount(
                new LambdaQueryWrapper<ExperimentReport>().eq(ExperimentReport::getTaskId, taskId)));
        stats.put("submitted", reportMapper.selectCount(
                new LambdaQueryWrapper<ExperimentReport>()
                        .eq(ExperimentReport::getTaskId, taskId)
                        .ge(ExperimentReport::getStatus, 1)));
        stats.put("reviewed", reportMapper.selectCount(
                new LambdaQueryWrapper<ExperimentReport>()
                        .eq(ExperimentReport::getTaskId, taskId)
                        .eq(ExperimentReport::getStatus, 2)));
        return Result.success(stats);
    }
}
