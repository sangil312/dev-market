package com.dev.market.domain.product.application;

import static com.dev.market.common.exception.enums.ErrorCode.PRODUCT_NOT_FOUND;
import static com.dev.market.common.exception.enums.ErrorCode.PRODUCT_QUANTITY_OVER;

import com.dev.market.common.exception.NotFoundException;
import com.dev.market.common.exception.QuantityOverException;
import com.dev.market.domain.cart.domain.CartItem;
import com.dev.market.domain.order.domain.OrderItem;
import com.dev.market.domain.product.application.request.ProductSearchCondition;
import com.dev.market.domain.product.domain.Product;
import com.dev.market.domain.product.domain.repository.ProductRepository;
import com.dev.market.domain.product.application.response.ProductResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(
            final ProductSearchCondition condition,
            final Pageable pageable
    ) {
        return productRepository.search(condition, pageable);
    }

    @Transactional
    public void productDecreasesStock(List<CartItem> cartItems) {
        List<Long> productIds = cartItems.stream()
                .map(item -> item.getProduct().getId())
                .toList();

        List<Product> requestProducts = productRepository.findAllWithPessimisticLockByIdIn(productIds);
        Map<Long, Product> productMap = requestProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 재고 차감
        for (CartItem item : cartItems) {
            Product product = productMap.get(item.getProduct().getId());
            if (Objects.isNull(product)) {
                throw new NotFoundException(PRODUCT_NOT_FOUND);
            }
            if (product.isQuantityLessThan(item.getQuantity())) {
                throw new QuantityOverException(PRODUCT_QUANTITY_OVER);
            }
            product.decreaseQuantity(item.getQuantity());
        }
    }

    @Transactional
    public void productStockRollback(List<OrderItem> orderItems) {
        List<Long> productIds = orderItems.stream()
                .map(orderItem -> orderItem.getProduct().getId())
                .toList();

        List<Product> rollbackProducts = productRepository.findAllWithPessimisticLockByIdIn(productIds);

        Map<Long, Product> productMap = rollbackProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 재고 롤백
        for (OrderItem orderItem : orderItems) {
            Product product = productMap.get(orderItem.getProduct().getId());
            product.increaseQuantity(orderItem.getQuantity());
        }
    }
}
