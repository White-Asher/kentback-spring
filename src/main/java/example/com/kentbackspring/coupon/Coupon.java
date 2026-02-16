package example.com.kentbackspring.coupon;

import java.time.LocalDateTime;

/**
 * 쿠폰의 기본 메타 정보를 담는 값 객체.
 *
 * @param code 고유 쿠폰 코드
 * @param name 쿠폰 이름
 * @param description 쿠폰 설명
 * @param validFrom 유효 시작 일시
 * @param validTo 유효 종료 일시
 */
public record Coupon(
        String code,
        String name,
        String description,
        LocalDateTime validFrom,
        LocalDateTime validTo
) {

    /**
     * 특정 시각에 쿠폰 사용 가능 여부를 반환한다.
     */
    public boolean isUsableAt(LocalDateTime dateTime) {
        return !dateTime.isBefore(validFrom) && !dateTime.isAfter(validTo);
    }

    /**
     * 발급 기준 N일 유효 쿠폰의 만료 시각(23:59:59)을 계산한다.
     */
    public static LocalDateTime calculateExpiresAt(LocalDateTime issuedAt, int validDays) {
        return issuedAt.toLocalDate().plusDays(validDays).atTime(23, 59, 59);
    }
}
