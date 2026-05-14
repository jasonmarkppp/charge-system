package com.experiment.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 实验任务实体（教师发布的实验作业）
 */
@Data
@TableName("experiment_task")
public class ExperimentTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskName;

    private Long projectId;

    private Long teacherId;

    private Long classId;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime deadline;

    /** 状态：0关闭 1进行中 2已截止 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /** 实验项目名称（非数据库字段） */
    @TableField(exist = false)
    private String projectName;

    /** 班级名称（非数据库字段） */
    @TableField(exist = false)
    private String className;

    /** 教师姓名（非数据库字段） */
    @TableField(exist = false)
    private String teacherName;

    /** 提交人数（非数据库字段） */
    @TableField(exist = false)
    private Integer submitCount;

    /** 班级总人数（非数据库字段） */
    @TableField(exist = false)
    private Integer totalStudentCount;
}
