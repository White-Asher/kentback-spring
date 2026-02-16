package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 쿠폰 발급 동시성 제어 테스트.
 */
class CouponIssueManagerConcurrencyTest {

    @Test
    void shouldHandleConcurrentIssueRequestsForLimitedQuantity() throws InterruptedException {
        // 한정 수량 쿠폰의 동시 발급 요청이 정확히 처리되어야 한다.
        CouponIssueManager issueManager = new CouponIssueManager(100, 1);

        ConcurrentIssueResult result = runConcurrentIssue(issueManager, 100, index -> "user-" + index);

        assertThat(result.successCount()).isEqualTo(100);
        assertThat(result.failureCount()).isEqualTo(0);
        assertThat(issueManager.issuedCount()).isEqualTo(100);
    }

    @Test
    void shouldFailIssueRequestsExceedingAvailableQuantityUnderConcurrency() throws InterruptedException {
        // 발급 가능 수량을 초과하는 동시 요청은 실패해야 한다.
        CouponIssueManager issueManager = new CouponIssueManager(50, 1);

        ConcurrentIssueResult result = runConcurrentIssue(issueManager, 200, index -> "user-" + index);

        assertThat(result.successCount()).isEqualTo(50);
        assertThat(result.failureCount()).isEqualTo(150);
        assertThat(issueManager.issuedCount()).isEqualTo(50);
    }

    @Test
    void shouldPreventDuplicateIssueInConcurrentRequests() throws InterruptedException {
        // 동시 요청 상황에서도 동일 사용자 중복 발급은 1건만 성공해야 한다.
        CouponIssueManager issueManager = new CouponIssueManager(100, 1);

        ConcurrentIssueResult result = runConcurrentIssue(issueManager, 100, index -> "same-user");

        assertThat(result.successCount()).isEqualTo(1);
        assertThat(result.failureCount()).isEqualTo(99);
        assertThat(issueManager.issuedCount()).isEqualTo(1);
    }

    private ConcurrentIssueResult runConcurrentIssue(
            CouponIssueManager issueManager,
            int requestCount,
            IntFunction<String> userIdProvider
    ) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(requestCount, 32));
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(requestCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < requestCount; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    start.await();
                    issueManager.issue(userIdProvider.apply(index));
                    successCount.incrementAndGet();
                } catch (IllegalStateException ignored) {
                    failureCount.incrementAndGet();
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    failureCount.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        done.await();
        executor.shutdown();

        return new ConcurrentIssueResult(successCount.get(), failureCount.get());
    }

    private record ConcurrentIssueResult(int successCount, int failureCount) {
    }
}
