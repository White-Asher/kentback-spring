package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import example.com.kentbackspring.coupon.FixedAmountDiscountCoupon;

import static org.assertj.core.api.Assertions.assertThat;

class FixedAmountDiscountCouponTest {

    @Test
    void shouldSubtractDiscountAmountFromProductPrice() {
        int productPrice = 10_000;
        int discountAmount = 1_000;
        FixedAmountDiscountCoupon coupon = new FixedAmountDiscountCoupon(discountAmount);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(9_000);
    }

    @Test
    void shouldReturnZeroWhenDiscountExceedsProductPrice() {
        int productPrice = 5_000;
        int discountAmount = 10_000;
        FixedAmountDiscountCoupon coupon = new FixedAmountDiscountCoupon(discountAmount);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(0);
    }
}
