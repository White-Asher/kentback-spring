package example.com.kentbackspring.coupon;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 쿠폰 발급/사용 수명주기 서비스.
 */
public class CouponLifecycleService {

    private final Map<String, Map<String, IssuedCoupon>> issuedCouponsByUser = new HashMap<>();

    public IssuedCoupon issueCoupon(String userId, String couponCode, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        // 사용자별 쿠폰 저장소를 준비하고 동일 코드 중복 발급을 차단한다.
        Map<String, IssuedCoupon> userCoupons = issuedCouponsByUser.computeIfAbsent(userId, ignored -> new HashMap<>());
        if (userCoupons.containsKey(couponCode)) {
            throw new IllegalStateException("coupon already issued to user");
        }

        IssuedCoupon issuedCoupon = new IssuedCoupon(userId, couponCode, issuedAt, expiresAt);
        userCoupons.put(couponCode, issuedCoupon);
        return issuedCoupon;
    }

    public Optional<IssuedCoupon> findIssuedCoupon(String userId, String couponCode) {
        // 사용자/코드 키 기준으로 발급 쿠폰을 조회한다.
        return Optional.ofNullable(issuedCouponsByUser.getOrDefault(userId, Map.of()).get(couponCode));
    }

    public void useCoupon(String userId, String couponCode, String orderId, LocalDateTime usedAt) {
        // 발급 쿠폰을 찾아 사용 처리로 위임한다.
        IssuedCoupon issuedCoupon = findIssuedCoupon(userId, couponCode)
                .orElseThrow(() -> new IllegalStateException("issued coupon not found"));
        issuedCoupon.use(orderId, usedAt);
    }

    public List<IssuedCoupon> listAvailableCoupons(String userId, LocalDateTime now) {
        // 미사용 + 미만료 조건을 모두 만족하는 쿠폰만 반환한다.
        return issuedCouponsByUser.getOrDefault(userId, Map.of()).values().stream()
                .filter(coupon -> coupon.isAvailableAt(now))
                .toList();
    }

    public void cancelOrderAndRestoreCoupon(
            String userId,
            String couponCode,
            LocalDateTime canceledAt,
            int extendDaysIfExpired
    ) {
        // 주문 취소 시 쿠폰 상태를 복구하고 필요 시 만료 연장 정책을 적용한다.
        IssuedCoupon issuedCoupon = findIssuedCoupon(userId, couponCode)
                .orElseThrow(() -> new IllegalStateException("issued coupon not found"));
        issuedCoupon.restore(canceledAt, extendDaysIfExpired);
    }
}
