DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE categories (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(100)  NOT NULL COMMENT '카테고리명',
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    UNIQUE KEY uk_name (name)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE products (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id     BIGINT        NOT NULL COMMENT '카테고리 ID',
    name            VARCHAR(255)  NOT NULL COMMENT '상품명',
    price           BIGINT        NOT NULL DEFAULT 0 COMMENT '상품 가격',
    quantity        INT           NOT NULL DEFAULT 0 COMMENT '상품 수량',
    version         INT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id),

    INDEX idx_category_id (category_id),
    INDEX idx_product_search (category_id, price)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE carts (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT    NOT NULL COMMENT '사용자 ID',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    CONSTRAINT fk_user_carts FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    UNIQUE KEY uk_user_id (user_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE cart_items (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id             BIGINT NOT NULL COMMENT '장바구니 ID',
    product_id          BIGINT NOT NULL COMMENT '상품 ID',
    quantity            INT    NOT NULL DEFAULT 0 COMMENT '상품 수량',
    unit_price          BIGINT NOT NULL DEFAULT 0 COMMENT '장바구니 상품 개당 가격',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    CONSTRAINT fk_cart_cartitems FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_cartitems FOREIGN KEY (product_id) REFERENCES products(id),

    UNIQUE KEY uk_cart_id_product_id (cart_id, product_id),

    INDEX idx_cart_id (cart_id),
    INDEX idx_category_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE orders (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT      NOT NULL COMMENT '사용자 ID',
    total_price     BIGINT      NOT NULL COMMENT '주문 금액',
    status          VARCHAR(20) NOT NULL COMMENT 'CREATED, PAYMENT_FAILED, PAID, CANCELED',
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_users_orders FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE order_items (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    BIGINT  NOT NULL COMMENT '주문 ID',
    product_id  BIGINT  NOT NULL COMMENT '상품 ID',
    quantity    BIGINT  NOT NULL COMMENT '주문 상품 수량',
    unit_price  BIGINT  NOT NULL COMMENT '주문 상품 개당 가격',

    CONSTRAINT fk_orders_orderitems FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_orderitems FOREIGN KEY (product_id) REFERENCES products(id),

    UNIQUE KEY uk_order_product (order_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE payments (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id        BIGINT       NOT NULL COMMENT '주문 ID',
    amount          BIGINT       NOT NULL COMMENT '결제 금액',
    status          VARCHAR(20)  NOT NULL COMMENT 'SUCCESS, FAILED',
    transaction_id  VARCHAR(100) NOT NULL COMMENT '외부 결제 API 트랜잭션 ID',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_payments FOREIGN KEY (order_id) REFERENCES orders(id),

    UNIQUE KEY uk_order_id(order_id),
    UNIQUE KEY uk_transaction_id(transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;