---
title: "[SQL] Group by 절 이해하기 (feat. **SUM, MAX, MIN - 프로그래머스 문제 풀이) (박기웅)"
excerpt: "SQL 학습 내용입니다."
date: 2025-09-10
author: kiwoong-park
author_profile: true
layout: single
---
## Group by 절 이해하기 (feat. **SUM, MAX, MIN - 프로그래머스 문제 풀이)**

> 도저히 아래 문제를 풀 수가 없어서 공부 부터 하고 보는 Group by 절… 은 유튜브 강의 찾아봤다가 도움이 안되서 문제를 머리를 쥐어뜯으며 풀어보았다...

### 순 공부 시간 
- 25.9.10 18:00 ~ 18:30 (30분), 19:30 ~ 21:30 (120분)

### 학습 내용 요약
- 서브 쿼리를 통해서 서브 쿼리의 결과를 조인하여 복합 질의하는 법 배움(Ex. select ~~ from table A join ( select ~~~ from table B group by ~~ ) as T on T.column = A.column)
- group by 절을 통해 해결할 수도 있지만, 요약하지 않는 윈도우 함수 ( over ~~ order by(순위함수) or partition by (집계함수)) 로 문제를 해결할  수도 있다.
- 윈도우 함수는 집계해주는 애가 아니라 창문을 만들어서 그 창문에서 데이터를 세거나, 정렬하거나, 순위를 매기거나 그런 용도로 활용될 수 있다. 해당 틀 전체에 적용되며 축소되지 않는다는 특징이 있다.



### 종류별로 가장 큰 물고기 문제 (물고기 종류별 대어 찾기

[문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/293261)

물고기 종류 별로 가장 큰 물고기의 ID, 물고기 이름, 길이를 출력하는 SQL 문을 작성해주세요.

물고기의 ID 컬럼명은 `ID`, 이름 컬럼명은 `FISH_NAME`, 길이 컬럼명은 `LENGTH`로 해주세요.

결과는 물고기의 ID에 대해 오름차순 정렬해주세요.

단, 물고기 종류별 가장 큰 물고기는 1마리만 있으며 10cm 이하의 물고기가 가장 큰 경우는 없습니다.

1차 접근

```sql
-- 종류별로 가장 큰 물고기
SELECT F.FISH_TYPE, MAX(F.LENGTH)
FROM FISH_INFO F
JOIN FISH_NAME_INFO N ON F.FISH_TYPE = N.FISH_TYPE
GROUP BY F.FISH_TYPE;
```

- 1차 적으로 물고기 종류별로 그룹 바이를 한다음에, 해당하는 물고기의 타입과 그룹 바이절로 묶은 그룹의 물고기 길이 중 가장 큰 길이가 나오도록 SELECT 해주었다. ⇒ | FISH_TYPE | MAX(F.LENGTH) | 로 돌아간다.
- 하지만 문제에서 요구하는 ID, FISH_NAME이 나오게 하자 제대로된 결과가 나오지 않는다.. 왜?

```sql
SELECT F.ID, N.FISH_NAME, MAX(F.LENGTH)
FROM FISH_INFO F
JOIN FISH_NAME_INFO N ON F.FISH_TYPE = N.FISH_TYPE
GROUP BY F.FISH_TYPE;
```

- 이 쿼리는 `SELECT` 절에 **`F.ID`**와 **`N.FISH_NAME`** 컬럼이 포함되어 있는데, 이 컬럼들은 `GROUP BY` 절에 포함되지 않았고 집계 함수도 아닙니다. 이것이 오류의 원인입니다.
- `GROUP BY` 절은 여러 행을 하나의 요약 행으로 합칩니다. 그런데 `F.FISH_TYPE`으로 그룹을 묶으면, **각 그룹에는 여러 개의 `F.ID`와 `N.FISH_NAME`이 존재할 수 있습니다.** 예를 들어, "참돔" 그룹에는 ID가 1, 5, 12 등 여러 개가 있을 수 있습니다.
- SQL은 `SELECT` 절에서 "어떤 `F.ID`와 `N.FISH_NAME`을 보여줘야 하는지"를 결정할 수 없기 때문에 오류를 발생시킵니다. 즉, **"그룹의 대표 값"을 지정하지 않았기 때문**입니다.

해결 방법

```sql
SELECT F.ID, N.FISH_NAME, F.LENGTH
FROM FISH_INFO F
JOIN FISH_NAME_INFO N ON F.FISH_TYPE = N.FISH_TYPE
-- 1. 종류별 가장 큰 물고기 정보를 가져오는 서브쿼리
JOIN (
    SELECT FISH_TYPE, MAX(LENGTH) AS MAX_LENGTH
    FROM FISH_INFO
    GROUP BY FISH_TYPE
) AS T ON F.FISH_TYPE = T.FISH_TYPE AND F.LENGTH = T.MAX_LENGTH;
```

- **서브쿼리**나 **CTE(Common Table Expression)**를 사용하여 먼저 종류별 최댓값을 찾고, 그 결과를 원래 테이블과 조인해야 함.
- WITH 절 사용 방법

```sql
WITH MAX_LENGTH_BY_TYPE AS (
    SELECT FISH_TYPE, MAX(LENGTH) AS MAX_LENGTH
    FROM FISH_INFO
    GROUP BY FISH_TYPE
)
SELECT
    F.ID,    N.FISH_NAME,    F.LENGTH
FROM FISH_INFO F
    JOIN FISH_NAME_INFO N ON F.FISH_TYPE = N.FISH_TYPE
    -- WITH 절에서 정의한 가상 테이블과 조인
    JOIN MAX_LENGTH_BY_TYPE M ON F.FISH_TYPE = M.FISH_TYPE AND F.LENGTH = M.MAX_LENGTH;
```

### 대장균 편차 문제

[문제링크](https://school.programmers.co.kr/learn/courses/30/lessons/299310)

- 분화된 연도, 분화된 연도별 대장균 크기의 편차(YEAR_DEV)
  -- 대장균 개체의 ID를 출력
  -- 분화된 연도별 대장균 크기의 편차 = 분화된 연도별 가장 큰 대장균의 크기 -각 대장균의 크기
- - 분화된 연도별 가장 큰 대장균의 크기와 해당 연도를 구하자 → 서브쿼리

```sql
SELECT YEAR(E.DIFFERENTIATION_DATE) AS YEAR, (T.MAX_COLONY - E.SIZE_OF_COLONY) AS YEAR_DEV, E.ID
FROM ECOLI_DATA E
JOIN (
    SELECT YEAR(DIFFERENTIATION_DATE) AS DIFF_YEAR, MAX(SIZE_OF_COLONY) AS MAX_COLONY
    FROM ECOLI_DATA
    GROUP BY YEAR(DIFFERENTIATION_DATE) -- YEAR(DATE) 하면 연도별로 그룹핑 가능
) AS T ON T.DIFF_YEAR = YEAR(E.DIFFERENTIATION_DATE)
ORDER BY YEAR, YEAR_DEV;
```

### 윈도우 함수 적용하기?!

> 세상에 이런게 있어?
>

```sql
SELECT
    YEAR(DIFFERENTIATION_DATE) AS YEAR,
    MAX(SIZE_OF_COLONY) OVER(PARTITION BY YEAR(DIFFERENTIATION_DATE)) - SIZE_OF_COLONY AS YEAR_DEV, ID
FROM ECOLI_DATA
ORDER BY YEAR, YEAR_DEV
```

윈도우 함수(`Window Function`)는 SQL에서 특정 행을 기준으로 **관련된 행들의 집합(윈도우)**에 대해 계산을 수행하는 함수입니다. `GROUP BY`와 달리, 윈도우 함수는 **행들을 그룹으로 묶어 요약하지 않고, 각 행의 개별성을 유지하면서** 그룹에 대한 집계 값을 계산할 수 있다.

방금 보셨던 쿼리의 `MAX(SIZE_OF_COLONY) OVER(PARTITION BY YEAR(DIFFERENTIATION_DATE))` 부분이 바로 윈도우 함수입니다.

---

### 윈도우 함수의 기본 구조

윈도우 함수는 다음과 같은 기본 형태를 가진다.

```sql
윈도우함수(컬럼) OVER (
    PARTITION BY 컬럼1, 컬럼2...  -- 윈도우(그룹)를 나눌 기준
    ORDER BY 컬럼3, 컬럼4...      -- 윈도우 내에서 정렬할 기준
    ROWS / RANGE BETWEEN ...      -- 윈도우의 범위를 지정 (선택 사항)
)
```

- **`윈도우함수`**: `MAX()`, `MIN()`, `SUM()`, `AVG()`와 같은 집계 함수나, `ROW_NUMBER()`, `RANK()` 같은 순위 함수가 올 수 있습니다.
- **`OVER()`**: 이 함수가 윈도우 함수임을 나타냅니다. 괄호 안의 내용이 '윈도우'를 정의합니다.
- **`PARTITION BY`**: 이 부분이 윈도우 함수 사용의 핵심입니다. `GROUP BY`처럼 행들을 특정 기준으로 나눕니다. 하지만 `GROUP BY`와 달리, `SELECT` 절의 다른 컬럼들을 생략하지 않고 모두 출력할 수 있습니다. 즉, **"같은 해(YEAR)를 가진 모든 행"**이 하나의 파티션(윈도우)이 됩니다.
- **`ORDER BY`**: 파티션 내에서 행을 정렬하는 기준을 정의합니다. 순위 함수를 사용할 때 주로 필요합니다.
- **`ROWS` / `RANGE`**: 윈도우의 범위를 지정합니다. 예를 들어, 현재 행의 이전 3개 행과 다음 2개 행까지를 윈도우로 지정하는 등의 복잡한 분석에 사용됩니다.

---

### 예시 쿼리의 원리

```sql
MAX(SIZE_OF_COLONY) OVER(PARTITION BY YEAR(DIFFERENTIATION_DATE))
```

1. **`PARTITION BY YEAR(DIFFERENTIATION_DATE)`**: 데이터베이스는 먼저 `DIFFERENTIATION_DATE`의 연도를 기준으로 데이터를 나눕니다.
  - 2021년 데이터 그룹
  - 2022년 데이터 그룹
  - 2023년 데이터 그룹
  - ...와 같이 연도별로 논리적인 그룹을 만듭니다.
2. **`MAX(SIZE_OF_COLONY)`**: 각 그룹(파티션) 내에서 `SIZE_OF_COLONY`의 최댓값을 계산합니다.
3. **결과 반환**: 이 최댓값은 **그룹 내 모든 행에 대해 동일하게** 반환됩니다.
  - 예를 들어, 2022년 데이터 중 가장 큰 콜로니 크기가 1000이라면, 2022년 그룹에 속한 모든 행의 `MAX()` 값은 1000이 됩니다.

따라서 이 쿼리는 다음과 같이 동작합니다.

- `YEAR(DIFFERENTIATION_DATE)`: 각 행의 연도를 출력합니다.
- `MAX(...) - SIZE_OF_COLONY`: 각 행의 `SIZE_OF_COLONY`에서 **해당 연도의 가장 큰 콜로니 크기**를 뺀 값을 계산

즉, `GROUP BY`를 써서 최대값만 출력하는 대신, **각 행마다 최대값과의 차이를 계산**하여 출력할 수 있는 것입니다. 윈도우 함수는 이처럼 복잡한 분석을 단일 쿼리로 효율적으로 처리할 수 있게 해준다.

### 윈도우 함수 - 유튜브 강의 내용 정리

> 이름과 같이 창문이다. 어떤 데이터에 대해서 창문과 같은 틀을 바탕으로 보는 것임.
>
- 하나의 칼럼 내에서 각 행에 대해서 연산을 수행하나, GROUP BY 연산과는 달리 각 행의 기존 순서를 유지한 상태로 해당 행에 대해서 새로운 값을 추가하거나 기존의 값을 변경함
- 대표적인 윈도우함수로 RANK 함수가 있으며 모든 윈도우함수는 OVER 키워드와 함께 사용
- 순위 함수
  - 같은 순위를 처리하는 방법에 따라 3가지로 나뉜다.

  ![image.png](/assets/images/sql_rankFunction.png)


- RANK 사용법

![image.png](/assets/images/sql_rankFunctionUsage.png)

- 연비별로 그룹화하여, 그 개수별로 내림차순으로 정렬한 순위를 매긴다.
- 순위함수이므로 기준을 `ORDER BY` 로 제시한다.

- 집계함수 적용하기
- COUNT(*)

![image.png](/assets/images/sql_windowFunctionUsage.png)

- 위의 적용 과정은 실린더 수(CYL)가 6 이하인 레코드들에 대하여, 실린더 수를 기준으로 파티션을 나누고 해당 파티션의 개수를 세서 (`COUNT(*) OVER (PARTITION BY CYL) AS PART_CYL_CNT`) 조회한다.