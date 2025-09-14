---
title: "[SQL] 프로그래머스 SQL kit Level2 문제 풀이 (박기웅)"
excerpt: "SQL 문제 풀이 및 학습 내용입니다."
date: 2025-09-14
author: kiwoong-park
author_profile: true
layout: single
---

> 두뇌 안의 CPU 클럭 수가 매우 후달림을 느낀다. 어려운 문제들은 아니라서 금방 풀 수 있을 줄 알았는데 생각보다 시간을 많이 잡아 먹었다. 그래도 그덕에 SQL 문법이 조금은 익숙해진 것 같긴하다.
> 이제 어려운 문제들만 남겨 놓았다! 
![image.png](/assets/images/kwp_ProgrammersSQL_250914.png)

### 순 공부 시간
'25.9.14. 1426 ~ 1536 (70분), 1556 ~ 1722 (86분), 1738 ~ 1904 (86분), 2004 ~ 2036 (32분), 2047 ~ 2147 (60분)

## SELECT 문제
### 3월에 태어난 여성 회원 목록 출력하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/131120))

```sql

SELECT MEMBER_ID, MEMBER_NAME, GENDER, DATE_FORMAT(DATE_OF_BIRTH,"%Y-%m-%d")
FROM MEMBER_PROFILE
WHERE MONTH(DATE_OF_BIRTH) = 3 AND GENDER = 'W' AND TLNO IS NOT NULL
ORDER BY MEMBER_ID;

```

- 배운 내용 : DATE_FORMAT 사용법 ([공식 링크](https://www.w3schools.com/sql/func_mysql_date_format.asp))

### 재구매가 일어난 상품과 회원 리스트 구하기([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/131536))

```sql
SELECT USER_ID, PRODUCT_ID
FROM ONLINE_SALE
GROUP BY USER_ID, PRODUCT_ID
HAVING COUNT(*) >= 2
ORDER BY USER_ID, PRODUCT_ID DESC;
```

### 업그레이드된 아이템 구하기([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/273711))

```sql
SELECT I.ITEM_ID, I.ITEM_NAME, I.RARITY
FROM ITEM_INFO I
JOIN (SELECT P.ITEM_ID AS PID, C.RARITY
     FROM ITEM_INFO C
     JOIN ITEM_TREE P ON P.PARENT_ITEM_ID = C.ITEM_ID) AS T
     ON T.PID = I.ITEM_ID
WHERE T.RARITY = 'RARE'
ORDER BY I.ITEM_ID DESC

```

- 배운 내용 : 열심히 필기해가면서 간신히 풀어낸 문제로 셀프 조인 느낌으로 접근하였다.
- 접근 방법 : 우선 아이템 트리 테이블의 부모 아이디와 아이템 인포의 아이디가 같은 경우의 아이템 트리 테이블의 아이템 아이디를 뽑아서 서브쿼리의 결과로 만들었다

    ```sql
    SELECT P.ITEM_ID AS PID, C.RARITY
         FROM ITEM_INFO C
         JOIN ITEM_TREE P ON P.PARENT_ITEM_ID = C.ITEM_ID
    ```

  - 해당하는 서브쿼리의 결과와 같은 부모아이디와 다시 아이템 인포의 아이디가 같은 경우를 조인하여, 서브쿼리의 희귀성이 rare 일 경우만 뽑아 주었다.

### 조건에 맞는 개발자 찾기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/276034))

```sql
-- & 비트 연산을 해서 SKILLCODES의 CODE 값이 나올 떄 
SELECT DISTINCT D.ID, D.EMAIL, D.FIRST_NAME, D.LAST_NAME
FROM DEVELOPERS D
CROSS JOIN (
    SELECT CODE
    FROM SKILLCODES
    WHERE NAME = 'Python' OR NAME = 'C#') AS S
WHERE D.SKILL_CODE & S.CODE = S.CODE
ORDER BY ID;
```

- 접근 방법 : 비트 연산을 한다는 것까진 생각해내서 코드를 짰는데 `BIN()` 함수를 쓸 필요가 없었는데 잘못썼다. `BIN(D.SKILL_CODE) & BIN(S.CODE) = BIN(S.CODE)` 의 결과와 `D.SKILL_CODE & S.CODE = S.CODE` 은 엄연히 다르다. 왜냐면 BIN() 함수는 십진수를 받아서 2진수 형태의 문자열을 반환하기 때문에, 내가 생각한 것과 다르게 동작할 확률이 높다.  아래의 실행 결과를 보자!

```sql
select BIN(400) & BIN(128); -- 결과 : 8951424 ??!!
select 400 & 128; -- 결과 : 128
```

- 개선 방법 : 크로스 조인을 이용하여 테이블을 두배로 만들고, distinct로 또 필터링 하는 작업이 비효율적인 것 같아서 다른 사람의 코드를 보고 방법을 찾았다.
- 아래의 서브쿼리를 활용한 방법에서 해당하는 코드의 숫자를 더하고, 그 더한 숫자와 & 비트 연산을 통해 해당같은 패턴이 하나라도 있으면 true를 반환하므로 where 절의 서브쿼리로 묶어서 결과를 도출하였다. 이때 `distinct` 절도 필요가 없다는 것을 알 수 있다. 왜냐면 id를 중복하여 크로스 조인하는 것이 아니므로 단지 필터링만 하기 때문에 distinct절도 필요가 없어서 훨씬 좋은 쿼리임을 이해하였다.

```sql
-- JOIN 활용
SELECT
    DISTINCT id,
    email,
    first_name,
    last_name
FROM
    developers d
    JOIN skillcodes s
    ON s.name IN ('C#', 'Python')
    AND d.skill_code & s.code = s.code
ORDER BY
    1

-- 서브쿼리 활용
SELECT
    DISTINCT id,
    email,
    first_name,
    last_name
FROM
    developers
WHERE
    skill_code & (SELECT SUM(code) FROM skillcodes WHERE name IN ('C#', 'Python')) 
ORDER BY
    1
```

### 특정 물고기를 잡은 총 수 구하기([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/298518))

```sql
SELECT COUNT(F.ID) AS FISH_COUNT
FROM FISH_NAME_INFO N
JOIN FISH_INFO F ON N.FISH_TYPE = F.FISH_TYPE
WHERE FISH_NAME IN ('BASS', 'SNAPPER');
```

### 부모의 형질을 모두 가지는 대장균 찾기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/301647))

```sql
SELECT C.ID, C.GENOTYPE, P.GENOTYPE AS PARENT_GENOTYPE
FROM ECOLI_DATA C
JOIN ECOLI_DATA P ON P.ID = C.PARENT_ID
WHERE C.GENOTYPE & P.GENOTYPE = P.GENOTYPE
ORDER BY C.ID;
```

- 더 이상 부모 id 문제로 고통받지 않고, 이미 조건에 맞는 개발자 찾기 문제에서 백신을 빡시게 맞았기 때문에 부모 형질을 모두 가지려면 & 비트 연산의 결과가 부모 형질과 같아야 한다!

## Group By 문제

### **진료과별 총 예약 횟수 출력하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/132202))**

> `APPOINTMENT` 테이블에서 2022년 5월에 예약한 환자 수를 진료과코드 별로 조회하는 SQL문을 작성해주세요. 이때, 컬럼명은 '진료과 코드', '5월예약건수'로 지정해주시고 결과는 진료과별 예약한 환자 수를 기준으로 오름차순 정렬하고, 예약한 환자 수가 같다면 진료과 코드를 기준으로 오름차순 정렬해주세요.
>

```sql
SELECT MCDP_CD AS '진료과코드', COUNT(MDDR_ID) AS '5월예약건수'
FROM APPOINTMENT
WHERE APNT_YMD >= '2022-05-01' AND APNT_YMD < '2022-06-01'
GROUP BY MCDP_CD
ORDER BY COUNT(MDDR_ID), MCDP_CD;
```

- 새로 배운 내용 : TIMESTAMP 형 데이터는 비교 연산자로 비교가 가능하다!

### 성분으로 구분한 아이스크림 총 주문량([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/133026))

> 상반기 동안 각 아이스크림 성분 타입과 성분 타입에 대한 아이스크림의 총주문량을 총주문량이 작은 순서대로 조회하는 SQL 문을 작성해주세요. 이때 총주문량을 나타내는 컬럼명은 TOTAL_ORDER로 지정해주세요.
>

```sql
SELECT INGREDIENT_TYPE, SUM(TOTAL_ORDER) AS TOTAL_ORDER
FROM FIRST_HALF F
JOIN ICECREAM_INFO I ON F.FLAVOR  = I.FLAVOR
GROUP BY INGREDIENT_TYPE
```

### 자동차 종류별 특정 옵션이 포함된 자동차 수 구하기([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/151137))

```sql
SELECT CAR_TYPE, COUNT(CAR_ID) AS CARS
FROM CAR_RENTAL_COMPANY_CAR
WHERE OPTIONS LIKE '%열선시트%' OR OPTIONS LIKE '%통풍시트%' OR OPTIONS LIKE '%가죽시트%'
GROUP BY CAR_TYPE
ORDER BY CAR_TYPE;

```

### 고양이와 개는 몇 마리 있을까? ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/59040))

```sql
SELECT ANIMAL_TYPE, COUNT(ANIMAL_ID)
FROM ANIMAL_INS
GROUP BY ANIMAL_TYPE
HAVING ANIMAL_TYPE = 'cat' OR ANIMAL_TYPE = 'dog'
ORDER BY ANIMAL_TYPE;
```

### 월별 잡은 물고기 수 구하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/293260))

```sql
SELECT COUNT(ID) AS FISH_COUNT, MONTH(TIME) AS MONTH
FROM FISH_INFO
GROUP BY MONTH(TIME)
HAVING COUNT(ID) <> 0
ORDER BY MONTH;
```

### 동명 동물 수 찾기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/59041))

```sql
SELECT NAME, COUNT(ANIMAL_ID) AS COUNT
FROM ANIMAL_INS
GROUP BY NAME
HAVING COUNT(ANIMAL_ID) >= 2 AND NAME IS NOT NULL
ORDER BY NAME;

```

### 입양 시각 구하기(1) ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/59412))

```sql
SELECT HOUR(DATETIME) AS HOUR, COUNT(ANIMAL_ID) AS COUNT
FROM ANIMAL_OUTS
WHERE HOUR(DATETIME) >= 9 AND HOUR(DATETIME) < 20
GROUP BY HOUR(DATETIME)
ORDER BY HOUR(DATETIME);
```

- 배운 내용 : HAVING 절보다는 WHERE 절로 해야 성능이 더 좋다.
- `HOUR(DATETIME)` 조건은 개별 행에 대해 필터링할 수 있는 조건입니다. 따라서 `HAVING` 대신 `WHERE`를 사용하는 것이 더 효율적입니다.
  - **`WHERE` 절**: `WHERE`는 **`GROUP BY`가 실행되기 전**에 개별 행(row)을 필터링합니다. 이는 그룹화할 데이터의 양을 미리 줄여주므로 훨씬 효율적입니다.
  - **`HAVING` 절**: `HAVING`은 **`GROUP BY`가 실행된 후**에 그룹화된 결과에 대해 필터링을 적용합니다. 이 절은 `COUNT()`나 `SUM()`과 같은 집계 함수에 조건을 걸 때 사용해야 합니다.

### 가격대 별 상품 개수 구하기([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/131530))

```sql
SELECT ((PRICE DIV 10000) * 10000) AS PRICE_GROUP, COUNT(PRODUCT_ID) AS PRODUCTS
FROM PRODUCT
GROUP BY PRICE DIV 10000
ORDER BY PRICE DIV 10000;
```

- 배운 내용 : DIV 함수 사용법 ⇒ 몫을 반환한다.

### 물고기 종류별 잡은 수 구하기([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/293257))

```sql
SELECT COUNT(I.ID) AS FISH_COUNT, N.FISH_NAME
FROM FISH_NAME_INFO N
JOIN FISH_INFO I ON N.FISH_TYPE = I.FISH_TYPE
GROUP BY FISH_NAME
ORDER BY COUNT(I.ID) DESC;
```

- 접근 방법 : 서브쿼리를 활용하여 먼저 물고기 종류별로 묶고 종류별 물고기 숫자 및 물고기 종류 컬럼을 뽑은 뒤에 조회 결과를 물고기 이름 테이블과 조인하여 select 하는 식으로 아래와 같이 접근했다. 는 틀렸다.
- 위와 같이 굳이 서브쿼리를 쓸 필요 없이 물고기의 이름 테이블을 기준으로 바로 조인 후 그룹바이를 하면 된다. 그렇다면 논리적으로 어디가 잘못되서 두 개의 쿼리는 답이 다를까?
- 이유는 우선 FISH_NAME_INFO 테이블에서 FISH_NAME이 PRIMARY KEY인지, 유니크한지에 대한 정보가 없기 때문이다. 만약에 FISH_NAME이 FISH_TYPE 하나에 두개라면? 정답 쿼리와는 다른 결과가 반환될 것이다. 그렇기 때문에 이름별로 집계하는 첫 번째 쿼리가 맞는 방식이라고 할 수 있겠다.

    ```sql
    SELECT T.COUNT AS FISH_COUNT, N.FISH_NAME
    FROM FISH_NAME_INFO N
    JOIN 
    	(SELECT COUNT(ID) AS COUNT, FISH_TYPE
    	FROM FISH_INFO
    	GROUP BY FISH_TYPE) AS T ON T.FISH_TYPE = N.FISH_TYPE
    ORDER BY FISH_COUNT DESC;
    ```


### 조건에 맞는 사원 정보 조회하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/284527))

```sql
SELECT SUM(G.SCORE) AS SCORE, E.EMP_NO, E.EMP_NAME, E.POSITION, E.EMAIL
FROM HR_EMPLOYEES E
JOIN HR_GRADE G on E.EMP_NO = G.EMP_NO
GROUP BY E.EMP_NO
ORDER BY SUM(G.SCORE) DESC
LIMIT 1;
```

### 노선별 평균 역 사이 거리 조회하기([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/284531))

일단 답은 맞긴했는데 문제 내 데이터가 데이터 베이스에 잘못 저장되어 있다. 총 누계거리라면 D_CUMULATIVE 값의 최대치를 구해야 한다고 생각하는데 문제에서는 그냥 구간별 거리의 합을 총 누계거리로 간주하였다. 사실 총 누계거리가 5.4, 6km 인 노선이 세상에 어디있을까…

```sql
SELECT ROUTE, CONCAT(ROUND(SUM(D_BETWEEN_DIST), 1), 'km') AS TOTAL_DISTANCE,
              CONCAT(ROUND(AVG(D_BETWEEN_DIST), 2), 'km') AS AVERAGE_DISTANCE
FROM SUBWAY_DISTANCE
GROUP BY ROUTE
ORDER BY SUM(D_CUMULATIVE) DESC;
```

- 배운 내용 : FORMAT() 함수를 쓰면 무조건 지정한 소수점자리까지 0이라도 표기되지만, ROUND() 함수를 쓰면 지정한 소수점 끝자리가 0인 경우 출력하지 않는다. ⇒ Ex. FORMAT(15.693, 2) = 15.70, ROUND(15.693,2) ⇒ 15.7

  ### `FORMAT()` 함수

  `FORMAT(X, D)` 함수는 **숫자 X**를 **소수점 D자리**까지 반올림하고, 지정된 소수점 자릿수에 맞춰 **항상 D개의 소수점 자리**를 출력합니다. 만약 소수점 끝자리가 0이라도 생략하지 않고 모두 표시합니다. 또한, 세 자리마다 콤마(`,`)를 추가하여 가독성을 높여줍니다. 반환되는 데이터 타입은 **문자열(String)**입니다.

  **예시:**

  - `SELECT FORMAT(12345.6789, 2);` → `'12,345.68'`
  - `SELECT FORMAT(100.5, 2);` → `'100.50'`

    ---

  ### `ROUND()` 함수

  `ROUND(X, D)` 함수는 **숫자 X**를 **소수점 D자리**까지 반올림합니다. 반환되는 데이터 타입은 **숫자(Numeric)**입니다. 이 함수는 소수점 끝자리가 0이면 **생략**하고 출력합니다.

  **예시:**

  - `SELECT ROUND(12345.6789, 2);` → `12345.68`
  - `SELECT ROUND(100.5, 2);` → `100.5`

## ISNULL 문제

### 경기도에 위치한 식품창고 목록 출력하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/131114))

```sql
SELECT WAREHOUSE_ID, WAREHOUSE_NAME, ADDRESS, IFNULL(FREEZER_YN, 'N') AS FREEZER_YN
FROM FOOD_WAREHOUSE
WHERE ADDRESS LIKE '경기도%'
ORDER BY WAREHOUSE_ID;
```

- 배운 내용 `IFNULL` 함수 사용법!

### 이름이 없는 동물의 아이디 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/59039))

```sql
SELECT ANIMAL_ID
FROM ANIMAL_INS
WHERE NAME IS NULL;
```

### 이름이 있는 동물의 아이디([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/59407))

```sql
SELECT ANIMAL_ID
FROM ANIMAL_INS
WHERE NAME IS NOT NULL
ORDER BY ANIMAL_ID;
```

### NULL 처리하기([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/59410))

```sql
SELECT ANIMAL_TYPE, IFNULL(NAME,'No name'), SEX_UPON_INTAKE
FROM ANIMAL_INS
ORDER BY ANIMAL_ID;
```

### 나이 정보가 없는 회원 수 구하기([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/131528))

```sql
SELECT COUNT(USER_ID) AS USERS
FROM USER_INFO
WHERE AGE IS NULL;
```

### ROOT 아이템 구하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/273710))

```sql
SELECT I.ITEM_ID, I.ITEM_NAME
FROM ITEM_INFO I
JOIN ITEM_TREE T ON I.ITEM_ID = T.ITEM_ID
WHERE T.PARENT_ITEM_ID IS NULL;
```

### 업그레이드 할 수 없는 아이템 구하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/273712))

```sql
SELECT T.ITEM_ID, I.ITEM_NAME, I.RARITY
FROM ITEM_TREE T
JOIN ITEM_INFO I ON I.ITEM_ID = T.ITEM_ID
WHERE T.ITEM_ID NOT IN (SELECT PARENT_ITEM_ID
    FROM ITEM_TREE
    WHERE PARENT_ITEM_ID IS NOT NULL
    GROUP BY PARENT_ITEM_ID)
ORDER BY T.ITEM_ID DESC;
```

- 접근 방법 : 먼저 업그레이드 할 수 없는 아이템을 어떻게 정의하지 열심히 종이에 적어가면서 생각한 결과 item_tree 테이블의 parent_id에 등장하지 않는 id라는 것을 알아내었다. 그래서 서브쿼리에서 item_tree를 parent_id로 그룹바이 하면서 null을 제거하고 해당하는 parent_id 가 아닌 id를 `not in` 연산을 통해서 필터링하여 join 결과를 출력하게 하였다.
- 위의 접근 방법이 먼가 어렵게 한 것 같아서 다른 사람의 풀이를 찾아보니 내 접근 방법을 코드화하면 `not in` 연산을 하는 것은 맞는데 굳이 join을 할 필요도, group by를 할 필요도 없었다. 그냥 item_tree 테이블에서 `null`  을 제외한 pid에 포함되지 않는 경우만 찾으면 되었다…

```sql
SELECT item_id, item_name, rarity
FROM item_info
WHERE item_id NOT IN 
    (SELECT parent_item_id
    FROM item_tree
    WHERE parent_item_id IS NOT NULL)
ORDER BY item_id DESC
```

### 잡은 물고기의 평균 길이 구하기([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/293259))

```sql
SELECT ROUND(AVG(IFNULL(LENGTH, 10)),2) AS AVERAGE_LENGTH
FROM FISH_INFO;
```


## JOIN 문제

### 조건에 맞는 도서와 저자 리스트 출력하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/144854))

```sql
select b.book_id, a.author_name, date_format(b.published_date, '%Y-%m-%d') as published_date
from book b
join author a on b.author_id = a.author_id
where category = '경제'
order by published_date;
```

### 상품별 오프라인 매출 구하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/131533))

```sql
select p.product_code, sum(o.sales_amount * p.price) as sales
from offline_sale o
join product p on o.product_id = p.product_id
group by p.product_code
order by sales desc, p.product_code;
```

## String, Date 문제

### 자동차 평균 대여 기간 구하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/157342))

```sql
select car_id, round(avg(datediff(end_date, start_date)+1),1) as average_duration
from car_rental_company_rental_history
group by car_id
having round(avg(datediff(end_date, start_date)+1),1) >= 7 
order by average_duration desc, car_id desc;
```

- 배운 내용 : `datediff()` 함수는 아래와 같은 케이스를 5일이 아닌 4일로 반환하므로 +1 일을 해주어야 한다.

```sql
select datediff(str_to_date('2022-10-01','%Y-%m-%d'),
                str_to_date('2022-09-27','%Y-%m-%d')); -- 4 반환
```

### 자동차 대여 기록에서 장기/단기 대여 구분하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/151138))

```sql
SELECT history_id, car_id, date_format(start_date,'%Y-%m-%d') as start_date,
date_format(end_date,'%Y-%m-%d') as end_date,
if(datediff(end_date, start_date)+1 >= 30, '장기 대여', '단기 대여') as rental_type
from car_rental_company_rental_history
where start_date between str_to_date('2022-09-01','%Y-%m-%d')
                and str_to_date('2022-09-30','%Y-%m-%d')
order by history_id desc;
```

### 조건에 부합하는 중고거래 상태 조회하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/164672))

```sql
select board_id, writer_id, title, price, (
    case status
        when 'sale' then '판매중'
        when 'reserved' then '예약중'
        else '거래완료'
    end) as status
from used_goods_board
where created_date = str_to_date('2022-10-05','%Y-%m-%d')
order by board_id desc;
```

### 루시와 엘라 찾기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/59046))

```sql
select animal_id, name, sex_upon_intake
from animal_ins
where name in ('Lucy','Ella','Pickle','Rogan','Sabrina','Mitty')
order by animal_id;
```

### 이름에 el 이 들어가는 동물 찾기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/59047))

```sql
select animal_id, name
from animal_ins
where lower(name) like '%el%' and animal_type = 'Dog'
order by name;
```

### 중성화 여부 파악하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/59409))

```sql
select animal_id, name, 
    if(sex_upon_intake like 'Neutered%' or sex_upon_intake like 'Spayed%', 'O','X') as 중성화
from animal_ins
order by animal_id;
```

### 분기별 분화된 대장균의 개체 수 구하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/299308))

```sql
SELECT
    CASE
        WHEN MONTH(DIFFERENTIATION_DATE) BETWEEN 1 AND 3 THEN '1Q'
        WHEN MONTH(DIFFERENTIATION_DATE) BETWEEN 4 AND 6 THEN '2Q'
        WHEN MONTH(DIFFERENTIATION_DATE) BETWEEN 7 AND 9 THEN '3Q'
        WHEN MONTH(DIFFERENTIATION_DATE) BETWEEN 10 AND 12 THEN '4Q'
    END AS QUARTER,
    COUNT(ID) AS ECOLI_COUNT
FROM
    ECOLI_DATA
GROUP BY
    QUARTER
ORDER BY
    QUARTER;
```

- 처음에는 group by 절 안에 case 문을 넣으려다가 안되서 인공지능의 도움을 받아 select 절 안에 넣을 수 있다는 것을 알게 되었고, 근데 궁금한게 분명 select 절은 group by 보다 나중에 수행되는데 어떻게 별칭을 알 수 있지? 해서 받은 답변으로 코드 가독성을 위한 백도어라고 할 수 있겠다.
- 아래와 같이 더 간단하게 구현할 수도 있다. 코드의 이해를 위한 해석이 별도로 필요하여 가독성은 떨어지지만 코드가 짧아진다.

```sql
SELECT
CONCAT(FLOOR((MONTH(DIFFERENTIATIONDATE)-1)/3)+1,'Q') as QUARTER,
COUNT(*) AS ECOLICOUNT
FROM ECOLI_DATA
GROUP BY QUARTER
ORDER BY QUARTER
;
```

정확히는, 데이터베이스 시스템이 **쿼리 실행 순서**를 최적화하면서 발생하는 **별칭(alias)의 가시성(visibility)** 차이 때문입니다.

---

표준 SQL의 논리적 쿼리 처리 순서는 다음과 같습니다.

1. **FROM / JOIN**: 대상 테이블을 결정하고 결합합니다.
2. **WHERE**: 행(row)을 필터링합니다.
3. **GROUP BY**: 그룹화 기준을 결정합니다.
4. **HAVING**: 그룹을 필터링합니다.
5. **SELECT**: 최종적으로 출력할 열(column)을 선택하고 별칭을 지정합니다.
6. **ORDER BY**: 결과를 정렬합니다.

이 순서를 따르면, `SELECT` 절의 별칭은 `SELECT` 절이 처리된 후에야 정의되므로, `SELECT`보다 먼저 처리되는 `GROUP BY` 절에서는 그 별칭을 사용할 수 없어야 합니다.

**MySQL과 같은 일부 DBMS의 특수 규칙**

하지만 MySQL, PostgreSQL 등 일부 데이터베이스 시스템은 **개발자의 편의성**을 위해 이 규칙에 **예외**를 두었습니다. 이들 시스템은 `SELECT` 절의 별칭을 `GROUP BY`와 `ORDER BY` 절에서도 사용할 수 있도록 허용합니다.

이것은 엄밀히 말해 SQL 표준에는 없는 기능이지만, 많은 개발자들이 이 기능을 선호하고 사용하기 때문에 보편적으로 적용되고 있습니다. 이로 인해 쿼리 코드가 더 간결하고 가독성이 높아집니다.

따라서 SQL에서 `GROUP BY` 절에 `SELECT` 절의 별칭을 사용하는 방법은 다음과 같이 두 가지로 구분할 수 있습니다.

1. **표준 SQL 방식**: `GROUP BY` 절에 `SELECT` 절과 동일한 표현식을 직접 반복해서 작성합니다. 모든 데이터베이스 시스템에서 호환됩니다.
2. **MySQL 등 비표준 방식**: `SELECT` 절의 별칭을 `GROUP BY`에서 재사용합니다. 코드가 간결하지만, 모든 시스템에서 작동하진 않을 수 있습니다.

당신이 사용한 쿼리가 작동하는 것은 바로 이 **MySQL의 비표준 확장 기능** 덕분입니다.

### 연도 별 평균 미세먼지 농도 조회하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/284530))

```sql
select year(YM) as year, round(avg(pm_val1),2) as 'pm10', round(avg(pm_val2),2) as 'pm2.5'
from air_pollution
where location2 = '수원'
group by year(YM)
order by year;
```

### DateTime 에서 Date로 형변환 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/59414))

```sql
select animal_id, name, date_format(datetime, '%Y-%m-%d') as 날짜
from animal_ins
order by animal_id;
```

### 카테고리 별 상품 개수 구하기 ([문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/131529))

```sql
select substring(product_code, 1, 2) as category, count(product_id) as products
from product
group by substring(product_code, 1, 2)
order by category;
```

- 배운 내용 : `substring(string, start_index, length)`   인데 나도 모르게 start_index 에 0을 넣고 왜 안되지 ? 1분 정도 고민하였다… SQL의 인덱스는 1부터 시작이다!
