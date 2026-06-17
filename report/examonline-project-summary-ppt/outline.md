# ExamOnline 在线考试系统项目总结 PPT 大纲

## Slide 1: 项目概览
- Key points:
  - ExamOnline 是基于 Spring Boot + Vue 3 的在线考试系统。
  - 覆盖管理员、教师、学生三类角色。
  - 支持题库建设、组卷发布、在线考试、阅卷统计、错题复习闭环。
  - 适用于课程设计、学校日常测验和阶段考试演示。
- Visual idea: 深色到浅色过渡的系统总览封面，中心呈现“考试全流程闭环”。
- Layout role and intent: Cover，用一句话建立项目定位与技术栈印象。
- Required source images: None.

## Slide 2: 背景与目标
- Key points:
  - 传统线下考试在组卷、监考、阅卷和成绩分析上成本较高。
  - 系统目标是让考试流程线上化、自动化、数据化。
  - 业务闭环从题库维护开始，最终落到成绩分析和错题复习。
  - 非功能目标包括角色清晰、操作路径短、支持基础并发和可部署演示。
- Visual idea: 左侧问题痛点，右侧目标能力，用箭头连接“线下流程”到“在线闭环”。
- Layout role and intent: Context / problem，说明为什么做以及要解决什么。
- Required source images: None.

## Slide 3: 技术架构
- Key points:
  - 前端采用 Vue 3 + Vite，负责角色页面、考试交互和数据展示。
  - 后端采用 Spring Boot 3.3.2、Spring Web、Spring Data JPA、Validation。
  - 数据层默认使用 H2 文件数据库，同时提供 MySQL 兼容脚本。
  - Dockerfile 将前端构建产物复制到 Spring Boot static，支持单容器部署。
- Visual idea: 三层架构图：Vue 前端、Spring Boot API、H2/MySQL 数据库，并展示 Docker 单容器交付。
- Layout role and intent: Architecture，帮助评委快速理解系统组成和部署方式。
- Required source images: None.

## Slide 4: 数据模型设计
- Key points:
  - 核心实体包括用户、班级、题目、试卷、试卷题目、答卷、答题记录、操作日志。
  - `paper_questions` 保存题目快照，保证试卷发布后题目内容稳定。
  - `exam_attempts` 记录考试状态、分数、切屏次数、可疑标记和时间信息。
  - `operation_logs` 支撑关键操作审计。
- Visual idea: ER 关系图风格，以考试流程为主线串联 8 张核心表。
- Layout role and intent: Data evidence / concept explanation，解释数据模型如何支撑业务闭环。
- Required source images: None.

## Slide 5: 三端角色功能
- Key points:
  - 管理员端：用户、班级、系统日志与统计维护。
  - 教师端：题库管理、Excel 导入、手动/自动组卷、发布考试、监考、阅卷与导出。
  - 学生端：待考列表、在线答题、历史记录、错题本、个人成绩分析、密码修改。
  - 前端按角色展示功能区，降低误操作和学习成本。
- Visual idea: 三列角色矩阵，每列配不同功能图标和任务链路。
- Layout role and intent: Comparison / feature map，展示功能覆盖范围。
- Required source images: None.

## Slide 6: 考试业务流程
- Key points:
  - 教师维护题库后，可按题目 ID 手动组卷或按条件自动抽题。
  - 试卷发布后按班级范围开放，学生只能在有效时间窗口进入。
  - 学生答题过程支持倒计时、自动保存、切屏记录和交卷。
  - 提交后客观题自动判分，主观题进入人工阅卷流程。
- Visual idea: 横向流程图：题库 -> 组卷 -> 发布 -> 答题 -> 提交 -> 阅卷 -> 分析。
- Layout role and intent: Process，说明核心业务从教师到学生再回到教师的数据流。
- Required source images: None.

## Slide 7: 关键亮点
- Key points:
  - 防切屏：前端监听窗口失焦，后端累计切屏次数，超限自动交卷并标记可疑。
  - 自动保存：答题时防止刷新或网络波动导致作答丢失。
  - 自动阅卷：单选、多选、判断、填空等客观题按标准答案即时判分。
  - Excel 能力：支持题库导入、题目模板下载、成绩导出。
- Visual idea: 四个重点能力卡片，配小型机制示意和结果标记。
- Layout role and intent: Highlights，突出项目区别于普通 CRUD 系统的能力。
- Required source images: None.

## Slide 8: 统计分析与学习反馈
- Key points:
  - 教师端支持平均分、最高分、最低分、及格率、班级成绩和题目正确率分析。
  - 监考视图统计进行中、已完成、可疑、待阅卷人数。
  - 学生端支持成绩趋势、可疑次数和知识点掌握情况。
  - 错题本按历史答题自动沉淀，形成复习闭环。
- Visual idea: 仪表盘式页面，包含成绩趋势折线、正确率条形图和错题标签。
- Layout role and intent: Data evidence，呈现系统的数据化价值。
- Required source images: None.

## Slide 9: 测试与部署
- Key points:
  - 测试覆盖管理员登录、教师新增题目、Excel 导入、手动/自动组卷、学生考试、自动保存、防切屏自动交卷、成绩导出。
  - 已补齐统一异常返回、CORS、默认数据初始化等联调问题。
  - 本地开发前后端分离运行，生产部署采用单容器模式。
  - Koyeb 可直接基于 Dockerfile 部署，默认 H2 文件库适合演示。
- Visual idea: 左侧测试用例通过清单，右侧部署流水线：源码 -> 构建前端 -> 打包后端 -> Docker -> Koyeb。
- Layout role and intent: Verification / deployment，证明项目可运行、可演示、可交付。
- Required source images: None.

## Slide 10: 总结与展望
- Key points:
  - 项目已完成在线考试核心闭环，并具备演示部署能力。
  - 技术实现上完成前后端分离、统一 API、数据持久化和角色化界面。
  - 后续可增强登录安全、权限体系、主观题阅卷体验、实时监控和消息提醒。
  - 若面向正式生产，建议接入外部 MySQL/PostgreSQL 和更完善的数据备份方案。
- Visual idea: 总结式路线图，左侧“已完成”，右侧“下一步优化”。
- Layout role and intent: Summary / recommendation，收束成果并给出后续方向。
- Required source images: None.
