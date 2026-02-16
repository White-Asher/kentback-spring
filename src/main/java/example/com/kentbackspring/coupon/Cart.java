package example.com.kentbackspring.coupon;

import java.util.List;

/**
 * 장바구니 도메인 모델.
 */
public record Cart(List<Product> products, int shippingFee) {

    public int productTotalAmount() {
        // 배송비를 제외한 상품 합계만 계산한다.
        return products.stream()
                .mapToInt(Product::totalPrice)
                .sum();
    }
}
