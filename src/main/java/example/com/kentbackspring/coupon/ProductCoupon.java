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

    public static ProductCoupon forProductIdsWithQuantityLimit(
            Set<String> productIds,
            int discountAmountPerUnit,
            int maxApplicableQuantityPerCoupon
    ) {
        return new ProductCoupon(
                productIds,
                Set.of(),
                false,
                Set.of(),
                false,
                discountAmountPerUnit,
                maxApplicableQuantityPerCoupon
        );
    }

    public static ProductCoupon forCategory(String categoryId, boolean includeSubcategories, int discountAmountPerUnit) {
        return new ProductCoupon(
                Set.of(),
                Set.of(categoryId),
                includeSubcategories,
                Set.of(),
                false,
                discountAmountPerUnit,
                Integer.MAX_VALUE
        );
    }

    public static ProductCoupon forBrand(String brandId, int discountAmountPerUnit) {
        return new ProductCoupon(
                Set.of(),
                Set.of(),
                false,
                Set.of(brandId),
                false,
                discountAmountPerUnit,
                Integer.MAX_VALUE
        );
    }

    public static ProductCoupon forBrandAndCategory(
            String brandId,
            String categoryId,
            boolean includeSubcategories,
            int discountAmountPerUnit
    ) {
        return new ProductCoupon(
                Set.of(),
                Set.of(categoryId),
                includeSubcategories,
                Set.of(brandId),
                true,
                discountAmountPerUnit,
                Integer.MAX_VALUE
        );
    }

    public Set<String> targetProductIds() {
        // 내부 컬렉션 캡슐화를 위해 읽기 전용 컬렉션을 반환한다.
        return Collections.unmodifiableSet(targetProductIds);
    }

    public boolean canApply(Product product) {
        // 각 조건별 매칭 결과를 구하고 정책(AND/OR)에 따라 최종 판정한다.
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
        // 대상이 아니면 할인 금액은 0원이다.
        if (!canApply(product)) {
            return 0;
        }
        // 쿠폰 1개당 적용 가능한 최대 수량 제한을 반영한다.
        int applicableQuantity = Math.min(product.quantity(), maxApplicableQuantityPerCoupon);
        int discountAmount = applicableQuantity * discountAmountPerUnit;
        // 할인 금액은 상품 총액을 초과할 수 없다.
        return Math.min(discountAmount, product.totalPrice());
    }

    private boolean matchesCategory(String categoryId) {
        // 카테고리 조건이 없으면 모든 카테고리를 허용한다.
        if (targetCategoryIds.isEmpty()) {
            return true;
        }
        if (!includeSubcategories) {
            // 하위 포함 옵션이 꺼진 경우 정확히 일치하는 카테고리만 허용한다.
            return targetCategoryIds.contains(categoryId);
        }
        // 하위 포함 옵션이 켜진 경우 부모/하위 경로를 함께 허용한다.
        return targetCategoryIds.stream().anyMatch(target ->
                categoryId.equals(target) || categoryId.startsWith(target + "/")
        );
    }

    private List<Boolean> configuredMatches(boolean productMatch, boolean categoryMatch, boolean brandMatch) {
        // 설정된 조건만 추려서 최종 정책(AND/OR) 계산 대상으로 만든다.
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
