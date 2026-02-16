package example.com.kentbackspring.coupon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 사용자에게 발급된 쿠폰 인스턴스.
 */
public class IssuedCoupon {

    private final String userId;
    private final String couponCode;
    private final LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private CouponUsageStatus status;
    private LocalDateTime usedAt;
    private String orderId;
    private final List<String> usageAuditLog = new ArrayList<>();

    public IssuedCoupon(String userId, String couponCode, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        // 발급 시점에는 항상 미사용 상태로 시작한다.
        this.userId = userId;
        this.couponCode = couponCode;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = CouponUsageStatus.UNUSED;
    }

    public String userId() {
        return userId;
    }

    public String couponCode() {
        return couponCode;
    }

    public LocalDateTime issuedAt() {
        return issuedAt;
    }

    public LocalDateTime expiresAt() {
        return expiresAt;
    }

    public CouponUsageStatus status() {
        return status;
    }

    public LocalDateTime usedAt() {
        return usedAt;
    }

    public String orderId() {
        return orderId;
    }

    public List<String> usageAuditLog() {
        // 외부에서 감사 로그를 수정하지 못하도록 읽기 전용 뷰를 제공한다.
        return Collections.unmodifiableList(usageAuditLog);
    }

    public void use(String orderId, LocalDateTime usedAt) {
        // 사용 이력의 무결성을 위해 중복 사용을 차단한다.
        if (status == CouponUsageStatus.USED) {
            throw new IllegalStateException("coupon already used");
        }
        this.status = CouponUsageStatus.USED;
        this.orderId = orderId;
        this.usedAt = usedAt;
        // 감사 목적의 사용 이벤트를 남긴다.
        this.usageAuditLog.add("USED:" + orderId + ":" + usedAt);
    }

    public void restore(LocalDateTime restoredAt, int extendDaysIfExpired) {
        // 복구 시점에 이미 만료된 경우에만 정책에 따라 만료일을 연장한다.
        if (restoredAt.isAfter(expiresAt) && extendDaysIfExpired > 0) {
            this.expiresAt = restoredAt.plusDays(extendDaysIfExpired);
        }
        this.status = CouponUsageStatus.UNUSED;
        this.orderId = null;
        this.usedAt = null;
        // 복구 이벤트를 감사 로그로 누적한다.
        this.usageAuditLog.add("RESTORED:" + restoredAt);
    }

    public boolean isAvailableAt(LocalDateTime now) {
        // 사용 가능 조건: 미사용 && 만료 전(또는 만료 시각 동일).
        return status == CouponUsageStatus.UNUSED && !now.isAfter(expiresAt);
    }
}
