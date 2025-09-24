---
title: "데이터베이스 테스트 예상문제 풀이"
excerpt: 24일 데이터베이스 테스트 예상문제 풀이
date: 2025-09-23 9:06 +0900
author: hyeonbin-lee
author_profile: true
layout: single
---

## 공부 시간

- 2025.9.22 23:22 ~ 2025.9.22 23:58 (36분)
- 2025.9.23 8:35 ~ 2025.9.23 9:10(35분)

---

## 예상문제 풀이

1번) 1) 200 2) 3 3) 1

2번)

```sql
select 학번, 이름
from 학생
where 학년 in (3, 4);
```

3번)

```sql
delete from 학생 where 이름 = '민수';
```

4번)

```sql
select 과목이름, min(점수) as 최소점수, max(점수) as 최대점수
from 성적
group by 과목이름
having avg(점수) >= 90;
```

5번)

```sql
alter table 학생 add column 주소 varchar(20);
```

6번)

```sql
select 학과, count(학번) as 학과별튜플수
from 학생
group by 학과;
```

7번)

|COUNT(*)|
|------|
|1|


8번) 카디널리티: 5  디그리: 4

9번)

```sql
update 학부생 set = 999 where 입학생수 >= 300;
```

10번)

```sql
select 사원.코드 as 코드, 사원.이름 as 이름, 동아리.동아리명 as 동아리명
from 사원 left join 동아리 on 사원.코드 = 동아리.코드;
```

11번)

```sql
select *
from 회원
where 이름 like '이%'
order by 가입일 desc;
```

12번)

```sql
insert into 학생(학번, 이름, 학년, 과목명, 전화번호) values('9830287', '한국산', 3, '경영학개론', '050-1234-1234');
```

13번)

|가격|
|------|
|25,000|

14번) SET


15번) (ㄱ) UPDATE (ㄴ) ON


16번)  (가) ALTER (나) DROP (다) COLUMN


17번) DISTINCT


18번) (가) IS (나) NOT (다) NULL


19번) LEFT JOIN


20번)

(가) 

|COUNT(GRADE)|
|------|
|645|


(나) 

|GRADE|
|------|
|40|

(다) 

|GRADE|COUNT(*)|
|------|---|
|사원|500|
|대리|100|
|과장|30|
|차장|10|
|부장|5|
|널|25|


21번) 도메인

22번) 개체의 특징을 나타내는 여러 개의 속성들로 구성된 집합

23번) CREATE, ALTER, DROP

24번) 외래키

25번) 후보키

26번) 이상

27번) 제2정규형

28번) 제3정규화

29번) 이상현상

30번) 개체 무결성 제약조건

31번) 3

32번) 권한 부여: GRANT  권한 회수: REVOKE