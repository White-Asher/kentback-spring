package example.com.kentbackspring.coupon;

import java.util.List;

/**
 * 장바구니 쿠폰이 적용된 주문 스냅샷.
 */
public record CartCouponOrder(
        String orderId,
        int minimumOrderAmountAtApply,
        List<CartCouponOrderItem> items
) {

    public int originalPaidAmount() {
        // 부분 취소 전 기준 결제 금액(상품별 실결제 합계)이다.
        return items.stream()
                .mapToInt(CartCouponOrderItem::paidAmount)
                .sum();
    }
}
