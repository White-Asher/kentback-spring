package example.com.kentbackspring.coupon;

import java.util.List;

/**
 * 상품 쿠폰이 적용된 주문 스냅샷.
 */
public record ProductCouponOrder(
        String orderId,
        String appliedCouponCode,
        List<ProductCouponOrderItem> items
) {
}
