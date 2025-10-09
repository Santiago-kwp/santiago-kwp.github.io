---
title: "[OS] Lecture19~20. Deadlock(이현빈)"
excerpt: OS 유튜브 강의 내용 중 Deadlock의 기본 개념 및 Deadlock Model에 관한 정리본입니다.
date: 2025-10-09 21:38 +0900
author: hyeonbin-lee
author_profile: true
layout: single
---

## 순 공부 시간

- 2025.10.09 19:51 ~ 21:31(90분)

## 학습 목표

- Deadlock의 기본 개념 및 발생 조건 이해하기

---

## 1. Deadlock(교착상태)의 개념

### Deadlock State

- 프로세스가 발생할 가능성이 없는 이벤트를 기다리는 상태
    - 현재 실행 중인 프로세스가 작업을 수행하지 못한 채 무한정 대기하는 상태
    - 현재 중단 상태(asleep state)인 프로세스의 실행이 재개될 변경될 가능성이 없음
    

cf) 기아 상태(starvation)

- 현재 준비 상태(ready state)인 프로세스가 실행 상태로 넘어가지 못한 채로 계속 실행 순서가 밀리는 것을 의미
- deadlock과는 달리 프로세스가 실행될 여지가 존재한다는 차이가 있음

### Deadlock의 발생 요건과 자원의 종류

#### 자원의 분류

- 선점 가능성에 따른 분류
    - 선점당해도 진행 중이던 작업을 재개할 수 있는 자원
        - Preemptible Resource
        - 프로세서(CPU), 메모리 등
    - 선점당하면 이후 진행에 문제가 발생하는 자원
        - Non-preemptible Resource
        - 디스크 드라이브 등
- 할당 단위에 따른 분류
    - 자원 전체를 프로세스에게 할당
        - Total Allocation Resource
        - 프로세서(싱글코어), 디스크 드라이브 등
    - 1개의 자원을 분할하여 여러 프로세스에게 할당
        - Partitioned Allocation Resource
        - 메모리가 대표적
- 동시 사용 가능 여부에 따른 분류
    - 한번에 1개의 프로세스에게만 할당할 수 있는 자원
        - Exclusive Allocation Resource
        - 프로세서, 메모리, 디스크 드라이브
            - 메모리는 분할될 수 있지만, 분할된 영역을 하나의 프로세스에게 할당했다면 동일한 영역을 다른 프로세스에게 다시 할당하는 것은 불가
    - 여러 프로세스에게 동시에 할당 가능한 자원
        - Shared Allocation Resource
        - S/W 프로그램, 공유 데이터
            - 동일한 프로그램을 여러 개의 창에서 실행
- 재사용 가능 여부에 따른 분류
    - 1개의 프로세스가 이용하면 사라지는 자원
        - Consumable Resource(CR)
        - signal, message 등
    - 이용이 끝나도 시스템에서 재사용 가능한 자원
        - Serially-Reusable Resource(SR)
        - 시스템 내부에 항상 존재하여 한 프로세스에서의 사용이 끝나도 다른 프로세스가 사용 가능
        - 프로세서, 메모리, 디스크 드라이브, 프로그램 등

#### Deadlock이 발생할 수 있는 자원의 종류

- Non-preemptible Resource
    - 프로세스가 자원을 할당받아 이미 사용하고 있다면, 다른 프로세스가 중간에 선점하는 것이 불가능한 자원
- Exclusive Allocation Resource
    - 한번에 1개 프로세스에게만 할당되므로, 자원의 할당이 곧 자원의 독점이 되는 자원
- Serially-Reusable Resource(SR)
    - Consumable Resource(CR)도 Deadlock을 유발할 수 있으나, 여기까지 고려하면 Deadlock 모델이 복잡
    - 이미 한 프로세스에게 할당된 자원을 다른 프로세스에게 새로 할당하는 대신 기존 자원을 할당하므로, 자원의 사용이 끝날 때까지 대기하게 됨
- 자원 할당 단위는 Deadlock에 영향을 끼치지 않음

---

## 2. Deadlock Model

### Graph Model

![lec20_deadlock_graph_model.png](/assets/images/lec20_deadlock_graph_model.png)

- 프로세스가 자원을 요청하거나, 자원이 프로세스에게 할당되는 양상을 유방향 그래프의 형태로 표현한 모델
    - 프로세스, 자원 → 노드
    - 자원 요청/할당 → 간선
- 간선의 화살표 방향에 따라 의미상 차이가 존재
    - 자원 노드 → 프로세스 노드 : 자원이 프로세스에게 할당됨
    - 프로세스 노드 → 자원 노드 : 프로세스가 자원을 요청
- **Graph Model에 기반한 Deadlock의 정의**
    - 프로세스가 자원을 요청하고, 자원이 프로세스에게 할당되는 양상이 사이클을 형성하는 것

### State Transition Model

- 여러 개의 프로세스에게 공유 자원을 할당할 때 나타날 수 있는 모든 상태변화를 상태 변화 다이어그램으로 표현한 모델
    - 1개의 프로세스에게 공유 자원을 할당할 때 나타날 수 있는 각각의  상태를 1개의 노드로 표현
    - 하나의 상태가 다른 상태로 전이될 수 있다면 단방향 화살표로 연결

![lec20_deadlock_state_transition_model.png](/assets/images/lec20_deadlock_state_transition_model.png)

- **State Transition Model에 기반한 Deadlock의 정의**
    - 상태 변화 다이어그램에서, 한번 상태 변화가 일어나면 다시 다른 상태로 전이되거나 이전 상태로 되돌아갈 수 없는 상태를 의미

---

## 3. Deadlock 발생 필요조건

cf) 아래의 조건들을 모두 충족해야만 Deadlock이 발생

### 자원의 특성 관련 Deadlock 발생 조건

#### 자원 독점(Exclusive use of Resource)

- 프로세스로의 할당이 항상 해당 프로세스의 독점이 되는 자원

#### 선점 불가능(Non-preemptible resource)

- 이미 한 프로세스가 점유하고 있다면, 해당 프로세스의 자원 사용이 끝나기 전에는 다른 프로세스가 가져올 수 없는 자원

### 프로세스의 특성 관련 Deadlock 발생 조건

#### 공유 자원 중 일부만 할당(Hold and Wait)

- 한 프로세스가 공유자원 중 일부는 점유했으나, 임계 영역에 진입하기 위한 조건을 충족하지 못하여 다른 공유자원을 할당받기 위해 대기하는 것을 의미

#### 순환 대기(Circular Wait)

- 여러 개의 프로세스들에게 자원을 할당할 때, 각각의 프로세스가 모두 Hold and Wait 상태를 이루면서 사이클을 형성하는 것을 의미