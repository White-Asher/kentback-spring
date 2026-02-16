package example.com.kentbackspring.coupon;

/**
 * 상품 쿠폰 주문 항목 스냅샷.
 */
public record ProductCouponOrderItem(
        String productId,
        int unitPrice,
        int quantity,
        int appliedDiscountAmount
) {

    public int paidAmount() {
        // 쿠폰 할인 적용 후 실제 결제된 금액이다.
        return unitPrice * quantity - appliedDiscountAmount;
    }
}
