package com.allra.market.domain.product.domain;

import com.allra.market.common.entity.BaseEntity;
import com.allra.market.domain.category.domain.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private String name;

    private Long price;

    private Integer quantity;

    /* 정적 팩토리 메서드 */
    public static Product create(Category category, String name, Long price, Integer quantity) {
        Product product = new Product();
        product.category = category;
        product.name = name;
        product.price = price;
        product.quantity = quantity;
        return product;
    }

    /* 비지니스 메서드 */
    public boolean isQuantityOver(int quantity) {
        return this.quantity < quantity;
    }
}
