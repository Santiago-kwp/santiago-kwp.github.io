---
title: "[OS] Lecture 6. Process Synchronization and Mutual Exclusion (2/7) (박기웅)"
excerpt: "운영체제 학습 내용입니다."
date: 2025-09-08
author: kiwoong-park
author_profile: true
layout: single
---
## [OS] Lecture 6. Process Synchronization (동기화) and Mutual Exclusion (상호배제) (2/7)

### 순 공부 시간

9.6.토 00:50 ~ 01:25(35분)

9.8.월 23:40 ~ 9.9.화 00:45 (65분)

### 학습 목표 : 동기화와 상호배제에 대한 개념적 이해

### 학습 내용 요약

- 동기화란 프로세스들이 서로 동작을 맞추는 것이다. 즉 프로세스 간의 정보를 공유해서 임계 영역에 들어가 있는지 아닌지 와 같은 정보를 공유해서 서로 필요한 동작을 진행할 수 있는 것이 동기화
- 반대로 비동기적이라는 뜻은 프로세스들이 서로에 대해서 관심이 없다는 뜻이며 다른 프로세스가 뭘 하고 있고, 어떤 상태든 간에 상관없이 내 갈길을 간다는 뜻이다. 이런 비동기적 병행 프로세스들이 공유 자원에 동시에 접근하면 문제가 발생할 수 있다.
- race condition 이 발생하면, 실행 순서에 따라 결과가 매번 달라질 수 있다. ⇒ 데이터의 무결성이 위배된다.
- 상호 배제의 기본 연산(primitives)은 임계 영역에 들어가기 전에 임계 영역에 다른 프로세스가 있는지 체크 해야 하고, 임계 영역에서 나올 때는 시스템에 알려야 한다.
- 상호 배제를 위한 세 가지 조건은
  1. 상호 배제 : CS에 한 프로세스가 들어가 있으면 다른 프로세스는 못들어간다.
  2. Progress : 임계 영역이 비어 있으면 다른 프로세스의 진입을 방해하지 못해야 한다.
  3. 유한 시간 대기 : 프로세스의 임계 영역 진입은 무한정 대기되면 안되고, 유한 시간 안에 허용되야 한다.
- 위의 세 가지 조건을 만족하는 두 프로세스의 상호 배제 알고리즘은 데커스 알고리즘과, 개선된 피터슨 알고리즘이 있다.

## Process Synchronization

- 다중 프로그래밍
  - 여러 개의 프로세스들이 존재하며, 프로세스들은 서로 독립적으로 동작
  - 공유 자원 또는 공유하는 데이터가 있을 때 문제가 발생할 수 있다.
- 동기화 (Synchronization)
  - **프로세스들이 서로 동작을 맞추는 것**
  - 프로세스들이 서로 정보를 공유하는 것

### Asynchronous and Concurrent Process

- Asynchronous : 비동기적 ⇒ 프로세스들이 서로에 대해 모름
- Concurrent : 병행적 ⇒ 여러 개의 프로세스들이 동시에 시스템에 존재
- 종합하면, **병행적인 비동기적 프로세스들이 공유된 자원에 동시에 접근할 때 문제가 발생할 수 있다!**!

### Terminologies

- 공유 데이터 (Shared data or Critical data)
- 임계 영역 (Critical section) → 공유 데이터를 접근하는 코드 영역
- 상호 배제 (Mutual Exclusion) → 둘 이상의 프로세스가 동시에 critical section에 접근하는 것을 막는 것

### Critical Section

- 기계어 명령어의 특성(Machine Instruction)
  - Atomicity (원자성 = 쪼갤 수 없음), Indivisible (분리 불가능)
  - “한” 기계어의 명령어 실행 도중에 인터럽트 받지 않음 ⇒ 다른 말로 하면 하나의 기계어 명령어가 끝나면 연속되지 않은 다른 명령어가 인터럽트되어 실행될 수는 있다
- 공유된 메모리에 있는 데이터는 CPU의 연산에 의해 값이 변경되기 위해서 레지스터에 저장되고, CPU는 연산 결과를 레지스터에 전달 그리고 레지스터에 있던 데이터가 메모리에 반영된다.

![image.png](/assets/images/lec6_raceCondition.png)

- 프로세스의 상태 복습 : ready (CPU 할당 X) ⇒ running (CPU 할당 O) ⇒ ready (preemption : 선점/선매 = 진행 중인 프로세스를 중단 시키고 먼저 권리를 얻음)
- 실행 순서에 따라서 결과가 달라지는 race condition 이 발생할 수 있다!

### Mutual Exclusion (상호배제)

- 한 프로세스가 임계 영역에 들어 있으면 다른 프로세스는 못들어온다.

Mutual Exclusion Primitives (primitives = 기본 연산)

- enterCS() primitives
  - CS 들어가기 전 진입 전 검사
  - 다른 프로세스가 CS안에 들어있는지 검사한다.
- exitCS() primitives
  - CS를 벗어날 때 후처리하는 과정
  - CS를 벗어났음을 시스템에 알림

### Requirements for ME primitives

- Mutual Exclustion (상호배제)
  - CS 안에 프로세스가 있으면, 다른 프로세스의 진입을 금지
- Progress (진행)
  - CS 안에 있는 프로세스 외에는, 다른 프로세스의 CS 진입을 방해해서는 안됨
- Bounded waiting
  - 프로세스의 CS 진입은 유한 시간 내에 허용되야 함

### Two Process Mutual Exclusion

> version 1: 내 턴일 때만 들어가서 일을 한다.
>
- 내 턴일 때 들어가고, 일을 다해서 나오면 턴을 상대 턴에 넘긴다
- 위의 세 가지 조건을 다 만족할까?
  - Progress 위배
    - 만약 한 프로세스가 자기 턴일 때 죽어 버리면? 다른 프로세스는 임계영역에 프로세스가 없는데 들어가지 못한다.
    - 만약 한 프로세스가 자기 턴일때 일을 마치고 다른 프로세스에게 턴을 넘겼지만, 아직 다른 프로세스는 도착하지 않아 임계 영역이 비어 있지만, 처음 프로세스가 또 들어가고 싶어도 들어갈 수가 없음 → 즉 한 프로세스가 두 번 연속 CS 진입이 불가능

> version 2: 프로세스 마다 true/false  깃발이 있고, 들어갈 때 깃발을 들고 나올 때 깃발을 내린다.
>
- 한 프로세는 다른 프로세스의 깃발 상태를 보고 (true/false) 들어갈지 말지를 판단한다.
  - Mutual Exclusion 위배
    - 한 프로세스가 다른 프로세스의 깃발이 내려간걸 보고 들어가려고 할 때, 들어가려고 했는데 Preemption 당해서 잠깐 멈췄다. (해당 프로세스는 깃발을 올리지 못함)
    - 다른 프로세스가 도착을 하니 한 프로세스의 깃발이 내려가 있어서 CS에 들어갔다.
    - Preemption이 끝나서 다른 프로세스의 깃발을 체크하는 과정은 이미 지나왔으므로 CS에 들어간다.
    - 두 프로세스가 CS에 들어가는 상황이 발생했다.

    ![image.png](/assets/images/lec6_MEPrimitives_v2.png)


> version 3: 깃발을 먼저 들고 의사 표시를 한 뒤에 들어가자
>
- Progress, Bounded waiting 위배
  - 한 프로세스가 들어가려고 깃발을 드는 의사 표시를 한 뒤 들어가려고 하는데, Preemption 당한다.
  - 다른 프로세스가 도착해서 들어가려고 깃발을 들고 의사 표시를 한 뒤 들어가려고 하는데, 한 프로세스의 깃발이 들려 있어서 들어가지 못한다.
  - Preemption 당한 프로세스가 원복되어 들어가려고 하는데 다른 프로세스의 깃발이 들어있어서 들어가지 못한다.

![image.png](/assets/images/lec6_MEPrimitives_v3.png)

### Dekker’s Algorithm

> Two Process ME를 보장하는 최초의 알고리즘
>
- 먼저 깃발을 들며 의사를 표시하는 것은 같음
- 상대편의 깃발이 들리지 않았다면 바로 임계 영역으로 들어간다.
- 만약, 상대편의 깃발이 들렸다면 다음으로 내 턴인지 확인한다.
- 만약 내턴이 아니라면, 내 깃발을 일단 먼저 내리고 내 턴이 올 때까지 대기한다.
- 내 턴이 오면 다시 깃발을 들고 임계 영역으로 들어간다.

![image.png](/assets/images/lec6_dekkersAlgo.png)

### Peterson’s Algorithm

> 데커스 알고리즘보다 간단하게 구현
>
- 들어가는 의사 표시로 깃발을 드는 것은 같음
- 깃발을 들었지만 턴은 상대 턴으로 바꿔서 상대에게 “양보” 함
- 상대의 깃발이 들리지 않았거나, 내 턴이라면 while 문을 빠져 나오면서 임계 영역에 들어감.
- 상대의 깃발이 들려 있고, 내 턴도 아니라면 대기함

![image.png](/assets/images/lec6_petersonAlgo.png)