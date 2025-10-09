---
title: "[OS] Lecture18. Process Synchronization and Mutual Exclusion(이현빈)"
excerpt: OS 유튜브 강의 내용 중 Monitor에 관한 정리본입니다.
date: 2025-10-09 18:43 +0900
author: hyeonbin-lee
author_profile: true
layout: single
---

## 순 공부 시간

- 2025.10.09 14:41 ~ 2025.10.09 16:17(96분)
- 2025.10.09 16:40 ~ 2025.10.09 18:40(120분)

## 학습 목표

- 운영체제가 지원하는 상호배제 기법 중 Monitor의 핵심 로직 이해하기
- Monitor 방식과 이전에 학습한 상호 배제 기법 간의 차이점 이해하기

---

## 1. Low-Level vs High-Level

### Low-Level Mechanism

- S/W, H/W, OS가 지원하는 상호배제 기법은 모두 OS의 Low-Level Mechanism(= 저수준 메커니즘)에 해당
    - OS가 하드웨어와 직접 상호작용하면서 시스템의 핵심 기능을 수행
    - 사용법이 복잡하여 실제로 사용하는 과정에서 의도치 않은 오류를 유발할 수 있음

### High-Level Mechanism

- 프로그래밍 언어 차원에서 제공되는 고수준 메커니즘
    - 대략, 프로그래밍 언어 차원에서 운영체제의 주요 기능을 제공하는 것으로 이해함.
    - OS가 사용자에게 시스템을 편리하게 이용할 수 있도록 하기 위해 추상화된 기능을 제공하는 방식

---

## 2. Monitor

### Monitor 개요

- 공유 데이터와 임계 영역의 집합
    - 임계 영역은 procedure라는 일종의 함수 형태로 모니터 내부에 존재
- 모니터 안에는 한번에 1개의 프로세스만 진입 가능
    - 즉, 공유 데이터에는 한번에 1개의 프로세스만 진입 가능
    - `wait()`, `signal()` 연산을 사용하여 프로세스의 진입을 통제
- C언어, Java 등의 프로그래밍 언어에서 지원

### Monitor의 구조

- 진입 큐(Entry Queue)
    - 모니터 내부의 임계 영역에 해당하는 procedure의 개수만큼 존재
- 조건 큐(Condition Queue)
    - 모니터 내부의 특정 이벤트를 기다리는 프로세스들의 대기열
- 신호제공자 큐(Signaler Queue)
    - 모니터 내부의 procedure의 실행을 완료한 프로세스가 `signal()` 명령을 실행하기 위해 들어가는 임시 대기열
    - 모니터에 항상 1개만 존재

### Monitor를 활용한 상호배제 문제의 해결

#### 자원할당 문제

![lec18_monitor_1.png](/assets/images/lec18_monitor_1.png)

**모니터의 구성**

- `R_Available`
    - 프로세스에게 할당할 자원의 상태
    - 자원을 이용할 수 있는지를 나타내는 공유 데이터
- `requestR()`
    - 프로세스가 자원을 요청하는 기능에 해당하는 임계 영역 → `wait()` 연산을 수행하는 procedure
    - 이 기능을 이용할 프로세스에 관한 entry queue가 존재
- `releaseR()`
    - 프로세스가 자원을 반납하는 기능에 해당하는 임계 영역 → `signal()` 연산을 수행하는 procedure
    - 이 기능을 이용할 프로세스에 관한 entry queue가 존재
- `R_free()`
    - 프로세스들이 자원을 할당받기 위한 대기실에 해당하는 condition queue
    - 자원의 이용이 끝난 프로세스는 signaler queue에서 `signal()`을 발생시켜 다음 프로세스에게 자원을 할당함.

**모니터를 활용한 자원 할당 문제에서의 상호배제**

1. 프로세스가 자원을 할당받기 위해 `requestR()`을 호출한다.
    - 여러 개의 프로세스 실행 시, 자원의 할당 순서는 `requestR()`에 관한 entry queue에 들어온 순서대로 실행된다.
    - `requestR()`을 실행했을 때, 자원을 할당할 수 있다면 현재 프로세스를 실행하고, 할당할 수 없다면 `wait()`를 호출하여 대기열 `R_Free`에 현재 프로세스를 추가한다.
2. 프로세스의 자원 이용이 끝나면 `releaseR()`을 호출한다.
    - 자원 이용이 끝난 프로세스는 signaler queue로 이동한 다음에 `signal()`을 호출하게 된다.
    - `releaseR()`이 호출되면 내부적으로 `signal()`을 호출하여 대기열 `R_Free` 에서 다음 프로세스를 꺼내고, `R_Free` 에서 나온 프로세스는 1.의 작업을 반복한다.
3. 모든 프로세스의 실행이 완료될 때까지 1~2.의 작업이 반복된다.

---

#### 생산자-소비자 문제(Producer-Consumer Problem)

![lec18_monitor_2.png](/assets/images/lec18_monitor_2.png)

- **모니터의 구성**
    - `buffer`, `in`, `out`, `validBufs`
        - 생산자-소비자 문제에서 사용되는 공유 데이터에 해당
        - `buffer` 는 N개 존재한다고 가정
        - `in`, `out` 은 각각 생산자와 소비자가 다음에 데이터를 추가하거나 사용할 공유 버퍼를 의미
        - `validBufs`는 생산자에 의해 데이터가 추가된 공유 버퍼의 개수를 의미
    - `fillBuf()`
        - 생산자 프로세스에서 지정한 공유 메모리 버퍼에 데이터를 추가하기 위한 프로시저
        - 이 프로시저에 관한 entry queue에는 공유 버퍼에 데이터를 추가할 생산자 프로세스들이 대기
    - `emptyBuf()`
        - 소비자 프로세스에서 지정한 공유 메모리 버퍼에 저장된 데이터를 소비하기 위한 프로시저
        - 이 프로시저에 관한 entry queue에는 공유 버퍼에 저장된 데이터를 이용할 소비자 프로세스들이 대기
    - `bufHasData`
        - `emptyBuf()` 를 통해 데이터를 사용할 공유 버퍼가 더 이상 없을 때, 다음에 데이터를 사용할 소비자 프로세스들에 관한 conditional queue
    - `bufHasSpace`
        - `fillBuf()`를 통해 데이터를 추가할 공유 버퍼가 더 이상 없을 경우, 다음에 데이터를 추가할 생산자 프로세스들에 관한 conditional queue
- **모니터를 활용한 생산자 - 소비자 문제에서의 상호배제**
    1. 생산자 프로세스는 프로시저 `fillBuf()`를 실행하여 공유버퍼에 데이터를 추가
        - 여러 개의 생산자 프로세스 실행 시, entry queue에 추가된 순서대로 생산자 프로세스들이 공유 버퍼에 데이터를 추가
        - `validBufs`의 값이 N이면 데이터를 추가할 공유버퍼가 더 이상 없다는 의미이므로, `wait()` 를 호출하여 현재 실행 중인 생산자 프로세스를 `bufHasSpace`에 추가
        - `validBufs`의 값이 N보다 작으면 생산자 프로세스는 공유 버퍼에 데이터를 추가하고 `bufHasData`의 `signal()` 연산을 실행하면 대기 중인 다음 차례의 소비자가 공유버퍼의 데이터를 소비
    2. 소비자 프로세스는 프로시저 `emptyBuf()` 를 실행하여 공유버퍼에 저장된 데이터를 소비
        - 여러 개의 소비자 프로세스 실행 시 entry queue에 추가된 순서대로 프로시저를 실행
        - `validBufs`의 값이 0이면 데이터를 소비할 공유버퍼가 없다는 의미이므로, `wait()`를 호출하여 현재 실행 중이던 소비자 프로세스를 `bufHasData`에 추가
        - `validBufs`의 값이 0보다 크면 소비자 프로세스는 공유 버퍼의 데이터를 소비하고 `bufHasSpace`의 `signal()` 연산을 실행하면 대기 중이던 다음 차례의 생산자가 공유버퍼에 데이터를 추가
    3. 1~2의 작업을 반복하면서 생산자 & 소비자 프로세스에서의 상호배제를 실현

---

#### 독자-작가 문제(Reader-Writer Problem)

- **기본 개요**
    - reader/writer 프로세스 간 데이터 무결성 보장 기법
    - writer 프로세스가 데이터에 접근할 때만 상호배제/동기화 적용 필요
- **모니터의 구성**
    - 공유 데이터 2개
        - `readCount` : 현재 읽기 작업 중인 reader 프로세스의 개수
        - `isWriting` : writer 프로세스가 현재 쓰기 작업을 진행 중인지를 표시
    - condition queue 2개 → `nextRead`, `nextWrite`
        - reader/writer 프로세스가 대기해야 하는 경우에 사용
    - 프로시저(→ 임계 영역) 4개 사용
        - 즉, 모니터에 관한 entry queue 역시 4개
        - `requestRead()`/ `finishRead()` : reader 프로세스에서 읽기 작업을 요청/종료할 때 호출
        - `requestWrite()` / `finishWrite()` : writer 프로세스에서 쓰기 작업을 요청/종료할 때 호출
- **모니터를 활용한 독자 - 작가 문제에서의 상호배제(읽기 우선)**
    1. reader 프로세스는 프로시저 `requestRead()`를 실행하여 데이터 읽기 수행
        - 이 프로시저에 관한 entry queue에 추가된 순서대로 데이터 쓰기 수행
        - 쓰기 작업을 수행할 writer 프로세스가 존재하면 `wait()`를 호출하여 현재 실행 중인 reader 프로세스를 `nextRead` 의 대기열에 추가
        - 데이터 쓰기 작업을 진행 중인 writer가 없으면 `readCount`는 1 증가하고, reader는 데이터 읽기 수행
    2. reader 프로세스의 데이터 쓰기 작업이 완료되면 프로시저 `finishRead()`를 실행
        - `readCount`는 1 감소
        - `finishRead()` 호출 시 `readCount`가 0보다 크면 `nextRead` 의 `signal()` 연산을 실행하고, 대기하던 다음 reader가 데이터 읽기를 수행
        - `finishRead()` 호출 시 `readCount`가 0이면 `nextWrite`의 의 `signal()` 연산을 실행하고, 대기하던 다음 writer가 데이터 쓰기를 수행
    3. writer 프로세스는 프로시저 `requestWrite()`를 실행하여 데이터 쓰기 수행
        - 이 프로시저에 관한 entry queue에 추가된 순서대로 데이터 쓰기 수행
        - 이미 쓰기 작업을 진행 중인 writer 프로세스가 존재하면 `wait()`를 호출하여 현재 실행 중인 writer 프로세스를 `nextWrite`의 대기열에 추가
    4. writer 프로세스의 데이터 쓰기 작업이 완료되면 `finishWrite()` 를 실행
        - `finishWrite()` 호출 시 `nextWrite`의 `signal()` 연산을 실행하고, 대기 중인 다음 차례의 writer가 데이터 쓰기를 수행

---

#### 식사하는 철학자 문제(Dining Philosopher Problem)

cf) Dining Philosopher Problem은 교착상태(Deadlock)에서도 다뤄지는 문제

![lec18_monitor_3.png](/assets/images/lec18_monitor_3.png)

- **문제 개요 / 모니터의 구성**
    - 5명의 철학자(= 프로세스)는 원탁에 둘러앉아 생각하는 일, 식사하는 일만 반복
        - 식사 → 식기구를 집음(`pickUp()`) → 임계영역 진입
        - 생각 → 식기구를 내려놓음(`pickDown()`) → 임계영역 탈출(`signal()` 연산 실행)
        - 왼손 사용 → `left()`
        - 오른손 사용 → `right()`
    - 식기구
        - 철학자마다 몇 개의 식기구를 사용할 수 있는지를 나타내는 배열
        - 공유 데이터에 해당
    - 양손 모두 식기구를 들고 있어야 식사 가능
        - 임계 영역 진입 조건에 해당
        - 이 조건을 충족하지 못한 프로세스는 `ready`에서 대기(`wait()`)
            - 철학자의 수만큼 존재하는 condition queue
- **모니터를 활용한 식사하는 철학자 문제에서의 상호 배제**
    1. 최초 실행 시 모든 철학자 프로세스는 포크를 2개씩 보유
    2. 철학자가 `pickup()` 연산 실행
        - 현재 철학자의 포크 개수가 2개보다 적으면 `wait()`을 호출하고, 현재 철학자 자신의 `ready`에서 대기
        - 현재 철학자가 보유한 포크가 2개면 이 철학자의 왼쪽, 오른쪽 철학자의 포크 개수는 각각 1씩 감소하고, 현재 철학자는 임계 영역에 진입
    3. 철학자의 ‘식사’ 가 끝나면 `pickdown()` 연산을 실행
        - `pickdown()` 실행 시 현재 철학자의 왼쪽, 오른쪽에 앉은 철학자의 포크의 개수가 각각 1씩 증가
        - 식사를 마친 철학자의 왼쪽, 오른쪽 철학자의 포크 개수가 2개가 되면 해당 철학자의 `ready`에서 `signal()` 연산을 실행

---

### Monitor의 장단점

#### 장점

- 이전에 학습했던 상호배제 기법에 비해 사용이 편리
- 사용하는 과정에서 오류가 발생할 가능성이 낮음

#### 단점

- 사용하는 프로그래밍 언어가 Monitor를 지원하지 않으면 사용 불가