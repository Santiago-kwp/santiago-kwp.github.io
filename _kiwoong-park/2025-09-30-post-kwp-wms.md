---
title: "[WMS 1차] 프로젝트를 마무리 하며 개선 방향 정리 (박기웅)"
excerpt: "WMS 프로젝트 랩업 내용 정리 포스팅입니다."
date: 2025-09-30
author: kiwoong-park
author_profile: true
layout: single
---

### 순 공부 시간 : 25.9.30.19:00 ~ 9.30.19:50 (50분), 23:20 ~ 24:27분 (67분)

### 창고관리시스템에 필요한 기능 및 업무 프로세스들에 대한 팀 단위 브레인스토밍(?)의 부재
> 어떤 창고를 관리할 것인지에 대해서는 팀에서 다양한 얘기를 나누었지만, 지나와 생각해보면 WMS가 무엇을 하는 소프트웨어인지, 어떤 기능들이 필요하고, 누가 사용하고, 어떤 데이터들이 필요한지에 대해서는 전체 팀원이 도메인 지식이 있는 것이 아닌 상황에서 성급하게 담당 업무를 구분했던 것 같다. 
나역시도 물류 분야에 대해서는 문외한이어서 리딩을 해줄 수 없다는 생각에 우선적으로 담당 업무를 맡기고, 자기 파트를 최대한 소화할 수 있는 충분한 시간을 부여하는 것이 팀장의 역할이라고 생각해서 의사결정을 내렸지만 업무를 나누기 전에 각자 이해한 내용에 대해서 토의하고 어떤 비지니스 로직과 핵심 데이터를 어떻게 관리해야 하는지를 충분히 논의하는 시간을 가졌어야 하지 않나 싶다. 

> 결과적으로 내 파트였던 입출고 로직에 대해서 깊이있게 생각하지 못했다. 입/출고 로직과 플로우에 대해 유스케이스를 그려볼때만 해도 수업시간에 해왔던 CRUD 정도라고 생각한게 아쉬웠다. 

### MVC 패턴 사용의 몰이해와 지식의 한계
> MVC 패턴에 대해 얕게 알았다고 해야 할까... VO, DAO, Controller, View 패키지로 나누어서 최대한 각자의 역할에 집중하도록 클래스의 책임을 구분한다는 정도로 머리로만 이해했지, 코드로 머릿속 개념을 구현할 정도의 레벨은 아니었다. 그러다 보니 일단 기능을 구현 해야 한다는 압박감으로 인해 끝없는 유사한 기능의 메소드 생성을 남발하게 되었다. 기능을 하나씩 구현할수록 코드는 점차 길어지고, 가독성은 떨어졌으며 내 이전 코드를 이해하는 데만 해도 적지 않은 에너지를 소모하게 되었다. 결과적으로 구현 속도는 점차 느려졌으며, 필수 기능을 구현하는데만 해도 내 예상 시간보다 최소 1.5배 이상의 시간을 쓰게 된 것 같다. 

> 인공지능의 시대... 내 실력을 인정하고 인공지능을 통해 빠르게 배워야 한다. 주말에 점심 짬을 내어 프리로 일하는 학교 후배 개발자를 만나 짧게 이야기를 나눌 수 있었는데, 그 친구가 해준 말이 인상 깊었다. 나는 그 친구에게 인공지능을 쓰게 되면 너무 머리를 안쓰게 되는 것 같아서 최대한 내 힘으로 코드를 구현해보려고 하니 생각보다 너무 시간이 걸린다고 푸념을 늘어놓았다. 그 친구도 딸깍이 되는 현실에 일부는 동의를 했지만, 추가로 해준 이야기는 미처 내가 생각치 못한 것이었다. 이제 프로그래머가 코드를 짜는 시대는 이미 지나왔다고, 코드를 리뷰하는 능력이 더 중요하니 최대한 많이 코드를 읽고 이해하려고 노력하고, 이해한 코드를 팀원들과 공유하고 사용해보는 능력이 더 중요하다고 했다. 그 얘기를 듣고 약간 얻어 맞은 느낌이랄까... 조금 더 적극적으로 좋은 코드를 내 것으로 만드려고 시도하는 경험을 가져야 겠다고 생각했고, 내 지식의 틀에 갇혀서 우물 안 개구리로 코드를 구현하는 것은 이번이 마지막이기를 바래본다.

### 메소드 지옥에서 벗어나자
- 온갖 종류의 조회 쿼리를 프로시저로 생성하는 것은 이제 그만
  - 조회 쿼리를 프로시저로 만드는 것은 사실 얼마 안걸리지만, 코드량이 많아지게 되면서 이 코드를 일단 DAO에서 callable statement를 통해 호출해야 하고, service 패키지에서 dao의 메소드를 사용해야 되며, 커맨드 패턴을 사용해보려고 하다보니 오히려 커맨드 클래스까지 만들어주는 이절, 삼절, 뇌절을 해버리는 결과가 되어버렸다.

**<해결 방법>**
- 🧩 1. **동적 쿼리 + 공통 조회 서비스 계층 활용**
  - **Service 계층**에서 공통 조회 로직을 만들고, 조건에 따라 동적으로 쿼리를 생성한다.
  - 예: `getInboundStatus(Member member, Status status, DateRange range, boolean isAdmin)` 같은 메소드 하나로 다양한 조건을 처리.

- 📦 2. **DTO나 Filter 객체로 조건 캡슐화**
  - 조회 조건을 하나의 객체로 묶어 전달하면 메소드 시그니처가 간결해지고 확장도 쉬워질 수 있다.
  ```java
public class InboundSearchFilter {
    private Long memberId;
    private Boolean isApproved;
    private Boolean isCompleted;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isAdminView;
}

```

```java
public List<InboundStatus> searchInboundStatus(InboundSearchFilter filter) {
    // 조건에 따라 동적 쿼리 생성
}

```

---
- 🧠 3. **Repository/DAO에서 조건별 분기 처리**

- Controller는 단순히 Filter 객체를 받아 Service에 넘기고,
- Service는 Repository에서 조건에 따라 분기 처리된 쿼리를 실행.

---

- 🧪 4. **Stored Procedure는 복잡한 로직에만 제한적으로**

- 단순 조회는 애플리케이션 레벨에서 처리하고,
- 복잡한 집계나 성능이 중요한 경우에만 프로시저 사용.

---

- 🧼 5. **Controller는 최대한 얇게 유지**

- Controller는 요청 파라미터를 Filter 객체로 변환하고 Service에 넘기는 역할만.
- 비즈니스 로직은 Service, 데이터 접근은 Repository에 위임.


### 논리 삭제를 이용하자
> 수업 시간에 병원 ERD를 만들어보고, 프로시저를 연습해보는 작업을 수행했는데, 이때 의문이 들었던 점이 간호사가 퇴사를 하면 간호사의 기본키를 외래키로 가지고 있는 차트나 진료 기록 등의 데이터 테이블은 어떻게 처리할까 였다. 처음에는 `CASCADE` 옵션을 줘서 에라 모르겠다 다 삭제해버리는게 깔끔하지 않을까 생각했는데 이 역시 잘못된 판단이었다...
이번 WMS 역시 입고나 출고를 취소할 수 있는 기능이 있어서 깔끔하게 회원이 취소를 하면 데이터를 삭제해버리도록 구현을 했는데 강사님의 피드백과 인공지능의 피드백을 보니 그렇게 하는게 아닌걸 이해했다.

- 논리 삭제(Soft Delete)는 데이터베이스에서 실제로 레코드를 삭제하지 않고, 삭제된 것처럼 처리하는 방식이다. 이렇게 하면 참조 무결성을 유지하면서도 과거 데이터를 보존할 수 있다. 

🛠️ 논리 삭제 방식 구현 방법

- 1. **삭제 상태를 나타내는 컬럼 추가**

회원 테이블에 다음과 같은 컬럼을 추가한다.
```sql
ALTER TABLE members
ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE,
ADD COLUMN deleted_at DATETIME NULL;
```

- `is_deleted`: 회원이 탈퇴했는지 여부 (`TRUE`면 탈퇴 상태)
- `deleted_at`: 탈퇴한 시점 (선택 사항)

---

- 2. **회원 탈퇴 시 실제 삭제 대신 상태 변경**
회원 탈퇴 요청이 들어오면 `DELETE` 대신 `UPDATE`를 사용합니다:

```sql
UPDATE members
SET is_deleted = TRUE,
    deleted_at = NOW()
WHERE member_id = ?;
```

---

- 3. **조회 시 탈퇴 회원 제외**

회원 정보를 조회할 때는 `is_deleted = FALSE` 조건을 추가합니다:

```sql
SELECT * FROM members
WHERE is_deleted = FALSE;
```

또는 입고 요청 테이블에서 회원 정보를 JOIN할 때도 조건을 걸어야 합니다:

```sql
SELECT r.*, m.name
FROM receiving_requests r
JOIN members m ON r.member_id = m.member_id
WHERE m.is_deleted = FALSE;
```

---

- 4. **입고 요청 테이블은 그대로 유지**

입고 요청 테이블은 회원의 `member_id`를 외래 키로 참조하지만, 회원이 논리 삭제되더라도 데이터는 그대로 유지됩니다. 외래 키 제약은 `ON DELETE NO ACTION` 또는 `ON DELETE RESTRICT`로 설정하는 것이 일반적입니다.

---

### 5. **관리자용 조회에는 탈퇴 회원 포함 가능**

관리자 페이지에서는 탈퇴 회원도 조회할 수 있도록 조건을 제거하거나 선택적으로 포함

```sql
SELECT * FROM members; -- 전체 조회
SELECT * FROM members WHERE is_deleted = TRUE; -- 탈퇴 회원만 조회

```

---

- ✅ 장점
  - 데이터 보존: 입고 요청 등 과거 기록을 유지할 수 있음
  - 참조 무결성 유지: 외래 키 관계가 깨지지 않음
  - 감사 및 추적 가능: 탈퇴 시점과 이유 등을 기록 가능

- ⚠️ 주의사항
  - 모든 SELECT 쿼리에 `is_deleted = FALSE` 조건을 빠뜨리지 않도록 주의
  - 성능 최적화를 위해 인덱스를 `is_deleted` 컬럼에 추가하는 것도 고려

- 🧠 고급 구현 팁
  - **뷰(View)**: 논리 삭제된 데이터를 자동으로 제외하는 뷰를 만들어 사용
    
    ```sql
    CREATE VIEW active_members AS
    SELECT * FROM members WHERE is_deleted = 0;
    
    ```
    
  - **트리거(Trigger)**: `DELETE` 명령을 가로채서 `UPDATE`로 변경
    - 예: PostgreSQL, Oracle, SQL Server에서 자주 사용
  - **ORM 지원**: 대부분의 ORM (예: Hibernate, Sequelize, TypeORM)은 `soft delete` 기능을 내장하고 있어 설정만으로 구현 가능


### 🧩싱글톤으로 구현해줘야 하는 객체들(feat. MVC 패턴)
- 1. **Controller (일부 경우)**
  - **설명**: 요청을 받아서 적절한 모델과 뷰를 연결하는 역할
  - **싱글톤 이유**: 대부분의 프레임워크에서는 컨트롤러를 한 번만 생성해서 재사용
  - **주의**: 상태를 가지면 안 되므로(stateless) 멀티스레드 환경에서는 주의 필요
    - 컨트롤러에 맴버가 아예 없어야 한다는 의미는 아니다. 서비스 객체나, 뷰, 유효성 검사 객체 정도는 의존해도 된다. 물론 이때 의존성 주입을 통해 생성자에서 받도록 하는 것이 좋겠다.

- 2. Service / Manager 클래스
  - **설명**: 비즈니스 로직을 처리하거나 특정 기능을 관리하는 클래스 (예: `UserService`, `SessionManager`)
  - **싱글톤 이유**: 상태를 공유하거나 자원을 효율적으로 관리하기 위해

- 3. DAO (Data Access Object)
  - **설명**: DB 접근을 담당하는 객체
  - **싱글톤 이유**: DB 커넥션 풀이나 ORM을 공유하며, 여러 객체가 동일한 DAO를 사용

- 4. **Configuration / Settings 객체**
  - **설명**: 앱 설정, 환경 변수, DB 설정 등을 관리
  - **싱글톤 이유**: 설정은 앱 전체에서 하나만 존재해야 하며, 어디서든 접근 가능해야 함

- 5. Logger
  - **설명**: 로그를 기록하는 객체
  - **싱글톤 이유**: 로그 파일이나 출력 스트림을 공유해야 하므로 하나만 존재해야 함

- 6. **Cache / Session 관리 객체**
  - **설명**: 사용자 세션, 캐시 데이터 등을 관리
  - **싱글톤 이유**: 상태를 공유하고 일관된 접근을 위해 하나의 인스턴스만 유지


### ERD 변경 사항
> 커피와 같은 유통 기한 및 신선도가 중요한 상품은 FIFO 출고가 필수이고, 짐작컨데 브랜드마다 특정한 원두를 취급하는 경향이 있다고 판단하여(왜냐면 특정 브랜드에서만 내는 맛과 향이 고객에게 특정한 이미지를 만드므로) 회원별 출고 제한을 고려해야 한다고 생각했다. 즉, 입고 이력 기반의 출고 처리가 필요하지 않을까 생각하였지만, 현재의 ERD에서는 구현이 어려워 리펙토링을 해보려고 한다. 

- 🧩 1. 핵심 정책 요약

| 정책 | 설명 |
| --- | --- |
| **FIFO 출고** | 먼저 입고된 상품부터 순서대로 출고 (First-In, First-Out) |
| **회원별 출고 제한** | 해당 회원이 입고한 상품만 출고 가능 |

- 🗃️ 2. 테이블 설계
  - ✅ `inbound_detail` (입고 상세 테이블)

```sql
CREATE TABLE inbound_detail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id BIGINT,
  product_id BIGINT,
  received_quantity INT,
  shipped_quantity INT DEFAULT 0,
  received_at DATETIME
);

```
  - `received_at`: FIFO 출고 순서를 위한 기준
  - `shipped_quantity`: 출고된 수량 기록

  - ✅ `outbound_history` (출고 이력 테이블)

```sql
CREATE TABLE outbound_history (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_id BIGINT,
  product_id BIGINT,
  quantity INT,
  outbound_at DATETIME,
  inbound_detail_id BIGINT
);

```

---

- 🔄 3. 출고 처리 로직 (JDBC 기준)

  -🔹 Step 1: 해당 회원의 입고 이력 조회 (FIFO 순서)

```sql
SELECT id, received_quantity, shipped_quantity
FROM inbound_detail
WHERE member_id = ? AND product_id = ?
ORDER BY received_at ASC;

```

  -🔹 Step 2: 출고 수량만큼 순차적으로 차감

    - 반복문으로 입고 레코드를 순회하며 출고 수량을 분배
    - 각 레코드에 대해 `received_quantity - shipped_quantity`만큼 출고 가능

  -🔹 Step 3: 트랜잭션 처리

    - `inbound_detail`의 `shipped_quantity` 갱신
    - `inventory`에서 전체 수량 차감
    - `outbound_history`에 출고 기록 삽입

---

- 🧠 예시 흐름

회원 A가 상품 X를 100개 입고 (2025-09-01), 50개 입고 (2025-09-10)

  - 출고 요청: 120개
  - 처리 순서:
    - 첫 입고(100개)에서 100개 출고
    - 두 번째 입고(50개)에서 20개 출고
  - `shipped_quantity` 각각 100, 20으로 갱신
  - `inventory.quantity -= 120`
  - `outbound_history`에 2건 기록

---

- ✅ 장점
  - **정확한 이력 추적**: 어떤 입고에서 출고되었는지 명확
  - **정책 준수**: FIFO 및 회원별 제한을 동시에 만족
  - **확장 가능**: 유통기한, 창고 위치 등 추가 조건도 쉽게 적용 가능

---

- ⚠️ 주의사항
  - 트랜잭션 처리 필수: 입고, 출고, 재고 갱신은 반드시 하나의 트랜잭션으로
  - 성능 고려: 입고 이력이 많을 경우 인덱스 최적화 필요 (`member_id`, `product_id`, `received_at`)

