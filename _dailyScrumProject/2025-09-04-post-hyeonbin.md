---
title: "데이터베이스 공부 내용 정리 1(이현빈)"
excerpt: 테이블 조인, 서브 쿼리, 테이블에서의 집합 연산 관련 공부내용 정리본입니다.
date: 2025-09-04
author: hyeonbin-lee
author_profile: true
layout: single
---

## 학습목표

- 테이블 조인 연산의 종류와 그 개념 이해하기
- 서브쿼리의 개념 및 활용법 이해하기
- MySQL 환경에서 테이블에 관한 집합연산을 수행하는 방법 이해하기

---

## 1. 테이블 조인

### 테이블 조인(JOIN) 개요

- 2개 이상의 테이블을 이용한 SQL 작성법
- 여러 개의 테이블을 연결하여 1개의 테이블을 만드는 과정
- 한 테이블의 행을 다른 테이블의 행에 연결하여 2개 이상의 테이블을 결합하는 연산
- `CROSS JOIN`, `INNER JOIN`, `OUTER JOIN`, `SELF JOIN`으로 구분
  - 이 중 `OUTER JOIN`은 다시 `LEFT OUTER JOIN`, `RIGHT OUTER JOIN`, `FULL OUTER JOIN`으로 세분됨

### CROSS JOIN

- 관계대수의 카티션 프로덕트에 기반한 조인 연산
- 무작위 결합 
→ 테이블의 각 행이 다른 테이블의 모든 행과 결합
- CROSS JOIN 결과 테이블의 행 개수 = 조인된 두 테이블의 행 개수의 곱

```sql
select *
from customer, book, orders
```

### INNER JOIN(내부 조인)

- 동등 조인(EQUI JOIN)이라고도 불림
- where절에서 `=` 연산자를 이용하여, 지정한 속성값이 같은 행의 정보만 사용
- 한 테이블의 외래키와 다른 테이블의 기본키에 대해 동등 연산을 수행할 수 있음
- 아래의 3가지 쿼리문에서의 내부 조인 연산의 결과는 모두 동일

```sql
-- INNER JOIN 키워드 사용
-- ON절에서 조인시킬 컬럼에 관한 동등조건을 지정
select *
from people p inner join card_company c on p.id = c.people_id;

select *
from people p join card_company c on p.id = c.people_id;

-- 아래는 CROSS JOIN 연산에 기반한 내부 조인
-- (WHERE 절에서 조인할 속성에 관한 동등조건을 지정)
select *
from people p, card_company c
where p.id = c.people_id;
```

### OUTER JOIN(외부 조인)

**OUTER JOIN 개요**

- 동등 조인으로 연결된 두 테이블 중 적어도 한 테이블 전체를 연결하려는 경우에 사용
- 조인할 값이 한 테이블에는 존재하지만 다른 테이블에는 없을 경우, 해당 값은 `NULL`로 표기

**LEFT OUTER JOIN**

- 두 테이블 중, 조인할 컬럼의 값이 왼쪽 테이블에만 존재하는 경우에 사용
- 왼쪽 테이블의 행은 모두 연결하지만, 오른쪽 테이블의 행은 `ON` 절의 동등 조건을 충족하는 것만 연결

```sql
select *
from people p 
	left outer join card_company c on p.id = c.people_id;

select *
from card_company c 
	left outer join people P on p.id = c.people_id;
```

**RIGHT OUTER JOIN**

- 두 테이블 중, 조인할 컬럼의 값이 오른쪽 테이블에만 존재할 때 사용
- 오른쪽 테이블의 행은 모두 연결하지만, 왼쪽 테이블의 행은 `ON` 절의 동등 조건을 충족하는 것만 연결

```sql

select *
from people p 
	right outer join card_company c on p.id = c.people_id;

select *
from card_company c
	right outer join people p on p.id = c.people_id;
```

**FULL OUTER JOIN**

- 조인된 속성을 기준으로 두 테이블을 연결
- 두 테이블 중 한 테이블에만 존재하는 값도 모두 연결
- MySQL에서는 `full outer join` 을 지원하지 않음
(따라서, full outer join 을 사용하려면 직접 구현해야 함)

**SELF JOIN(자신 조인)**

- 별도의 구문을 사용하지 않음
    - 상황에 맞게, 내부 조인/외부 조인을 선택하여 활용
- 테이블이 자기 자신에게 조인한다는 것을 의미
- 직속 관리자 정보 확인 등, 계층이 존재하는 정보를 조회할 때 활용

```sql
-- self join 연습용 테이블 생성 및 값 추가
CREATE table ugaga_tribes (
	id INT AUTO_INCREMENT PRIMARY KEY ,
	name VARCHAR(64),
	classes_id INT
);
desc ugaga_tribes;
commit;

insert into ugaga_tribes VALUES(1,'족장_우가콜라',null);
insert into ugaga_tribes (name,classes_id) VALUES('부족장_우가펩시',1);
insert into ugaga_tribes (name,classes_id) VALUES('부하1_우가팔일오',2);
insert into ugaga_tribes (name,classes_id) VALUES('부하2_우가우간다',3);
insert into ugaga_tribes (name,classes_id) VALUES('부하3_우가막내',4);
select * from ugaga_tribes;
commit;
-- 테이블 생성 및 데이터 추가 완료

-- 부족원의 직속상사 정보를 출력
-- (내부 조인을 사용한 셀프 조인)
select 
	ut_a.id as 부하ID, 
	ut_a.name as 부하, 
	ut_b.id as 직속상사ID, 
	ut_b.name as 직속상사
from ugaga_tribes ut_a 
	join ugaga_tribes ut_b on ut_a.classes_id = ut_b.id;

-- 직속상사가 없는 부족원의 정보까지 모두 출력
-- (외부 조인을 사용한 셀프 조인)
select 
	ut_a.id as 부하ID, 
	ut_a.name as 부하, 
	ut_b.id as 직속상사ID, 
	ut_b.name as 직속상사
from ugaga_tribes ut_a 
	left join ugaga_tribes ut_b on ut_a.classes_id = ut_b.id;
```

---

## 2. 서브 쿼리

### 서브 쿼리(= 부속 질의, 중첩 질의) 개요

- 한 테이블에 관한 쿼리 수행 결과를 다른 테이블에서 이용하기 위해, 다른 select문의 where절 등으로 넘겨주는 쿼리문
  - 서브쿼리를 사용하는 쿼리문은 메인 쿼리(= 주 쿼리)라고 호칭
- 서브 쿼리를 수행한 결과는 테이블 형태 
→ 메인 쿼리의 where절, from절 등에서 사용 가능
- 테이블의 속성을 통해 서브 쿼리와 메인 쿼리 간 상하관계가 형성됨
    - 서브 쿼리에서는 메인 쿼리의 테이블과 그 속성을 활용할 수 있지만,
    그 반대의 경우는 불가
    
    ```sql
    select c.name, b.bookname
    -- 메인 쿼리에서는 customer, book 테이블을 사용
    from customer c
    	join book b 
        on exists (
    				select orders o
            from orders o
            -- 메인 쿼리에서 사용한 테이블의 속성을 서브쿼리에서도 사용
            where o.custid = c.custid and o.bookid = b.bookid
        ) order by c.name, b.bookname;
    ```
    

### 서브 쿼리에 따른 결과 테이블 행 개수

- (조회할 테이블의 행 개수) - (서브 쿼리로 탐색할 열의 개수) 
→ (결과 테이블 행 개수)
    - 단일 행 - 단일 열 → (1*1)
    - 다중 행 - 단일 열 → (N*1)
    - 단일 행 - 다중 열 → (1*N)
    - 다중 행(M) - 다중 열(N) → (M*N)

### 서브 쿼리와 테이블 조인 간 비교

- 여러 개의 테이블을 1개의 쿼리 안에서 다룬다는 점이 유사
- 테이블 조인을 사용할 경우, select문의 결과 테이블의 속성은 from 절에서 사용한 테이블의 속성만 사용 가능
- 여러 개의 테이블을 1개의 테이블로 연결 → 테이블 조인이 유리
- 1개의 테이블로부터 여러 개의 테이블 질의를 얻는 경우 → 서브 쿼리가 편리

### exists

- 서브쿼리의 조건을 충족시키는 메인 쿼리의 행을 메인 쿼리의 결과 테이블에 포함시키는 연산
- select문의 `where` 절이나 테이블 조인의 `on` 절 등에서 활용
- 상관부속질의(= 연관 서브 쿼리)
  1. 메인 쿼리의 각 행마다 서브 쿼리의 조건을 충족하는지 확인
     - a. 서브쿼리의 `where`절에서는 메인 쿼리 테이블의 속성과 서브쿼리 테이블의 속성에 관한 조건을 설정
     - b. 메인 쿼리에서 사용한 테이블의 어떤 행으로 인해, 서브쿼리 행 중 1개라도 a.의 조건을 충족시키는 즉시 서브쿼리를 종료
  2. 서브 쿼리의 조건을 충족하는 메인 쿼리의 행만 메인 쿼리의 결과 테이블에 포함

```sql
-- 도서를 주문한 고객의 이름과 주소를 조회
select cs.name, cs.address
from customer cs
where exists(
	-- orders 테이블에서 customer 테이블과 custid를 비교
	select * 
  from orders o 
  where cs.custid = o.custid
);
```

- 테이블의 조인 조건을 설정할 때도 활용 가능

```sql
-- 아래 두 쿼리의 결과는 서로 동일
-- 1.
select c.name, b.bookname
from customer c
	join book b 
    -- 아래와 같이 on 절에서 exists를 조인 조건으로 활용 가능
    on exists (
				select o.orderid
        from orders o
        where o.custid = c.custid and o.bookid = b.bookid
    ) 
order by c.name, b.bookname;

-- 2.
select distinct c.name, b.bookname
from customer c
	join orders o on c.custid = o.custid
  join book b on o.bookid = b.bookid
group by c.name, b.bookname
order by c.name;
```

---

## 3. 테이블에서의 집합 연산

### 개요

- SQL의 결과는 항상 테이블 형태로 출력
- DB의 테이블은 여러 개의 튜플(= 행)로 이루어진 집합
- 따라서, DB의 테이블에 관해서도 합집합, 교집합, 차집합 등의 집합 연산 수행 가능
- Oracle에서는 합집합, 교집합, 차집합 연산을 각각 `UNION`, `INTERSECT`, `MINUS` 로 지원하나, MySQL에서는 합집합 연산만 지원
(따라서, 교집합, 차집합 연산은 `not in`, `in`을 활용하여 직접 쿼리를 작성해야 함)

### UNION

- 일반적인 합집합 연산에 해당
- 행의 중복을 포함한 결과까지 모두 출력

```sql
-- UNION 연산 예시
-- 도서를 주문한 적 있거나, 대한민국에 거주하는 고객의 정보 출력
select name
from customer
where custid in (
    select custid
    from orders
)

UNION

select name
from customer 
where address like '%대한민국%';
```

### UNION ALL

- 기본적인 기능은 `UNION` 과 동일
- 출력되는 결과에서는 행의 중복을 허용

```sql
-- UNION 연산 예시
-- 도서를 주문한 적 있거나, 대한민국에 거주하는 고객의 정보 출력
select name
from customer
where custid in (
	select custid
    from orders
)

UNION ALL

select name
from customer 
where address like '%대한민국%';
```

### 교집합

- MySQL에서는 별도의 교집합 연산을 지원하지 않음
- 따라서, where 절에서 `in` 과 서브 쿼리를 활용하여 교집합 연산을 직접 구현해야 함
- 아래는 그 구현 예시

```sql
-- 대한민국에서 거주하는 고객 중 도서를 주문한 고객의 이름 조회
-- MySQL에서의 Oracle의 intersect 연산을 구현
select name
from customer 
where address like '%대한민국%' 
	and name in (
		select name
		from customer
		where custid in (
			select custid
			from orders
	)
);
```

### 차집합

- MySQL에서는 별도의 차집합 연산을 지원하지 않음
- 따라서, where 절에서 `not in` 과 서브 쿼리를 활용하여 교집합 연산을 직접 구현해야 함
- 아래는 그 구현 예시

```sql
-- 3.32. 도서를 주문하지 않은 고객의 이름 조회
select name
from customer
where custid not in (
	select custid
	from orders
);
```