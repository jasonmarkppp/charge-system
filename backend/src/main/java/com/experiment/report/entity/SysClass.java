package com.experiment.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 班级实体
 */
@Data
@TableName("sys_class")
public class SysClass {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String className;

    private Long gradeId;

    private Long teacherId;

    private Integer studentCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /** 年级名称（非数据库字段） */
    @TableField(exist = false)
    private String gradeName;

    /** 班主任姓名（非数据库字段） */
    @TableField(exist = false)
    private String teacherName;
}
