package example.com.kentbackspring.coupon;

/**
 * 쿠폰 적용 대상 상품.
 */
public record Product(
        String productId,
        String categoryId,
        String brandId,
        int unitPrice,
        int quantity
) {

    public int totalPrice() {
        // 주문 수량을 반영한 상품 총액이다.
        return unitPrice * quantity;
    }
}
