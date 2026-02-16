package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void shouldNotApplyCouponWhenBelowMinimumPurchaseAmount() {
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(10, null, 10_000, 1);

        assertThatThrownBy(() -> coupon.apply(9_000, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("minimum purchase amount not met");
    }

    @Test
    void shouldApplyCouponWhenExactlyMeetingMinimumPurchaseAmount() {
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(10, null, 10_000, 1);

        int discountedPrice = coupon.apply(10_000, 1);

        assertThat(discountedPrice).isEqualTo(9_000);
    }

    @Test
    void shouldNotApplyCouponWhenBelowMinimumPurchaseQuantity() {
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(10, null, 0, 2);

        assertThatThrownBy(() -> coupon.apply(10_000, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("minimum purchase quantity not met");
    }

    @Test
    void shouldValidateMinimumPurchaseBasedOnAmountBeforeDiscount() {
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(90, null, 10_000, 1);

        int discountedPrice = coupon.apply(10_000, 1);

        assertThat(discountedPrice).isEqualTo(1_000);
    }
}
