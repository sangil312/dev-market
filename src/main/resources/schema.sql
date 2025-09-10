DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE category (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(100)  NOT NULL COMMENT '카테고리명',
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    UNIQUE KEY uk_name (name)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE product (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id     BIGINT        NOT NULL COMMENT '카테고리 ID',
    name            VARCHAR(255)  NOT NULL COMMENT '상품명',
    price           BIGINT        NOT NULL DEFAULT 0 COMMENT '상품 가격',
    quantity        INT           NOT NULL DEFAULT 0 COMMENT '상품 수량',
    version         INT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category(id),

    INDEX idx_category_id (category_id),
    INDEX idx_product_search (category_id, price)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE cart (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT    NOT NULL COMMENT '사용자 ID',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    UNIQUE KEY uk_user_id (user_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE cart_item (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id             BIGINT        NOT NULL COMMENT '장바구니 ID',
    product_id          BIGINT        NOT NULL COMMENT '상품 ID',
    quantity            INT           NOT NULL DEFAULT 0 COMMENT '상품 수량',
    unit_price          BIGINT        NOT NULL DEFAULT 0 COMMENT '상품 개당 가격',
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',

    CONSTRAINT fk_cart_id FOREIGN KEY (cart_id) REFERENCES cart(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES product(id),

    UNIQUE KEY uk_cart_id_product_id (cart_id, product_id),

    INDEX idx_cart_id (cart_id),
    INDEX idx_category_id (product_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;