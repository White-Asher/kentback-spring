package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 정률 할인 및 최소 구매 조건 규칙 검증 테스트.
 */
class PercentageDiscountCouponTest {

    @Test
    void shouldDiscountProductPriceByGivenPercentage() {
        // 상품 금액에 할인율이 정상 적용되는지 검증한다.
        int productPrice = 10_000;
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(20);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(8_000);
    }

    @Test
    void shouldTruncateDiscountAmountToWonUnit() {
        // 할인액 계산 시 정수 연산으로 원 단위 절사가 되는지 검증한다.
        int productPrice = 9_999;
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(15);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(8_500);
    }

    @Test
    void shouldNotExceedMaximumDiscountAmountWhenCapExists() {
        // 최대 할인 한도 설정 시 한도 이상 할인되지 않아야 한다.
        int productPrice = 50_000;
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(30, 10_000);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(40_000);
    }

    @Test
    void shouldMakeProductPriceZeroWhenDiscountRateIsOneHundredPercent() {
        // 100% 할인은 결과 금액이 정확히 0원이 되어야 한다.
        int productPrice = 12_345;
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(100);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(0);
    }

    @Test
    void shouldNotApplyCouponWhenBelowMinimumPurchaseAmount() {
        // 최소 구매 금액을 못 채우면 쿠폰 적용이 불가해야 한다.
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(10, null, 10_000, 1);

        assertThatThrownBy(() -> coupon.apply(9_000, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("minimum purchase amount not met");
    }

    @Test
    void shouldApplyCouponWhenExactlyMeetingMinimumPurchaseAmount() {
        // 최소 구매 금액을 정확히 만족하면 쿠폰 적용이 가능해야 한다.
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(10, null, 10_000, 1);

        int discountedPrice = coupon.apply(10_000, 1);

        assertThat(discountedPrice).isEqualTo(9_000);
    }

    @Test
    void shouldNotApplyCouponWhenBelowMinimumPurchaseQuantity() {
        // 최소 구매 수량을 못 채우면 쿠폰 적용이 불가해야 한다.
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(10, null, 0, 2);

        assertThatThrownBy(() -> coupon.apply(10_000, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("minimum purchase quantity not met");
    }

    @Test
    void shouldValidateMinimumPurchaseBasedOnAmountBeforeDiscount() {
        // 최소 구매 금액 검증은 할인 후가 아니라 할인 전 총액 기준이어야 한다.
        PercentageDiscountCoupon coupon = new PercentageDiscountCoupon(90, null, 10_000, 1);

        int discountedPrice = coupon.apply(10_000, 1);

        assertThat(discountedPrice).isEqualTo(1_000);
    }
}
