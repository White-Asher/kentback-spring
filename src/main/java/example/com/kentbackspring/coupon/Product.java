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
        return unitPrice * quantity;
    }
}
