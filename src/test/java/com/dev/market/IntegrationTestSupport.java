package com.dev.market;

import com.dev.market.domain.product.infrastructure.PaymentGatewayServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

    @MockitoBean
    protected PaymentGatewayServiceImpl PaymentGatewayServiceImpl;
}
