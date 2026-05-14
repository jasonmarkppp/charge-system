package com.experiment.report.vo;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 统计数据VO
 */
@Data
public class StatisticsVO {

    /** 学生总数 */
    private Long totalStudents;

    /** 教师总数 */
    private Long totalTeachers;

    /** 实验任务总数 */
    private Long totalTasks;

    /** 报告总数 */
    private Long totalReports;

    /** 已批阅数 */
    private Long reviewedReports;

    /** 平均分 */
    private Double avgScore;

    /** 各学科实验数量 */
    private List<Map<String, Object>> subjectStats;

    /** 各班级提交率 */
    private List<Map<String, Object>> classSubmitRate;

    /** 分数分布 */
    private List<Map<String, Object>> scoreDistribution;
}
