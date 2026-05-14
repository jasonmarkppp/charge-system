中小学实验报告评价系统
项目简介
基于 SpringBoot + Vue3 的前后端分离实验报告评价系统，适用于中小学信息技术、物理、化学、生物等实验教学场景。

技术栈
后端
SpringBoot 2.7.18
MyBatis-Plus 3.5.5
MySQL 8.0+
Spring Security + JWT
EasyExcel（批量导入导出）
Knife4j（API文档）
前端
Vue 3.4 + Vite 5
Element Plus 2.6
Pinia（状态管理）
Axios（HTTP请求）
ECharts 5（数据可视化）
Vue Router 4（路由管理）
系统角色
角色	功能
管理员	年级班级管理、师生账号管理、学科管理、实验项目管理、评分标准管理、数据统计
教师	发布实验任务、批阅报告、按维度打分、写评语、退回重做、班级统计
学生	查看任务、填写报告、上传附件、提交作业、查看成绩评语
快速启动
1. 数据库配置
# 1. 创建数据库并导入数据
mysql -u root -p < database/init.sql
2. 后端启动
cd backend

# 修改数据库配置（src/main/resources/application.yml）
# 修改 spring.datasource.username 和 password

# Maven编译运行
mvn spring-boot:run

# 后端启动在 http://localhost:8088/api
# API文档 http://localhost:8088/api/doc.html
3. 前端启动
cd frontend
npm install
npm run dev

# 前端启动在 http://localhost:5173
演示账号
角色	用户名	密码
管理员	admin	123456
教师	teacher01	123456
教师	teacher02	123456
学生	student01	123456
学生	student02	123456
所有初始账号密码均为 123456，数据库中存储的是BCrypt加密后的值。

项目结构
├── backend/                     # 后端SpringBoot项目
│   ├── pom.xml                  # Maven依赖
│   └── src/main/java/com/experiment/report/
│       ├── ExperimentReportApplication.java  # 启动类
│       ├── common/              # 通用类（Result、异常处理）
│       ├── config/              # 配置（Security、JWT、CORS、MyBatis-Plus）
│       ├── controller/          # 控制器（Auth、Admin、Teacher、Student）
│       ├── dto/                 # 数据传输对象
│       ├── entity/              # 实体类
│       ├── mapper/              # MyBatis Mapper
│       ├── service/             # 业务服务层
│       ├── utils/               # 工具类（JWT）
│       └── vo/                  # 视图对象
├── frontend/                    # 前端Vue3项目
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/                 # API接口
│       ├── components/          # 公共组件（Layout）
│       ├── router/              # 路由配置
│       ├── store/               # Pinia状态管理
│       ├── utils/               # 工具（axios封装）
│       └── views/               # 页面
│           ├── login/           # 登录页
│           ├── admin/           # 管理员页面
│           ├── teacher/         # 教师页面
│           └── student/         # 学生页面
└── database/
    └── init.sql                 # 数据库初始化脚本
API接口说明
模块	路径前缀	说明
认证	/api/auth	登录、获取用户信息、修改密码
管理员	/api/admin	年级/班级/教师/学生/学科/项目/标准管理
教师	/api/teacher	任务发布/报告批阅/统计
学生	/api/student	查看任务/填写报告/上传附件
核心功能亮点
JWT无状态认证 - Token过期自动跳转登录
RBAC角色权限 - 管理员/教师/学生三级权限隔离
多维度评分 - 自定义评分标准，按维度独立打分
AI智能分析 - 一键调用豆包大模型分析实验报告，自动给出各维度评分建议和评语
图表可视化诊断 - 雷达图+柱状图直观展示学生能力分布，自动识别薄弱点并给出改进建议
文件上传 - 支持PDF/Word/图片等多格式实验附件
数据统计 - ECharts可视化展示实验数据
分页查询 - MyBatis-Plus分页插件
前后端分离 - Vite代理解决跨域，独立部署
AI智能分析功能
教师端集成了 AI 一键分析能力，基于豆包（火山方舟）大模型 API：

功能特性
一键分析 - 点击按钮即可AI自动阅读报告并分析
多维度评分 - AI根据评分标准逐个维度给出分数和理由
雷达图分析 - 直观展示学生各能力维度的强弱分布
得分率柱状图 - 绿色(良好)/橙色(待提高)/红色(不足)三色区分
薄弱点自动诊断 - 筛选低于75%得分率的维度，给出改进建议
优缺点总结 - AI自动总结报告优点和改进方向
一键采纳 - 分析结果可直接填入评分表单，老师可修改后提交
双模式支持 - AI模式（调用大模型）+ 本地规则分析（fallback）
AI配置说明
在 backend/src/main/resources/application.yml 中配置：

# AI分析配置（豆包/火山方舟API）
ai:
  api:
    enabled: true
    base-url: https://ark.cn-beijing.volces.com/api/v3
    api-key: 你的API Key
    model: 你的Endpoint ID（如 ep-xxxxxxxxxxxx）
    timeout: 120000        # 超时时间（毫秒）
    max-tokens: 2000       # 最大返回 token 数
未配置 AI 时系统会自动回退到本地规则分析模式，不影响正常使用。

页面截图说明
页面	说明
登录页	支持三种角色登录
管理员-班级管理	年级班级树形结构管理
管理员-学科管理	学科及实验项目配置
教师-任务列表	发布实验任务、查看提交情况
教师-报告批阅	AI分析 + 图表诊断 + 多维度评分
学生-任务列表	查看实验任务和要求
学生-填写报告	在线编写实验报告 + 附件上传
学生-成绩查询	查看老师评分和评语
