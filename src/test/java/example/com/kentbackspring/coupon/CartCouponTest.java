package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 장바구니 쿠폰 테스트.
 */
class CartCouponTest {

    @Test
    void shouldApplyFixedDiscountToCartTotalAmount() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 10_000, 1),
                new Product("P-200", "FOOD", "B-2", 5_000, 1)
        ), 0);
        CartCoupon coupon = CartCoupon.fixedAmount(3_000, 0);

        CartCouponResult result = coupon.apply(cart);

        assertThat(result.totalDiscount()).isEqualTo(3_000);
        assertThat(result.discountedProductTotal()).isEqualTo(12_000);
    }

    @Test
    void shouldApplyPercentageDiscountToCartTotalAmount() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 10_000, 1),
                new Product("P-200", "FOOD", "B-2", 10_000, 1)
        ), 0);
        CartCoupon coupon = CartCoupon.percentage(10, 0);

        CartCouponResult result = coupon.apply(cart);

        assertThat(result.totalDiscount()).isEqualTo(2_000);
        assertThat(result.discountedProductTotal()).isEqualTo(18_000);
    }

    @Test
    void shouldNotApplyCartCouponWhenBelowMinimumOrderAmount() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 10_000, 1),
                new Product("P-200", "FOOD", "B-2", 5_000, 1)
        ), 0);
        CartCoupon coupon = CartCoupon.fixedAmount(3_000, 20_000);

        assertThatThrownBy(() -> coupon.apply(cart))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("minimum order amount not met");
    }
}
