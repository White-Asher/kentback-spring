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
        return Collections.unmodifiableList(usageAuditLog);
    }

    public void use(String orderId, LocalDateTime usedAt) {
        if (status == CouponUsageStatus.USED) {
            throw new IllegalStateException("coupon already used");
        }
        this.status = CouponUsageStatus.USED;
        this.orderId = orderId;
        this.usedAt = usedAt;
        this.usageAuditLog.add("USED:" + orderId + ":" + usedAt);
    }

    public void restore(LocalDateTime restoredAt, int extendDaysIfExpired) {
        if (restoredAt.isAfter(expiresAt) && extendDaysIfExpired > 0) {
            this.expiresAt = restoredAt.plusDays(extendDaysIfExpired);
        }
        this.status = CouponUsageStatus.UNUSED;
        this.orderId = null;
        this.usedAt = null;
        this.usageAuditLog.add("RESTORED:" + restoredAt);
    }

    public boolean isAvailableAt(LocalDateTime now) {
        return status == CouponUsageStatus.UNUSED && !now.isAfter(expiresAt);
    }
}
