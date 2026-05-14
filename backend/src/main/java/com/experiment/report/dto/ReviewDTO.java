package com.experiment.report.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 教师批阅报告DTO
 */
@Data
public class ReviewDTO {

    /** 报告ID */
    private Long reportId;

    /** 总评语 */
    private String comment;

    /** 各维度评分 */
    private List<ScoreItem> scores;

    /** 操作类型：APPROVE-通过, REJECT-退回 */
    private String action;

    @Data
    public static class ScoreItem {
        /** 评分标准ID */
        private Long criteriaId;
        /** 得分 */
        private BigDecimal score;
        /** 备注 */
        private String remark;
    }
}
