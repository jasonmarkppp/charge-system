-- ======================================================
-- 中小学实验报告评价系统 数据库初始化脚本
-- 数据库：MySQL 8.0+
-- 字符集：utf8mb4
-- ======================================================

CREATE DATABASE IF NOT EXISTS experiment_report DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE experiment_report;

-- ----------------------------
-- 1. 用户表（管理员、教师、学生统一用户表）
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    role VARCHAR(20) NOT NULL COMMENT '角色：ADMIN/TEACHER/STUDENT',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    class_id BIGINT DEFAULT NULL COMMENT '班级ID（学生专用）',
    student_no VARCHAR(50) DEFAULT NULL COMMENT '学号（学生专用）',
    teacher_no VARCHAR(50) DEFAULT NULL COMMENT '工号（教师专用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0正常 1删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 2. 年级表
-- ----------------------------
DROP TABLE IF EXISTS sys_grade;
CREATE TABLE sys_grade (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '年级ID',
    grade_name VARCHAR(50) NOT NULL COMMENT '年级名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年级表';

-- ----------------------------
-- 3. 班级表
-- ----------------------------
DROP TABLE IF EXISTS sys_class;
CREATE TABLE sys_class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    grade_id BIGINT NOT NULL COMMENT '所属年级ID',
    teacher_id BIGINT DEFAULT NULL COMMENT '班主任教师ID',
    student_count INT DEFAULT 0 COMMENT '学生人数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- ----------------------------
-- 4. 学科表
-- ----------------------------
DROP TABLE IF EXISTS sys_subject;
CREATE TABLE sys_subject (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学科ID',
    subject_name VARCHAR(50) NOT NULL COMMENT '学科名称',
    subject_code VARCHAR(20) DEFAULT NULL COMMENT '学科编码',
    description VARCHAR(255) DEFAULT NULL COMMENT '学科描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学科表';

-- ----------------------------
-- 5. 教师-班级关联表（一个教师可教多个班级）
-- ----------------------------
DROP TABLE IF EXISTS teacher_class;
CREATE TABLE teacher_class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    subject_id BIGINT NOT NULL COMMENT '所教学科ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师班级关联表';

-- ----------------------------
-- 6. 实验项目表
-- ----------------------------
DROP TABLE IF EXISTS experiment_project;
CREATE TABLE experiment_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '实验项目ID',
    project_name VARCHAR(100) NOT NULL COMMENT '实验名称',
    subject_id BIGINT NOT NULL COMMENT '所属学科ID',
    description TEXT COMMENT '实验描述',
    objectives TEXT COMMENT '实验目标',
    requirements TEXT COMMENT '实验要求',
    difficulty TINYINT DEFAULT 1 COMMENT '难度：1简单 2中等 3困难',
    create_user_id BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验项目表';

-- ----------------------------
-- 7. 评分标准表（评分维度）
-- ----------------------------
DROP TABLE IF EXISTS scoring_criteria;
CREATE TABLE scoring_criteria (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评分标准ID',
    criteria_name VARCHAR(100) NOT NULL COMMENT '维度名称（如：实验目的、实验过程）',
    max_score DECIMAL(5,1) NOT NULL COMMENT '该维度满分',
    weight DECIMAL(3,2) DEFAULT 1.00 COMMENT '权重',
    description VARCHAR(255) DEFAULT NULL COMMENT '评分说明',
    project_id BIGINT DEFAULT NULL COMMENT '关联实验项目ID（NULL表示通用标准）',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评分标准表';

-- ----------------------------
-- 8. 实验任务表（教师发布的实验作业）
-- ----------------------------
DROP TABLE IF EXISTS experiment_task;
CREATE TABLE experiment_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    project_id BIGINT NOT NULL COMMENT '关联实验项目ID',
    teacher_id BIGINT NOT NULL COMMENT '发布教师ID',
    class_id BIGINT NOT NULL COMMENT '目标班级ID',
    description TEXT COMMENT '任务说明',
    start_time DATETIME DEFAULT NULL COMMENT '开始时间',
    deadline DATETIME NOT NULL COMMENT '截止时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0关闭 1进行中 2已截止',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验任务表';

-- ----------------------------
-- 9. 实验报告表（学生提交的报告）
-- ----------------------------
DROP TABLE IF EXISTS experiment_report;
CREATE TABLE experiment_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报告ID',
    task_id BIGINT NOT NULL COMMENT '关联任务ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    title VARCHAR(200) DEFAULT NULL COMMENT '报告标题',
    content TEXT COMMENT '报告正文内容',
    experiment_data TEXT COMMENT '实验数据记录',
    conclusion TEXT COMMENT '实验结论',
    submit_time DATETIME DEFAULT NULL COMMENT '提交时间',
    status TINYINT DEFAULT 0 COMMENT '状态：0草稿 1已提交 2已批阅 3退回重做',
    total_score DECIMAL(5,1) DEFAULT NULL COMMENT '总分',
    comment TEXT COMMENT '教师评语',
    review_time DATETIME DEFAULT NULL COMMENT '批阅时间',
    review_teacher_id BIGINT DEFAULT NULL COMMENT '批阅教师ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验报告表';

-- ----------------------------
-- 10. 报告评分明细表（按维度打分）
-- ----------------------------
DROP TABLE IF EXISTS report_score_detail;
CREATE TABLE report_score_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    report_id BIGINT NOT NULL COMMENT '报告ID',
    criteria_id BIGINT NOT NULL COMMENT '评分标准ID',
    score DECIMAL(5,1) NOT NULL COMMENT '得分',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告评分明细表';

-- ----------------------------
-- 11. 报告附件表
-- ----------------------------
DROP TABLE IF EXISTS report_attachment;
CREATE TABLE report_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '附件ID',
    report_id BIGINT NOT NULL COMMENT '报告ID',
    file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '存储路径',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    file_type VARCHAR(50) DEFAULT NULL COMMENT '文件类型(pdf/doc/docx/png/jpg)',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报告附件表';

-- ----------------------------
-- 12. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT DEFAULT NULL COMMENT '操作用户ID',
    username VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    operation VARCHAR(100) DEFAULT NULL COMMENT '操作描述',
    method VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ======================================================
-- 初始数据
-- ======================================================

-- 管理员账号（密码：123456，BCrypt加密）
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$GlQG2MyL9EkL0mPSmzkQB.oHVcJWW70n2XcAPZ7h36CDPGQ/9s5bO', '系统管理员', 'ADMIN', 1);

-- 教师账号（密码：123456）
INSERT INTO sys_user (username, password, real_name, role, phone, gender, teacher_no, status) VALUES
('teacher01', '$2a$10$GlQG2MyL9EkL0mPSmzkQB.oHVcJWW70n2XcAPZ7h36CDPGQ/9s5bO', '张老师', 'TEACHER', '13800138001', 1, 'T2024001', 1),
('teacher02', '$2a$10$GlQG2MyL9EkL0mPSmzkQB.oHVcJWW70n2XcAPZ7h36CDPGQ/9s5bO', '李老师', 'TEACHER', '13800138002', 2, 'T2024002', 1);

-- 年级数据
INSERT INTO sys_grade (grade_name, sort_order) VALUES
('七年级', 1), ('八年级', 2), ('九年级', 3),
('高一', 4), ('高二', 5), ('高三', 6);

-- 班级数据
INSERT INTO sys_class (class_name, grade_id, teacher_id) VALUES
('七年级一班', 1, 2), ('七年级二班', 1, 2),
('八年级一班', 2, 3), ('八年级二班', 2, 3);

-- 学生账号（密码：123456）
INSERT INTO sys_user (username, password, real_name, role, gender, class_id, student_no, status) VALUES
('student01', '$2a$10$GlQG2MyL9EkL0mPSmzkQB.oHVcJWW70n2XcAPZ7h36CDPGQ/9s5bO', '王小明', 'STUDENT', 1, 1, 'S2024001', 1),
('student02', '$2a$10$GlQG2MyL9EkL0mPSmzkQB.oHVcJWW70n2XcAPZ7h36CDPGQ/9s5bO', '李小红', 'STUDENT', 2, 1, 'S2024002', 1),
('student03', '$2a$10$GlQG2MyL9EkL0mPSmzkQB.oHVcJWW70n2XcAPZ7h36CDPGQ/9s5bO', '张小华', 'STUDENT', 1, 2, 'S2024003', 1);

-- 学科数据
INSERT INTO sys_subject (subject_name, subject_code, description, sort_order) VALUES
('信息技术', 'IT', '信息技术与计算机科学', 1),
('物理', 'PHY', '物理实验', 2),
('化学', 'CHE', '化学实验', 3),
('生物', 'BIO', '生物实验', 4);

-- 教师-班级关联
INSERT INTO teacher_class (teacher_id, class_id, subject_id) VALUES
(2, 1, 1), (2, 2, 1),
(3, 3, 2), (3, 4, 2);

-- 实验项目数据
INSERT INTO experiment_project (project_name, subject_id, description, objectives, requirements, difficulty, create_user_id) VALUES
('物联网气象站实验', 1, '利用传感器采集温湿度、气压等气象数据，通过物联网平台实现数据可视化', '掌握传感器使用方法，理解物联网数据传输原理', '完成传感器连接、数据采集和展示', 2, 1),
('简单电路连接实验', 2, '使用电池、导线、灯泡等器材，完成串并联电路连接', '理解串联和并联电路的特点', '正确连接电路，记录实验数据', 1, 1),
('酸碱中和反应实验', 3, '观察酸碱中和反应的现象，记录实验数据', '掌握酸碱中和反应原理', '安全操作，详细记录现象', 2, 1),
('植物细胞观察实验', 4, '使用显微镜观察洋葱表皮细胞', '学会制作临时装片，使用显微镜', '绘制细胞结构图', 1, 1);

-- 评分标准（通用标准）
INSERT INTO scoring_criteria (criteria_name, max_score, weight, description, project_id, sort_order) VALUES
('实验目的', 10.0, 1.00, '是否清晰阐述实验目的和意义', NULL, 1),
('实验步骤', 20.0, 1.00, '实验步骤描述是否完整、条理清晰', NULL, 2),
('实验数据', 25.0, 1.00, '数据记录是否准确、完整', NULL, 3),
('实验结论', 25.0, 1.00, '结论是否正确，分析是否到位', NULL, 4),
('报告格式', 10.0, 1.00, '报告格式是否规范，图表是否清晰', NULL, 5),
('创新思考', 10.0, 1.00, '是否有创新想法和深入思考', NULL, 6);

-- 实验任务示例
INSERT INTO experiment_task (task_name, project_id, teacher_id, class_id, description, start_time, deadline, status) VALUES
('物联网气象站实验报告', 1, 2, 1, '请同学们完成物联网气象站实验，并提交实验报告', '2024-03-01 08:00:00', '2024-03-15 23:59:59', 1),
('简单电路实验报告', 2, 3, 3, '完成串并联电路实验并提交报告', '2024-03-05 08:00:00', '2024-03-20 23:59:59', 1);

-- 测试用学生报告数据（方便测试AI分析功能）
INSERT INTO experiment_report (task_id, student_id, title, content, experiment_data, conclusion, submit_time, status) VALUES
(1, 4, '物联网气象站实验报告 - 王小明',
'一、实验目的\n本实验旨在通过搭建物联网气象站，学习传感器的使用方法，理解物联网数据传输的基本原理。通过实际操作，掌握温湿度传感器、气压传感器的连接和数据采集方法。\n\n二、实验步骤\n1. 准备硬件设备：Arduino开发板、DHT11温湿度传感器、BMP180气压传感器、面包板、跳线\n2. 连接电路：将DHT11的VCC接Arduino的5V，GND接地，DATA接数字引脚2\n3. 连接BMP180：SDA接A4，SCL接A5，VCC接3.3V\n4. 编写程序代码，读取传感器数据\n5. 将数据通过WiFi模块上传到物联网平台\n6. 在平台上配置数据可视化图表',
'测量时间 | 温度(℃) | 湿度(%) | 气压(hPa)\n09:00 | 22.5 | 65 | 1013.2\n10:00 | 23.1 | 62 | 1013.0\n11:00 | 24.8 | 58 | 1012.8\n12:00 | 26.2 | 55 | 1012.5\n13:00 | 27.0 | 52 | 1012.3\n14:00 | 26.5 | 54 | 1012.6',
'通过本次实验，我成功搭建了物联网气象站，实现了温度、湿度和气压数据的实时采集和上传。数据显示一天中温度逐渐升高，在下午达到最高值，湿度则相应下降。气压变化较小，说明当天天气比较稳定。这次实验让我对物联网技术有了更深入的理解。',
'2024-03-10 15:30:00', 1);
