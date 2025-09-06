---
title: "데이터베이스 공부 내용 정리 2(이현빈)"
excerpt: 테이블 속성의 제약조건 관련 공부내용 정리본입니다.
date: 2025-09-06
author: hyeonbin-lee
author_profile: true
layout: single
---

## 학습목표

- 테이블 속성에 설정 가능한 제약조건의 종류를 파악하기
- 테이블의 속성에 제약조건을 설정하는 방법 이해하기

---

# 1. 제약조건(Constraint) 개요

- 테이블에 저장된 데이터의 무결성을 보장하기 위해 설정하는 제한사항
- 테이블의 제약조건을 만족해야만 입력한 데이터를 테이블에 추가/수정/삭제 가능
- 테이블의 속성에 대해, 다음의 제약조건을 설정 가능
    - primary key
    - foreign key
    - unique
    - check
    - default
    - null 허용 여부

---

# 2. 테이블 속성 제약조건 설정 방법

- 아래의 3가지 방법을 모두 활용할 수 있음

## 1) 테이블 속성에서 직접 지정

- `CREATE TABLE` 에서 사용할 속성과 그 타입을 지정할 때 제약 조건도 같이 지정하는 방식
- 복합키 기반 기본키 제약조건을 지정할 때는 활용하기 어려운 방법

```sql
CREATE TABLE usertbl -- 회원 테이블
( -- 테이블의 userID 속성을 기본키로 지정
	userID  	CHAR(8) PRIMARY KEY, -- 사용자 아이디(PK)
  name    	VARCHAR(10) NOT NULL,
  birthYear   INT NOT NULL,
  addr	  	CHAR(2) NOT NULL,
  mobile1	CHAR(3),
  mobile2	CHAR(8),
  height    	SMALLINT,
  mDate    	DATE
);
```

## 2) 테이블 생성 시 별도로 제약조건을 설정

- `CREATE TABLE` 문 내부에서 `CONSTRAINT` 키워드를 사용
- 설정한 제약조건에 이름을 부여할 수 있어 `ALTER TABLE` 을 활용한 변경 등에 유리하고, 여러 개의 제약조건을 한번에 파악할 수 있
- 기본키 지정(복합키 기반), 외래키 지정 등의 상황에서 활용 가능한 방법

```sql
CREATE TABLE usertbl
( 
	userID  	CHAR(8) NOT NULL,
  name    	VARCHAR(10) NOT NULL,
  birthYear   INT NOT NULL,
  addr	  	CHAR(2) NOT NULL,
  mobile1	CHAR(3),
  mobile2	CHAR(8),
  height    	SMALLINT,
  mDate    	DATE,
  
  -- 아래와 같이 기본키의 이름을 별도로 지정할 수 있음
  CONSTRAINT PK_usertbl_userID PRIMARY KEY(userID)
);
```

## 3) 테이블 생성 이후 제약조건 추가

- `ALTER TABLE ~ ADD CONSTRAINT ~` 문을 사용
- 테이블 생성 작업과 속성에 제약조건을 부여하는 작업을 구분
- 테이블에 데이터를 추가하는 작업을 완료한 후 외래키를 설정할 때 주로 활용

```sql
-- 회원 테이블 생성
CREATE TABLE usertbl
( 
	userID  	CHAR(8) NOT NULL,
	name    	VARCHAR(10) NOT NULL,
	birthYear   INT NOT NULL,
	addr	  	CHAR(2) NOT NULL,
	mobile1	CHAR(3),
	mobile2	CHAR(8),
	height    	SMALLINT,
	mDate    	DATE
);

-- 테이블이 생성된 이후 속성에 대한 제약조건을 추가
ALTER TABLE usertbl 
-- 추가할 제약조건의 이름은 'PK_userTBL_userID'
ADD CONSTRAINT PK_userTBL_userID 
-- 추가할 제약조건은 primary key
PRIMARY KEY (userID);
```

---

# 3. 주요 제약 조건

## primary key(기본키)

- 테이블에 포함된 각각의 튜플(= 행)의 데이터를 구분하기 위한 식별자
- 기본키로 지정할 속성의 값은 `not null`과 `unique` 조건을 모두 충족해야 함
- **테이블마다 기본키는 오직 1번만 지정 가능**
- 복합키 기반 기본키(= 복합키) 지정 가능
    - 단일 속성이 기본키의 요건을 충족하지 못할 경우, 2개 이상의 속성을 조합하여 기본키를 만들 수 있음
    - 기본키의 요건을 충족하는 속성 조합 중, 조합에 사용한 속성의 개수가 가장 적은 것을 기본키로 사용

```sql
create table productTBL (
		-- p_code, p_id 속성은 유일성(unique)을 보장할 수 없음
		p_code char(3) not null,
    p_id char(4) not null,
		p_date date not null,
    p_status varchar(10) not null
);

alter table productTBL 
	add constraint PK_productTBL_p_code_p_id -- 기본키의 이름
	-- 여러 개의 속성을 조합하여 1개의 기본키로 지정
	-- (여기서는 p_code, p_id를 사용)
	primary key (p_code, p_id);
```

## foreign key(외래키)

- 한 테이블(= 부모 테이블)을 다른 테이블(= 자식 테이블)에서 참조하기 위한 제약 조건
    - 자식 테이블에 외래키 제약조건 설정을 추가
    - 외래키 제약 조건 설정 시, `REFERENCE`절에서 참조할 부모 테이블의 속성을 지정
    (일반적으로, 참조할 테이블의 기본키 속성을 지정)

```sql
CREATE TABLE buytbl -- 회원 구매 테이블(Buy Table의 약자)
(  num 		INT AUTO_INCREMENT PRIMARY KEY, -- 순번(PK)
   userID  	CHAR(8) NOT NULL, -- 아이디(FK): 회원 테이블의 기본키를 외래키로 저장
   prodName 	CHAR(6) NOT NULL,
   groupName 	CHAR(4),
   price     	INT  NOT NULL,
   amount    	SMALLINT  NOT NULL,
   
   -- 설정한 제약조건에 이름 부여
   CONSTRAINT FK_userTBL_buyTBL
	   -- 생성할 테이블의 속성들 중 외래키로 사용할 속성을 지정 
	   foreign key(userID)
	   -- 외래키가 참조할 테이블과 그 속성을 지정(userTBL 테이블의 userID를 참조)
	   references userTBL(userID)
);
```

- 현재 테이블의 속성을 다른 테이블의 외래키가 참조하는 경우, 외래키를 보유한 테이블들이 먼저 모두 삭제되어야 `DROP TABLE`을 통해 현재 테이블 삭제 가능

## unique

- 속성의 값이 중복되지 않은 유일한 값을 보유하도록 제한하는 제약조건
- `primary key` 와 유사하나, `null` 을 허용한다는 차이가 있음

```sql
create table test1(
		-- id의 값은 유일하지만 null도 허용
	  id int auto_increment unique,
    name varchar(30) not null,
    constraint  PK_TEST1_id  primary key(id)
);
```

## check

- 지정한 속성의 값에 대한 유효성 검증을 수행하는 제약 조건
- 속성값에 대한 유효성 검증 시, 비교, 범위, 포함 여부 등을 확인

```sql
CREATE TABLE usertbl -- 회원 테이블
( 
	userID  	CHAR(8) NOT NULL,
	name    	VARCHAR(10),
  -- check 제약조건 설정 방법 1): 테이블의 속성에서 직접 지정
	birthYear   INT CHECK (birthYear >= 1980 and birthYear <= 2020) NOT NULL,
	addr	  	  CHAR(2) NOT NULL,
	mobile1	    CHAR(3),   -- 국번
	mobile2	    CHAR(8),
	height      SMALLINT,  -- 키
	mDate    	  DATE
    
  -- check 제약조건 설정 방법 2): constraint 키워드를 사용하여 제약조건을 구분
  constraint CK_name check(name is not null)
);

-- check 제약조건 설정 방법 3): 이미 생성된 테이블에 ALTER TABLE을 사용하여 제약조건을 추가
-- mobile1 속성에 check 조건을 지정: 011, 016, 017, 018, 019, 010만 사용 가능
alter table usertbl
	add constraint ck_mobile1 
	check(mobile1 in (011, 016, 017, 018, 019, 010));
```

## default

- 테이블의 한 속성에서 사용할 초기값을 지정하는 제약 조건
- `INSERT`문으로 테이블의 한 행을 추가할 때, 사용자가 별도의 값을 지정하지 않은 테이블의 속성에는 `default` 제약조건으로 설정한 초기값을 부여

```sql
-- 기본값 제약조건 설정 방법 1): 테이블 속성에 직접 지정
CREATE TABLE usertbl -- 회원 테이블
( 
	userID  	CHAR(8) NOT NULL,
	name    	VARCHAR(10),
	birthYear   INT NOT NULL,
	
	-- 주소지의 기본값은 '서울'
	addr	  	CHAR(2) NOT NULL DEFAULT '서울',
	mobile1	  CHAR(3),
	mobile2	  CHAR(8),
	
	-- 키(height)의 기본값은 160(cm)
	height    SMALLINT NULL DEFAULT 160,
	mDate    	DATE
  
  constraint CK_name check(name is not null)
);
```

## null 허용 여부

- `null` : 테이블의 속성값으로 `null` 사용 가능(이 키워드는 생략 가능)
- `not null` : 테이블의 속성값으로는 `null` 사용 불가