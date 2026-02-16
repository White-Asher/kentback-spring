package example.com.kentbackspring.coupon;

/**
 * 부분 취소 처리 결과.
 */
public record PartialCancelResult(
        boolean couponRestored,
        int refundAmount
) {
    // 부분 취소 처리 후 쿠폰 복구 여부와 환불 금액을 함께 반환한다.
}
