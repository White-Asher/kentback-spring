package example.com.kentbackspring.coupon;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 상품 단위 적용 쿠폰.
 */
public class ProductCoupon {

    private final Set<String> targetProductIds;
    private final Set<String> targetCategoryIds;
    private final boolean includeSubcategories;
    private final Set<String> targetBrandIds;
    private final boolean requireAllConditions;
    private final int discountAmountPerUnit;
    private final int maxApplicableQuantityPerCoupon;

    public ProductCoupon(
            Set<String> targetProductIds,
            Set<String> targetCategoryIds,
            boolean includeSubcategories,
            Set<String> targetBrandIds,
            boolean requireAllConditions,
            int discountAmountPerUnit,
            int maxApplicableQuantityPerCoupon
    ) {
        this.targetProductIds = targetProductIds;
        this.targetCategoryIds = targetCategoryIds;
        this.includeSubcategories = includeSubcategories;
        this.targetBrandIds = targetBrandIds;
        this.requireAllConditions = requireAllConditions;
        this.discountAmountPerUnit = discountAmountPerUnit;
        this.maxApplicableQuantityPerCoupon = maxApplicableQuantityPerCoupon;
    }

    public static ProductCoupon forProductIds(Set<String> productIds, int discountAmountPerUnit) {
        return new ProductCoupon(
                productIds,
                Set.of(),
                false,
                Set.of(),
                false,
                discountAmountPerUnit,
                Integer.MAX_VALUE
        );
    }

    public Set<String> targetProductIds() {
        return Collections.unmodifiableSet(targetProductIds);
    }

    public boolean canApply(Product product) {
        boolean productMatch = targetProductIds.isEmpty() || targetProductIds.contains(product.productId());
        boolean categoryMatch = matchesCategory(product.categoryId());
        boolean brandMatch = targetBrandIds.isEmpty() || targetBrandIds.contains(product.brandId());

        List<Boolean> configuredMatches = configuredMatches(productMatch, categoryMatch, brandMatch);
        if (configuredMatches.isEmpty()) {
            return true;
        }

        if (requireAllConditions) {
            return configuredMatches.stream().allMatch(Boolean::booleanValue);
        }
        return configuredMatches.stream().anyMatch(Boolean::booleanValue);
    }

    public int calculateDiscount(Product product) {
        if (!canApply(product)) {
            return 0;
        }
        int applicableQuantity = Math.min(product.quantity(), maxApplicableQuantityPerCoupon);
        int discountAmount = applicableQuantity * discountAmountPerUnit;
        return Math.min(discountAmount, product.totalPrice());
    }

    private boolean matchesCategory(String categoryId) {
        if (targetCategoryIds.isEmpty()) {
            return true;
        }
        if (!includeSubcategories) {
            return targetCategoryIds.contains(categoryId);
        }
        return targetCategoryIds.stream().anyMatch(target ->
                categoryId.equals(target) || categoryId.startsWith(target + "/")
        );
    }

    private List<Boolean> configuredMatches(boolean productMatch, boolean categoryMatch, boolean brandMatch) {
        java.util.ArrayList<Boolean> matches = new java.util.ArrayList<>();
        if (!targetProductIds.isEmpty()) {
            matches.add(productMatch);
        }
        if (!targetCategoryIds.isEmpty()) {
            matches.add(categoryMatch);
        }
        if (!targetBrandIds.isEmpty()) {
            matches.add(brandMatch);
        }
        return matches;
    }
}
