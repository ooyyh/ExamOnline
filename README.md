# ExamOnline 在线考试系统

Spring Boot + Vue 3 在线考试系统，覆盖管理员、教师、学生三类角色，支持题库分类、智能组卷、在线考试、防切屏、阅卷、成绩分析和错题本。

## 项目结构

- `backend/`：Spring Boot 后端，生产环境同时托管前端静态资源。
- `frontend/`：Vue 3 + Vite 前端。
- `database/`：数据库脚本。
- `docs/`：项目文档。
- `Dockerfile`：Koyeb/GitHub 部署入口。

## 默认账号

- 管理员：`admin / admin123`
- 教师：`teacher / teacher123`
- 学生：`student / student123`

## 本地开发

后端：

```bash
cd backend
mvn spring-boot:run
```

前端：

```bash
cd frontend
npm install
npm run dev
```

前端开发服务器会通过 Vite 代理访问后端接口。生产部署时不需要单独启动前端，Dockerfile 会把前端构建结果复制到 Spring Boot 的 `static` 目录。

## Koyeb 部署

本项目已经整理为单容器部署，Koyeb 直接连接 GitHub 仓库即可。

1. 将代码推送到 GitHub。
2. 在 Koyeb 创建 Web Service。
3. 选择 GitHub repository：`ExamOnline`。
4. Builder 选择 `Dockerfile`。
5. Dockerfile path 保持根目录：`Dockerfile`。
6. Service type 选择 Web Service，端口使用 Koyeb 自动注入的 `PORT` 环境变量。
7. 部署完成后访问 Koyeb 分配的域名。

可选环境变量：

```text
SPRING_DATASOURCE_URL=jdbc:h2:file:./data/examonline;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
JAVA_OPTS=-Xms128m -Xmx512m
```

说明：默认使用 H2 文件数据库，适合演示和课程项目。Koyeb 免费实例的文件系统不适合长期持久化生产数据；如果要正式使用，建议换成外部 PostgreSQL/MySQL，并通过 `SPRING_DATASOURCE_*` 环境变量配置。

## 本地生产构建

```bash
cd frontend
npm run build

cd ../backend
mvn test
mvn package
```

