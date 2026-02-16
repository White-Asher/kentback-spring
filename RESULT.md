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

## 2026-02-16 - Phase 3.1 Product Coupon (Single Product Target)

### Green
- 구현: `Product` 모델 추가
- 구현: `ProductCoupon` 추가 (단일 상품 ID 대상 적용 판별)
- 구현: `ProductCouponTest`에 단일 상품 대상 3개 테스트 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.ProductCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 3.2 Product Coupon (Multiple Product Targets)

### Green
- 구현: `ProductCouponTest`에 다중 상품 ID 대상 2개 테스트 추가
- 검증: 기존 `ProductCoupon` 다중 ID 매칭 로직으로 요구사항 충족 확인
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.ProductCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (기존 구현 재사용)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 3.3 Product Coupon (Category Based)

### Green
- 구현: `ProductCoupon.forCategory(...)` 팩토리 추가
- 구현: `ProductCouponTest`에 카테고리 기반 3개 테스트 추가
- 검증: 하위 카테고리 포함 여부에 따라 적용 범위가 달라짐을 확인
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.ProductCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 3.4 Product Coupon (Brand Based)

### Green
- 구현: `ProductCoupon.forBrand(...)` 팩토리 추가
- 구현: `ProductCouponTest`에 브랜드 기반 2개 테스트 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.ProductCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 3.5 Product Coupon (Combined Conditions)

### Green
- 구현: `ProductCoupon.forBrandAndCategory(...)` 팩토리 추가 (AND 조건)
- 구현: `ProductCouponTest`에 조합 조건 2개 테스트 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.ProductCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 3.6 Product Coupon (Per-Product Quantity Limit)

### Green
- 구현: `ProductCoupon.forProductIdsWithQuantityLimit(...)` 팩토리 추가
- 구현: `ProductCouponTest`에 상품당 적용 개수 제한 2개 테스트 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.ProductCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 4.1 Cart Coupon (Cart Total Discount)

### Green
- 구현: `Cart`, `CartCoupon`, `CartCouponResult` 추가
- 구현: `CartCouponTest`에 정액/정률/최소 주문 금액 테스트 3개 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CartCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 4.2 Cart Coupon (Proportional Distribution)

### Green
- 구현: `CartCouponTest`에 비례 분배 4개 테스트 추가
- 검증: 비례 분배/원 단위 절사/나머지 최고가 할당/합계 일치 확인
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CartCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (기존 분배 로직 재사용)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 4.3 Cart Coupon (Target Product Filtering)

### Green
- 구현: `CartCouponTest`에 카테고리 포함/제외 상품/필터 후 최소금액 테스트 3개 추가
- 검증: `CartCoupon` 필터링 로직(`withIncludedCategoryIds`, `withExcludedProductIds`) 동작 확인
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CartCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (기존 필터링 로직 재사용)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 4.4 Cart Coupon (Shipping Fee Exclusion)

### Green
- 구현: `CartCouponTest`에 배송비 제외 규칙 테스트 2개 추가
- 검증: 최소 주문 금액/할인 계산 모두 배송비 제외 기준으로 동작 확인
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CartCouponTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (기존 구현 재사용)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 5.1 Coupon Stacking Policy (No Stacking)

### Green
- 구현: `CouponCandidate`, `CouponPolicyService` 추가
- 구현: `CouponPolicyServiceTest`에 중복 불가 정책 3개 테스트 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CouponPolicyServiceTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 5.2 Coupon Stacking Policy (Auto Best Discount)

### Green
- 구현: `CouponPolicyService.selectBestCoupon(...)` 추가
- 구현: `CouponPolicyServiceTest`에 최대 할인 자동 선택/동일 할인 시 빠른 만료 선택 테스트 2개 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CouponPolicyServiceTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 5.3 Coupon Stacking Policy (Sequential Apply)

### Green
- 구현: `SequentialDiscountResult` 추가
- 구현: `CouponPolicyService.applySequential(...)`, `isSequentialBetterThanNoStackingPolicy(...)` 추가
- 구현: `CouponPolicyServiceTest`에 순차 적용 정책 3개 테스트 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CouponPolicyServiceTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 6.1 Coupon Issue

### Green
- 구현: `CouponUsageStatus`, `IssuedCoupon`, `CouponLifecycleService` 추가
- 구현: `CouponLifecycleServiceTest`에 쿠폰 발급 4개 테스트 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CouponLifecycleServiceTest"`
- 결과(단위): 성공 (`BUILD SUCCESSFUL`)

### Refactor
- 변경: 구조 리팩터링 없음 (최소 구현 유지)
- 실행(전체): `.\gradlew test`
- 결과(전체): 성공 (`BUILD SUCCESSFUL`)

## 2026-02-16 - Phase 6.2 Coupon Use

### Green
- 구현: `CouponLifecycleService.useCoupon(...)` 추가
- 구현: `CouponLifecycleServiceTest`에 쿠폰 사용 4개 테스트 추가
- 실행(단위): `.\gradlew test --tests "example.com.kentbackspring.coupon.CouponLifecycleServiceTest"`
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
