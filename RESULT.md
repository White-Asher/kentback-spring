# Test & Verification Results

## 2026-02-15 - Phase 1.1 Fixed Amount Discount Coupon

### Red
- 대상: `FixedAmountDiscountCouponTest.shouldRejectNegativeDiscountAmount`
- 실행: `.\gradlew test --tests "example.com.kentbackspring.coupon.FixedAmountDiscountCouponTest"`
- 결과: 실패
  - `4 tests completed, 1 failed`
  - 실패 테스트: `shouldRejectNegativeDiscountAmount()`

### Green
- 구현: `FixedAmountDiscountCoupon` 생성자에 음수 할인 금액 검증 추가
- 실행: `.\gradlew test --tests "example.com.kentbackspring.coupon.FixedAmountDiscountCouponTest"`
- 결과: 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 생성자 검증 로직을 `validateDiscountAmount` 메서드로 분리
- 변경: 테스트 파일의 불필요 import 제거
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.FixedAmountDiscountCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-15 - Source Comment Update

- 변경 파일: `src/main/java/example/com/kentbackspring/coupon/FixedAmountDiscountCoupon.java`
- 변경 내용: 클래스 설명, 할인 하한 처리, 입력 검증 의도를 설명하는 주석 추가
- 실행: `.\gradlew test --tests "example.com.kentbackspring.coupon.FixedAmountDiscountCouponTest"`
- 결과: 성공 (`BUILD SUCCESSFUL`)

## 2026-02-15 - Test Comment Update

- 변경 파일: `src/test/java/example/com/kentbackspring/coupon/FixedAmountDiscountCouponTest.java`
- 변경 내용: 각 테스트 케이스의 검증 의도를 설명하는 주석 추가
- 실행: `.\gradlew test --tests "example.com.kentbackspring.coupon.FixedAmountDiscountCouponTest"`
- 결과: 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 1.2 Percentage Discount Coupon

### Red
- 대상: `PercentageDiscountCouponTest.shouldDiscountProductPriceByGivenPercentage`
- 실행: `.\gradlew test --tests "example.com.kentbackspring.coupon.PercentageDiscountCouponTest"`
- 결과: 실패
  - `compileTestJava FAILED`
  - 원인: `PercentageDiscountCoupon` 클래스 미구현

- 대상: `PercentageDiscountCouponTest.shouldNotExceedMaximumDiscountAmountWhenCapExists`
- 실행: `.\gradlew test --tests "example.com.kentbackspring.coupon.PercentageDiscountCouponTest"`
- 결과: 실패
  - `compileTestJava FAILED`
  - 원인: `(int, int)` 생성자 미구현

### Green
- 구현: `PercentageDiscountCoupon` 신규 추가 (정률 할인 계산)
- 구현: 최대 할인 금액 제한을 위한 오버로드 생성자와 상한 처리 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.PercentageDiscountCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)
- 검증: `원 단위 절사`, `100% 할인 시 0원` 테스트 추가 후 동일 테스트 실행 성공

### Refactor
- 변경: 별도 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)
