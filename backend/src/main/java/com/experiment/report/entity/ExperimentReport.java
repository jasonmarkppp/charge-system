package com.experiment.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实验报告实体
 */
@Data
@TableName("experiment_report")
public class ExperimentReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;

    private Long studentId;

    private String title;

    private String content;

    private String experimentData;

    private String conclusion;

    private LocalDateTime submitTime;

    /** 状态：0草稿 1已提交 2已批阅 3退回重做 */
    private Integer status;

    private BigDecimal totalScore;

    private String comment;

    private LocalDateTime reviewTime;

    private Long reviewTeacherId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /** 学生姓名（非数据库字段） */
    @TableField(exist = false)
    private String studentName;

    /** 学号（非数据库字段） */
    @TableField(exist = false)
    private String studentNo;

    /** 任务名称（非数据库字段） */
    @TableField(exist = false)
    private String taskName;
}
