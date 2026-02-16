package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 상품 쿠폰 적용 조건 테스트.
 */
class ProductCouponTest {

    @Test
    void shouldCreateCouponForSpecificProductId() {
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100"), 1_000);

        assertThat(coupon.targetProductIds()).containsExactly("P-100");
    }

    @Test
    void shouldNotApplyCouponWhenProductIsNotTarget() {
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100"), 1_000);
        Product product = new Product("P-999", "C-1", "B-1", 10_000, 1);

        assertThat(coupon.canApply(product)).isFalse();
    }

    @Test
    void shouldApplyCouponWhenProductIsTarget() {
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100"), 1_000);
        Product product = new Product("P-100", "C-1", "B-1", 10_000, 1);

        assertThat(coupon.canApply(product)).isTrue();
    }

    @Test
    void shouldApplyCouponWhenAnyProductIdInMultiTargetListMatches() {
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100", "P-200", "P-300"), 1_000);
        Product product = new Product("P-200", "C-1", "B-1", 10_000, 1);

        assertThat(coupon.canApply(product)).isTrue();
    }

    @Test
    void shouldNotApplyCouponWhenProductNotInMultiTargetList() {
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100", "P-200", "P-300"), 1_000);
        Product product = new Product("P-999", "C-1", "B-1", 10_000, 1);

        assertThat(coupon.canApply(product)).isFalse();
    }
}
