package example.com.kentbackspring.coupon;

import java.util.List;

/**
 * 쿠폰 중복 적용 정책 서비스.
 */
public class CouponPolicyService {

    public CouponCandidate chooseSingleCoupon(List<CouponCandidate> coupons, String selectedCouponCode) {
        if (coupons.isEmpty()) {
            throw new IllegalArgumentException("no coupons to apply");
        }

        if (selectedCouponCode != null) {
            return coupons.stream()
                    .filter(coupon -> coupon.code().equals(selectedCouponCode))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("selected coupon not found"));
        }

        if (coupons.size() > 1) {
            throw new IllegalStateException("multiple coupons are not allowed");
        }
        return coupons.get(0);
    }

    public CouponCandidate selectBestCoupon(List<CouponCandidate> coupons) {
        return coupons.stream()
                .sorted((left, right) -> {
                    int discountCompare = Integer.compare(right.discountAmount(), left.discountAmount());
                    if (discountCompare != 0) {
                        return discountCompare;
                    }
                    return left.expiresAt().compareTo(right.expiresAt());
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("no coupons to apply"));
    }
}
