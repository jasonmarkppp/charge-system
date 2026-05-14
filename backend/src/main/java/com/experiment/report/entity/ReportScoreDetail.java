package com.experiment.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 报告评分明细
 */
@Data
@TableName("report_score_detail")
public class ReportScoreDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reportId;

    private Long criteriaId;

    private BigDecimal score;

    private String remark;

    private LocalDateTime createTime;

    /** 评分维度名称（非数据库字段） */
    @TableField(exist = false)
    private String criteriaName;

    /** 该维度满分（非数据库字段） */
    @TableField(exist = false)
    private BigDecimal maxScore;
}
