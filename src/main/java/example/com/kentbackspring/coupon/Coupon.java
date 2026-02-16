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
}
