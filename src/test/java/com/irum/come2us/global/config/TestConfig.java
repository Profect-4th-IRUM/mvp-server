package com.irum.come2us.global.config;

import com.irum.come2us.domain.auth.application.service.AuthService;
import com.irum.come2us.domain.auth.application.service.JwtTokenService;
import com.irum.come2us.domain.cart.application.service.CartService;
import com.irum.come2us.domain.category.application.service.CategoryService;
import com.irum.come2us.domain.deliveryaddress.application.service.DeliveryAddressService;
import com.irum.come2us.domain.discount.application.service.DiscountService;
import com.irum.come2us.domain.member.application.service.ManagerService;
import com.irum.come2us.domain.member.application.service.MemberService;
import com.irum.come2us.domain.order.application.service.CustomerOrderService;
import com.irum.come2us.domain.order.application.service.OwnerOrderService;
import com.irum.come2us.domain.order.application.service.SalesService;
import com.irum.come2us.domain.product.application.service.ProductImageService;
import com.irum.come2us.domain.refund.application.service.RefundService;
import com.irum.come2us.domain.review.application.service.ReviewService;
import com.irum.come2us.global.util.CookieUtil;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public MemberService memberService() {
        return Mockito.mock(MemberService.class);
    }

    @Bean
    public ManagerService managerService() {
        return Mockito.mock(ManagerService.class);
    }

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }

    @Bean
    public DeliveryAddressService deliveryAddressService() {
        return Mockito.mock(DeliveryAddressService.class);
    }

    @Bean
    public CookieUtil cookieUtil() {
        return Mockito.mock(CookieUtil.class);
    }

    @Bean
    public JwtTokenService jwtTokenService() {
        return Mockito.mock(JwtTokenService.class);
    }

    @Bean
    public RefundService refundService() {
        return Mockito.mock(RefundService.class);
    }

    @Bean
    public DiscountService discountService() {
        return Mockito.mock(DiscountService.class);
    }

    @Bean
    public CategoryService categoryService() {
        return Mockito.mock(CategoryService.class);
    }

    @Bean
    public ReviewService reviewService() {
        return Mockito.mock(ReviewService.class);
    }

    public CartService cartService() {
        return Mockito.mock(CartService.class);
    }

    public OwnerOrderService ownerOrderService() {
        return Mockito.mock(OwnerOrderService.class);
    }

    @Bean
    public CustomerOrderService customerOrderService() {
        return Mockito.mock(CustomerOrderService.class);
    }

    @Bean
    public SalesService salesService() {
        return Mockito.mock(SalesService.class);
    }

    @Bean
    public ProductImageService productImageService() {
        return Mockito.mock(ProductImageService.class);
    }
}
