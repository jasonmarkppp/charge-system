package com.experiment.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学科实体
 */
@Data
@TableName("sys_subject")
public class SysSubject {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String subjectName;

    private String subjectCode;

    private String description;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
