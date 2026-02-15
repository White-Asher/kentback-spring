package example.com.kentbackspring.coupon;

public class FixedAmountDiscountCoupon {

    private final int discountAmount;

    public FixedAmountDiscountCoupon(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int apply(int productPrice) {
        return Math.max(productPrice - discountAmount, 0);
    }
}
