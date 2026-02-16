package example.com.kentbackspring.coupon;

import java.util.HashMap;
import java.util.Map;

/**
 * 쿠폰 발급 수량/중복 발급 정책을 관리한다.
 */
public class CouponIssueManager {

    private final int totalIssuableQuantity;
    private final int perUserLimit;
    private int issuedCount;
    private final Map<String, Integer> issuedCountByUser = new HashMap<>();

    public CouponIssueManager(int totalIssuableQuantity, int perUserLimit) {
        // 쿠폰 정책의 전역 발급 수량과 사용자당 발급 한도를 초기화한다.
        this.totalIssuableQuantity = totalIssuableQuantity;
        this.perUserLimit = perUserLimit;
    }

    public int totalIssuableQuantity() {
        return totalIssuableQuantity;
    }

    public synchronized int issuedCount() {
        return issuedCount;
    }

    public synchronized void issue(String userId) {
        // 전역 발급 수량을 먼저 확인한다.
        if (issuedCount >= totalIssuableQuantity) {
            throw new IllegalStateException("coupon quantity exhausted");
        }

        int userIssuedCount = issuedCountByUser.getOrDefault(userId, 0);
        // 사용자별 발급 상한을 초과하면 중복 발급으로 본다.
        if (userIssuedCount >= perUserLimit) {
            throw new IllegalStateException("coupon already issued to user");
        }

        // 사용자별/전체 발급 카운트를 함께 증가시킨다.
        issuedCountByUser.put(userId, userIssuedCount + 1);
        issuedCount += 1;
    }
}
