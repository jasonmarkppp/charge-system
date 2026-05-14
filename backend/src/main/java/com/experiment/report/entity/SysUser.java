package com.experiment.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统用户实体
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String realName;

    /** 角色：ADMIN/TEACHER/STUDENT */
    private String role;

    private String phone;

    private String email;

    private String avatar;

    /** 性别：0未知 1男 2女 */
    private Integer gender;

    /** 状态：0禁用 1启用 */
    private Integer status;

    /** 班级ID（学生专用） */
    private Long classId;

    /** 学号（学生专用） */
    private String studentNo;

    /** 工号（教师专用） */
    private String teacherNo;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
