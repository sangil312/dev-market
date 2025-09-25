package com.dev.market;

import com.dev.market.domain.cart.application.CartService;
import com.dev.market.domain.cart.interfaces.CartController;
import com.dev.market.domain.order.application.OrderAndPayFacadeService;
import com.dev.market.domain.order.interfaces.OrderController;
import com.dev.market.domain.product.interfaces.ProductController;
import com.dev.market.domain.product.application.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        ProductController.class,
        CartController.class,
        OrderController.class,
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected ProductServiceImpl productServiceImpl;

    @MockitoBean
    protected CartService cartService;

    @MockitoBean
    protected OrderAndPayFacadeService orderAndPayFacadeService;
}
