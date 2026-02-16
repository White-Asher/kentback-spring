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
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100"), 1_000);

        assertThat(coupon.targetProductIds()).containsExactly("P-100");
    }

    @Test
    void shouldNotApplyCouponWhenProductIsNotTarget() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100"), 1_000);
        Product product = new Product("P-999", "C-1", "B-1", 10_000, 1);

        assertThat(coupon.canApply(product)).isFalse();
    }

    @Test
    void shouldApplyCouponWhenProductIsTarget() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100"), 1_000);
        Product product = new Product("P-100", "C-1", "B-1", 10_000, 1);

        assertThat(coupon.canApply(product)).isTrue();
    }

    @Test
    void shouldApplyCouponWhenAnyProductIdInMultiTargetListMatches() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100", "P-200", "P-300"), 1_000);
        Product product = new Product("P-200", "C-1", "B-1", 10_000, 1);

        assertThat(coupon.canApply(product)).isTrue();
    }

    @Test
    void shouldNotApplyCouponWhenProductNotInMultiTargetList() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100", "P-200", "P-300"), 1_000);
        Product product = new Product("P-999", "C-1", "B-1", 10_000, 1);

        assertThat(coupon.canApply(product)).isFalse();
    }

    @Test
    void shouldApplyCouponOnlyToSpecificCategory() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forCategory("FOOD", false, 1_000);
        Product targetProduct = new Product("P-100", "FOOD", "B-1", 10_000, 1);
        Product nonTargetProduct = new Product("P-200", "BEAUTY", "B-1", 10_000, 1);

        assertThat(coupon.canApply(targetProduct)).isTrue();
        assertThat(coupon.canApply(nonTargetProduct)).isFalse();
    }

    @Test
    void shouldApplyCouponToSubcategoryWhenIncludeSubcategoriesOn() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forCategory("FOOD", true, 1_000);
        Product subcategoryProduct = new Product("P-100", "FOOD/SNACK", "B-1", 10_000, 1);

        assertThat(coupon.canApply(subcategoryProduct)).isTrue();
    }

    @Test
    void shouldApplyCouponOnlyExactCategoryWhenIncludeSubcategoriesOff() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forCategory("FOOD", false, 1_000);
        Product subcategoryProduct = new Product("P-100", "FOOD/SNACK", "B-1", 10_000, 1);

        assertThat(coupon.canApply(subcategoryProduct)).isFalse();
    }

    @Test
    void shouldApplyCouponOnlyToSpecificBrand() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forBrand("BRAND-A", 1_000);
        Product targetProduct = new Product("P-100", "FOOD", "BRAND-A", 10_000, 1);

        assertThat(coupon.canApply(targetProduct)).isTrue();
    }

    @Test
    void shouldNotApplyCouponWhenBrandDoesNotMatch() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forBrand("BRAND-A", 1_000);
        Product nonTargetProduct = new Product("P-100", "FOOD", "BRAND-B", 10_000, 1);

        assertThat(coupon.canApply(nonTargetProduct)).isFalse();
    }

    @Test
    void shouldCombineBrandAndCategoryConditionsWithAnd() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forBrandAndCategory("BRAND-A", "FOOD", false, 1_000);
        Product product = new Product("P-100", "FOOD", "BRAND-A", 10_000, 1);

        assertThat(coupon.canApply(product)).isTrue();
    }

    @Test
    void shouldApplyCouponOnlyWhenAllCombinedConditionsSatisfied() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forBrandAndCategory("BRAND-A", "FOOD", false, 1_000);
        Product brandMismatch = new Product("P-100", "FOOD", "BRAND-B", 10_000, 1);
        Product categoryMismatch = new Product("P-100", "BEAUTY", "BRAND-A", 10_000, 1);

        assertThat(coupon.canApply(brandMismatch)).isFalse();
        assertThat(coupon.canApply(categoryMismatch)).isFalse();
    }

    @Test
    void shouldApplyDiscountToMultipleQuantityOfSameProductWithOneCoupon() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forProductIds(Set.of("P-100"), 1_000);
        Product product = new Product("P-100", "FOOD", "BRAND-A", 10_000, 3);

        int discount = coupon.calculateDiscount(product);

        assertThat(discount).isEqualTo(3_000);
    }

    @Test
    void shouldLimitDiscountToOneProductWhenConfiguredAsOnePerCoupon() {
        // 시나리오의 기대 동작을 검증한다.
        ProductCoupon coupon = ProductCoupon.forProductIdsWithQuantityLimit(Set.of("P-100"), 1_000, 1);
        Product product = new Product("P-100", "FOOD", "BRAND-A", 10_000, 3);

        int discount = coupon.calculateDiscount(product);

        assertThat(discount).isEqualTo(1_000);
    }
}
