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

    @Test
    void shouldMarkUnusedCouponAsUsed() {
        lifecycleService.issueCoupon(
                "user-1",
                "WELCOME-10",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );

        lifecycleService.useCoupon("user-1", "WELCOME-10", "ORDER-1", LocalDateTime.of(2026, 2, 17, 9, 0));
        IssuedCoupon issuedCoupon = lifecycleService.findIssuedCoupon("user-1", "WELCOME-10").orElseThrow();

        assertThat(issuedCoupon.status()).isEqualTo(CouponUsageStatus.USED);
    }

    @Test
    void shouldRecordUsedDateTimeWhenCouponUsed() {
        lifecycleService.issueCoupon(
                "user-1",
                "WELCOME-10",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );
        LocalDateTime usedAt = LocalDateTime.of(2026, 2, 17, 9, 0);

        lifecycleService.useCoupon("user-1", "WELCOME-10", "ORDER-1", usedAt);
        IssuedCoupon issuedCoupon = lifecycleService.findIssuedCoupon("user-1", "WELCOME-10").orElseThrow();

        assertThat(issuedCoupon.usedAt()).isEqualTo(usedAt);
    }

    @Test
    void shouldNotUseAlreadyUsedCouponAgain() {
        lifecycleService.issueCoupon(
                "user-1",
                "WELCOME-10",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );
        lifecycleService.useCoupon("user-1", "WELCOME-10", "ORDER-1", LocalDateTime.of(2026, 2, 17, 9, 0));

        assertThatThrownBy(() ->
                lifecycleService.useCoupon("user-1", "WELCOME-10", "ORDER-2", LocalDateTime.of(2026, 2, 18, 9, 0))
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("coupon already used");
    }

    @Test
    void shouldLinkOrderIdWhenCouponUsed() {
        lifecycleService.issueCoupon(
                "user-1",
                "WELCOME-10",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );

        lifecycleService.useCoupon("user-1", "WELCOME-10", "ORDER-1", LocalDateTime.of(2026, 2, 17, 9, 0));
        IssuedCoupon issuedCoupon = lifecycleService.findIssuedCoupon("user-1", "WELCOME-10").orElseThrow();

        assertThat(issuedCoupon.orderId()).isEqualTo("ORDER-1");
    }
}
