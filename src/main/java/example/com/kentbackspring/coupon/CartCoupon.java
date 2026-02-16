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
        // 포함/제외 필터를 모두 통과한 상품만 할인 대상으로 본다.
        List<Product> eligibleProducts = cart.products().stream()
                .filter(this::isIncludedCategory)
                .filter(product -> !excludedProductIds.contains(product.productId()))
                .toList();

        int eligibleSubtotal = eligibleProducts.stream().mapToInt(Product::totalPrice).sum();
        // 최소 주문 금액 검증은 배송비 제외, 필터링 후 합계 기준이다.
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
            // 정액 쿠폰은 대상 합계를 초과할 수 없다.
            return Math.min(discountValue, eligibleSubtotal);
        }
        // 정률 쿠폰은 정수 연산으로 원 단위 절사한다.
        return eligibleSubtotal * discountValue / 100;
    }

    private Map<String, Integer> distributeDiscount(List<Product> eligibleProducts, int eligibleSubtotal, int totalDiscount) {
        Map<String, Integer> discountByProductId = new LinkedHashMap<>();
        if (eligibleSubtotal == 0 || totalDiscount == 0) {
            return discountByProductId;
        }

        int allocated = 0;
        for (Product product : eligibleProducts) {
            // 상품 금액 비율로 분배하며 소수점은 버림 처리된다.
            int discount = totalDiscount * product.totalPrice() / eligibleSubtotal;
            discountByProductId.put(product.productId(), discount);
            allocated += discount;
        }

        int remainder = totalDiscount - allocated;
        if (remainder > 0) {
            // 버림으로 남은 금액은 최고가 상품에 몰아준다.
            Product mostExpensive = eligibleProducts.stream()
                    .max(Comparator.comparingInt(Product::totalPrice))
                    .orElseThrow();
            discountByProductId.computeIfPresent(mostExpensive.productId(), (key, value) -> value + remainder);
        }
        return discountByProductId;
    }

    private boolean isIncludedCategory(Product product) {
        // 카테고리 필터가 비어있으면 전체 허용한다.
        return includedCategoryIds.isEmpty() || includedCategoryIds.contains(product.categoryId());
    }
}
