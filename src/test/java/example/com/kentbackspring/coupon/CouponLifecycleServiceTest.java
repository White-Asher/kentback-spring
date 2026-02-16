package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

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

    @Test
    void shouldRetrieveAvailableCouponListForUser() {
        lifecycleService.issueCoupon(
                "user-1",
                "WELCOME-10",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );
        lifecycleService.issueCoupon(
                "user-1",
                "VIP-20",
                LocalDateTime.of(2026, 2, 16, 11, 0),
                LocalDateTime.of(2026, 3, 10, 23, 59, 59)
        );

        List<IssuedCoupon> available = lifecycleService.listAvailableCoupons("user-1", LocalDateTime.of(2026, 2, 20, 10, 0));

        assertThat(available).extracting(IssuedCoupon::couponCode)
                .containsExactlyInAnyOrder("WELCOME-10", "VIP-20");
    }

    @Test
    void shouldExcludeExpiredCouponFromAvailableList() {
        lifecycleService.issueCoupon(
                "user-1",
                "EXPIRED",
                LocalDateTime.of(2026, 2, 1, 10, 0),
                LocalDateTime.of(2026, 2, 10, 23, 59, 59)
        );

        List<IssuedCoupon> available = lifecycleService.listAvailableCoupons("user-1", LocalDateTime.of(2026, 2, 20, 10, 0));

        assertThat(available).isEmpty();
    }

    @Test
    void shouldExcludeUsedCouponFromAvailableList() {
        lifecycleService.issueCoupon(
                "user-1",
                "USED-COUPON",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );
        lifecycleService.useCoupon("user-1", "USED-COUPON", "ORDER-1", LocalDateTime.of(2026, 2, 17, 9, 0));

        List<IssuedCoupon> available = lifecycleService.listAvailableCoupons("user-1", LocalDateTime.of(2026, 2, 20, 10, 0));

        assertThat(available).isEmpty();
    }
}
