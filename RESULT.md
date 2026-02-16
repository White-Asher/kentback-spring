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

## 2026-02-16 - Phase 1.3 Minimum Purchase Conditions

### Red
- 대상: `PercentageDiscountCouponTest`의 최소 구매 조건 4개 테스트
  - `shouldNotApplyCouponWhenBelowMinimumPurchaseAmount`
  - `shouldApplyCouponWhenExactlyMeetingMinimumPurchaseAmount`
  - `shouldNotApplyCouponWhenBelowMinimumPurchaseQuantity`
  - `shouldValidateMinimumPurchaseBasedOnAmountBeforeDiscount`
- 실행: `.\gradlew test --tests "example.com.kentbackspring.coupon.PercentageDiscountCouponTest"`
- 결과: 실패
  - `compileTestJava FAILED`
  - 원인: 최소 구매 조건 생성자/수량 기반 `apply` 미구현

### Green
- 구현: `PercentageDiscountCoupon`에 최소 구매 금액/수량 필드 및 오버로드 생성자 추가
- 구현: `apply(int productPrice, int purchaseQuantity)` 추가
- 구현: 최소 구매 금액/수량 검증 로직 추가 (검증 기준: 할인 전 금액)
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.PercentageDiscountCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (기능 검증 중심 최소 변경)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 2.1 Coupon Basic Info (Coupon Code)

### Red
- 대상: `CouponTest.shouldHaveUniqueCouponCode`
- 실행: `.\gradlew test --tests "example.com.kentbackspring.coupon.CouponTest"`
- 결과: 실패
  - `compileTestJava FAILED`
  - 원인: `Coupon` 클래스 미구현

### Green
- 구현: `Coupon` 도메인 타입 추가 (`record Coupon(String code)`)
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 2.2 Coupon Validity Period

### Green
- 구현: `Coupon.isUsableAt(LocalDateTime)` 추가 (시작/종료 경계 포함 판정)
- 구현: `Coupon.calculateExpiresAt(LocalDateTime, int)` 추가 (발급일 기준 N일 만료 계산)
- 구현: `CouponTest`에 유효 시작 전/기간 내/만료 후/발급 후 N일 만료 계산 테스트 4건 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 2.3 Coupon Issue Quantity Management

### Green
- 구현: `CouponIssueManager` 추가 (총 발급 수량, 사용자당 발급 제한, 중복 발급 차단)
- 구현: `CouponIssueManagerTest`에 4개 정책 테스트 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CouponIssueManagerTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 2.1 Coupon Basic Info (Name & Description)

### Red
- 대상: `CouponTest.shouldHaveCouponNameAndDescription`
- 실행: `.\gradlew test --tests "*CouponTest"`
- 결과: 실패
  - `compileTestJava FAILED`
  - 원인: `Coupon` 생성자 `(String, String, String)` 및 `name()/description()` 접근자 미구현

### Green
- 구현: `Coupon` 레코드를 `record Coupon(String code, String name, String description)`로 확장
- 구현: 기존 코드 검증 테스트를 새 생성자 시그니처에 맞게 갱신
- 실행(단위): `.\gradlew test --tests "*CouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 2.1 Coupon Basic Info (Validity Start/End DateTime)

### Red
- 대상: `CouponTest.shouldHaveCouponValidityStartAndEndDateTime`
- 실행: `.\gradlew test --tests "*CouponTest"`
- 결과: 실패
  - `compileTestJava FAILED`
  - 원인: `Coupon` 생성자 `(String, String, String, LocalDateTime, LocalDateTime)` 및 `validFrom()/validTo()` 접근자 미구현

### Green
- 구현: `Coupon` 레코드를 `validFrom`, `validTo` 필드를 포함하도록 확장
- 구현: 기존 `CouponTest` 생성자 호출을 유효 시작/종료일시 인자를 포함하도록 갱신
- 실행(단위): `.\gradlew test --tests "*CouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)
