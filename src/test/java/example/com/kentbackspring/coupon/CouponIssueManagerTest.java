package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 쿠폰 발급 수량 정책 검증 테스트.
 */
class CouponIssueManagerTest {

    @Test
    void shouldHaveTotalIssuableQuantity() {
        // 시나리오의 기대 동작을 검증한다.
        CouponIssueManager issueManager = new CouponIssueManager(100, 1);

        assertThat(issueManager.totalIssuableQuantity()).isEqualTo(100);
    }

    @Test
    void shouldNotIssueWhenTotalQuantityExhausted() {
        // 시나리오의 기대 동작을 검증한다.
        CouponIssueManager issueManager = new CouponIssueManager(1, 1);
        issueManager.issue("user-1");

        assertThatThrownBy(() -> issueManager.issue("user-2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("coupon quantity exhausted");
    }

    @Test
    void shouldNotIssueDuplicateWhenPerUserLimitExists() {
        // 시나리오의 기대 동작을 검증한다.
        CouponIssueManager issueManager = new CouponIssueManager(100, 1);
        issueManager.issue("user-1");

        assertThatThrownBy(() -> issueManager.issue("user-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("coupon already issued to user");
    }

    @Test
    void shouldNotIssueAlreadyIssuedCouponAgain() {
        // 시나리오의 기대 동작을 검증한다.
        CouponIssueManager issueManager = new CouponIssueManager(100, 1);
        issueManager.issue("user-1");

        assertThatThrownBy(() -> issueManager.issue("user-1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("coupon already issued to user");
    }
}
