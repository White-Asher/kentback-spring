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
        this.totalIssuableQuantity = totalIssuableQuantity;
        this.perUserLimit = perUserLimit;
    }

    public int totalIssuableQuantity() {
        return totalIssuableQuantity;
    }

    public int issuedCount() {
        return issuedCount;
    }

    public void issue(String userId) {
        if (issuedCount >= totalIssuableQuantity) {
            throw new IllegalStateException("coupon quantity exhausted");
        }

        int userIssuedCount = issuedCountByUser.getOrDefault(userId, 0);
        if (userIssuedCount >= perUserLimit) {
            throw new IllegalStateException("coupon already issued to user");
        }

        issuedCountByUser.put(userId, userIssuedCount + 1);
        issuedCount += 1;
    }
}
