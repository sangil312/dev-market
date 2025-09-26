# Dev Market
### Language & Framework
- `Java 17`
- `Spring Boot 3.5.5`
- `JPA(Hibernate)` & `QueryDsl`
- `JUnit5` & `Mokito`
### DB & DevOps
- `MySQL 8.x`
- `Docker`

### Architecture
- Layerd Architecture
- Clean Architecture

### API 명세
- Spring REST Docs
  ```gradle
  dav-market/
  ./gradlew clean
  ./gradlew bootJar
  java -jar build/libs/market-0.0.1-SNAPSHOT.jar
  ```
  - jar 파일 실행 후 `http://localhost:8080/docs/index.html`
### Language & Framework
- `Java 17`
- `Spring Boot 3.5.5`
- `JPA(Hibernate)` & `QueryDsl`
- `JUnit5` & `Mokito`
### DB & DevOps
- `MySQL 8.x`
- `Docker`

### Architecture
- Layerd Architecture
- Clean Architecture

### API 명세
- Spring REST Docs
  ```gradle
  dav-market/
  ./gradlew clean
  ./gradlew bootJar
  java -jar build/libs/market-0.0.1-SNAPSHOT.jar
  ```
  - jar 파일 실행 후 `http://localhost:8080/docs/index.html` 접근 시 API 명세 확인 가능
---
## ✅ 상품 조회
상품 목록을 불러오는 API 구현

#### 요구사항 정의

- 상품은 카테고리, 상품명, 가격 범위로 검색할 수 있어야 합니다.
- 페이징 처리를 구현해주세요.
- 상품의 품절 여부가 표시되어야 합니다.

## ✅ 장바구니
사용자별 장바구니 API 구현

#### 요구사항 정의

- 장바구니에 상품을 추가, 수정, 삭제, 조회할 수 있어야 합니다.
- 장바구니 조회 시 각 상품의 현재 품절 상태를 확인할 수 있어야 합니다.
- 회원가입 기능은 별도로 구현하지 않아도 됩니다.

## ✅ 주문 및 결제
주문을 처리하는 API 구현

#### 요구사항 정의

- 주문 요청 시 장바구니에 담긴 모든 상품들의 금액을 합산하여 결제를 진행해야 합니다.
- 주문 요청 시 상품의 재고를 관리해야 합니다.
- 결제 요청은 외부 결제 API를 사용하여 처리해야 합니다. (하단 모의 API 스펙 참고)
- 결제 요청 이력을 관리할 수 있어야 합니다.
- 결제 성공 및 실패 여부에 따라 적절한 응답을 반환해야 합니다.
- 재고 부족 시 적절한 처리를 구현해주세요.
- 주문 상태 관리 (주문 생성, 결제 완료, 주문 취소 등)를 구현해주세요.

---

## 📋 모의 API
외부 결제 API 모킹 처리를 위한 모의 API

#### 모의 API 생성 사이트
https://beeceptor.com/

#### 모의 결제 API 스펙
```java
HTTP Method : POST
URL : 'https://devmarket.free.beeceptor.com/api/v1/payment'

[Request Header]
Content-Type: application/json

[Request Body]
{
    "orderId": String,  // 주문 ID
    "amount": Number    // 결제 금액
}

[Response - 성공]
{
    "status": "SUCCESS",
    "transactionId": "txn_{{faker 'number.bigInt'}}",
    "message": "Payment processed successfully"
}

[Response - 실패]
{
    "status": "FAILED",
    "transactionId": null,
    "message": "Something wrong!"
}
```
