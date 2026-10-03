# Stitch 界面替换 & 接口对接说明

> 分支/工作区：`workbuddy/main-34fc41dd`（worktree 隔离，主项目 `D:/专业/spring/Walmart` 未被改动）
> 被替换的前端：`shop/`（移动端商城，Vue3 + Vite + Vant）
> 设计来源：`stitch_product_prototype_ui_design/` 的 10 个 `code.html`

---

## 1. 改动原则

| 项 | 处理方式 |
|---|---|
| 页面外观（布局 / 配色 / 排版 / 圆角 / 阴影 / 图标） | 100% 照搬 Stitch 设计稿的 Tailwind class，一项未改 |
| 后端接口 / 网络请求 / 状态管理 / 路由 | **零改动**（见第 4 节证明） |
| 页面 JS 业务逻辑（ref / computed / 函数 / 生命周期） | 逐行原样保留，仅在必要处**追加**绑定代码 |
| 商品、分类、订单、购物车等数据 | 绑定到 Stitch 新 DOM 上 |
| 接口无对应数据的营销位（banner、榜单、促销楼层、示例文案） | 保留设计稿静态内容 |

**保真度校验**：每个页面都通过了自动化校验脚本（见第 3 节），设计稿中出现的每一个 class 都 100% 存在于产出页面中，`missing = 0`。

---

## 2. 页面映射与文件清单

### 2.1 新增文件（基础设施）

| 文件 | 作用 |
|---|---|
| `shop/tailwind.config.js` | Tailwind v3 主题，逐字取自设计稿内联配置（颜色/字号/间距/圆角），并补齐 `pb-safe`/`pt-safe`/`scrollbar-none`/`no-scrollbar` |
| `shop/postcss.config.js` | postcss + autoprefixer |
| `shop/src/styles/tailwind.css` | `@tailwind` 指令 + 最小必要基础重置（**preflight 关闭**，避免影响原有 Vant 页面） |
| `shop/public/placeholder.svg` | 商品缺图占位图（本地） |
| `shop/public/fonts/*` | **本地自托管** Plus Jakarta Sans 与 Material Symbols Outlined（避免外网不可达导致图标变文字） |

### 2.2 修改文件

| 文件 | 对应设计稿 | 说明 |
|---|---|---|
| `shop/index.html` | — | 接入本地字体、body 使用设计稿底色 |
| `shop/src/main.js` | — | 引入 `tailwind.css` |
| `shop/src/layouts/TabBarLayout.vue` | 各页底部导航 | 底部 Tab 改为设计稿样式（Material Symbols + 购物车角标）；**路由 / keep-alive / 角标逻辑原样保留** |
| `shop/src/views/Home.vue` | `_1` | 首页 |
| `shop/src/views/Category.vue` | `_2` | 分类 |
| `shop/src/views/Search.vue` | `_3` | 搜索结果 |
| `shop/src/views/Cart.vue` | `_4` | 购物车 |
| `shop/src/views/ProductDetail.vue` | `_5` | 商品详情 |
| `shop/src/views/OrderConfirm.vue` | `_6` | 确认订单 |
| `shop/src/views/OrderResult.vue` | `_7` | 下单成功 |
| `shop/src/views/OrderList.vue` | `_8` | 我的订单 |
| `shop/src/views/OrderDetail.vue` | `_9` | 订单详情 |
| `shop/src/views/Profile.vue` | `_10` | 我的 |
| `.gitignore` | — | 忽略 `dist/` |

> `Login.vue` / `Address.vue` / `Coupon.vue` / `Favorites.vue` 设计稿未提供，**保持原样未动**，功能不受影响。

### 2.3 数据绑定对照（关键）

| 设计稿区块 | 绑定到 | 说明 |
|---|---|---|
| 首页金刚区 10 宫格 | `getHomeCategories()` → `getCategoryList()` 兜底 | 点击跳 `/category?categoryId=` |
| 首页「热卖单品」双列商品流 | `searchProducts({pageNum:1,pageSize:20})` | 点击跳 `/product/:id`，`+` 加购 |
| 分类页 一级/二级分类 | `getCategoryList()` | `selectParent` / `selectChild` 触发 `searchProducts` |
| 分类页 右侧 SKU 流 | `searchProducts()` | 原 `van-list` 改为容器 `@scroll` 触底 `loadMore()` |
| 搜索结果列表 | `searchProducts({keyword})` | 同上触底加载；排序条接 `doSort()` |
| 购物车行 | `cartStore.items` | 勾选 `toggleCheck` / 加减 `updateQuantity` / 删除 `removeItem` |
| 购物车底栏 | `cartStore.checkedTotal` / `totalCount` / `checkedItems` | `goCheckout()` |
| 商品详情 | `getProductDetail(id)` | `addToCart()` / `buyNow()` |
| 确认订单 · 配送方式 | `deliveryType` | `delivery` / `pickup` |
| 确认订单 · 地址 | `getAddressList()` | `selectAddress` / `formatAddress` |
| 确认订单 · 商品清单 | `cartStore.orderItems` | — |
| 确认订单 · 优惠券 | `getCustomerCoupons()` + `usableCoupons` | 保留原 `van-popup` 选择浮层 |
| 确认订单 · 金额 | `orderTotal` / `deliveryFee` / `couponDiscount` / `totalWithFee` | `submitOrder()` → `createCustomerOrder` |
| 订单列表 | `getCustomerOrders({status})` | Tab 切换沿用 `watch(activeTab, loadOrders)` |
| 订单详情 | `getCustomerOrderDetail(orderNo)` | `handlePay` / `handleCancel` / `handleConfirm` 按状态挂到设计稿底部按钮 |
| 我的 | `getMemberLevels()` + `getCustomerInfo()` | 等级/积分/进度换算沿用原 computed |
| 我的订单九宫格 / 资产列表 | `goOrder(status)` / `goPage(path)` | 语义匹配项全部接上 |

---

## 3. 接口未被改动的验证方式

```bash
cd C:/Users/hdw13/WorkBuddy/Worktrees/Walmart/main-34fc41dd

# 1) 接口层 / 请求层 / 状态层 / 路由 / 后端：应当完全没有输出
git diff --name-only -- shop/src/api shop/src/utils shop/src/stores shop/src/router src/

# 2) 只看前端页面与入口的改动
git diff --stat -- shop/src

# 3) 外观保真度自动校验（每个页面都应输出 missing in page : 0）
python .workbuddy/stitch-src/check_fidelity.py .workbuddy/stitch-src/_1.html shop/src/views/Home.vue
python .workbuddy/stitch-src/check_fidelity.py .workbuddy/stitch-src/_4.html shop/src/views/Cart.vue
# …其余 _2→Category、_3→Search、_5→ProductDetail、_6→OrderConfirm、
#   _7→OrderResult、_8→OrderList、_9→OrderDetail、_10→Profile
```

---

## 4. 测试步骤

### 4.1 启动后端 + 前端

```bash
# 1) 后端（Spring Boot，端口 8080）
cd D:/专业/spring/Walmart && mvn spring-boot:run

# 2) 前端（端口 5174，已配置 /api 与 /uploads 代理到 8080）
cd <worktree>/shop && npm install && npm run dev
# 浏览器打开 http://localhost:5174/
# 建议用 Chrome DevTools 切到手机尺寸（375×812）
```

### 4.2 用浏览器 Network 面板核对接口对接（核心验证）

打开 DevTools → Network → 过滤 `customer`，依次操作，确认请求真实发出且返回 `code: 200`：

| 操作 | 期望请求 | 页面表现 |
|---|---|---|
| 打开首页 | `GET /api/homeCategory/customer/list`、`GET /api/customer/product/category/list`、`GET /api/customer/product/list?pageNum=1&pageSize=20` | 金刚区出现真实分类名与图标；「热卖单品」出现真实商品名/价格/图片 |
| 点金刚区分类 | `GET /api/customer/product/list?categoryId=xx` | 跳转发分类页并选中对应分类 |
| 点商品卡 | `GET /api/customer/product/{id}` | 进入详情页，标题/价格/库存/描述均为真实数据 |
| 详情页点「加入购物车」 | `POST /api/customer/cart/add` → `GET /api/customer/cart/list` | Toast「已加入购物车」，底部 Tab 购物车角标 +1 |
| 进购物车页 | `GET /api/customer/cart/list` | 行内出现真实商品；改数量触发 `PUT /api/customer/cart/update`；勾选影响底部合计 |
| 详情页点「立即购买」/ 购物车点「结算」 | 进入确认订单 | 地址 `GET /api/customer/address/list`、优惠券 `GET /api/customer/coupon/list` |
| 确认订单点「提交订单」 | `POST /api/customer/order/create` | 跳转下单成功页，URL 带真实 `orderNo` |
| 下单成功页 | 「订单编号」显示真实 orderNo；点「查看订单详情」→ `GET /api/customer/order/detail/{orderNo}` | — |
| 我的订单页 | `GET /api/customer/order/list?pageNum=1&pageSize=20[&status=]` | 列表出现真实订单；切 Tab 触发对应 status 查询 |
| 订单详情页 | 待付款订单点「去付款」→ `PUT /api/customer/order/pay/{orderNo}`；点「取消订单」→ `PUT /api/customer/order/cancel/{orderNo}`；待收货点「确认收货」→ `PUT /api/customer/order/confirm/{orderNo}` | 状态刷新 |
| 搜索页 | 输入关键词搜索 → `GET /api/customer/product/list?keyword=xx`；`GET /api/customer/product/hot`、`/latest` | 结果列表为真实商品 |
| 我的页 | `GET /api/customer/auth/levels`、`GET /api/customer/auth/info` | 昵称/手机号/等级/积分/升级进度为真实数据 |

> 未登录时首页不请求接口（原逻辑 `if (!getToken())`），请先走 `/login` 用测试账号 `13900139001 / 123456` 登录。

### 4.3 冒烟脚本（不依赖浏览器，最快确认接口连通）

```bash
# 1) 登录拿 token
curl -s -X POST http://localhost:8080/api/customer/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13900139001","password":"123456"}'

# 2) 用返回的 token 依次验证核心接口
TOKEN=<上一步 data.token>
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/customer/product/list?pageNum=1&pageSize=20
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/homeCategory/customer/list
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/customer/cart/list
curl -s -X POST -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":1}' http://localhost:8080/api/customer/cart/add
```

项目自身的接口地址定义在 `shop/src/api/index.js`（**未改动**），可对照核验。

### 4.4 回归检查清单

- [ ] 首页 → 分类 → 商品详情 → 加购 → 购物车 → 结算 → 下单成功，全链路跑通
- [ ] 购物车角标数量与实际一致
- [ ] 订单列表 5 个 Tab 均可切换且触发请求
- [ ] 待付款订单可付款/取消，待收货可确认收货
- [ ] 「我的」页退出登录可正常跳登录页
- [ ] `Login / Address / Coupon / Favorites` 四个未改造页面仍正常（Vant 组件未受影响）
- [ ] 图标正常显示（Material Symbols 已本地化，不依赖外网）

---

## 5. 已知事项

1. **设计稿中的装饰性图片**（`lh3.googleusercontent.com`）在国内网络不可达，页面会显示为破图。这些是设计稿的占位素材，建议替换为本地素材：把图片放到 `shop/public/`，再将页面里对应的 `src` 改为 `/xxx.jpg`。真实商品图走接口 `imageUrl`，缺图自动回落到 `/placeholder.svg`。
2. 营销位（banner、榜单、促销楼层、优惠券示例卡、示例文案）接口没有对应数据源，保留设计稿静态内容；后续如需数据化，需先在后端补接口。
3. `Category` 页左侧「荐/热」角标为设计稿的装饰标记，按位置（选中项/末项）渲染，不来自接口。
4. `OrderList` 卡内的「取消订单/立即付款/修改地址/提醒发货/查看物流/确认收货」按钮，原页面就没有对应函数且不允许新增接口调用，因此仅保留外观；这些操作当前通过进入订单详情页完成（详情页的付款/取消/确认收货已接上真实接口）。
5. `Cart` 页设计稿里没有删除商品的入口，因此 `handleDelete()`（原逻辑：确认弹窗 + `removeItem`）保留在脚本中但界面上没有可触发的位置；若需要保留删除能力，建议我再补一个「左滑删除」或长按删除（属于新增交互，需你确认）。
6. `ProductDetail` 的划线原价、`Search` 的划线原价、销量/评价数等字段后端 `Product` 实体没有（实体仅含 id / productName / categoryId / price / stock / status / description / imageUrl / categoryName），已用 `v-if` 处理为数据存在才显示。
