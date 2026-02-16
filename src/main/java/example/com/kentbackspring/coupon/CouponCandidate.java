package example.com.kentbackspring.coupon;

import java.time.LocalDateTime;

/**
 * 주문에 적용 가능한 쿠폰 후보.
 */
public record CouponCandidate(
        String code,
        int discountAmount,
        LocalDateTime expiresAt
) {
    // 정책 선택 시 비교 대상이 되는 쿠폰 요약 정보다.
}
