package example.com.kentbackspring.coupon;

import java.util.List;

/**
 * 쿠폰 중복 적용 정책 서비스.
 */
public class CouponPolicyService {

    public CouponCandidate chooseSingleCoupon(List<CouponCandidate> coupons, String selectedCouponCode) {
        // 단건 선택 정책: 사용자 명시 선택이 없으면 1개만 허용한다.
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
        // 할인 금액 내림차순, 동률 시 만료일 오름차순으로 최적 쿠폰을 고른다.
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

    public SequentialDiscountResult applySequential(
            int originalOrderAmount,
            CouponCandidate productCoupon,
            CouponCandidate cartCoupon
    ) {
        // 순차 적용 순서는 상품 쿠폰 -> 장바구니 쿠폰으로 고정한다.
        int afterProductCoupon = applyCouponAmount(originalOrderAmount, productCoupon);
        int afterCartCoupon = applyCouponAmount(afterProductCoupon, cartCoupon);
        return new SequentialDiscountResult(afterProductCoupon, afterCartCoupon);
    }

    public boolean isSequentialBetterThanNoStackingPolicy(
            int originalOrderAmount,
            CouponCandidate productCoupon,
            CouponCandidate cartCoupon,
            List<CouponCandidate> singlePolicyCandidates
    ) {
        // 순차 적용 최종 결제액이 단일 쿠폰 정책보다 작으면 더 유리하다고 판단한다.
        int sequentialFinalAmount = applySequential(originalOrderAmount, productCoupon, cartCoupon).afterCartCoupon();
        CouponCandidate bestSingleCoupon = selectBestCoupon(singlePolicyCandidates);
        int singlePolicyFinalAmount = applyCouponAmount(originalOrderAmount, bestSingleCoupon);
        return sequentialFinalAmount < singlePolicyFinalAmount;
    }

    private int applyCouponAmount(int baseAmount, CouponCandidate coupon) {
        // 쿠폰 적용 후 금액은 음수가 될 수 없다.
        int discounted = baseAmount - coupon.discountAmount();
        return Math.max(discounted, 0);
    }
}
