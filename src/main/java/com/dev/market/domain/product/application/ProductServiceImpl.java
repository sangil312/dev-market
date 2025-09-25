package com.dev.market.domain.product.application;

import com.dev.market.domain.order.domain.OrderItem;
import com.dev.market.domain.product.application.request.ProductSearchServiceRequest;
import com.dev.market.domain.product.domain.Product;
import com.dev.market.domain.product.domain.repository.ProductRepository;
import com.dev.market.domain.product.application.response.ProductResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> searchProducts(
            final ProductSearchServiceRequest condition,
            final Pageable pageable
    ) {
        return productRepository.search(condition, pageable);
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
