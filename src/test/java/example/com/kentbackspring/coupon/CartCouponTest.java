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

    @Test
    void shouldDistributeCartDiscountByProductPriceRatio() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 10_000, 1),
                new Product("P-200", "FOOD", "B-2", 5_000, 1),
                new Product("P-300", "FOOD", "B-3", 5_000, 1)
        ), 0);
        CartCoupon coupon = CartCoupon.fixedAmount(4_000, 0);

        CartCouponResult result = coupon.apply(cart);

        assertThat(result.discountByProductId().get("P-100")).isEqualTo(2_000);
        assertThat(result.discountByProductId().get("P-200")).isEqualTo(1_000);
        assertThat(result.discountByProductId().get("P-300")).isEqualTo(1_000);
    }

    @Test
    void shouldTruncateFractionalDiscountOnDistributionToWonUnit() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 3_333, 1),
                new Product("P-200", "FOOD", "B-2", 3_333, 1),
                new Product("P-300", "FOOD", "B-3", 3_334, 1)
        ), 0);
        CartCoupon coupon = CartCoupon.fixedAmount(1_000, 0);

        CartCouponResult result = coupon.apply(cart);

        assertThat(result.discountByProductId().get("P-100")).isEqualTo(333);
        assertThat(result.discountByProductId().get("P-200")).isEqualTo(333);
    }

    @Test
    void shouldAllocateRemainderDiscountToMostExpensiveProduct() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 3_333, 1),
                new Product("P-200", "FOOD", "B-2", 3_333, 1),
                new Product("P-300", "FOOD", "B-3", 3_334, 1)
        ), 0);
        CartCoupon coupon = CartCoupon.fixedAmount(1_000, 0);

        CartCouponResult result = coupon.apply(cart);

        assertThat(result.discountByProductId().get("P-300")).isEqualTo(334);
    }

    @Test
    void shouldMakeDistributedDiscountSumExactlyEqualToTotalDiscount() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 3_333, 1),
                new Product("P-200", "FOOD", "B-2", 3_333, 1),
                new Product("P-300", "FOOD", "B-3", 3_334, 1)
        ), 0);
        CartCoupon coupon = CartCoupon.fixedAmount(1_000, 0);

        CartCouponResult result = coupon.apply(cart);
        int distributedSum = result.discountByProductId().values().stream().mapToInt(Integer::intValue).sum();

        assertThat(distributedSum).isEqualTo(result.totalDiscount());
    }

    @Test
    void shouldIncludeOnlySpecificCategoryProductsForCartCoupon() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 10_000, 1),
                new Product("P-200", "BEAUTY", "B-2", 10_000, 1)
        ), 0);
        CartCoupon coupon = CartCoupon.fixedAmount(5_000, 0)
                .withIncludedCategoryIds(java.util.Set.of("FOOD"));

        CartCouponResult result = coupon.apply(cart);

        assertThat(result.eligibleSubtotal()).isEqualTo(10_000);
        assertThat(result.discountByProductId()).containsOnlyKeys("P-100");
    }

    @Test
    void shouldExcludeProductsInExclusionListFromDiscountCalculation() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 10_000, 1),
                new Product("P-200", "FOOD", "B-2", 10_000, 1)
        ), 0);
        CartCoupon coupon = CartCoupon.fixedAmount(4_000, 0)
                .withExcludedProductIds(java.util.Set.of("P-200"));

        CartCouponResult result = coupon.apply(cart);

        assertThat(result.eligibleSubtotal()).isEqualTo(10_000);
        assertThat(result.discountByProductId()).containsOnlyKeys("P-100");
        assertThat(result.totalDiscount()).isEqualTo(4_000);
    }

    @Test
    void shouldNotApplyWhenFilteredProductsBelowMinimumOrderAmount() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 10_000, 1),
                new Product("P-200", "BEAUTY", "B-2", 10_000, 1)
        ), 0);
        CartCoupon coupon = CartCoupon.fixedAmount(3_000, 12_000)
                .withIncludedCategoryIds(java.util.Set.of("FOOD"));

        assertThatThrownBy(() -> coupon.apply(cart))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("minimum order amount not met");
    }

    @Test
    void shouldExcludeShippingFeeFromMinimumOrderAmountCalculation() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 10_000, 1)
        ), 5_000);
        CartCoupon coupon = CartCoupon.fixedAmount(1_000, 12_000);

        assertThatThrownBy(() -> coupon.apply(cart))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("minimum order amount not met");
    }

    @Test
    void shouldExcludeShippingFeeFromDiscountCalculation() {
        Cart cart = new Cart(List.of(
                new Product("P-100", "FOOD", "B-1", 10_000, 1)
        ), 5_000);
        CartCoupon coupon = CartCoupon.percentage(10, 0);

        CartCouponResult result = coupon.apply(cart);

        assertThat(result.totalDiscount()).isEqualTo(1_000);
    }
}
