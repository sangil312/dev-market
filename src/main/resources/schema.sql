DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS product;

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

    INDEX idx_category_id (category_id),
    INDEX idx_product_search (category_id, price)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;