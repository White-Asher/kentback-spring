package example.com.kentbackspring.coupon;

/**
 * 정률 할인과 최소 구매 조건을 함께 표현하는 쿠폰.
 */
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
        // 단일 상품 기준 기본 수량은 1개로 본다.
        return apply(productPrice, 1);
    }

    public int apply(int productPrice, int purchaseQuantity) {
        // 최소 구매 조건은 할인 전 총 금액 기준으로 검증한다.
        validateMinimumPurchaseCondition(productPrice, purchaseQuantity);

        int totalBeforeDiscount = productPrice * purchaseQuantity;
        // 정수 연산으로 원 단위 절사 효과를 낸다.
        int discountAmount = totalBeforeDiscount * discountRatePercent / 100;
        if (maxDiscountAmount != null) {
            // 최대 할인 한도가 있으면 실제 할인액을 상한 처리한다.
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
