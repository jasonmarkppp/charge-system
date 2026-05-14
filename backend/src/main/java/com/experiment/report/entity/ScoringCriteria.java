package com.experiment.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评分标准实体
 */
@Data
@TableName("scoring_criteria")
public class ScoringCriteria {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String criteriaName;

    private BigDecimal maxScore;

    private BigDecimal weight;

    private String description;

    /** 关联实验项目ID（NULL表示通用标准） */
    private Long projectId;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
