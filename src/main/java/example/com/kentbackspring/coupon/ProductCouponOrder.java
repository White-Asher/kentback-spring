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
    // 부분 취소 계산에 필요한 주문 상태를 불변 스냅샷으로 보존한다.
}
