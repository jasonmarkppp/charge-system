package com.experiment.report.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.experiment.report.common.Result;
import com.experiment.report.entity.ExperimentReport;
import com.experiment.report.entity.ScoringCriteria;
import com.experiment.report.mapper.ExperimentReportMapper;
import com.experiment.report.mapper.ScoringCriteriaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实验报告AI分析服务
 * 调用大语言模型API对学生实验报告进行智能分析
 */
@Service
public class ReportAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(ReportAnalysisService.class);

    @Value("${ai.api.enabled:false}")
    private boolean aiEnabled;

    @Value("${ai.api.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${ai.api.api-key:}")
    private String apiKey;

    @Value("${ai.api.model:deepseek-chat}")
    private String model;

    @Value("${ai.api.timeout:60000}")
    private int timeout;

    @Value("${ai.api.max-tokens:2000}")
    private int maxTokens;

    @Autowired
    private ExperimentReportMapper reportMapper;

    @Autowired
    private ScoringCriteriaMapper criteriaMapper;

    /**
     * 一键分析实验报告
     */
    public Result<?> analyzeReport(Long reportId, Long projectId) {
        // 1. 获取报告内容
        ExperimentReport report = reportMapper.selectById(reportId);
        if (report == null) {
            return Result.error("报告不存在");
        }

        // 2. 获取评分标准
        LambdaQueryWrapper<ScoringCriteria> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.isNull(ScoringCriteria::getProjectId)
                .or().eq(ScoringCriteria::getProjectId, projectId));
        wrapper.orderByAsc(ScoringCriteria::getSortOrder);
        List<ScoringCriteria> criteriaList = criteriaMapper.selectList(wrapper);

        if (!aiEnabled || apiKey == null || apiKey.isEmpty() || "your-api-key-here".equals(apiKey)) {
            // AI未配置时，使用本地规则分析
            log.info("AI未配置，使用本地分析。enabled={}, apiKey={}", aiEnabled, apiKey != null ? apiKey.substring(0, Math.min(8, apiKey.length())) + "***" : "null");
            return Result.success(localAnalysis(report, criteriaList));
        }

        // 3. 调用AI接口分析
        try {
            log.info("开始调用AI分析，URL={}/chat/completions, model={}", baseUrl, model);
            Map<String, Object> analysisResult = callAiAnalysis(report, criteriaList);
            return Result.success(analysisResult);
        } catch (Exception e) {
            log.error("AI分析失败，错误信息: {}", e.getMessage(), e);
            Map<String, Object> result = localAnalysis(report, criteriaList);
            result.put("notice", "AI服务调用失败(" + e.getMessage() + ")，已使用本地规则分析");
            return Result.success(result);
        }
    }

    /**
     * 调用AI大模型分析报告
     */
    private Map<String, Object> callAiAnalysis(ExperimentReport report, List<ScoringCriteria> criteriaList) {
        // 构建评分维度描述
        StringBuilder criteriaDesc = new StringBuilder();
        for (ScoringCriteria c : criteriaList) {
            criteriaDesc.append(String.format("- %s（满分%.1f分）：%s\n",
                    c.getCriteriaName(), c.getMaxScore(),
                    c.getDescription() != null ? c.getDescription() : ""));
        }

        // 截断过长内容，避免 prompt 过大导致超时
        String reportContent = truncate(nullToEmpty(report.getContent()), 1500);
        String reportData = truncate(nullToEmpty(report.getExperimentData()), 800);
        String reportConclusion = truncate(nullToEmpty(report.getConclusion()), 500);

        // 构建提示词
        String prompt = String.format(
                "你是一位中小学实验课程的资深教师，请对以下学生实验报告进行专业评价分析。\n\n" +
                "【报告标题】%s\n\n" +
                "【实验报告正文】\n%s\n\n" +
                "【实验数据】\n%s\n\n" +
                "【实验结论】\n%s\n\n" +
                "【评分维度】\n%s\n\n" +
                "请按照以下JSON格式返回分析结果（只返回JSON，不要其他内容）：\n" +
                "{\n" +
                "  \"scores\": [\n" +
                "    {\"criteriaName\": \"维度名称\", \"suggestedScore\": 分数, \"reason\": \"打分理由\"}\n" +
                "  ],\n" +
                "  \"overallComment\": \"总体评语（100字左右）\",\n" +
                "  \"strengths\": [\"优点1\", \"优点2\"],\n" +
                "  \"improvements\": [\"改进建议1\", \"改进建议2\"],\n" +
                "  \"totalScore\": 建议总分\n" +
                "}",
                nullToEmpty(report.getTitle()),
                reportContent,
                reportData,
                reportConclusion,
                criteriaDesc.toString()
        );

        // 构建请求体 (OpenAI兼容格式)
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", model);
        requestBody.set("max_tokens", maxTokens);
        requestBody.set("temperature", 0.3);

        JSONArray messages = new JSONArray();
        JSONObject systemMsg = new JSONObject();
        systemMsg.set("role", "system");
        systemMsg.set("content", "你是一位专业的中小学实验报告评审专家，擅长对学生实验报告进行分析评价。请严格按照JSON格式返回结果。");
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", prompt);
        messages.add(userMsg);

        requestBody.set("messages", messages);

        // 发送请求（豆包/火山方舟使用 /chat/completions 路径）
        String url = baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions";
        log.info("发送AI请求: URL={}, model={}, body长度={}", url, model, requestBody.toString().length());
        String responseStr = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .setConnectionTimeout(15000)
                .setReadTimeout(timeout)
                .execute()
                .body();
        log.info("AI响应: {}", responseStr != null && responseStr.length() > 200 ? responseStr.substring(0, 200) + "..." : responseStr);

        // 解析响应
        JSONObject response = JSONUtil.parseObj(responseStr);
        String content = response.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getStr("content");

        // 提取JSON部分（去除可能的markdown代码块标记）
        content = content.trim();
        if (content.startsWith("```json")) {
            content = content.substring(7);
        }
        if (content.startsWith("```")) {
            content = content.substring(3);
        }
        if (content.endsWith("```")) {
            content = content.substring(0, content.length() - 3);
        }
        content = content.trim();

        Map<String, Object> result = new HashMap<>();
        result.put("analysis", JSONUtil.parseObj(content));
        result.put("source", "ai");
        return result;
    }

    /**
     * 本地规则分析（无AI接口时的fallback方案）
     * 基于简单的文本长度、关键词等规则进行打分建议
     */
    private Map<String, Object> localAnalysis(ExperimentReport report, List<ScoringCriteria> criteriaList) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> scores = new ArrayList<>();
        double totalScore = 0;
        List<String> strengths = new ArrayList<>();
        List<String> improvements = new ArrayList<>();

        String content = nullToEmpty(report.getContent());
        String data = nullToEmpty(report.getExperimentData());
        String conclusion = nullToEmpty(report.getConclusion());

        for (ScoringCriteria criteria : criteriaList) {
            Map<String, Object> scoreItem = new HashMap<>();
            scoreItem.put("criteriaName", criteria.getCriteriaName());
            double maxScore = criteria.getMaxScore().doubleValue();
            double suggestedScore = 0;
            String reason = "";

            String name = criteria.getCriteriaName();
            if (name.contains("目的")) {
                // 分析实验目的描述
                if (content.length() > 50) {
                    suggestedScore = maxScore * 0.8;
                    reason = "报告正文内容较充实，实验目的表述基本清晰";
                } else if (content.length() > 20) {
                    suggestedScore = maxScore * 0.6;
                    reason = "内容偏少，实验目的表述不够完整";
                } else {
                    suggestedScore = maxScore * 0.3;
                    reason = "内容过少，缺乏对实验目的的明确阐述";
                    improvements.add("建议补充实验目的和意义的描述");
                }
            } else if (name.contains("步骤") || name.contains("过程")) {
                if (content.length() > 200) {
                    suggestedScore = maxScore * 0.85;
                    reason = "实验步骤描述较为详细，条理清晰";
                    strengths.add("实验步骤描述详细");
                } else if (content.length() > 80) {
                    suggestedScore = maxScore * 0.65;
                    reason = "步骤有一定描述，但不够详细";
                } else {
                    suggestedScore = maxScore * 0.4;
                    reason = "实验步骤描述过于简略";
                    improvements.add("建议详细描述每一步实验操作");
                }
            } else if (name.contains("数据")) {
                if (data.length() > 100) {
                    suggestedScore = maxScore * 0.85;
                    reason = "实验数据记录较为完整";
                    strengths.add("数据记录较完整");
                } else if (data.length() > 30) {
                    suggestedScore = maxScore * 0.6;
                    reason = "有实验数据记录，但不够完整";
                } else {
                    suggestedScore = maxScore * 0.3;
                    reason = "实验数据记录严重不足";
                    improvements.add("需要补充详细的实验数据记录");
                }
            } else if (name.contains("结论")) {
                if (conclusion.length() > 80) {
                    suggestedScore = maxScore * 0.8;
                    reason = "实验结论表述较为完整，有分析思考";
                    strengths.add("结论分析较深入");
                } else if (conclusion.length() > 30) {
                    suggestedScore = maxScore * 0.6;
                    reason = "有实验结论，但分析深度不够";
                } else {
                    suggestedScore = maxScore * 0.35;
                    reason = "结论过于简短，缺乏深入分析";
                    improvements.add("建议对实验结果进行更深入的分析和总结");
                }
            } else if (name.contains("格式") || name.contains("规范")) {
                boolean hasTitle = report.getTitle() != null && !report.getTitle().isEmpty();
                boolean hasContent = content.length() > 50;
                boolean hasData = data.length() > 10;
                boolean hasConclusion = conclusion.length() > 10;
                int completeness = (hasTitle ? 1 : 0) + (hasContent ? 1 : 0) + (hasData ? 1 : 0) + (hasConclusion ? 1 : 0);
                suggestedScore = maxScore * (completeness / 4.0) * 0.9;
                reason = String.format("报告结构完整度：%d/4（标题、正文、数据、结论）", completeness);
                if (completeness >= 3) {
                    strengths.add("报告结构完整");
                } else {
                    improvements.add("报告结构不够完整，部分章节缺失");
                }
            } else if (name.contains("创新") || name.contains("思考")) {
                // 创新思考给一个中等偏下分数，需要教师主观判断
                suggestedScore = maxScore * 0.5;
                reason = "创新思考维度建议教师根据报告内容主观评判";
            } else {
                // 默认按内容长度给出中等分
                int totalLen = content.length() + data.length() + conclusion.length();
                if (totalLen > 300) {
                    suggestedScore = maxScore * 0.7;
                } else if (totalLen > 100) {
                    suggestedScore = maxScore * 0.55;
                } else {
                    suggestedScore = maxScore * 0.4;
                }
                reason = "根据报告整体内容充实度评估";
            }

            // 四舍五入到0.5
            suggestedScore = Math.round(suggestedScore * 2) / 2.0;
            scoreItem.put("suggestedScore", suggestedScore);
            scoreItem.put("reason", reason);
            totalScore += suggestedScore;
            scores.add(scoreItem);
        }

        // 构建总体评语
        StringBuilder overallComment = new StringBuilder();
        if (strengths.isEmpty()) {
            overallComment.append("报告内容较为基础。");
        } else {
            overallComment.append("该报告");
            for (int i = 0; i < strengths.size(); i++) {
                if (i > 0) overallComment.append("，");
                overallComment.append(strengths.get(i));
            }
            overallComment.append("。");
        }
        if (!improvements.isEmpty()) {
            overallComment.append("建议：");
            for (int i = 0; i < improvements.size(); i++) {
                if (i > 0) overallComment.append("；");
                overallComment.append(improvements.get(i));
            }
            overallComment.append("。");
        }

        Map<String, Object> analysis = new HashMap<>();
        analysis.put("scores", scores);
        analysis.put("overallComment", overallComment.toString());
        analysis.put("strengths", strengths);
        analysis.put("improvements", improvements);
        analysis.put("totalScore", Math.round(totalScore * 10) / 10.0);

        result.put("analysis", analysis);
        result.put("source", "local");
        return result;
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private String truncate(String s, int maxLen) {
        if (s == null || s.length() <= maxLen) {
            return s == null ? "" : s;
        }
        return s.substring(0, maxLen) + "...(已截断)";
    }
}
