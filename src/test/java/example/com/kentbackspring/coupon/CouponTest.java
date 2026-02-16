package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CouponTest {

    @Test
    void shouldHaveUniqueCouponCode() {
        Coupon coupon = new Coupon(
                "WELCOME-2026",
                "웰컴 쿠폰",
                "신규 회원 환영 쿠폰",
                LocalDateTime.of(2026, 2, 16, 0, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );

        assertThat(coupon.code()).isEqualTo("WELCOME-2026");
    }

    @Test
    void shouldHaveCouponNameAndDescription() {
        Coupon coupon = new Coupon(
                "WELCOME-2026",
                "신규 가입 쿠폰",
                "신규 가입 고객 대상 10% 할인",
                LocalDateTime.of(2026, 2, 16, 0, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );

        assertThat(coupon.name()).isEqualTo("신규 가입 쿠폰");
        assertThat(coupon.description()).isEqualTo("신규 가입 고객 대상 10% 할인");
    }

    @Test
    void shouldHaveCouponValidityStartAndEndDateTime() {
        LocalDateTime validFrom = LocalDateTime.of(2026, 2, 16, 0, 0);
        LocalDateTime validTo = LocalDateTime.of(2026, 2, 28, 23, 59, 59);

        Coupon coupon = new Coupon(
                "WELCOME-2026",
                "신규 가입 쿠폰",
                "신규 가입 고객 대상 10% 할인",
                validFrom,
                validTo
        );

        assertThat(coupon.validFrom()).isEqualTo(validFrom);
        assertThat(coupon.validTo()).isEqualTo(validTo);
    }
}
