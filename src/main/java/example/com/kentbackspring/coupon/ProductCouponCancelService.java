package example.com.kentbackspring.coupon;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 상품 쿠폰 주문의 부분 취소 처리 서비스.
 */
public class ProductCouponCancelService {

    public PartialCancelResult cancel(
            ProductCouponOrder order,
            Set<String> canceledProductIds,
            IssuedCoupon issuedCoupon,
            LocalDateTime canceledAt
    ) {
        // 취소 대상 상품의 실결제 금액만 합산해 환불 금액을 계산한다.
        int refundAmount = order.items().stream()
                .filter(item -> canceledProductIds.contains(item.productId()))
                .mapToInt(ProductCouponOrderItem::paidAmount)
                .sum();

        // 취소된 항목 중 쿠폰 할인 적용 상품이 포함되어 있는지 판정한다.
        boolean canceledCouponAppliedItems = order.items().stream()
                .filter(item -> canceledProductIds.contains(item.productId()))
                .anyMatch(item -> item.appliedDiscountAmount() > 0);

        if (canceledCouponAppliedItems) {
            // 쿠폰 적용 상품이 취소되면 쿠폰을 복구한다.
            issuedCoupon.restore(canceledAt, 0);
            return new PartialCancelResult(true, refundAmount);
        }
        // 쿠폰 미적용 상품만 취소되면 쿠폰 상태는 유지한다.
        return new PartialCancelResult(false, refundAmount);
    }
}
