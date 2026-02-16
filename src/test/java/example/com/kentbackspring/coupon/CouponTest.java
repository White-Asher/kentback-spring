package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 쿠폰 기본 메타 정보 모델 검증 테스트.
 */
class CouponTest {

    @Test
    void shouldHaveUniqueCouponCode() {
        // 쿠폰은 식별 가능한 고유 코드를 반드시 가진다.
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
        // 운영/표시 목적의 이름과 설명이 보존되어야 한다.
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
        // 쿠폰 유효성 검증을 위한 시작/종료 일시를 정확히 저장해야 한다.
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
