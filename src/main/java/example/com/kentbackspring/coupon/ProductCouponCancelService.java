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
        int refundAmount = order.items().stream()
                .filter(item -> canceledProductIds.contains(item.productId()))
                .mapToInt(ProductCouponOrderItem::paidAmount)
                .sum();

        boolean canceledCouponAppliedItems = order.items().stream()
                .filter(item -> canceledProductIds.contains(item.productId()))
                .anyMatch(item -> item.appliedDiscountAmount() > 0);

        if (canceledCouponAppliedItems) {
            issuedCoupon.restore(canceledAt, 0);
            return new PartialCancelResult(true, refundAmount);
        }
        return new PartialCancelResult(false, refundAmount);
    }
}
