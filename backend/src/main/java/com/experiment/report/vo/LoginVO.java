package com.experiment.report.vo;

import lombok.Data;

/**
 * 登录返回VO
 */
@Data
public class LoginVO {

    private Long userId;
    private String username;
    private String realName;
    private String role;
    private String avatar;
    private String token;
}
