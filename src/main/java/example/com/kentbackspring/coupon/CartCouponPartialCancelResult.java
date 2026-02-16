package example.com.kentbackspring.coupon;

/**
 * 장바구니 쿠폰 부분 취소 결과.
 */
public record CartCouponPartialCancelResult(
        int refundAmount,
        int remainingAmount,
        int refundedDistributedDiscountAmount,
        boolean couponMaintained
) {
}
