package example.com.kentbackspring.coupon;

/**
 * 순차 쿠폰 적용 결과.
 */
public record SequentialDiscountResult(
        int afterProductCoupon,
        int afterCartCoupon
) {
}
