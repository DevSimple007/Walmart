# 沃尔玛超市管理系统 · 部署文档

> 更新日期：2026-10-11
> 本文档对应当前代码版本（含移动端商城 shop、桌面端商城 Edition、后台管理 frontend 三个前端）。

---

## 1. 系统组成

| 模块 | 目录 | 技术栈 | 开发端口 |
|---|---|---|---|
| 后端服务 | `src/` | Spring Boot 3.3.5 + MyBatis-Plus + MySQL 8 | **8080**（context-path: `/api`） |
| 后台管理系统 | `frontend/` | Vue 3 + TypeScript + Element Plus + Vite | **3000** |
| 移动端商城 | `shop/` | Vue 3 + Tailwind CSS + Vant + Vite | **5174** |
| 桌面端商城 | `Edition/` | Vue 3 + Element Plus + SCSS + Vite | **5174**（冲突时自动递增为 5175） |
| 图片上传目录 | `D:/专业/spring/WalmartUploads` | 由后端 `upload.path` 配置，经 `/api/uploads/**` 访问 | — |

浏览器访问商品图片的路径形如：`http://localhost:8080/api/uploads/商品图片/<分类>/<文件名>`。

---

## 2. 环境要求

### 2.1 后端
- JDK 17+（Spring Boot 3.x 要求）
- Maven 3.8+
- MySQL 8.0+
- 本地开发推荐 phpStudy 集成的 MySQL（本项目开发者使用 `D:/PhpStudy/phpstudy_pro/Extensions/MySQL8.0.12`）

### 2.2 前端
- Node.js 18+（推荐 20+）
- npm 9+

---

## 3. 本地开发环境启动

### 3.1 数据库

1. 启动 MySQL（phpStudy 面板启动，或系统服务方式）。
2. 首次初始化：
   ```bash
   mysql -uroot -proot
   ```
   ```sql
   CREATE DATABASE walmart_oms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. 建表与初始化数据（首次）：
   - 表结构：`src/main/resources/schema.sql`
   - 种子数据：`src/main/resources/data.sql`
   - 直接用客户端执行这两个脚本即可。
4. 关键表说明：
   - `product`：商品（含 `original_price` 原价/划线价、`is_international` 国际品牌标记）
   - `member`：会员（含 `gender` 性别、`birthday` 生日、`avatar` 头像字段）
   - `member_coupon` / `coupon`：优惠券
   - `favorite`：收藏
   - `home_category` / `product_category`：金刚区与分类（含 `icon_url` 图标）

### 3.2 后端启动

**方式 A：IDEA 直接运行** `OmsApplication.java`（推荐）。

**方式 B：命令行**：
```bash
cd D:/专业/spring/Walmart
mvn spring-boot:run
# 或打包后：
mvn clean package -DskipTests
java -jar target/walmart-oms-1.0.0.jar
```

**方式 C：用已编译的 classes 直接跑**（跳过 Maven）：
```bash
mvn -q -DskipTests compile
mvn -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt
java -cp "target/classes;$(cat target/cp.txt)" com.walmart.oms.OmsApplication
```

启动后访问 `http://localhost:8080/api/doc.html` 查看接口文档（Knife4j）。

> ⚠️ 修改后端代码后必须**重启进程**才会生效（项目未配置 devtools 热重载）。

### 3.3 前端启动（三个前端各自独立）

```bash
# 后台管理
cd frontend && npm install && npm run dev     # http://localhost:3000

# 移动端商城
cd shop && npm install && npm run dev         # http://localhost:5174

# 桌面端商城
cd Edition && npm install && npm run dev      # 端口 5174 被占时自动用 5175
```

三个前端的 Vite 都已配置代理：`/api` → `http://localhost:8080`（shop 与 frontend 另配了 `/uploads` 代理）。

---

## 4. 生产部署

### 4.1 后端打包部署（Linux）

1. 打包：
   ```bash
   mvn clean package -DskipTests
   # 产物：target/walmart-oms-1.0.0.jar
   ```
2. 上传到服务器：
   ```bash
   scp target/walmart-oms-1.0.0.jar root@your-server-ip:/opt/walmart/
   ```
3. 启动（参数覆盖数据源，勿在生产直接用 root/root）：
   ```bash
   java -jar /opt/walmart/walmart-oms-1.0.0.jar \
     --server.port=8080 \
     --spring.datasource.url="jdbc:mysql://localhost:3306/walmart_oms?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai" \
     --spring.datasource.username=walmart \
     --spring.datasource.password=**改成你的强密码**
   ```
4. systemd 托管（`/etc/systemd/system/walmart-oms.service`）：
   ```ini
   [Unit]
   Description=Walmart OMS Service
   After=network.target

   [Service]
   User=root
   WorkingDirectory=/opt/walmart
   ExecStart=/usr/bin/java -jar /opt/walmart/walmart-oms-1.0.0.jar
   Restart=always

   [Install]
   WantedBy=multi-user.target
   ```
   ```bash
   systemctl daemon-reload && systemctl enable --now walmart-oms
   ```

> ⚠️ **上传目录**：生产机上必须存在 `upload.path` 指向的目录（默认 `D:/专业/spring/WalmartUploads` 是 Windows 路径，Linux 部署时务必改成如 `/opt/walmart/uploads`），并把商品图片同步过去，否则全站图片 404。

### 4.2 前端构建与 Nginx

三个前端分别构建：
```bash
cd frontend && npm install && npm run build   # 产物 frontend/dist
cd shop     && npm install && npm run build   # 产物 shop/dist
cd Edition  && npm install && npm run build   # 产物 Edition/dist
```

Nginx 多站点示例（域名按需替换）：
```nginx
# 后台管理
server {
    listen 80;
    server_name admin.your-domain.com;
    root /opt/walmart/frontend/dist;
    index index.html;
    location / { try_files $uri $uri/ /index.html; }
    location /api {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}

# 移动端商城（shop，移动浏览器访问）
server {
    listen 80;
    server_name m.your-domain.com;
    root /opt/walmart/shop/dist;
    index index.html;
    location / { try_files $uri $uri/ /index.html; }
    location /api { proxy_pass http://127.0.0.1:8080; proxy_set_header Host $host; }
}

# 桌面端商城（Edition）
server {
    listen 80;
    server_name shop.your-domain.com;
    root /opt/walmart/edition/dist;
    index index.html;
    location / { try_files $uri $uri/ /index.html; }
    location /api { proxy_pass http://127.0.0.1:8080; proxy_set_header Host $host; }
}
```

HTTPS（SSL 证书）按需在各 server 块中补充，参考通用 Nginx SSL 配置即可。

---

## 5. 配置说明（application.yml）

| 配置项 | 说明 | 当前值 |
|---|---|---|
| `server.port` | 服务端口 | 8080 |
| `server.servlet.context-path` | 接口前缀 | `/api` |
| `spring.datasource.url` | 数据库连接 | `jdbc:mysql://localhost:3306/walmart_oms?...` |
| `spring.datasource.username / password` | 数据库账号 | root / root（**生产必须改**） |
| `jwt.secret` | JWT 密钥 | 见配置文件（生产建议更换） |
| `jwt.expire` | 访问令牌有效期 | 86400000 ms（1 天） |
| `jwt.refresh-expire` | 刷新令牌有效期 | 604800000 ms（7 天） |
| `upload.path` | 商品图片上传根目录 | `D:/专业/spring/WalmartUploads` |

**接口限流**：后端对接口有频率限制（`RateLimitInterceptor`，默认 300 次 / 60 秒）。批量压测或爬数据时如遇 429，等待窗口恢复即可，正常使用不会触发。

---

## 6. 账号信息

| 端 | 账号 | 密码 | 说明 |
|---|---|---|---|
| 后台管理 | admin | 123456 | 首次登录后请修改 |
| 移动端/桌面端商城 | 13900139001 | 123456 | 测试会员（可在界面注册新会员） |

---

## 7. 常见问题（FAQ）

1. **接口 404 / `No static resource xxx`**
   → 后端没重启，新加的接口未生效。重启后端进程。
2. **页面图片全部裂图**
   → 检查 `upload.path` 目录是否存在、图片文件是否在该目录内、`/api/uploads/...` 能否用浏览器直接打开。
3. **前端调接口报 502 / CORS**
   → 后端没起来（先 `curl http://localhost:8080/api/...` 验证），或 Vite 代理失效（重启 dev server）。
4. **弹窗/确认框错位、跑到页面底部**
   → 检查 `shop/src/main.js` 是否引入了完整 Vant 样式 `import 'vant/lib/index.css'`（函数式调用的组件不会自动引样式）。
5. **接口频繁 429**
   → 命中限流（300 次/60 秒），等 1 分钟或调整 `RateLimitInterceptor` 配置。
6. **推送 GitHub 报 `GH007: Your push would publish a private email address`**
   → 仓库已配置使用 GitHub noreply 邮箱提交（`git config user.email` 查看）。若换了机器，重新配置：
   ```bash
   git config user.email "<你的GitHubID>+<用户名>@users.noreply.github.com"
   ```
7. **MySQL 服务起不来（phpStudy）**
   → phpStudy 面板里启动；端口冲突时检查 3306 是否被占用。

---

## 8. 仓库信息

- GitHub：`https://github.com/DevSimple007/Walmart`（main 分支）
- 提交人邮箱使用 GitHub noreply 地址，避免暴露私人邮箱。
- `WalmartUploads` 商品图片**不在仓库内**，属独立资源目录，迁移服务器时需单独拷贝。
