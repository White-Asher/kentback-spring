package example.com.kentbackspring.coupon;

/**
 * 상품 금액에서 고정 할인 금액을 차감하는 쿠폰.
 * 할인 결과 금액은 항상 0 이상이어야 한다.
 */
public class FixedAmountDiscountCoupon {

    private final int discountAmount;

    public FixedAmountDiscountCoupon(int discountAmount) {
        validateDiscountAmount(discountAmount);
        this.discountAmount = discountAmount;
    }

    public int apply(int productPrice) {
        // 정액 할인 후 금액이 음수가 되지 않도록 0으로 하한을 둔다.
        return Math.max(productPrice - discountAmount, 0);
    }

    private static void validateDiscountAmount(int discountAmount) {
        // 음수 할인 금액은 유효하지 않은 입력으로 간주한다.
        if (discountAmount < 0) {
            throw new IllegalArgumentException("discountAmount must be greater than or equal to 0");
        }
    }
}
