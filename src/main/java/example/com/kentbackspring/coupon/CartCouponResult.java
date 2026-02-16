package example.com.kentbackspring.coupon;

import java.util.Map;

/**
 * 장바구니 쿠폰 적용 결과.
 */
public record CartCouponResult(
        int eligibleSubtotal,
        int totalDiscount,
        int discountedProductTotal,
        int finalPayableAmount,
        Map<String, Integer> discountByProductId
) {
    // 장바구니 쿠폰 적용의 계산 결과 스냅샷을 보관한다.
}
