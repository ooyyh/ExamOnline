CREATE DATABASE IF NOT EXISTS examonline DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE examonline;

DROP TABLE IF EXISTS attempt_answers;
DROP TABLE IF EXISTS exam_attempts;
DROP TABLE IF EXISTS paper_questions;
DROP TABLE IF EXISTS exam_papers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS school_classes;
DROP TABLE IF EXISTS app_users;
DROP TABLE IF EXISTS operation_logs;

CREATE TABLE app_users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  username VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  real_name VARCHAR(255) NOT NULL,
  student_no VARCHAR(255),
  employee_no VARCHAR(255),
  class_name VARCHAR(255),
  department VARCHAR(255),
  major VARCHAR(255),
  enabled BIT(1) NOT NULL
);

CREATE TABLE school_classes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  name VARCHAR(255) NOT NULL UNIQUE,
  department VARCHAR(255) NOT NULL,
  major VARCHAR(255) NOT NULL,
  student_count INT NOT NULL
);

CREATE TABLE questions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  title VARCHAR(1000) NOT NULL,
  type VARCHAR(100) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  knowledge_point VARCHAR(255) NOT NULL,
  difficulty VARCHAR(100) NOT NULL,
  options_json VARCHAR(4000) NOT NULL,
  correct_answer VARCHAR(1000) NOT NULL,
  score INT NOT NULL,
  explanation VARCHAR(255),
  creator_username VARCHAR(255)
);

CREATE TABLE exam_papers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  title VARCHAR(255) NOT NULL,
  duration_minutes INT NOT NULL,
  pass_score INT NOT NULL,
  start_time DATETIME,
  end_time DATETIME,
  published BIT(1) NOT NULL,
  auto_generated BIT(1) NOT NULL,
  total_score INT NOT NULL,
  target_classes_json VARCHAR(4000) NOT NULL
);

CREATE TABLE paper_questions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  paper_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  order_no INT NOT NULL,
  score INT NOT NULL,
  question_snapshot_json VARCHAR(5000) NOT NULL
);

CREATE TABLE exam_attempts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  paper_id BIGINT NOT NULL,
  paper_title VARCHAR(255) NOT NULL,
  student_username VARCHAR(255) NOT NULL,
  class_name VARCHAR(255) NOT NULL,
  status VARCHAR(100) NOT NULL,
  start_time DATETIME,
  submit_time DATETIME,
  score INT NOT NULL,
  suspicious BIT(1) NOT NULL,
  switch_count INT NOT NULL,
  auto_submit_limit INT NOT NULL
);

CREATE TABLE attempt_answers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  attempt_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  answer_text VARCHAR(4000) NOT NULL,
  correct BIT(1) NOT NULL,
  score INT NOT NULL,
  reviewed BIT(1) NOT NULL
);

CREATE TABLE operation_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  actor_username VARCHAR(255) NOT NULL,
  action VARCHAR(255) NOT NULL,
  target_type VARCHAR(255) NOT NULL,
  target_id VARCHAR(255),
  detail VARCHAR(2000) NOT NULL
);

INSERT INTO school_classes(created_at, updated_at, name, department, major, student_count) VALUES
(NOW(), NOW(), '计算机 2301 班', '计算机学院', '软件工程', 40);

INSERT INTO app_users(created_at, updated_at, username, password, role, real_name, department, enabled) VALUES
(NOW(), NOW(), 'admin', 'admin123', 'ADMIN', '系统管理员', '教务处', 1),
(NOW(), NOW(), 'teacher', 'teacher123', 'TEACHER', '张老师', '计算机学院', 1),
(NOW(), NOW(), 'student', 'student123', 'STUDENT', '李同学', '计算机学院', 1);

INSERT INTO questions(created_at, updated_at, title, type, subject, knowledge_point, difficulty, options_json, correct_answer, score, explanation, creator_username) VALUES
(NOW(), NOW(), 'Java 中用于表示继承的关键字是？', 'SINGLE_CHOICE', 'Java', '面向对象', 'EASY', '["A. extends","B. implements","C. this","D. super"]', 'A', 5, 'extends 用于类继承', 'teacher'),
(NOW(), NOW(), '以下哪些属于 Java 集合框架？', 'MULTI_CHOICE', 'Java', '集合', 'MEDIUM', '["A. List","B. Set","C. Map","D. Thread"]', 'A,B,C', 10, '前三项属于集合', 'teacher'),
(NOW(), NOW(), 'JVM 负责 Java 程序的运行。', 'TRUE_FALSE', 'Java', 'JVM', 'EASY', '["A. 正确","B. 错误"]', 'A', 5, NULL, 'teacher'),
(NOW(), NOW(), 'Spring Boot 默认内嵌的 Web 容器是什么？', 'FILL_BLANK', 'Spring', '基础', 'EASY', '[]', 'Tomcat', 5, NULL, 'teacher'),
(NOW(), NOW(), '简述 MVC 模式的三个组成部分。', 'SHORT_ANSWER', '软件工程', '架构', 'MEDIUM', '[]', 'Model View Controller', 10, '答出核心词即可', 'teacher');

INSERT INTO exam_papers(created_at, updated_at, title, duration_minutes, pass_score, start_time, end_time, published, auto_generated, total_score, target_classes_json) VALUES
(NOW(), NOW(), 'Java 基础测试卷', 90, 60, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 1, 0, 35, '["计算机 2301 班"]');

INSERT INTO paper_questions(created_at, updated_at, paper_id, question_id, order_no, score, question_snapshot_json)
SELECT NOW(), NOW(), 1, id, ROW_NUMBER() OVER (ORDER BY id), score,
JSON_OBJECT('id', id, 'title', title, 'type', type, 'subject', subject, 'knowledgePoint', knowledge_point, 'difficulty', difficulty, 'options', JSON_EXTRACT(options_json, '$'), 'correctAnswer', correct_answer, 'score', score, 'explanation', explanation)
FROM questions;

