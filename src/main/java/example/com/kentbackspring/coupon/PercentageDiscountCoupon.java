package example.com.kentbackspring.coupon;

public class PercentageDiscountCoupon {

    private final int discountRatePercent;
    private final Integer maxDiscountAmount;

    public PercentageDiscountCoupon(int discountRatePercent) {
        this(discountRatePercent, null);
    }

    public PercentageDiscountCoupon(int discountRatePercent, Integer maxDiscountAmount) {
        this.discountRatePercent = discountRatePercent;
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public int apply(int productPrice) {
        int discountAmount = productPrice * discountRatePercent / 100;
        if (maxDiscountAmount != null) {
            discountAmount = Math.min(discountAmount, maxDiscountAmount);
        }
        return productPrice - discountAmount;
    }
}
