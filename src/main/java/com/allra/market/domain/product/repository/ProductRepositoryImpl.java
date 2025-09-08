package com.allra.market.domain.product.repository;

import static com.allra.market.domain.product.QProduct.product;

import com.allra.market.domain.product.request.ProductSearchCondition;
import com.allra.market.domain.product.response.ProductResponse;
import com.allra.market.domain.product.response.QProductResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductResponse> search(ProductSearchCondition condition, Pageable pageable) {
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
                .where(categoryEq(condition.categoryId()),
                        productNameEq(condition.productName()),
                        priceGoe(condition.minPrice()),
                        priceLoe(condition.maxPrice()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .join(product.category)
                .where(categoryEq(condition.categoryId()),
                        productNameEq(condition.productName()),
                        priceGoe(condition.minPrice()),
                        priceLoe(condition.maxPrice()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
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
