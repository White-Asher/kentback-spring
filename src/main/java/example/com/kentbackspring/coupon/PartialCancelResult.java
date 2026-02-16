package example.com.kentbackspring.coupon;

/**
 * 부분 취소 처리 결과.
 */
public record PartialCancelResult(
        boolean couponRestored,
        int refundAmount
) {
}
