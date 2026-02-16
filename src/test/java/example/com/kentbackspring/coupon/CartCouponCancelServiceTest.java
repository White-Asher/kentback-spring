package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 장바구니 쿠폰 부분 취소 테스트.
 */
class CartCouponCancelServiceTest {

    private final CartCouponCancelService cancelService = new CartCouponCancelService();

    @Test
    void shouldRefundDistributedDiscountAmountForPartiallyCanceledProduct() {
        // 부분 취소된 상품에 배분된 할인 금액이 환불 내역에 반영되어야 한다.
        CartCouponOrder order = new CartCouponOrder(
                "ORDER-1",
                20_000,
                List.of(
                        new CartCouponOrderItem("P-100", 10_000, 2_000),
                        new CartCouponOrderItem("P-200", 10_000, 2_000)
                )
        );

        CartCouponPartialCancelResult result = cancelService.cancel(order, Set.of("P-100"));

        assertThat(result.refundedDistributedDiscountAmount()).isEqualTo(2_000);
    }

    @Test
    void shouldKeepCouponEvenIfRemainingAmountBelowMinimumOrderAfterPartialCancel() {
        // 부분 취소 후 남은 주문 금액이 최소 주문 금액 미달이어도 쿠폰은 유지된다.
        CartCouponOrder order = new CartCouponOrder(
                "ORDER-1",
                20_000,
                List.of(
                        new CartCouponOrderItem("P-100", 10_000, 2_000),
                        new CartCouponOrderItem("P-200", 10_000, 2_000)
                )
        );

        CartCouponPartialCancelResult result = cancelService.cancel(order, Set.of("P-100"));

        assertThat(result.remainingAmount()).isEqualTo(8_000);
        assertThat(result.couponMaintained()).isTrue();
    }

    @Test
    void shouldMatchOriginalAmountByAddingRefundAndRemainingAfterPartialCancel() {
        // 환불 금액과 남은 주문 금액의 합은 원래 결제 금액과 일치해야 한다.
        CartCouponOrder order = new CartCouponOrder(
                "ORDER-1",
                20_000,
                List.of(
                        new CartCouponOrderItem("P-100", 10_000, 2_000),
                        new CartCouponOrderItem("P-200", 10_000, 2_000)
                )
        );

        CartCouponPartialCancelResult result = cancelService.cancel(order, Set.of("P-100"));

        assertThat(result.refundAmount() + result.remainingAmount()).isEqualTo(order.originalPaidAmount());
    }
}
