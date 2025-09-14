---
title: "[OS] Lecture 7. DeadLock (2/5) (박기웅)"
excerpt: "운영체제 데드락 학습 내용입니다."
date: 2025-09-14
author: kiwoong-park
author_profile: true
layout: single
---

### 순 공부 시간

‘25.9.14 11:20 ~ 12:00 (40분), 12:16 ~ 12:55 (39분)

### 학습 목표 : 데드락의 개념 및 자원의 종류(resource type)에 대한 이해와 데드락의 표현법 및 발생 조건 이해

### 학습 내용 요약

- 데드락(교착상태)는 어떤 자원을 원하는 프로세스가 해당 자원을 가질 가능성이 아예 없는 상태임.
  - 데드락은 Blocked/Asleep 상태인 대기상태에서 발생하며
  - 기아현상은 Ready 상태에서 CPU자원을 받지 못한 상태가 지속되는 현상임. 또한 받을 수 있는 가능성은 있음.
- 자원은 일반적으로 하드웨어/소프트웨어로 구분할 수도 있으나 아래의 네 가지로 분류할 수 있음
  - 선점 가능 여부 (선점 허용= 선점 당해도 문제 없음 / 비허용 = 선점 당하면 문제가 생김)
  - 할당 단위 (전체 할당 / 부분 할당)
  - 재사용 가능 여부 (재사용 가능 / 불가능 = 사용하면 없어짐 ~ signal, message)
  - 동시 사용 가능 여부 (프로그램, 소스코드 등은 동시 사용 가능, 한 순간에 한 프로세스만 사용 가능한 배타적인 프로세서, 메모리 등은 동시 사용 불가능)
- 데드락을 발생시킬 수 있는 자원의 분류
  - 선점 불가능한 자원
  - 할당 단위는 관계 없음
  - 재사용 가능한 자원 (Serially - resuable Resource : 한 프로세스가 사용하고 나면 다른 프로세스가 재사용 가능함, 불가능한 자원도 교착 상태를 일으킬 수 있으나 모델이 너무 복잡해짐)
  - 동시 사용이 불가능한 자원 (프로세서, 메모리, 디스크 등)
- 데드락을 표현하는 방법
  - 그래프 모델 : 노드와 엣지 → 노드는 프로세스와 자원, 엣지는 자원이 프로세스에게 할당되거나, 프로세스가 자원을 요청하거나
    - 데드락이 나타날 때는 노드와 엣지 간에 순환 관계가 나타날 때임
  - 상태전이모델(state transition model) : 프로세스의 자원 할당 개수, 프로세스가 자원을 요청하는 상태에 따라 만약 1개의 프로세스에 자원이 2개있다면 총 5개의 상태를 정의할 수 있음
    - 5 = 2+ 2+ 1 ⇒ 할당 받은 자원이 없고 → 자원 요청 안함/함 (2) + 할당 받은 자원이 1개 있고 → 자원 요청 안함/함 (2) + 할당 받은 자원이 2개 있어서 더 이상 자원 요청 못함(1)
    - 2개의 프로세스로 나타낸다면 총 25개의 상태 전이가 있을 수 있으며 데드락이 나타날 때는 두 프로세스가 하나 가지고 있고, 하나를 요청하는 상태
- 데드락이 발생할 수 있는 필요 조건 4개
  - 데드락은 자원의 특성과 프로세스의 특징에 따라서 4개의 조건이 충족되어야 발생할 수 있음
  - 자원의 경우 선점이 불가능하며(Non-preemptible), 배타적인 자원(exclusive use : 나 혼자만 쓸 수 있는 자원) 일때
  - 프로세스는 hold and wait : 즉, 자원을 가지고 있는 상태에서 또 다른 자원을 요청하는 상태일 때, 순환적 대기 상태일때임


### DeadLock

- 어느 프로세스도 자신이 원하는 자원을 가지지 못한 상태
- Blocked / Asleep state
  - 프로세스가 특정 이벤트를 가지는 상태
  - 프로세스가 필요한 자원을 기다리는 상태
- DeadLock State
  - 프로세스가 발생 가능성이 없는 이벤트를 기다리는 경우
    - 프로세스가 교착상태에 빠져 있음
  - 시스템 내에 데드락에 빠진 프로세스가 있는 경우
    - 시스템이 deadLock 상태에 있음
- DeadLock vs Starvation
  - 데드락은 Blocked/Asleep 상태에 있음. 발생 가능성이 아예 없음
  - 기아현상은 CPU를 받지 못하는 Ready 상태에 있음. 또한 발생 가능성이 있음

  ![image.png](/assets/images/lec7_DeadLockVsStarvation.png)


### 자원의 분류

- 일반적 분류
  - 하드웨어 자원 vs 소프트웨어 자원
- 다른 분류법
  - 선점 가능 여부
  - 할당 단위
  - 동시 사용 가능 여부
  - 재사용 가능 여부

### 선점 가능 여부에 따른 자원의 분류

- Preemptible Resource
  - 선점 당한 후, 돌아와도 문제가 발생하지 않는 자원
  - Processor (context switching : saving, restoring), Memory 등
- Non-Preemptible Resource
  - 선점 당한 후, 돌아오면 문제가 발생하는 자원
    - Rollback, restart 등 특별한 동작이 필요
  - E.g. disk drive 등

### 할당 단위에 따른 자원의 분류

- Total allocation Resource
  - 줄 때 전체를 다 준다. ⇒ 자원 전체를 프로세스에게 할당
  - E.g., Processor, disk drive
- Partitioned allocation Resource
  - 하나의 자원을 여러 조각으로 나누어, 여러 프로세스들에게 할당
  - E.g., Memory 등

### 동시 사용 가능 여부에 따른 분류

- Exclusive(배타적) allocation Resource
  - 한 순간에 한 프로세스만 사용 가능한 자원
  - E.g., Processor, memory, disk drive 등
- Shared allocation Resource
  - 여러 프로세스가 동시에 사용 가능한 자원
  - E.g. program(SW, source code), shared data

### 재사용 가능 여부에 따른 자원의 분류

- SR (Serially-reusable Resources)
  - 시스템 내에 항상 존재하는 자원
  - 사용이 끝나면, 다른 프로세스가 사용 가능
  - E.g., Processor, memory, disk drive, program 등
- CR (Consumable Resource)
  - 한 프로세스가 사용한 후에 사라지는 자원
  - E.g., signal, message

### DeadLock 과 자원의 종류

- DeadLock을 발생시킬 수 있는 자원의 분류
  - Non-Preemptible resource : 자원을 뺏을 수 없음
  - Exclusive allocation resource : 혼자 쓰는 자원
  - Serially Reusable Resource :
    - Consumed resource 역시 누군가 자원을 원하는 데 소비해버려서 데드락이 되는 경우도 있을 수 있지만 그것까지 고려하기에는 너무나 복잡함
  - 할당 단위는 데드락에 영향을 미치지 않음

### DeadLock 발생의 예

- 2개의 프로세스 (P1, P2)
- 2개의 자원 (R1, R2)
- 시간에 따른 프로세스
  1. P1이 R2 요청해서 받음
  2. P2가 R1 요청해서 받음
  3. P1이 R1 요청했지만, 받지 못함 — 여기까진 교착 상태는 아님, P2가 R1을 반납하면 받을 수 있으므로
  4. P2가 R2를 요청함 — 받을 가능성이 있나? P2가 R2를 받으려면, P1이 끝나야 하고, P1이 끝나려면 P2가 끝나서 R1을 받아야함 → 순환 관계 (서로 가진 걸 요청하고 있는 상태임)

### DeadLock 표현법

- Graph Model
- State Transition Model

### Graph Model

- Node
  - 프로세스 노드 (P1, P2), 자원 노드 (R1, R2)
- Edge
  - R_j → P_i : 자원 j 가 P_i에 할당됨
  - P_i → R_j : P_i 가 자원 j 를 요청함 (request)
- 데드락인 경우 아래와 같이 그래프 모델에서 사이클이 발생한다

![image.png](/assets/images/lec7_DeadLockGraphModel.png)

### State Transition Model

- 예제
  - 2개의 프로세스와 A type의 자원 2개(unit) 존재
  - 프로세스는 한번에 자원 하나만 요청/반납 가능
- State
  - 프로세스가 하나이고 자원이 2개 존재할 경우의 상태는 5개로 나타낼 수 있음

  | state | # of R units allocated (현재 할당된 자원의 수) | Request | 상태의 의미 |
      | --- | --- | --- | --- |
  | 0 | 0 | x | 할당된 자원도 없고, 요청도 안한 상태 |
  | 1 | 0 | o | 할당된 자원은 없고, 요청은 한 상태 |
  | 2 | 1 | x | 1개 할당 되었고, 요청은 안한 상태 |
  | 3 | 1 | o | 1개 할당 되었고, 하나를 더 요청한 상태 |
  | 4 | 2 | x | 2개 할당되어서, 더 이상 요청할 수 없는 상태 |
- 프로세스가 2개라면 위의 상태가 5 x 5 = 25 개의 상태로 나타낼 수 있다.
  - 아래와 같이 P1이 하나를 가지고 있고, 1개를 추가 요청한 상태와 , P2 역시 하나를 가지고 있고, 1개를 추가로 요청한다면 데드락 상태가 된다.
    ![image.png](/assets/images/lec7_DeadLockStateTransitionModel.png)
  
### DeadLock 발생 필요 조건

- 아래의 네 가지 요건이 충족해야 데드락이 발생된다.
- 자원의 특성
  - Exclusive use of resources : 나 혼자만 쓰는 자원이면서
  - Non-preemptible resources : 선점 당하지 않는 자원
- 프로세스의 특징
  - Hold and wait (Partial allocation)
    - 자원을 하나 가지고 다른 자원을 요청함
  - Circular wait