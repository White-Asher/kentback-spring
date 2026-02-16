package example.com.kentbackspring.coupon;

import java.time.LocalDateTime;

public record Coupon(
        String code,
        String name,
        String description,
        LocalDateTime validFrom,
        LocalDateTime validTo
) {
}
