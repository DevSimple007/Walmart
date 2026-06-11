CREATE DATABASE IF NOT EXISTS walmart_oms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE walmart_oms;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS shopping_cart;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS inventory_out;
DROP TABLE IF EXISTS inventory_in;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS member_points;
DROP TABLE IF EXISTS member_card;
DROP TABLE IF EXISTS shipping_address;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS member_level;
DROP TABLE IF EXISTS warehouse;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS brand;
DROP TABLE IF EXISTS product_category;
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

-- 系统用户
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    theme VARCHAR(20) DEFAULT 'default',
    status INT DEFAULT 1 COMMENT '1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_sys_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统角色
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统菜单
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0,
    menu_name VARCHAR(50) NOT NULL,
    path VARCHAR(200),
    component VARCHAR(200),
    perms VARCHAR(100),
    type INT DEFAULT 0 COMMENT '0-目录，1-菜单，2-按钮',
    icon VARCHAR(50),
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_sys_menu_parent (parent_id),
    KEY idx_sys_menu_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户角色关联
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_sys_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_sys_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色菜单关联
CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, menu_id),
    CONSTRAINT fk_sys_role_menu_role FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    CONSTRAINT fk_sys_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品分类
CREATE TABLE product_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_product_category_parent (parent_id),
    KEY idx_product_category_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 品牌
CREATE TABLE brand (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    brand_name VARCHAR(50) NOT NULL,
    logo VARCHAR(200),
    description TEXT,
    status INT DEFAULT 1 COMMENT '1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_brand_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 商品
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL,
    category_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    status INT DEFAULT 1 COMMENT '1-在售，0-下架',
    description TEXT,
    image_url VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES product_category(id),
    KEY idx_product_category (category_id),
    KEY idx_product_status (status),
    KEY idx_product_price (price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 仓库
CREATE TABLE warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    warehouse_name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    contact VARCHAR(50),
    phone VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会员等级
CREATE TABLE member_level (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    level_name VARCHAR(50) NOT NULL,
    min_points INT DEFAULT 0,
    max_points INT DEFAULT 0,
    discount DECIMAL(3,2) DEFAULT 1.00,
    status INT DEFAULT 1 COMMENT '1-启用，0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会员
CREATE TABLE member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(100) NOT NULL DEFAULT '123456',
    email VARCHAR(100),
    address VARCHAR(200),
    level_id BIGINT,
    points INT DEFAULT 0,
    balance DECIMAL(10,2) DEFAULT 0.00,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_level FOREIGN KEY (level_id) REFERENCES member_level(id),
    UNIQUE KEY uk_member_phone (phone),
    KEY idx_member_level (level_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会员卡
CREATE TABLE member_card (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    member_name VARCHAR(50) NOT NULL,
    card_no VARCHAR(50) NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    status INT DEFAULT 1 COMMENT '1-正常，0-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_card_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    UNIQUE KEY uk_member_card_no (card_no),
    KEY idx_member_card_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 会员积分流水
CREATE TABLE member_points (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    member_name VARCHAR(50) NOT NULL,
    points INT NOT NULL,
    type INT NOT NULL COMMENT '1-增加，0-减少',
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_points_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    KEY idx_member_points_member (member_id),
    KEY idx_member_points_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 库存
CREATE TABLE inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_inventory_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id) ON DELETE CASCADE,
    UNIQUE KEY uk_inventory_product_warehouse (product_id, warehouse_id),
    KEY idx_inventory_quantity (quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 入库单
CREATE TABLE inventory_in (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    in_no VARCHAR(50) NOT NULL,
    warehouse_id BIGINT NOT NULL,
    warehouse_name VARCHAR(100) NOT NULL,
    total_quantity INT DEFAULT 0,
    operator VARCHAR(50),
    remark VARCHAR(255),
    status INT DEFAULT 1 COMMENT '0-草稿，1-已完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_in_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
    UNIQUE KEY uk_inventory_in_no (in_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 出库单
CREATE TABLE inventory_out (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    out_no VARCHAR(50) NOT NULL,
    warehouse_id BIGINT NOT NULL,
    warehouse_name VARCHAR(100) NOT NULL,
    total_quantity INT DEFAULT 0,
    operator VARCHAR(50),
    remark VARCHAR(255),
    status INT DEFAULT 1 COMMENT '0-草稿，1-已完成',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_out_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id),
    UNIQUE KEY uk_inventory_out_no (out_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单
CREATE TABLE `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL COMMENT '此处存会员ID',
    total_amount DECIMAL(10,2) NOT NULL,
    remark VARCHAR(255),
    status INT DEFAULT 0 COMMENT '0-待支付，1-已支付，2-待发货，3-已发货，4-已完成，5-已取消',
    delivery_type VARCHAR(20) DEFAULT 'pickup' COMMENT 'pickup-到店自提, delivery-送货上门',
    address VARCHAR(255) COMMENT '收货地址',
    phone VARCHAR(20) COMMENT '联系电话',
    delivery_time VARCHAR(50) COMMENT '期望送达时间',
    coupon_id BIGINT DEFAULT NULL COMMENT '使用的优惠券ID',
    coupon_discount DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠券优惠金额',
    actual_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '实付金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_member FOREIGN KEY (user_id) REFERENCES member(id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_order_user (user_id),
    KEY idx_order_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单明细
CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product(id),
    KEY idx_order_item_order (order_id),
    KEY idx_order_item_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 购物车
CREATE TABLE shopping_cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    UNIQUE KEY uk_cart_member_product (member_id, product_id),
    KEY idx_cart_member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收货地址
CREATE TABLE IF NOT EXISTS shipping_address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    address VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default INT DEFAULT 0 COMMENT '1-默认，0-非默认',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_address_member FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    KEY idx_address_member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;