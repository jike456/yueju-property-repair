# 悦居物业报修管理系统

全栈物业报修与工单管理：Spring Boot 后端 + Vue 3 前端。
## 前端仓库
- Spring Boot 后端：https://github.com/jike456/yueju-property-repair-frontend
## 技术栈

- **后端**：Spring Boot 3、Java 17、Spring Security、JWT、MyBatis、PageHelper、MySQL  
- **前端**：Vue 3、Element Plus、Vue Router、Axios、Vite（见各前端仓库）

## 本地运行（后端）

1. JDK 17、Maven 3.8+，安装 MySQL 并创建数据库（与 `yueju_property` 一致或自行改 URL）。  
2. 配置数据源与 JWT（任选其一）：  
   - **推荐**：复制 `src/main/resources/application-local.properties.example` 为 `application-local.properties`，填写密码与路径。  
   - 或设置环境变量：`SPRING_DATASOURCE_PASSWORD`、`YUEJU_JWT_SECRET`、`FILE_UPLOAD_PATH` 等。  
3. 在项目根目录执行：

```bash
mvn spring-boot:run
```

4. 接口文档见 [`docs/API接口文档.md`](docs/API接口文档.md)。



## 许可证

「仅供学习展示」。
