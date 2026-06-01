# 超市进销存管理系统

## 技术栈
- 后端：Spring Boot 2.7 + MyBatis + MySQL
- 前端：Vue 3 + Element Plus + Axios
- 版本控制：Git + GitHub

## 目录结构说明
- `数据库/` – 数据库建表及初始数据脚本
- `后端服务代码/` – Spring Boot 项目
- `前端代码/` – Vue 项目
- `log/` – 每日开发日志（Markdown）

## 部署运行

### 后端
1. 导入 `数据库/init.sql` 到 MySQL
2. IDEA 打开 `后端服务代码/`，修改 `application.properties` 中的数据库密码
3. 运行 `SupermarketApplication.java`
4. API 根路径：`http://localhost:8080/api`

### 前端
1. VSCode 打开 `前端代码/`
2. 执行 `npm install`
3. 执行 `npm run dev`
4. 访问 `http://localhost:5173`

### 注意事项
- 前后端分离，已配置跨域，直接调用即可
- 管理员初始账号：admin / 123（可自行在 employee 表插入）

## 已完成功能
- [ ] 供应商管理
- [ ] 商品管理
- [ ] 员工管理
- [ ] 采购主表+明细
- [ ] 会员管理
- [ ] 普通员工登录及查看功能

## 开发日志
每日日志位于 `log/` 文件夹中，按日期命名。

## 团队信息
- 姓名：文泽仁
- 班级：软件工程241
- 学号：2024082123