package example.com.kentbackspring.coupon;

/**
 * 장바구니 쿠폰이 적용된 주문의 상품 항목 스냅샷.
 */
public record CartCouponOrderItem(
        String productId,
        int productAmount,
        int distributedDiscountAmount
) {

    public int paidAmount() {
        // 환불 기준은 상품에 배분된 할인액을 반영한 실결제 금액이다.
        return productAmount - distributedDiscountAmount;
    }
}
