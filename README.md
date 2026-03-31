# 悦居物业报修管理系统

全栈物业报修与工单管理：Spring Boot 后端 + Vue 3 前端（仓库若分开展示可分别链接）。

## 技术栈

- **后端**：Spring Boot 3、Java 17、Spring Security、JWT、MyBatis、PageHelper、MySQL  
- **前端**：Vue 3、Element Plus、Vue Router、Axios、Vite（见各前端仓库或子目录）

## 本地运行（后端）

1. JDK 17、Maven 3.8+，安装 MySQL 并创建数据库（与 `yueju_property` 一致或自行改 URL）。  
2. 配置数据源与 JWT（任选其一）：  
   - **推荐**：复制 `src/main/resources/application-local.properties.example` 为 `application-local.properties`，填写密码与路径；该文件已被 Git 忽略。  
   - 或设置环境变量：`SPRING_DATASOURCE_PASSWORD`、`YUEJU_JWT_SECRET`、`FILE_UPLOAD_PATH` 等。  
3. 在项目根目录执行：

```bash
mvn spring-boot:run
```

4. 接口文档见 [`docs/API接口文档.md`](docs/API接口文档.md)。

## 上传到 GitHub（作品集）

1. **切勿提交**真实数据库密码、JWT 密钥、内网地址。本仓库已用环境变量 + 可选 `application-local.properties` 处理；若你曾在旧版本中提交过密钥，请在数据库 **改密码**、并 **更换 JWT secret**。  
2. 在项目根目录初始化并推送：

```bash
git init
git add .
git commit -m "Initial commit: 悦居物业报修系统后端"
```

3. 在 GitHub 新建 **空仓库**（不要勾选添加 README），按页面提示添加远程并推送：

```bash
git remote add origin https://github.com/你的用户名/仓库名.git
git branch -M main
git push -u origin main
```

4. 在仓库 **About** 里可填写项目简介，并勾选 **Topics**，例如：`spring-boot`、`vue`、`fullstack`、`portfolio`。

## 许可证

按需补充（如 MIT）；若仅作作品集展示，可注明「仅供学习展示」。
