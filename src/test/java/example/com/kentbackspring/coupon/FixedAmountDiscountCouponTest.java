package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixedAmountDiscountCouponTest {

    @Test
    void shouldSubtractDiscountAmountFromProductPrice() {
        // 정액 쿠폰은 상품 금액에서 할인 금액만큼 정확히 차감되어야 한다.
        int productPrice = 10_000;
        int discountAmount = 1_000;
        FixedAmountDiscountCoupon coupon = new FixedAmountDiscountCoupon(discountAmount);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(9_000);
    }

    @Test
    void shouldReturnZeroWhenDiscountExceedsProductPrice() {
        // 할인 금액이 상품 금액을 초과해도 최종 금액은 0원으로 제한된다.
        int productPrice = 5_000;
        int discountAmount = 10_000;
        FixedAmountDiscountCoupon coupon = new FixedAmountDiscountCoupon(discountAmount);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isEqualTo(0);
    }

    @Test
    void shouldNeverReturnNegativeAmountAfterApplyingFixedDiscount() {
        // 어떤 입력에서도 할인 결과가 음수가 되면 안 된다.
        int productPrice = 1_000;
        int discountAmount = 3_000;
        FixedAmountDiscountCoupon coupon = new FixedAmountDiscountCoupon(discountAmount);

        int discountedPrice = coupon.apply(productPrice);

        assertThat(discountedPrice).isGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldRejectNegativeDiscountAmount() {
        // 음수 할인 금액은 잘못된 쿠폰 정의이므로 예외를 던져야 한다.
        assertThatThrownBy(() -> new FixedAmountDiscountCoupon(-1_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("discountAmount must be greater than or equal to 0");
    }
}
