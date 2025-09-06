---
title: "[SQL] 프로그래머스 SQL SELECT level1 풀이 (박기웅)"
excerpt: "프로그래머스 SQL SELECT lev1 풀이입니다."
date: 2025-09-02
author: kiwoong-park
author_profile: true
layout: single
---

> SQL 문제를 풀어보고 찾아서 새로 알게 된 내용을 정리해보는 포스팅을 올려보는 게 좋을 것 같아 공유합니다.

### 프로그래머스 고득점 KIT LEVEL1 - SELECT

문제 : 조건에 부합하는 중고거래 댓글 조회하기

`USED_GOODS_BOARD`와 `USED_GOODS_REPLY` 테이블에서 2022년 10월에 작성된 게시글 제목, 게시글 ID, 댓글 ID, 댓글 작성자 ID, 댓글 내용, 댓글 작성일을 조회하는 SQL문을 작성해주세요. 결과는 댓글 작성일을 기준으로 오름차순 정렬해주시고, 댓글 작성일이 같다면 게시글 제목을 기준으로 오름차순 정렬해주세요.

정답 :

```sql
SELECT B.TITLE, B.BOARD_ID, R.REPLY_ID, R.WRITER_ID, R.CONTENTS, 
        DATE_FORMAT(R.CREATED_DATE, '%Y-%m-%d') AS CREATED_DATE
FROM USED_GOODS_BOARD AS B
JOIN USED_GOODS_REPLY AS R ON B.BOARD_ID = R.BOARD_ID
WHERE B.CREATED_DATE BETWEEN '2022-10-01' AND '2022-10-31'
ORDER BY R.CREATED_DATE asc, B.TITLE asc

```

새로 배운 내용 :

- 두 테이블의 Column 이름이 같은 경우 별도 표기 필요
- Alias 만들기 `FROM USED_GOODS_BOARD AS B`
- `DATE_FORMAT(R.CREATED_DATE, '%Y-%m-%d')` 날짜 출력 형식 맞추기
- Join 연산 : `JOIN USED_GOODS_REPLY AS R ON B.BOARD_ID = R.BOARD_ID`

문제 : 과일로 만든 아이스크림 고르기

상반기 아이스크림 총주문량이 3,000보다 높으면서 아이스크림의 주 성분이 과일인 아이스크림의 맛을 총주문량이 큰 순서대로 조회하는 SQL 문을 작성해주세요.

```sql
SELECT I.FLAVOR
FROM FIRST_HALF AS F
JOIN ICECREAM_INFO AS I ON F.FLAVOR = I.FLAVOR
WHERE F.TOTAL_ORDER > 3000 AND I.INGREDIENT_TYPE = 'fruit_based'
ORDER BY F.TOTAL_ORDER DESC
```

문제 : 흉부외과 또는 일반외과 의사 목록 출력하기

`DOCTOR` 테이블에서 진료과가 흉부외과(CS)이거나 일반외과(GS)인 의사의 이름, 의사ID, 진료과, 고용일자를 조회하는 SQL문을 작성해주세요. 이때 결과는 고용일자를 기준으로 내림차순 정렬하고, 고용일자가 같다면 이름을 기준으로 오름차순 정렬해주세요.

```sql
SELECT DR_NAME, DR_ID, MCDP_CD, DATE_FORMAT(HIRE_YMD, '%Y-%m-%d') AS HIRE_YMD
FROM DOCTOR
WHERE MCDP_CD IN ('CS', 'GS')
ORDER BY HIRE_YMD DESC, DR_NAME;
```

문제 : 12세 이하인 여자 환자 목록 출력하기

`PATIENT` 테이블에서 12세 이하인 여자환자의 환자이름, 환자번호, 성별코드, 나이, 전화번호를 조회하는 SQL문을 작성해주세요. 이때 전화번호가 없는 경우, 'NONE'으로 출력시켜 주시고 결과는 나이를 기준으로 내림차순 정렬하고, 나이 같다면 환자이름을 기준으로 오름차순 정렬해주세요.

```sql
SELECT PT_NAME, PT_NO, GEND_CD, AGE, IFNULL(TLNO,'NONE') as TLNO
FROM PATIENT
WHERE GEND_CD = 'W' AND AGE <= 12 
ORDER BY AGE DESC, PT_NAME
```

새로 배운 내용 : `IFNULL(TLNO, 'NONE')` 데이터가 없는 경우 디폴트 값 출력 방법

문제 : 인기 있는 아이스크림

상반기에 판매된 아이스크림의 맛을 총주문량을 기준으로 내림차순 정렬하고 총주문량이 같다면 출하 번호를 기준으로 오름차순 정렬하여 조회하는 SQL 문을 작성해주세요.

```sql
SELECT FLAVOR
FROM FIRST_HALF
ORDER BY TOTAL_ORDER DESC, SHIPMENT_ID;
```

문제 : 조건에 맞는 도서 리스트 출력하기

`BOOK` 테이블에서 `2021년`에 출판된 `'인문'` 카테고리에 속하는 도서 리스트를 찾아서 도서 ID(`BOOK_ID`), 출판일 (`PUBLISHED_DATE`)을 출력하는 SQL문을 작성해주세요.

결과는 출판일을 기준으로 오름차순 정렬해주세요.

```sql
SELECT BOOK_ID, DATE_FORMAT(PUBLISHED_DATE,'%Y-%m-%d') AS PUBLISHED_DATE
FROM BOOK
WHERE DATE_FORMAT(PUBLISHED_DATE,'%Y') = '2021' AND CATEGORY = '인문'
ORDER BY PUBLISHED_DATE
```

문제 : 평균 일일 대여 요금 구하기

`CAR_RENTAL_COMPANY_CAR` 테이블에서 자동차 종류가 'SUV'인 자동차들의 평균 일일 대여 요금을 출력하는 SQL문을 작성해주세요. 이때 평균 일일 대여 요금은 소수 첫 번째 자리에서 반올림하고, 컬럼명은 `AVERAGE_FEE` 로 지정해주세요.

```sql
SELECT ROUND(AVG(DAILY_FEE),0) AS AVERAGE_FEE
FROM CAR_RENTAL_COMPANY_CAR
WHERE CAR_TYPE = 'SUV'
```

새로 배운 내용 : 컬럼의 평균 구하기 및 반올림 방법 → `ROUND(AVG(컬럼), n-1)` : 컬럼의 평균을 구하고, 소수 n 번째 자리에서 반올림

문제 : 특정 형질을 가지는 대장균 찾기

2번 형질이 보유하지 않으면서 1번이나 3번 형질을 보유하고 있는 대장균 개체의 수(`COUNT`)를 출력하는 SQL 문을 작성해주세요. 1번과 3번 형질을 모두 보유하고 있는 경우도 1번이나 3번 형질을 보유하고 있는 경우에 포함합니다.

```sql
Select count(*) as COUNT
from ecoli_data
where genotype & 2 = 0 
AND (genotype & 1 > 0 OR genotype & 4 > 0);
```

새로 배운 내용 :

**CONV**

[CONV 사용해 10진수에서 2진수, 8진수, 16진수로 진법 데이터 형변환 실시]

1. CONV : 숫자 기반 시스템을 다른 진법의 수로 표시해줍니다

2. CONV(데이터, 원본 진법, 변환할 진법) 으로 문법을 사용합니다

**2진수 비트 연산**

- MySQL은 숫자를 2진수로 자동 변환하여 비트 연산자를 지원합니다.
- `&` (비트 AND), `|` (비트 OR), `^` (비트 XOR), `~` (비트 NOT), `<<` (비트 왼쪽 시프트), `>>` (비트 오른쪽 시프트)를 사용할 수 있습니다.
- 예시: `SELECT 6 & 3;` (110 & 011) → `2` (010)
  - **`&` (비트 AND 연산)**: 두 이진수의 같은 위치에 있는 비트가 모두 1일 때만 결과가 1이 됩니다. 이 연산을 사용해야 특정 비트가 켜져 있는지(형질이 있는지)를 정확하게 확인할 수 있습니다.
  - 해당 비트가 1이면 해당 비트의 자리수를 포함한 값을 반환

문제 :  **가장 큰 물고기 10마리 구하기**

`FISH_INFO` 테이블에서 가장 큰 물고기 10마리의 ID와 길이를 출력하는 SQL 문을 작성해주세요. 결과는 길이를 기준으로 내림차순 정렬하고, 길이가 같다면 물고기의 ID에 대해 오름차순 정렬해주세요. 단, 가장 큰 물고기 10마리 중 길이가 10cm 이하인 경우는 없습니다.

```sql
select ID, LENGTH
from fish_info
order by length desc, id
limit 10;
```

새로 배운 내용 : `limit 10` ⇒ 질의로 나오는 데이터의 개수를 제한하는 문법

문제 : 강원도에 위치한 생산공장 목록 출력하기

`FOOD_FACTORY` 테이블에서 강원도에 위치한 식품공장의 공장 ID, 공장 이름, 주소를 조회하는 SQL문을 작성해주세요. 이때 결과는 공장 ID를 기준으로 오름차순 정렬해주세요.

```sql
SELECT FACTORY_ID, FACTORY_NAME, ADDRESS
FROM FOOD_FACTORY
WHERE ADDRESS like '강원도%'
ORDER BY FACTORY_ID;
```

문제 : 모든 레코드 조회하기

동물 보호소에 들어온 모든 동물의 정보를 ANIMAL_ID순으로 조회하는 SQL문을 작성해주세요.

```sql
SELECT ANIMAL_ID, ANIMAL_TYPE, DATETIME, INTAKE_CONDITION, NAME, SEX_UPON_INTAKE
FROM ANIMAL_INS
ORDER BY ANIMAL_ID;
```

문제 : 역순 정렬하기

```sql
SELECT NAME, DATETIME
FROM ANIMAL_INS
ORDER BY ANIMAL_ID DESC;
```

문제 : 아픈 동물 찾기

동물 보호소에 들어온 동물 중 아픈 동물[1](https://school.programmers.co.kr/learn/courses/30/lessons/59036#fn1)의 아이디와 이름을 조회하는 SQL 문을 작성해주세요. 이때 결과는 아이디 순으로 조회해주세요.

```sql
SELECT ANIMAL_ID, NAME
FROM ANIMAL_INS
WHERE INTAKE_CONDITION = 'Sick'
ORDER BY ANIMAL_ID;
```

문제: 어린 동물 찾기

동물 보호소에 들어온 동물 중 젊은 동물[1](https://school.programmers.co.kr/learn/courses/30/lessons/59037#fn1)의 아이디와 이름을 조회하는 SQL 문을 작성해주세요. 이때 결과는 아이디 순으로 조회해주세요.

```sql
SELECT ANIMAL_ID, NAME
FROM ANIMAL_INS
WHERE INTAKE_CONDITION <> 'Aged'
ORDER BY ANIMAL_ID;
```

새로 배운 내용 : `<>` = not equal

문제 :  **동물의 아이디와 이름**

동물 보호소에 들어온 모든 동물의 아이디와 이름을 ANIMAL_ID순으로 조회하는 SQL문을 작성해주세요. SQL을 실행하면 다음과 같이 출력되어야 합니다.

```sql
SELECT ANIMAL_ID, NAME
FROM ANIMAL_INS
ORDER BY ANIMAL_ID
```

문제 : 여러 기준으로 정렬하기

동물 보호소에 들어온 모든 동물의 아이디와 이름, 보호 시작일을 이름 순으로 조회하는 SQL문을 작성해주세요. 단, 이름이 같은 동물 중에서는 보호를 나중에 시작한 동물을 먼저 보여줘야 합니다.

```sql
SELECT ANIMAL_ID, NAME, DATETIME
FROM ANIMAL_INS
ORDER BY NAME, DATETIME DESC;
```

문제 :  **상위 n개 레코드**

동물 보호소에 가장 먼저 들어온 동물의 이름을 조회하는 SQL 문을 작성해주세요.

```sql
SELECT NAME
FROM ANIMAL_INS
ORDER BY DATETIME
LIMIT 1;

```

문제 : 잔챙이 잡은 수 구하기

잡은 물고기 중 길이가 10cm 이하인 물고기의 수를 출력하는 SQL 문을 작성해주세요.

물고기의 수를 나타내는 컬럼 명은 FISH_COUNT로 해주세요.

```sql
SELECT COUNT(ID) AS FISH_COUNT
FROM FISH_INFO
WHERE LENGTH IS NULL;
```

문제 : 조건에 맞는 회원 수 구하기

`USER_INFO` 테이블에서 2021년에 가입한 회원 중 나이가 20세 이상 29세 이하인 회원이 몇 명인지 출력하는 SQL문을 작성해주세요.

```sql
SELECT COUNT(USER_ID) AS USERS
FROM USER_INFO
WHERE JOINED BETWEEN str_to_date('2021/01/01','%Y/%m/%d') AND str_to_date('2021/12/31','%Y/%m/%d') AND AGE BETWEEN 20 AND 29
```

문제 : Python 개발자 찾기

`DEVELOPER_INFOS` 테이블에서 Python 스킬을 가진 개발자의 정보를 조회하려 합니다. Python 스킬을 가진 개발자의 ID, 이메일, 이름, 성을 조회하는 SQL 문을 작성해 주세요.

결과는 ID를 기준으로 오름차순 정렬해 주세요.

```sql

SELECT ID, EMAIL, FIRST_NAME, LAST_NAME
FROM DEVELOPER_INFOS
WHERE SKILL_1 = 'Python' OR SKILL_2 = 'Python' OR SKILL_3 = 'Python'
ORDER BY ID;
```

조금 더 개선된 코드

```sql

SELECT ID, EMAIL, FIRST_NAME, LAST_NAME
FROM DEVELOPER_INFOS
WHERE 'Python' IN (SKILL_1, SKILL_2, SKILL_3)
ORDER BY ID;
```

새로 배운 내용

IN 연산자 활용 하여 열 IN 원소 가 아니라 원소 in 다중 열 형태로 작성할 수도 있다!!