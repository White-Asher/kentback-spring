package example.com.kentbackspring.coupon;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 쿠폰 발급/사용 수명주기 서비스.
 */
public class CouponLifecycleService {

    private final Map<String, Map<String, IssuedCoupon>> issuedCouponsByUser = new HashMap<>();

    public IssuedCoupon issueCoupon(String userId, String couponCode, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        Map<String, IssuedCoupon> userCoupons = issuedCouponsByUser.computeIfAbsent(userId, ignored -> new HashMap<>());
        if (userCoupons.containsKey(couponCode)) {
            throw new IllegalStateException("coupon already issued to user");
        }

        IssuedCoupon issuedCoupon = new IssuedCoupon(userId, couponCode, issuedAt, expiresAt);
        userCoupons.put(couponCode, issuedCoupon);
        return issuedCoupon;
    }

    public Optional<IssuedCoupon> findIssuedCoupon(String userId, String couponCode) {
        return Optional.ofNullable(issuedCouponsByUser.getOrDefault(userId, Map.of()).get(couponCode));
    }
}
