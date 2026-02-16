package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 쿠폰 발급/사용 수명주기 테스트.
 */
class CouponLifecycleServiceTest {

    private final CouponLifecycleService lifecycleService = new CouponLifecycleService();

    @Test
    void shouldIssueCouponToUser() {
        IssuedCoupon issuedCoupon = lifecycleService.issueCoupon(
                "user-1",
                "WELCOME-10",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );

        assertThat(issuedCoupon.couponCode()).isEqualTo("WELCOME-10");
    }

    @Test
    void shouldStartIssuedCouponAsUnusedState() {
        IssuedCoupon issuedCoupon = lifecycleService.issueCoupon(
                "user-1",
                "WELCOME-10",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );

        assertThat(issuedCoupon.status()).isEqualTo(CouponUsageStatus.UNUSED);
    }

    @Test
    void shouldNotIssueAlreadyIssuedCouponAgain() {
        lifecycleService.issueCoupon(
                "user-1",
                "WELCOME-10",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );

        assertThatThrownBy(() -> lifecycleService.issueCoupon(
                "user-1",
                "WELCOME-10",
                LocalDateTime.of(2026, 2, 16, 11, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("coupon already issued to user");
    }

    @Test
    void shouldRecordIssuedDateTimeWhenIssuingCoupon() {
        LocalDateTime issuedAt = LocalDateTime.of(2026, 2, 16, 10, 0);
        IssuedCoupon issuedCoupon = lifecycleService.issueCoupon(
                "user-1",
                "WELCOME-10",
                issuedAt,
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );

        assertThat(issuedCoupon.issuedAt()).isEqualTo(issuedAt);
    }
}
