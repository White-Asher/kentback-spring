package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 쿠폰 중복 정책 테스트.
 */
class CouponPolicyServiceTest {

    private final CouponPolicyService policyService = new CouponPolicyService();

    @Test
    void shouldAllowOnlyOneCouponPerOrder() {
        CouponCandidate coupon = new CouponCandidate("COUPON-A", 1_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));

        CouponCandidate chosen = policyService.chooseSingleCoupon(List.of(coupon), null);

        assertThat(chosen.code()).isEqualTo("COUPON-A");
    }

    @Test
    void shouldThrowWhenApplyingMultipleCouponsSimultaneously() {
        CouponCandidate couponA = new CouponCandidate("COUPON-A", 1_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));
        CouponCandidate couponB = new CouponCandidate("COUPON-B", 2_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));

        assertThatThrownBy(() -> policyService.chooseSingleCoupon(List.of(couponA, couponB), null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("multiple coupons are not allowed");
    }

    @Test
    void shouldAllowUserToSelectCouponExplicitly() {
        CouponCandidate couponA = new CouponCandidate("COUPON-A", 1_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));
        CouponCandidate couponB = new CouponCandidate("COUPON-B", 2_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));

        CouponCandidate chosen = policyService.chooseSingleCoupon(List.of(couponA, couponB), "COUPON-B");

        assertThat(chosen.code()).isEqualTo("COUPON-B");
    }

    @Test
    void shouldAutomaticallySelectCouponWithLargestDiscount() {
        CouponCandidate couponA = new CouponCandidate("COUPON-A", 1_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));
        CouponCandidate couponB = new CouponCandidate("COUPON-B", 3_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));
        CouponCandidate couponC = new CouponCandidate("COUPON-C", 2_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));

        CouponCandidate selected = policyService.selectBestCoupon(List.of(couponA, couponB, couponC));

        assertThat(selected.code()).isEqualTo("COUPON-B");
    }

    @Test
    void shouldSelectEarliestExpiringCouponWhenFixedDiscountIsSame() {
        CouponCandidate couponA = new CouponCandidate("COUPON-A", 2_000, LocalDateTime.of(2026, 2, 25, 23, 59, 59));
        CouponCandidate couponB = new CouponCandidate("COUPON-B", 2_000, LocalDateTime.of(2026, 2, 20, 23, 59, 59));

        CouponCandidate selected = policyService.selectBestCoupon(List.of(couponA, couponB));

        assertThat(selected.code()).isEqualTo("COUPON-B");
    }

    @Test
    void shouldApplyProductCouponThenCartCouponSequentially() {
        CouponCandidate productCoupon = new CouponCandidate("PRODUCT", 1_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));
        CouponCandidate cartCoupon = new CouponCandidate("CART", 2_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));

        SequentialDiscountResult result = policyService.applySequential(10_000, productCoupon, cartCoupon);

        assertThat(result.afterProductCoupon()).isEqualTo(9_000);
        assertThat(result.afterCartCoupon()).isEqualTo(7_000);
    }

    @Test
    void shouldDefineSequentialOrderAsProductThenCart() {
        CouponCandidate productCoupon = new CouponCandidate("PRODUCT", 9_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));
        CouponCandidate cartCoupon = new CouponCandidate("CART", 1_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));

        SequentialDiscountResult result = policyService.applySequential(10_000, productCoupon, cartCoupon);

        assertThat(result.afterProductCoupon()).isEqualTo(1_000);
        assertThat(result.afterCartCoupon()).isEqualTo(0);
    }

    @Test
    void shouldCompareSequentialResultAgainstNoStackingPolicy() {
        CouponCandidate productCoupon = new CouponCandidate("PRODUCT", 1_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));
        CouponCandidate cartCoupon = new CouponCandidate("CART", 2_000, LocalDateTime.of(2026, 2, 28, 23, 59, 59));
        CouponCandidate bestSingle = new CouponCandidate("SINGLE-BEST", 2_500, LocalDateTime.of(2026, 2, 28, 23, 59, 59));

        boolean better = policyService.isSequentialBetterThanNoStackingPolicy(
                10_000,
                productCoupon,
                cartCoupon,
                List.of(bestSingle)
        );

        assertThat(better).isTrue();
    }
}
