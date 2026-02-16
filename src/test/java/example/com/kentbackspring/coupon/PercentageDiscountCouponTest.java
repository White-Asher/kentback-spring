package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PercentageDiscountCouponTest {

    @Test
    void shouldDiscountProductPriceByGivenPercentage() {
        int productPrice = 10_000;
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(20);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(8_000);
    }

    @Test
    void shouldTruncateDiscountAmountToWonUnit() {
        int productPrice = 9_999;
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(15);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(8_500);
    }

    @Test
    void shouldNotExceedMaximumDiscountAmountWhenCapExists() {
        int productPrice = 50_000;
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(30, 10_000);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(40_000);
    }

    @Test
    void shouldMakeProductPriceZeroWhenDiscountRateIsOneHundredPercent() {
        int productPrice = 12_345;
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(100);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(0);
    }
}
