package example.com.kentbackspring.coupon;

import java.util.Set;

/**
 * 장바구니 쿠폰 주문의 부분 취소 계산 서비스.
 */
public class CartCouponCancelService {

    public CartCouponPartialCancelResult cancel(CartCouponOrder order, Set<String> canceledProductIds) {
        // 취소된 상품에 배분된 할인액 합계를 환불 대상 할인 금액으로 기록한다.
        int refundedDistributedDiscountAmount = order.items().stream()
                .filter(item -> canceledProductIds.contains(item.productId()))
                .mapToInt(CartCouponOrderItem::distributedDiscountAmount)
                .sum();

        // 환불 금액은 취소된 상품의 실결제 금액 합계다.
        int refundAmount = order.items().stream()
                .filter(item -> canceledProductIds.contains(item.productId()))
                .mapToInt(CartCouponOrderItem::paidAmount)
                .sum();

        // 남은 주문 금액도 실결제 기준으로 계산한다.
        int remainingAmount = order.items().stream()
                .filter(item -> !canceledProductIds.contains(item.productId()))
                .mapToInt(CartCouponOrderItem::paidAmount)
                .sum();

        // 정책상 부분 취소 후 최소 주문 금액 미달이어도 쿠폰은 유지한다.
        return new CartCouponPartialCancelResult(
                refundAmount,
                remainingAmount,
                refundedDistributedDiscountAmount,
                true
        );
    }
}
