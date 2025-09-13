package com.allra.market;

import com.allra.market.domain.cart.application.CartService;
import com.allra.market.domain.cart.interfaces.CartController;
import com.allra.market.domain.order.application.OrderAndPayFacadeService;
import com.allra.market.domain.order.interfaces.OrderController;
import com.allra.market.domain.product.interfaces.ProductController;
import com.allra.market.domain.product.application.ProductService;
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
    protected ProductService productService;

    @MockitoBean
    protected CartService cartService;

    @MockitoBean
    protected OrderAndPayFacadeService orderAndPayFacadeService;
}
