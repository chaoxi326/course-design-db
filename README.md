# 超市进销存管理系统

## 项目简介

本项目是数据库原理与应用课程设计，实现了一个基于 **Spring Boot + Vue 3** 的超市进销存管理系统。系统采用 B/S 架构、前后端分离，涵盖员工管理、供应商管理、商品管理、采购订单与明细管理、登录认证与角色权限控制、仪表盘数据可视化等核心功能模块，并集成了 **DeepSeek 大语言模型 API** 提供 AI 智能经营分析功能。

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.4.1 (Java 21) |
| 持久层 | MyBatis 3.0.4 + MySQL 8.0 |
| 安全 | BCrypt 密码加密 |
| 前端框架 | Vue 3.5 (Composition API) |
| UI 组件库 | Element Plus 2.14 |
| 构建工具 | Vite 8 |
| 状态管理 | Pinia |
| HTTP 客户端 | Axios |
| AI 集成 | DeepSeek API (Chat Completions) |
| 版本控制 | Git + GitHub |

## 目录结构

```
course-design-db/
├── 数据库/                  # 数据库脚本
│   └── init.sql            # 建表 + 初始数据
├── 后端服务代码/            # Spring Boot 项目
│   └── supermarket/
│       ├── src/main/java/com/example/supermarket/
│       │   ├── entity/     # 实体类 (5个)
│       │   ├── mapper/     # MyBatis Mapper 接口 + XML (5个)
│       │   ├── service/    # 业务逻辑层 (6个ServiceImpl)
│       │   ├── controller/ # REST 控制器 (5个)
│       │   ├── common/     # 公共组件 (Result/ResultCode/全局异常)
│       │   └── config/     # 配置类 (CORS/Security)
│       └── src/main/resources/
│           ├── application.yaml  # 主配置文件
│           ├── mapper/           # MyBatis XML 映射
│           └── db/               # 数据库脚本
├── 前端代码/                # Vue 3 项目
│   └── src/
│       ├── api/            # API 请求模块 (5个)
│       ├── views/          # 页面组件 (8个路由)
│       ├── layout/         # 布局组件
│       ├── router/         # 路由配置 + 权限守卫
│       ├── stores/         # Pinia 状态管理
│       └── style.css       # 全局样式
├── log/                    # 每日开发日志 (6篇)
├── doc/                    # 课程设计报告 + ER图文档
├── generate_er.py          # ER图生成脚本
├── generate_report.py      # 报告生成脚本
└── README.md               # 本文件
```

## 功能模块

| 模块 | 功能 | 权限 |
|------|------|------|
| 登录 | 工号+密码登录，BCrypt 加密验证 | 所有员工 |
| 仪表盘 | 欢迎语、功能导航、库存概览、AI 分析 | 所有员工 |
| 员工管理 | 增删改查、批量录入（JSON）、拖拽排序 | 管理员 |
| 个人信息 | 查看个人资料 | 普通员工 |
| 供应商管理 | 增删改查、批量录入、拖拽排序 | 管理员 |
| 商品管理 | 增删改查、批量录入、拖拽排序、价格校验 | 管理员 |
| 采购管理 | 采购单+明细 CRUD、级联删除、汇总刷新、经办人修改 | 管理员 |
| AI 分析 | 点击按钮调用 DeepSeek 生成经营分析报告 | 所有员工 |

## 数据库设计

系统数据库 `supermarket_db` 包含 **5 张核心业务表**，InnoDB 引擎，utf8mb4 字符集：

| 表名 | 说明 | 主键 | 外键 |
|------|------|------|------|
| `supplier` | 供应商表 | s_id | — |
| `product` | 商品表 | p_id | s_id → supplier |
| `employee` | 员工表 | e_id | — |
| `purchase_order` | 采购订单主表 | o_id | e_id → employee |
| `purchase_detail` | 采购明细表 | d_id | o_id → purchase_order (CASCADE), p_id → product |

详细 E-R 图见 `doc/数据库ER图.docx`。

## 部署运行

### 环境要求

- **后端**：JDK 21+、Maven 3.8+、MySQL 8.0+
- **前端**：Node.js 20+、npm 10+

### 后端启动

1. 在 MySQL 中执行 `后端服务代码/supermarket/src/main/resources/db/schema.sql` 建表
2. 修改 `application.yaml` 中的数据库密码
3. 设置环境变量（或直接修改 yaml）：
   ```bash
   export DEEPSEEK_API_KEY=sk-xxxxxxxx   # AI 分析功能需要
   ```
4. 编译运行：
   ```bash
   cd 后端服务代码/supermarket
   ./mvnw spring-boot:run
   ```
5. API 根路径：`http://localhost:8080/api`

### 前端启动

1. 安装依赖：
   ```bash
   cd 前端代码
   npm install
   ```
2. 启动开发服务器：
   ```bash
   npm run dev
   ```
3. 浏览器访问 `http://localhost:5173`

## API 接口一览

### 员工管理 `/api/employees`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/employees/login` | 登录验证 |
| GET | `/api/employees` | 查询所有员工 |
| POST | `/api/employees/batch` | 批量录入 |
| PUT | `/api/employees` | 修改员工 |
| DELETE | `/api/employees/{eId}` | 删除员工 |

### 供应商管理 `/api/suppliers`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/suppliers` | 查询所有供应商 |
| POST | `/api/suppliers/batch` | 批量录入 |
| PUT | `/api/suppliers` | 修改供应商 |
| DELETE | `/api/suppliers/{sId}` | 删除供应商 |

### 商品管理 `/api/products`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/products` | 查询所有商品 |
| POST | `/api/products/batch` | 批量录入 |
| PUT | `/api/products` | 修改商品 |
| DELETE | `/api/products/{pId}` | 删除商品 |

### 采购管理 `/api/purchase`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/purchase/orders` | 查询所有采购单 |
| GET | `/api/purchase/details` | 查询所有明细 |
| POST | `/api/purchase/batch` | 创建采购单+明细 |
| PUT | `/api/purchase/order` | 修改采购单 |
| PUT | `/api/purchase/detail` | 修改明细 |
| PUT | `/api/purchase/refresh/{oId}` | 刷新汇总 |
| DELETE | `/api/purchase/{oId}` | 删除采购单（级联） |

### AI 分析 `/api/analysis`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/analysis` | AI 经营分析 |

## 项目统计

| 指标 | 数值 |
|------|------|
| 总提交数 | 31 次 |
| 总代码行数 | 5,299 行 |
| 后端 Java 文件 | 33 个 |
| MyBatis XML 文件 | 5 个 |
| 前端 Vue 组件 | 10 个 |
| 前端 JS 模块 | 9 个 |
| 数据库表 | 5 张 |
| REST API 接口 | 17 个 |

## 测试数据

系统预置测试账号：

| 工号 | 密码 | 级别 |
|------|------|------|
| E001 | 123456 | 管理员 |
| E002 | 123456 | 普通员工 |
| E003 | 123456 | 普通员工 |
| E004 | 123456 | 普通员工 |

## 开发日志

每日开发日志位于 `log/` 文件夹中，记录了从数据库设计到系统完成的完整开发过程：

- `2026-06-01` — 数据库初建，ER 图绘制
- `2026-06-04` — 持久层（Mapper）完工
- `2026-06-09` — 后端项目编译修复、前后端联调
- `2026-06-11` — 前端页面全面美化
- `2026-06-12` — 全量代码审查与 Bug 修复
- `2026-06-15` — AI 分析功能开发、全量测试、报告撰写

## 作者

- 姓名：陈卓熙
- 课程：数据库原理与应用
- 项目：超市进销存管理系统
