---
title: "데이터베이스 공부 내용 정리 3(이현빈)"
excerpt: DDL(데이터 정의어)를 사용한 테이블 생성, 수정, 삭제 방법에 관한 정리본입니다.
date: 2025-09-07
author: hyeonbin-lee
author_profile: true
layout: single
---

## 학습 목표

- SQL 데이터 정의어를 사용하여 테이블을 생성, 수정, 삭제하는 방법 이해하기

## 순 공부 시간

- 2025.9.7 18:27 ~ 2025.9.7 19:38(71분)
- 2025.9.7 20:13 ~ 2025.9.7 20:43(30분)

---

## 1. CREATE TABLE

- 행을 구성할 속성의 이름과 타입 및 속성에 적용할 제약조건을 정의하고, 이를 바탕으로 테이블을 생성하는 SQL 명령어
- 제약조건은 테이블의 속성에 직접 적용하거나 `CONSTRAINT` 키워드를 사용하여 지정할 수 있으며, 적용 가능한 제약조건은 아래와 같음.
    - primary key(기본키)
    - foreign key(외래키)
    - unique(유일한 값)
    - check(테이블 속성을 검증하기 위한 조건)
    - default(초기값)
    - null 허용 여부
- `CREATE TABLE` 문의 실제 사용 예시

```sql
CREATE TABLE buytbl -- 회원 구매 테이블(Buy Table의 약자)
(  num 		    INT AUTO_INCREMENT PRIMARY KEY, -- 순번(PK)
   userID  	  CHAR(8) NOT NULL, -- 아이디(FK): 회원 테이블의 기본키를 외래키로 저장
   prodName 	CHAR(6) NOT NULL,
   groupName 	CHAR(4),
   price     	INT  NOT NULL,
   amount    	SMALLINT  NOT NULL,
   
   -- CONSTRAINT 키워드를 사용하여 userID를 외래키로 지정
   CONSTRAINT FK_userTBL_buyTBL foreign key(userID) references userTBL(userID)
);
```

---

## 2. ALTER TABLE

### 개요

- 이미 생성된 테이블의 구조를 변경하기 위해 사용하는 SQL 명령어
- 주로 테이블의 속성 및 제약조건을 변경(추가, 수정, 삭제)할 때 사용

### 테이블 속성에 관한 제약조건 변경

- `ALTER TABLE 테이블명 ADD [CONSTRAINT 제약조건명] ~`
    - 지정한 이름의 제약조건을 테이블에 추가
    - 제약조건에 별도의 이름을 부여하지 않을 경우 대괄호로 표시한 부분은 생략 가능
    
    ```sql
    -- primary key(이름 지정 X)
    alter table buyTBL 
    	add primary key (num);
    
    -- primary key(이름 지정 O)
    alter table buyTBL 
    	add constraint PK_buyTBL_num primary key (num);
    
    -- foreign key
    alter table buyTBL
    	add constraint FK_userTBL_buyTBL foreign key(userID) 
    	references userTBL(userID);
    
    -- unique
    alter table buyTBL
    	add constraint unique(num);
    
    -- check
    alter table usertbl
    	add constraint ck_mobile1 check(mobile1 in (011, 016, 017, 018, 019, 010));
    
    ```
    
- `ALTER TABLE 테이블명 DROP CONSTRAINT ~`
    - 테이블에서 제약조건을 삭제
    
    ```sql
    alter table
    	drop primary key;
    
    alter table buyTBL
    	drop foreign key FK_userTBL_buyTBL;
    ```
    

### 테이블 속성 변경

- 다음의 명령어를 사용하여 테이블 속성 변경(추가, 수정, 삭제) 시, `COLUMN` 키워드는 생략 가능
- `ALTER TABLE 테이블명 ADD [COLUMN] 속성명 ~`
    - 테이블에 지정한 이름과 타입, 제약조건이 부여된 속성을 추가
    - 새로 추가할 속성에 관한 default 제약조건 설정 가능
    
    ```sql
    alter table test1 
    	add age int unsigned default 0 not null;
    ```
    
- `ALTER TABLE 테이블명 MODIFY [COLUMN] 속성명 ~`
    - 지정한 테이블 속성의 이름은 유지하면서 타입 및 제약조건만 수정
    
    ```sql
    alter table test1
    	modify column name varchar(50) not null;
    ```
    
- `ALTER TABLE 테이블명 CHANGE [COLUMN] 이전_속성명 새로운_속성명 ~`
    - 지정한 테이블 속성의 이름과 타입, 제약조건을 모두 수정
    
    ```sql
    -- name 속성의 이름을 Uname으로 변경
    alter table test1
    	change column name Uname varchar(50) not null;
    ```
    
- `ALTER TABLE 테이블명 DROP [COLUMN] ~`
    - 테이블에서 지정한 속성을 삭제
    - 삭제하려는 속성을 다른 테이블에서 외래키로 사용할 경우, 해당 테이블을 삭제하거나, 삭제할 속성을 더 이상 외래키로 사용하지 않도록 제약조건을 변경해야 정상적으로 삭제 가능
    
    ```sql
    alter table test1
    	drop column age;
    ```
    

---

## 3. DROP TABLE

- 지정한 테이블의 구조 및 데이터를 모두 삭제
- 삭제할 테이블의 특정 속성을 다른 테이블이 참조할 경우, 이 명령어를 통해 바로 삭제하는 것은 불가능
    - 삭제할 테이블의 속성을 외래키로 참조 중인 테이블 자체를 삭제하거나, 해당 외래키 제약조건을 사용하지 않도록 변경해야 정상적으로 삭제 가능
- 실제 사용 예시

```sql
drop table test1;
```