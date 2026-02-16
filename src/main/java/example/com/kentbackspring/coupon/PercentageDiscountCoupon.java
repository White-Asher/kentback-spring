package example.com.kentbackspring.coupon;

public class PercentageDiscountCoupon {

    private final int discountRatePercent;
    private final Integer maxDiscountAmount;
    private final int minimumPurchaseAmount;
    private final int minimumPurchaseQuantity;

    public PercentageDiscountCoupon(int discountRatePercent) {
        this(discountRatePercent, null, 0, 1);
    }

    public PercentageDiscountCoupon(int discountRatePercent, Integer maxDiscountAmount) {
        this(discountRatePercent, maxDiscountAmount, 0, 1);
    }

    public PercentageDiscountCoupon(
            int discountRatePercent,
            Integer maxDiscountAmount,
            int minimumPurchaseAmount,
            int minimumPurchaseQuantity
    ) {
        this.discountRatePercent = discountRatePercent;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minimumPurchaseAmount = minimumPurchaseAmount;
        this.minimumPurchaseQuantity = minimumPurchaseQuantity;
    }

    public int apply(int productPrice) {
        return apply(productPrice, 1);
    }

    public int apply(int productPrice, int purchaseQuantity) {
        validateMinimumPurchaseCondition(productPrice, purchaseQuantity);

        int totalBeforeDiscount = productPrice * purchaseQuantity;
        int discountAmount = totalBeforeDiscount * discountRatePercent / 100;
        if (maxDiscountAmount != null) {
            discountAmount = Math.min(discountAmount, maxDiscountAmount);
        }
        return totalBeforeDiscount - discountAmount;
    }

    private void validateMinimumPurchaseCondition(int productPrice, int purchaseQuantity) {
        if (productPrice * purchaseQuantity < minimumPurchaseAmount) {
            throw new IllegalStateException("minimum purchase amount not met");
        }
        if (purchaseQuantity < minimumPurchaseQuantity) {
            throw new IllegalStateException("minimum purchase quantity not met");
        }
    }
}
