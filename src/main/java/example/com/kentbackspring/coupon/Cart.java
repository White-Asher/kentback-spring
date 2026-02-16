package example.com.kentbackspring.coupon;

import java.util.List;

/**
 * 장바구니 도메인 모델.
 */
public record Cart(List<Product> products, int shippingFee) {

    public int productTotalAmount() {
        return products.stream()
                .mapToInt(Product::totalPrice)
                .sum();
    }
}
