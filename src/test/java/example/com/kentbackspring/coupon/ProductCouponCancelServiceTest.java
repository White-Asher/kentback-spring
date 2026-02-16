package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 상품 쿠폰 부분 취소 테스트.
 */
class ProductCouponCancelServiceTest {

    private final ProductCouponCancelService cancelService = new ProductCouponCancelService();

    @Test
    void shouldRestoreCouponWhenCancelingOnlyCouponAppliedItems() {
        IssuedCoupon issuedCoupon = new IssuedCoupon(
                "user-1",
                "PRODUCT-COUPON",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );
        issuedCoupon.use("ORDER-1", LocalDateTime.of(2026, 2, 16, 11, 0));

        ProductCouponOrder order = new ProductCouponOrder(
                "ORDER-1",
                "PRODUCT-COUPON",
                List.of(
                        new ProductCouponOrderItem("P-100", 10_000, 1, 1_000),
                        new ProductCouponOrderItem("P-200", 5_000, 1, 0)
                )
        );

        PartialCancelResult result = cancelService.cancel(order, Set.of("P-100"), issuedCoupon, LocalDateTime.of(2026, 2, 17, 10, 0));

        assertThat(result.couponRestored()).isTrue();
        assertThat(issuedCoupon.status()).isEqualTo(CouponUsageStatus.UNUSED);
    }

    @Test
    void shouldKeepCouponWhenCancelingOnlyNonCouponAppliedItems() {
        IssuedCoupon issuedCoupon = new IssuedCoupon(
                "user-1",
                "PRODUCT-COUPON",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );
        issuedCoupon.use("ORDER-1", LocalDateTime.of(2026, 2, 16, 11, 0));

        ProductCouponOrder order = new ProductCouponOrder(
                "ORDER-1",
                "PRODUCT-COUPON",
                List.of(
                        new ProductCouponOrderItem("P-100", 10_000, 1, 1_000),
                        new ProductCouponOrderItem("P-200", 5_000, 1, 0)
                )
        );

        PartialCancelResult result = cancelService.cancel(order, Set.of("P-200"), issuedCoupon, LocalDateTime.of(2026, 2, 17, 10, 0));

        assertThat(result.couponRestored()).isFalse();
        assertThat(issuedCoupon.status()).isEqualTo(CouponUsageStatus.USED);
    }

    @Test
    void shouldCalculateAccurateRefundAmountOnPartialCancel() {
        IssuedCoupon issuedCoupon = new IssuedCoupon(
                "user-1",
                "PRODUCT-COUPON",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );
        issuedCoupon.use("ORDER-1", LocalDateTime.of(2026, 2, 16, 11, 0));

        ProductCouponOrder order = new ProductCouponOrder(
                "ORDER-1",
                "PRODUCT-COUPON",
                List.of(
                        new ProductCouponOrderItem("P-100", 10_000, 1, 1_000),
                        new ProductCouponOrderItem("P-200", 5_000, 2, 0)
                )
        );

        PartialCancelResult result = cancelService.cancel(order, Set.of("P-100"), issuedCoupon, LocalDateTime.of(2026, 2, 17, 10, 0));

        assertThat(result.refundAmount()).isEqualTo(9_000);
    }
}
