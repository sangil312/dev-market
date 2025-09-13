package com.allra.market.domain.product.domain;

import com.allra.market.common.entity.BaseEntity;
import com.allra.market.domain.cart.domain.CartItem;
import com.allra.market.domain.category.domain.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private String name;

    private Long price;

    private Integer quantity;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

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
    public boolean isQuantityLessThan(int quantity) {
        return this.quantity < quantity;
    }

    public void decreaseQuantity(int quantity) {
        this.quantity -= quantity;
    }

    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }
}
