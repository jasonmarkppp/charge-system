package com.experiment.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实验项目实体
 */
@Data
@TableName("experiment_project")
public class ExperimentProject {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String projectName;

    private Long subjectId;

    private String description;

    private String objectives;

    private String requirements;

    /** 难度：1简单 2中等 3困难 */
    private Integer difficulty;

    private Long createUserId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /** 学科名称（非数据库字段） */
    @TableField(exist = false)
    private String subjectName;
}
