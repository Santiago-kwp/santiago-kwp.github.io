---
title: "[OS] Lecture 6. Process Synchronization and Mutual Exclusion (7/7) (박기웅)"
excerpt: "운영체제 학습 내용입니다."
date: 2025-09-13
author: kiwoong-park
author_profile: true
layout: single
---

### 순 공부 시간

‘25.9.13 19:20 ~ 20:00 (40분), 20:17 ~ 21:25 (68분), 21:34 ~ 21:54 (20분), 22:34 ~ 23:05분(36분)

### 학습 목표 : high level 상호배제 솔루션 이해하기 → language supported solution → Monitor 이해하기

### 학습 내용 요약

- 프로그래밍 언어가 지원해주는 상호배제 솔루션에는 모니터가 있다.
- 모니터는 공유 데이터와 임계 영역을 포함하는 영역으로, 하나의 프로세스만 내부에 있을 수 있다.
- 모니터의 구조는 프로시저의 수 만큼의 진입 큐(entry queue), ready queue (대기큐), 항상 하나의 신호 큐(signaler queue)가 존재한다.
  - 명령어는 wait(), signal() 이 존재하며 각각 대기큐에서의 대기와, 대기하고 있던 프로시저를 호출하는 역할을 담당한다.
- 모니터를 통해 비교적 간단하게 상호배제 문제인 자원 할당 문제, 생산자 - 소비자 문제, Reader / Writer 문제, 5명의 철학자 문제를 해결할 수 있다.
  - 기본적인 로직은 해야 할 프로시저를 정의한다. 예를 들어,
  - 자원할당 문제 : 자원을 빌리는 프로시저, 자원을 반납하는 프로시저,
  - 생산자 - 소비자 문제 : 자원을 생산하는 프로시저, 자원을 소비하는 프로시저
  - reader/writer 문제 : 책을 읽기 시작하는 프로시저, 책 읽기를 끝내는 프로시저, 책을 쓰기 시작하는 프로시저, 책 쓰기를 끝내는 프로시저
  - 5명의 철학자 문제 : 포크를 드는 프로시저, 포크를 내려놓는 프로시저
- 각각의 문제에 따라서 구현 코드는 조금씩 다르지만 공통적으로 자원의 상태를 확인하고, 있으면 진입, 없으면 대기, 자원의 자료 구조 형태에 따라 변수 하나면 할당 / 반납, 여러 개면 버퍼를 두고 버퍼의 위치 지정, 개수 확인, 자원을 다 활용한 경우 기다리는 프로세스 확인하여 신호를 보내주는 것이다.
- 모니터의 장점은 구현이 쉽다는 것이고 그렇기 때문에 데드락 같은 에러가 잘 나지 않는다는 점이고, 단점은 언어가 지원을 해줘야 하며 그뜻은 컴파일러가 OS를 이해하고 있어야 한다는 뜻이다.

### 상호 배제 솔루션들

- Low-level mechanisms
  - SW solutions (Dekker’s, Peterson’s, Dijkstra’s, etc)
  - HW solutions (TestAndSet 명령어)
  - OS supported Solutions (spinlock, semaphore, eventcounter/sequencer)
  - Flexible
  - Difficult to use → Error-prone
- High-level mechanism
  - Language-level solutions
    - Monitor : 사용이 쉬움

### Monitor

- 공유 데이터와 critical section의 집합
- Conditional Variable
  - wait(), signal() operations

![image.png](/assets/images/lec6_Monitor.png)

### Monitor의 구조

- Entry queue (진입 큐) : 모니터 내의 procedure 수만큼 존재
- Mutual Exclusion  : 모니터 내에는 항상 하나의 프로세스만 존재 가능
- Information hiding (정보 은폐) : 공유 데이터는 모니터 내의 프로세스만 접근 가능
- Condition queue (조건 큐) : 모니터 내의 특정 이벤트를 기다리는 프로세스가 대기
- Signaler queue (신호제공자 큐) : 전화부스 같은 곳 - 시그널을 보내기 위해 잠깐 들어가는 공간
  - 모니터에 항상 하나의 신호제공자 큐가 존재
  - signal() 명령을 실행한 프로세스가 임시 대기

### 자원 할당 문제

- 자원을 빌리는 진입 큐 존재 (entry queues for request_R) : 책을 빌리려는 애
- 자원을 반납하는 진입 큐 존재 (entry queues for release_R) : 책을 반납하려는 애
- 자원을 빌리려는 대기 공간 큐 존재 (condition queue for R_free) : 책을 빌리려고 기다리고 있는 애
- 자원이 비었는지 신호를 주는 큐 존재 ( signaler queue )

```sql
## Procedure requestR
begin 
	if (~R_Available) then -- 현재 자원이 없다면
		R_free.wait();       -- 대기해라
	set R_available = false; -- 자원이 있다면 자원을 쓰고 false로 돌려놔라
end

## Procedure ReleaseR
begin
	R_available = true -- 자원을 다 썼으면 true로 다른 프로세스가 쓸 수 있도록
	R_free.signal()    -- 자원이 쓸 수 있다고 신호를 날려라
end
```

### 자원 할당 시나리오

- 처음에 자원이 있다고 초기화하고 시작
- `P_j` 가 모니터 안에서 `R`을 요청 → 아무도 없으니 `R`을 밖에서 읽는다.
- 이때 `P_m`, `P_k`가 대여 진입 큐에 도착하였지만 자원이 없으니 조건 대기 큐(condition queue for R_free)에 가서 기다린다.
- `P_j` 가 책을 다 봐서 반납 진입 큐로 들어감 → 모니터 안의 `releaseR()` 영역에 들어 간다.
- 이때 `P_m`, `P_k` 가 들어가고 싶어도 `P_j` 가 모니터 안의 `releaseR()`에 있기 때문에 못 들어옴
  ⇒ `P_j` 가 모니터 영역 밖에 있는 신호 큐(`signaler queue`)로 들어간다. ⇒ 신호 큐에서 `P_m`, `P_k` 를 깨워줌
- 자원 `R`이 `P_k` 에게 할당되고, 모니터 밖에 나가서 책을 읽는다. → `P_j`가 다시 모니터 안의 `releaseR()` 영역으로 돌아와서, 남은 작업을 수행한다. → 남은 일도 끝냈으면 밖으로 나간다.

![image.png](/assets/images/lec6_RAllocationWithMonitor.png)

### 생산자와 소비자 문제 - Monitor를 활용한

- entry queue for
  - fillBuf() : 생산자가 진입하는 큐로 버퍼를 채우기 위한 큐
  - emptyBuf() : 소비자가 진입하는 큐로 버퍼를 비우기 위한 큐
- bufHas
  - Data : 소비자가 대기하는 공간으로 버퍼에 데이터가 있는지 확인하는 큐
  - Space : 생산자가 대기하는 공간으로 버퍼에 남은 공간이 있는지 확인하는 큐
- signaler queue : 신호 전달 큐
- monitor 내부
  - shared data
    - in : 버퍼에 데이터를 넣을 위치
    - out : 버퍼에서 데이터를 꺼낼 위치
    - validBufs : 데이터(물건) 수
  - critical section
    - fillBuf() : 버퍼를 채우는 공간
    - emptyBuf() : 버퍼를 비우는 공간

![image.png](/assets/images/lec6_PdCsProbWithMonitor.png)

```sql
## procedure fillBuf (data : message)
begin
	if (validBufs = N) then bufHasSpace.wait() -- 버퍼가 꽉 차있으면 잠시 기다려라
	set buffer[in] = data;                     -- 기다림이 끝나면, 넣을 위치에 데이터 넣고
	set validBufs = valifBufs + 1;             -- 데이터 수 1 증가
	set in = (in + 1) mod N;                   -- 다음 데이터를 넣을 위치를 1 증가시켜서 갱신함
	bufHasData.signal();                       -- 기다리고 있는 소비자에게 신호를 보내서 깨움
end

## procedure emptyBuf 
begin
	if (valifBufs = 0) then bufHasData.wait() -- 버퍼에 데이터가 없으면 잠시 대기
	set data = buffer[out]                    -- 가져올 위치에서 데이터를 꺼내고
	set validBufs = validBufs - 1;            -- 데이터 수 1 감소
	set out = (out + 1) mod N;                -- 다음 가져올 위치 갱신
	bufHasSpae.signal();                      -- 생산자에게 버퍼에 자리가 하나 비었다고 신호를 보내서 깨움
end
```

### Reader - Writer 문제 with Monitor

- reader/writer 프로세스 간의 데이터 무결성 보장 기법
- writer 프로세스에 의한 데이터접근 시에만 상호배제 및 동기화가 필요하다

모니터 구성

- 변수 2개
  - 현재 읽기 작업을 하고 있는 reader 프로세스의 수 : `numReader`
  - 현재 writer 프로세스가 쓰기 작업을 진행 중인지 표시 : `isWrite`
- 조건 큐 2개
  - reader/writer 프로세스가 대기해야 될 경우에 사용 : `bufisWrite`, `bufisRead`
- 프로시저 4개
  - reader/writer 프로세스가 읽기/쓰기 작업을 원할 경우에 호출, 읽기/쓰기 작업을 마쳤을 경우에 호출
    : `readBook` , `writeBook`, `readBookEnd`, `writeBookEnd`

```sql
## procedure writeBook
begin
	if (numReader > 0 or isWrite) then bufIsWrite.wait() -- 현재 독자가 있거나, 쓰는 중이면 기다려라
	set isWrite = true; -- 쓰러 들어간 경우 쓰고 있다고 표시
end

## procedure readBook
begin
	if (isWrite or queue(bufIsWrite)) then bufisRead.wait(); -- 현재 쓰고 있거나, 쓰려고 하는 큐에 진입한 경우 대기
	set numReader = numReader + 1 -- 독자 수 1 증가
	if (queue(bufIsRead) then bufIsRead.signal(); -- 누군가 또 읽고 있는 큐에 있다면 깨움
end

## procedure writeBookEnd
begin
	set isWrite = false; -- 다 썼다고 표시 
	if (queue(bufIsRead)) then bufIsRead.signal() -- 읽기 위해 대기하고 있는 사람 깨움
	else bufIsWrite.signal(); -- 없으면 쓰기 위해 대기하고 있는 사람 꺠움
end
	

## procedure readBookEnd
begin
	set numReader = numReader - 1; -- 다 읽었으면 독자 수 감소
	if (numReader = 0) then bufisWrite.signal(); -- 마지막 문 닫고 나오는 경우 쓰기 위해 대기하고 있는 사람 깨움
end

```

### Dining philosopher problem

- 5명의 철학자
- 철학자들은 생각하는 일, 스파게티를 먹는 일만 반복함
- 공유 자원 : 스파게티, 포크
- 스파게티를 먹기 위해서는 포크를 좌우 하나씩 두 개 들어야 한다.

철학자의 프로세스 : `P_i`

```sql
do forever
	pickup(i);  -- 포크를 들고
	eating;     -- 먹고
	putdown(i); -- 포크를 내려놓고
	thinking;   -- 생각하고
end
```

![image.png](/assets/images/lec6_DiningPhilosopherWithMonitor.png)

```sql
## procedure pickup(me)
begin
	if (numForks[me] !=2 ) ready[me].wait(); -- 현재 내가 쥘 수있는 포크가 2개가 아니면 기다려
	numForks[right(me)] =	numForks[right(me)] - 1; -- 내가 쥘 수 있으면 양 옆의 포크를 하나씩 줄임
	numForks[left(me)] =	numForks[left(me)] - 1;
end
 
 
## procedure putdown(me)
begin
	numForks[right(me)] =	numForks[right(me)] + 1;
	numForks[left(me)] =	numForks[left(me)] + 1;
	if (numForks[right(me)] = 2) then ready[right(me)].signal(); -- 오른쪽 애의 포크가 2개가 되면 부름
	if (numForks[left(me)] = 2) then ready[left(me)].signal(); -- 왼쪽 애의 포크가 2개가 되면 부름	
	

	
```

### Monitor의 장단점

- 장점
  - 사용이 쉽다
  - DeadLock 등 error 발생 가능성이 낮음
- 단점
  - 지원하는 언어에서만 쓸 수 있다.
  - 컴파일러가 OS를 이해하고 있어야 한다.