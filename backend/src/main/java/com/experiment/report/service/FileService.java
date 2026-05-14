package com.experiment.report.service;

import com.experiment.report.common.Result;
import com.experiment.report.entity.ReportAttachment;
import com.experiment.report.mapper.ReportAttachmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Service
public class FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.allowed-types}")
    private String allowedTypes;

    @Autowired
    private ReportAttachmentMapper attachmentMapper;

    /**
     * 上传文件
     */
    public Result<?> uploadFile(MultipartFile file, Long reportId) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        // 验证文件类型
        if (!allowedTypes.contains(suffix)) {
            return Result.error("不支持的文件类型：" + suffix);
        }

        // 生成存储路径：uploads/2024/03/uuid.pdf
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
        String relativePath = datePath + "/" + fileName;
        String fullPath = uploadPath + relativePath;

        // 创建目录
        File destFile = new File(fullPath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }

        // 保存附件记录
        if (reportId != null) {
            ReportAttachment attachment = new ReportAttachment();
            attachment.setReportId(reportId);
            attachment.setFileName(originalFilename);
            attachment.setFilePath(relativePath);
            attachment.setFileSize(file.getSize());
            attachment.setFileType(suffix);
            attachment.setUploadTime(LocalDateTime.now());
            attachmentMapper.insert(attachment);
        }

        // 返回文件路径
        return Result.success("上传成功", relativePath);
    }

    /**
     * 删除附件
     */
    public Result<?> deleteAttachment(Long attachmentId) {
        ReportAttachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment != null) {
            // 删除物理文件
            File file = new File(uploadPath + attachment.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            attachmentMapper.deleteById(attachmentId);
        }
        return Result.success("删除成功");
    }
}
