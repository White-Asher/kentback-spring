package example.com.kentbackspring.coupon;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 쿠폰 사용 동시성 제어 테스트.
 */
class CouponUsageConcurrencyTest {

    private final CouponLifecycleService lifecycleService = new CouponLifecycleService();

    @Test
    void shouldAllowOnlyOneSuccessForConcurrentUseRequestsOnSameCoupon() throws InterruptedException {
        // 동일 쿠폰 동시 사용 요청은 1건만 성공해야 한다.
        lifecycleService.issueCoupon(
                "user-1",
                "CONCURRENT-USE",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );

        ConcurrentUseResult result = runConcurrentUseRequests(50, index -> "ORDER-" + index);

        assertThat(result.successCount()).isEqualTo(1);
        assertThat(result.failureCount()).isEqualTo(49);
    }

    @Test
    void shouldNotAllowCouponAlreadyInUseForAnotherOrder() throws InterruptedException {
        // 이미 사용 진행/완료된 쿠폰은 다른 주문에서 사용할 수 없어야 한다.
        lifecycleService.issueCoupon(
                "user-1",
                "CONCURRENT-USE-2",
                LocalDateTime.of(2026, 2, 16, 10, 0),
                LocalDateTime.of(2026, 2, 28, 23, 59, 59)
        );

        ConcurrentUseResult result = runConcurrentUseRequests("CONCURRENT-USE-2", 2, index -> "ORDER-" + (index + 1));
        IssuedCoupon issuedCoupon = lifecycleService.findIssuedCoupon("user-1", "CONCURRENT-USE-2").orElseThrow();

        assertThat(result.successCount()).isEqualTo(1);
        assertThat(result.failureCount()).isEqualTo(1);
        assertThat(issuedCoupon.orderId()).isIn("ORDER-1", "ORDER-2");
    }

    private ConcurrentUseResult runConcurrentUseRequests(int requestCount, IntFunction<String> orderIdProvider) throws InterruptedException {
        return runConcurrentUseRequests("CONCURRENT-USE", requestCount, orderIdProvider);
    }

    private ConcurrentUseResult runConcurrentUseRequests(
            String couponCode,
            int requestCount,
            IntFunction<String> orderIdProvider
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
                    lifecycleService.useCoupon(
                            "user-1",
                            couponCode,
                            orderIdProvider.apply(index),
                            LocalDateTime.of(2026, 2, 17, 9, 0)
                    );
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

        return new ConcurrentUseResult(successCount.get(), failureCount.get());
    }

    private record ConcurrentUseResult(int successCount, int failureCount) {
    }
}
