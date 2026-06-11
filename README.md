# 沃尔玛超市后台管理系统 (Walmart OMS)

## 📋 项目简介

这是一个基于 Spring Boot + Vue 3 的沃尔玛超市全栈管理系统，包含后台管理系统和移动端商城两个前端应用。系统提供商品管理、订单管理、库存管理、会员管理等核心功能，支持完整的电商业务流程。

---

## 🏗️ 项目结构

```
Walmart/
├── src/main/java/          # 后端源码 (Spring Boot)
├── frontend/               # 后台管理系统前端 (Vue 3 + Element Plus)
├── shop/                   # 移动端商城前端 (Vue 3 + Vant)
├── WalmartUploads/         # 商品图片上传目录
├── pom.xml                 # Maven 配置文件
└── DEPLOYMENT.md           # 详细部署文档
```

---

## 🛠️ 技术栈

### 后端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.3.5 | 核心框架 |
| Spring Security | - | 安全认证框架 |
| MyBatis-Plus | 3.5.10 | ORM 框架 |
| Spring Data JPA | - | 数据持久化 |
| MySQL | 8.0+ | 数据库 |
| JWT | 0.12.5 | 令牌认证 |
| Knife4j | 4.4.0 | API 文档工具 |
| Lombok | 1.18.40 | 简化代码 |

### 前端技术（后台管理系统）
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.0 | 前端框架 |
| TypeScript | 5.6.0 | 类型支持 |
| Vue Router | 4.4.0 | 路由管理 |
| Pinia | 2.2.0 | 状态管理 |
| Element Plus | 2.8.0 | UI 组件库 |
| ECharts | 5.5.1 | 图表可视化 |
| Axios | 1.7.0 | HTTP 客户端 |
| Vite | 6.0.0 | 构建工具 |

### 前端技术（移动端商城）
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.33 | 前端框架 |
| Vant | 4.9.24 | 移动端 UI 组件库 |
| Vue Router | 4.6.4 | 路由管理 |
| Pinia | 3.0.4 | 状态管理 |
| Axios | 1.15.2 | HTTP 客户端 |
| Vite | 8.0.10 | 构建工具 |

---

## ✨ 功能模块

### 🔐 系统管理
- **用户管理**：用户增删改查、权限分配
- **角色管理**：角色定义、权限配置
- **菜单管理**：动态菜单、权限控制

### 📦 商品管理
- **商品信息**：商品录入、编辑、上下架
- **商品分类**：多级分类管理
- **品牌管理**：品牌信息维护
- **图片上传**：商品图片管理（存储至 WalmartUploads 目录）

### 🛒 订单管理
- **订单处理**：订单创建、查询、状态流转
- **购物车**：购物车管理
- **客户订单**：客户端订单查询

### 📊 库存管理
- **库存查询**：实时库存查看
- **入库管理**：商品入库记录
- **出库管理**：商品出库记录
- **仓库管理**：多仓库支持

### 👥 会员管理
- **会员信息**：会员档案管理
- **会员等级**：等级体系配置
- **会员卡**：会员卡管理
- **积分管理**：积分累计与使用

### 📈 数据分析
- **仪表盘**：关键指标展示
- **报表统计**：销售报表、库存报表

---

## 🚀 快速开始

### 环境要求
- **JDK 17+**
- **Maven 3.8+**
- **MySQL 8.0+**
- **Node.js 18+**
- **npm 9+**

### 1. 数据库配置

```sql
-- 创建数据库
CREATE DATABASE walmart_oms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权
CREATE USER 'walmart'@'localhost' IDENTIFIED BY 'walmart123';
GRANT ALL PRIVILEGES ON walmart_oms.* TO 'walmart'@'localhost';
FLUSH PRIVILEGES;
```

### 2. 后端启动

```bash
# 进入项目目录
cd D:\专业\spring\Walmart

# 安装依赖并打包
mvn clean package -DskipTests

# 运行
java -jar target/walmart-oms-1.0.0.jar
```

后端服务将启动在 `http://localhost:8080/api`

### 3. 前端启动（后台管理系统）

```bash
# 进入前端目录
cd D:\专业\spring\Walmart\frontend

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 生产构建
npm run build
```

### 4. 前端启动（移动端商城）

```bash
# 进入商城目录
cd D:\专业\spring\Walmart\shop

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 生产构建
npm run build
```

---

## ⚙️ 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 8080                    # 服务端口
  servlet:
    context-path: /api          # API 上下文路径

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/walmart_oms
    username: root
    password: root

jwt:
  secret: walmart_oms_secret_key_for_jwt_hs256_algorithm
  expire: 86400000              # Token 过期时间（24小时）

upload:
  path: D:/专业/spring/WalmartUploads  # 图片上传路径
```

### API 文档
启动后端后访问：`http://localhost:8080/api/doc.html`

---

## 📁 图片存储

商品图片统一存储在 `D:\专业\spring\WalmartUploads` 目录：
- 支持商品主图、详情图上传
- 自动生成唯一文件名
- 通过 `/api/upload` 接口访问

---

## 🔑 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |

> ⚠️ 生产环境请务必修改默认密码

---

## 📖 详细文档

完整的部署、配置、运维文档请查看 [DEPLOYMENT.md](./DEPLOYMENT.md)

---

## 🌐 访问地址

| 应用 | 开发环境 | 说明 |
|------|----------|------|
| 后端 API | http://localhost:8080/api | Spring Boot 服务 |
| API 文档 | http://localhost:8080/api/doc.html | Knife4j 接口文档 |
| 后台管理系统 | http://localhost:5173 | Vue 3 + Element Plus |
| 移动端商城 | http://localhost:5174 | Vue 3 + Vant |

---

## 📝 开发说明

### 项目特点
1. **前后端分离**：后端提供 RESTful API，前端独立部署
2. **双前端应用**：后台管理系统 + 移动端商城
3. **JWT 认证**：无状态认证，支持 Token 刷新
4. **权限控制**：基于角色的动态权限管理
5. **API 文档**：集成 Knife4j，自动生成接口文档

### 目录说明
```
src/main/java/com/walmart/oms/
├── config/          # 配置类（安全、跨域、MyBatis等）
├── controller/      # 控制器层
├── entity/          # 实体类
├── mapper/          # MyBatis Mapper 接口
├── service/         # 服务层接口及实现
└── utils/           # 工具类（JWT、验证码等）
```

---

## 🤝 技术支持

如有问题或建议，请联系开发团队。

---

## 📄 License

本项目仅供学习交流使用。
