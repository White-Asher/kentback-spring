package example.com.kentbackspring.coupon;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 장바구니 전체 금액 기준 쿠폰.
 */
public class CartCoupon {

    private enum DiscountType {
        FIXED,
        PERCENTAGE
    }

    private final DiscountType discountType;
    private final int discountValue;
    private final int minimumOrderAmount;
    private final Set<String> includedCategoryIds;
    private final Set<String> excludedProductIds;

    private CartCoupon(
            DiscountType discountType,
            int discountValue,
            int minimumOrderAmount,
            Set<String> includedCategoryIds,
            Set<String> excludedProductIds
    ) {
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minimumOrderAmount = minimumOrderAmount;
        this.includedCategoryIds = includedCategoryIds;
        this.excludedProductIds = excludedProductIds;
    }

    public static CartCoupon fixedAmount(int discountAmount, int minimumOrderAmount) {
        return new CartCoupon(DiscountType.FIXED, discountAmount, minimumOrderAmount, Set.of(), Set.of());
    }

    public static CartCoupon percentage(int discountRatePercent, int minimumOrderAmount) {
        return new CartCoupon(DiscountType.PERCENTAGE, discountRatePercent, minimumOrderAmount, Set.of(), Set.of());
    }

    public CartCoupon withIncludedCategoryIds(Set<String> categoryIds) {
        return new CartCoupon(discountType, discountValue, minimumOrderAmount, categoryIds, excludedProductIds);
    }

    public CartCoupon withExcludedProductIds(Set<String> productIds) {
        return new CartCoupon(discountType, discountValue, minimumOrderAmount, includedCategoryIds, productIds);
    }

    public CartCouponResult apply(Cart cart) {
        List<Product> eligibleProducts = cart.products().stream()
                .filter(this::isIncludedCategory)
                .filter(product -> !excludedProductIds.contains(product.productId()))
                .toList();

        int eligibleSubtotal = eligibleProducts.stream().mapToInt(Product::totalPrice).sum();
        if (eligibleSubtotal < minimumOrderAmount) {
            throw new IllegalStateException("minimum order amount not met");
        }

        int totalDiscount = calculateTotalDiscount(eligibleSubtotal);
        Map<String, Integer> discountByProductId = distributeDiscount(eligibleProducts, eligibleSubtotal, totalDiscount);
        int discountedProductTotal = cart.productTotalAmount() - totalDiscount;
        int finalPayableAmount = discountedProductTotal + cart.shippingFee();

        return new CartCouponResult(
                eligibleSubtotal,
                totalDiscount,
                discountedProductTotal,
                finalPayableAmount,
                discountByProductId
        );
    }

    private int calculateTotalDiscount(int eligibleSubtotal) {
        if (eligibleSubtotal == 0) {
            return 0;
        }
        if (discountType == DiscountType.FIXED) {
            return Math.min(discountValue, eligibleSubtotal);
        }
        return eligibleSubtotal * discountValue / 100;
    }

    private Map<String, Integer> distributeDiscount(List<Product> eligibleProducts, int eligibleSubtotal, int totalDiscount) {
        Map<String, Integer> discountByProductId = new LinkedHashMap<>();
        if (eligibleSubtotal == 0 || totalDiscount == 0) {
            return discountByProductId;
        }

        int allocated = 0;
        for (Product product : eligibleProducts) {
            int discount = totalDiscount * product.totalPrice() / eligibleSubtotal;
            discountByProductId.put(product.productId(), discount);
            allocated += discount;
        }

        int remainder = totalDiscount - allocated;
        if (remainder > 0) {
            Product mostExpensive = eligibleProducts.stream()
                    .max(Comparator.comparingInt(Product::totalPrice))
                    .orElseThrow();
            discountByProductId.computeIfPresent(mostExpensive.productId(), (key, value) -> value + remainder);
        }
        return discountByProductId;
    }

    private boolean isIncludedCategory(Product product) {
        return includedCategoryIds.isEmpty() || includedCategoryIds.contains(product.categoryId());
    }
}
