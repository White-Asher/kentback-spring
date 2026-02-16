package example.com.kentbackspring.coupon;

/**
 * 순차 쿠폰 적용 결과.
 */
public record SequentialDiscountResult(
        int afterProductCoupon,
        int afterCartCoupon
) {
    // 순차 할인 전후 금액 비교를 위한 중간/최종 결과를 함께 담는다.
}
