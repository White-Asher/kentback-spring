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

    @Test
    void shouldApplyCouponOnlyToSpecificCategory() {
        ProductCoupon coupon = ProductCoupon.forCategory("FOOD", false, 1_000);
        Product targetProduct = new Product("P-100", "FOOD", "B-1", 10_000, 1);
        Product nonTargetProduct = new Product("P-200", "BEAUTY", "B-1", 10_000, 1);

        assertThat(coupon.canApply(targetProduct)).isTrue();
        assertThat(coupon.canApply(nonTargetProduct)).isFalse();
    }

    @Test
    void shouldApplyCouponToSubcategoryWhenIncludeSubcategoriesOn() {
        ProductCoupon coupon = ProductCoupon.forCategory("FOOD", true, 1_000);
        Product subcategoryProduct = new Product("P-100", "FOOD/SNACK", "B-1", 10_000, 1);

        assertThat(coupon.canApply(subcategoryProduct)).isTrue();
    }

    @Test
    void shouldApplyCouponOnlyExactCategoryWhenIncludeSubcategoriesOff() {
        ProductCoupon coupon = ProductCoupon.forCategory("FOOD", false, 1_000);
        Product subcategoryProduct = new Product("P-100", "FOOD/SNACK", "B-1", 10_000, 1);

        assertThat(coupon.canApply(subcategoryProduct)).isFalse();
    }

    @Test
    void shouldApplyCouponOnlyToSpecificBrand() {
        ProductCoupon coupon = ProductCoupon.forBrand("BRAND-A", 1_000);
        Product targetProduct = new Product("P-100", "FOOD", "BRAND-A", 10_000, 1);

        assertThat(coupon.canApply(targetProduct)).isTrue();
    }

    @Test
    void shouldNotApplyCouponWhenBrandDoesNotMatch() {
        ProductCoupon coupon = ProductCoupon.forBrand("BRAND-A", 1_000);
        Product nonTargetProduct = new Product("P-100", "FOOD", "BRAND-B", 10_000, 1);

        assertThat(coupon.canApply(nonTargetProduct)).isFalse();
    }
}
