package com.experiment.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 报告附件实体
 */
@Data
@TableName("report_attachment")
public class ReportAttachment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reportId;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private String fileType;

    private LocalDateTime uploadTime;
}
