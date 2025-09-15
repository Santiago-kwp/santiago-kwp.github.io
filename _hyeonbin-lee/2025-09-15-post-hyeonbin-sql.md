---
title: "프로그래머스 Level 2 SELECT 문제풀이(이현빈)"
excerpt: 프로그래머스 Level 2 SELECT 문제풀이입니다.
date: 2025-09-15 23:52 +0900
author: hyeonbin-lee
author_profile: true
layout: single
---

## 공부 시간

- 문제풀이 시간은 따로 측정하지 못해 데일리스크럼 작성 시간만 기재합니다.
- 2025.9.15 22:51 ~ 2025.9.16 00:

---

## SELECT

### 3월에 태어난 여성 회원 목록

```sql
SELECT 
    MEMBER_ID, 
    MEMBER_NAME, 
    GENDER, 
    DATE_FORMAT(DATE_OF_BIRTH, '%Y-%m-%d') AS DATE_OF_BIRTH
FROM MEMBER_PROFILE
WHERE TLNO IS NOT NULL AND GENDER = 'W' AND MONTH(DATE_OF_BIRTH) = 3
ORDER BY MEMBER_ID ASC;
```
- `DATE_FORMAT()` 함수를 사용하여 `DATE_OF_BIRTH` 속성값은 연월일만 출력
- WHERE절의 조건 설정 시, 날짜 관련 함수 중 `MONTH()` 함수를 사용


### 재구매가 일어난 상품과 회원 리스트 구하기

```sql
SELECT USER_ID, PRODUCT_ID
FROM ONLINE_SALE
GROUP BY USER_ID, PRODUCT_ID
HAVING COUNT(PRODUCT_ID) > 1
ORDER BY USER_ID ASC, PRODUCT_ID DESC;
```

- 재구매가 이루어졌다는 것은 지정한 `USER_ID`를 갖는 사용자가 `PRODUCT_ID` 가 나타내는 제품을 1회보다 많이 구매한 것을 의미
- HAVING절에서는 GROUP BY절로 테이블을 그룹화했을 때 충족해야 할 조건을 지정하므로, 집계함수에 관한 조건을 설정할 수 있음


### 업그레이드된 아이템 구하기

**문제풀이 1**

```sql
SELECT A.ITEM_ID, A.ITEM_NAME, A.RARITY
FROM ITEM_INFO A 
    JOIN ITEM_INFO B ON EXISTS (
        SELECT *
        FROM ITEM_TREE C
        WHERE A.ITEM_ID = C.ITEM_ID AND C.PARENT_ITEM_ID = B.ITEM_ID
    )
WHERE B.RARITY = 'RARE'
ORDER BY A.ITEM_ID DESC;
```

- 문제의 출발점은 `ITEM_TREE`과 `ITEM_INFO`란 두 테이블을 조인할 때, `PARENT_ITEM_ID` 속성과 `ITEM_ID` 속성에 관한 동등비교를 수행하는 것
- 이는 곧 **`PARENT_ITEM_ID` 속성값이 `ITEM_INFO`의 `ITEM_ID` 속성의 값 중에 존재할 수 있다**는 것을 의미
- 또한, 결국 2개의 `ITEM_ID` 속성에 관한 조인 연산을 수행하므로, 위와 같이 `ITEM_INFO` 테이블에 관한 셀프 조인을 수행할 수 있음


**문제풀이 2**

```sql
SELECT A.ITEM_ID, A.ITEM_NAME, A.RARITY
FROM ITEM_INFO A
    JOIN ITEM_TREE B ON A.ITEM_ID = B.ITEM_ID
    LEFT JOIN ITEM_INFO C ON B.PARENT_ITEM_ID = C.ITEM_ID
WHERE C.RARITY = 'RARE'
ORDER BY A.ITEM_ID DESC;
```

- 문제풀이1의 코드는 결국 `ITEM_TREE` 테이블이 2개의 `ITEM_INFO` 테이블 사이를 중계하는 것처럼 조인 연산이 이루어진다고 나타낼 수 있음
- 이때, 왼쪽에서 조인된 `ITEM_INFO` 가 자식 테이블, 오른쪽에서 조인된 `ITEM_INFO`가 부모 테이블에 해당


**문제풀이 3**

```sql
SELECT A.ITEM_ID, A.ITEM_NAME, A.RARITY
FROM ITEM_INFO A
WHERE A.ITEM_ID IN (
    SELECT B.ITEM_ID
    FROM ITEM_TREE B 
        JOIN ITEM_INFO C ON B.PARENT_ITEM_ID = C.ITEM_ID
    WHERE C.RARITY = 'RARE'
)
ORDER BY A.ITEM_ID DESC;
```

- 문제풀이 2에서 SELECT문을 통해 출력되는 대상은 모두 자식 테이블이 되는 `ITEM_INFO`의 속성들에 해당
- 따라서 FROM절에서 조인 연산 없이 자식 테이블에 해당하는 `ITEM_INFO` 테이블만 사용하는 대신, WHERE절에서 서브쿼리를 사용할 수 있음


### 조건에 맞는 개발자 찾기

```sql
SELECT ID, EMAIL, FIRST_NAME, LAST_NAME
FROM DEVELOPERS
WHERE EXISTS (
    SELECT *
    FROM SKILLCODES
    WHERE SKILL_CODE & CODE = CODE && NAME IN ('C#', 'Python')
)
ORDER BY ID;
```
- 비트마스킹을 사용하여 해결 가능한 문제
- `SKILLCODES`의 `CODE` 속성의 값은 모두 2의 거듭제곱에 해당
- sql문에서도 비트 연산자를 사용하여 정수형 속성값에 대해 비트 단위로 논리 연산을 수행할 수 있음
- 문제에서 "스킬을 보유한다"는 것은 다음 조건을 충족한다는 것을 의미
  - `DEVELOPERS`의 `SKILL_CODE`를 비트값으로 나타냈을 때, `SKILLCODES`의 `CODE`를 **비트값으로 표현한 패턴을 포함**하는 것을 의미
  - 즉, `DEVELOPERS`의 `SKILL_CODE`의 정수값과 `SKILLCODES`의 `CODE` 정수값에 관해 비트 단위 AND 연산을 수행한 결과값이 `CODE` 와 일치하는지를 찾아야 함
    - 비트 단위 AND 연산 수행 시, 동일한 자릿수에서 비트값이 모두 1로 일치할 때만 해당 비트값이 1


### 부모의 형질을 모두 가지는 대장균 찾기

```sql
SELECT A.ID, A.GENOTYPE, B.GENOTYPE AS PARENT_GENOTYPE
FROM ECOLI_DATA A 
    JOIN ECOLI_DATA B ON A.PARENT_ID = B.ID
WHERE A.GENOTYPE & B.GENOTYPE = B.GENOTYPE
ORDER BY A.ID ASC;
```
- 바로 위의 개발자 찾기 문제처럼 정수값에 관한 비트 단위 논리 연산을 활용 가능한 문제
- 대장균의 부모도 결국 대장균이므로, `ECOLI_DATA` 테이블에 관한 셀프 조인을 활용할 수 있음
  - 조인에 사용할 속성은 `PARENT_ID`와 `ID`


### 특정 물고기를 잡은 총 수 구하기

**서브쿼리 사용 시**

```sql
SELECT COUNT(ID) AS FISH_COUNT
FROM FISH_INFO
WHERE FISH_TYPE IN (
    SELECT FISH_TYPE
    FROM FISH_NAME_INFO
    WHERE FISH_NAME IN ('BASS', 'SNAPPER')
)
```

**테이블 조인 사용 시**

```sql
SELECT COUNT(A.ID) AS FISH_COUNT
FROM FISH_INFO A 
    JOIN FISH_NAME_INFO B ON A.FISH_TYPE = B.FISH_TYPE
WHERE B.FISH_NAME IN ('BASS', 'SNAPPER');
```
