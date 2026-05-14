package com.experiment.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.experiment.report.common.Result;
import com.experiment.report.dto.ReviewDTO;
import com.experiment.report.entity.*;
import com.experiment.report.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 实验报告服务
 */
@Service
public class ReportService {

    @Autowired
    private ExperimentReportMapper reportMapper;

    @Autowired
    private ReportScoreDetailMapper scoreDetailMapper;

    @Autowired
    private ReportAttachmentMapper attachmentMapper;

    @Autowired
    private ExperimentTaskMapper taskMapper;

    @Autowired
    private ScoringCriteriaMapper criteriaMapper;

    /**
     * 学生提交报告
     */
    @Transactional
    public Result<?> submitReport(ExperimentReport report) {
        report.setStatus(1); // 已提交
        report.setSubmitTime(LocalDateTime.now());

        if (report.getId() != null) {
            // 更新已有报告
            reportMapper.updateById(report);
        } else {
            // 新增报告
            reportMapper.insert(report);
        }
        return Result.success("提交成功");
    }

    /**
     * 保存草稿
     */
    public Result<?> saveDraft(ExperimentReport report) {
        report.setStatus(0); // 草稿
        if (report.getId() != null) {
            reportMapper.updateById(report);
        } else {
            reportMapper.insert(report);
        }
        return Result.success("草稿已保存");
    }

    /**
     * 教师批阅报告
     */
    @Transactional
    public Result<?> reviewReport(ReviewDTO dto, Long teacherId) {
        ExperimentReport report = reportMapper.selectById(dto.getReportId());
        if (report == null) {
            return Result.error("报告不存在");
        }

        if ("REJECT".equals(dto.getAction())) {
            // 退回重做
            report.setStatus(3);
            report.setComment(dto.getComment());
            report.setReviewTeacherId(teacherId);
            report.setReviewTime(LocalDateTime.now());
            reportMapper.updateById(report);
            return Result.success("已退回");
        }

        // 保存各维度评分
        BigDecimal totalScore = BigDecimal.ZERO;
        if (dto.getScores() != null) {
            // 先删除旧评分
            scoreDetailMapper.delete(new LambdaQueryWrapper<ReportScoreDetail>()
                    .eq(ReportScoreDetail::getReportId, dto.getReportId()));

            for (ReviewDTO.ScoreItem item : dto.getScores()) {
                ReportScoreDetail detail = new ReportScoreDetail();
                detail.setReportId(dto.getReportId());
                detail.setCriteriaId(item.getCriteriaId());
                detail.setScore(item.getScore());
                detail.setRemark(item.getRemark());
                detail.setCreateTime(LocalDateTime.now());
                scoreDetailMapper.insert(detail);
                totalScore = totalScore.add(item.getScore());
            }
        }

        // 更新报告状态
        report.setStatus(2); // 已批阅
        report.setTotalScore(totalScore);
        report.setComment(dto.getComment());
        report.setReviewTeacherId(teacherId);
        report.setReviewTime(LocalDateTime.now());
        reportMapper.updateById(report);

        return Result.success("批阅完成");
    }

    /**
     * 获取报告详情（含评分明细和附件）
     */
    public ExperimentReport getReportDetail(Long reportId) {
        ExperimentReport report = reportMapper.selectById(reportId);
        return report;
    }

    /**
     * 获取报告评分明细
     */
    public List<ReportScoreDetail> getScoreDetails(Long reportId) {
        return scoreDetailMapper.selectList(
                new LambdaQueryWrapper<ReportScoreDetail>()
                        .eq(ReportScoreDetail::getReportId, reportId));
    }

    /**
     * 获取报告附件
     */
    public List<ReportAttachment> getAttachments(Long reportId) {
        return attachmentMapper.selectList(
                new LambdaQueryWrapper<ReportAttachment>()
                        .eq(ReportAttachment::getReportId, reportId));
    }

    /**
     * 查询学生的某个任务报告
     */
    public ExperimentReport getStudentReport(Long taskId, Long studentId) {
        return reportMapper.selectOne(
                new LambdaQueryWrapper<ExperimentReport>()
                        .eq(ExperimentReport::getTaskId, taskId)
                        .eq(ExperimentReport::getStudentId, studentId));
    }

    /**
     * 教师查看某任务的所有报告
     */
    public IPage<ExperimentReport> getTaskReports(Page<ExperimentReport> page, Long taskId, Integer status) {
        LambdaQueryWrapper<ExperimentReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExperimentReport::getTaskId, taskId);
        if (status != null) {
            wrapper.eq(ExperimentReport::getStatus, status);
        }
        wrapper.orderByDesc(ExperimentReport::getSubmitTime);
        return reportMapper.selectPage(page, wrapper);
    }

    /**
     * 学生查看自己的所有报告
     */
    public IPage<ExperimentReport> getStudentReports(Page<ExperimentReport> page, Long studentId) {
        LambdaQueryWrapper<ExperimentReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExperimentReport::getStudentId, studentId);
        wrapper.orderByDesc(ExperimentReport::getCreateTime);
        return reportMapper.selectPage(page, wrapper);
    }

    /**
     * 获取评分标准
     */
    public List<ScoringCriteria> getCriteriaList(Long projectId) {
        LambdaQueryWrapper<ScoringCriteria> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.isNull(ScoringCriteria::getProjectId)
                .or().eq(ScoringCriteria::getProjectId, projectId));
        wrapper.orderByAsc(ScoringCriteria::getSortOrder);
        return criteriaMapper.selectList(wrapper);
    }
}
