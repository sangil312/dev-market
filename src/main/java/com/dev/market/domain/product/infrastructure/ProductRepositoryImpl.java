package com.dev.market.domain.product.infrastructure;

import static com.dev.market.domain.product.domain.QProduct.product;

import com.dev.market.domain.product.application.request.ProductSearchServiceRequest;
import com.dev.market.domain.product.domain.Product;
import com.dev.market.domain.product.domain.repository.ProductRepositoryCustom;
import com.dev.market.domain.product.application.response.ProductResponse;
import com.dev.market.domain.product.application.response.QProductResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductResponse> search(ProductSearchServiceRequest request, Pageable pageable) {
        List<ProductResponse> content = queryFactory
                .select(new QProductResponse(
                        product.id,
                        product.category.id,
                        product.name,
                        product.price,
                        product.quantity,
                        product.quantity.eq(0)))
                .from(product)
                .join(product.category)
                .where(categoryEq(request.categoryId()),
                        productNameEq(request.productName()),
                        priceGoe(request.minPrice()),
                        priceLoe(request.maxPrice()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .join(product.category)
                .where(categoryEq(request.categoryId()),
                        productNameEq(request.productName()),
                        priceGoe(request.minPrice()),
                        priceLoe(request.maxPrice()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Product> findAllWithPessimisticLockByIdIn(List<Long> productIds) {
        return queryFactory
                .selectFrom(product)
                .where(product.id.in(productIds))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }

    private BooleanExpression productNameEq(String productName) {
        return productName == null ? null : product.name.contains(productName);
    }

    private BooleanExpression categoryEq(Long categoryId) {
        return categoryId == null ? null : product.category.id.eq(categoryId);
    }

    private BooleanExpression priceGoe(Long minPrice) {
        return minPrice == null ? null : product.price.goe(minPrice);
    }

    private BooleanExpression priceLoe(Long maxPrice) {
        return maxPrice == null ? null : product.price.loe(maxPrice);
    }
}
