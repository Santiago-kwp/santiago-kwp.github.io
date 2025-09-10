---
title: "[OS] Lecture 6. Process Synchronization and Mutual Exclusion (4/7) (박기웅)"
excerpt: "운영체제 학습 내용입니다."
date: 2025-09-10
author: kiwoong-park
author_profile: true
layout: single
---
> 팀원들이 자신들의 속도로 잘 따라오고 있는 것 같아 나름 즐거운 시간이었다. 이제 살짝 긴장감을 주어도 될 때가 아닌가 싶다.

# [OS] Lecture 6. Process Synchronization (동기화) and Mutual Exclusion (상호배제)  (4/7)

## Mutual Exclusion Solutions

### 순 공부 시간

- 9.10.23:50 ~ 9.11.00:30 (40분)

### 학습 목표 : OS 가 도와줘서 해결하는 상호배제 방법론 이해하기

### 학습 내용 요약

- OS가 atomic 한 연산을 보장하는 초기화, P(S), V(S) 연산을 가지는 정수형 변수 S
- 변수 S 를 통해서 상호배제를 하는 스핀락(spinlock) 방법으로 P(S) 연산은 잠금을 풀고(s=s-1) 임계 영역으로 들어가서 일을 처리하고 V(S) 연산을 통해 (s = s+1) 다른 프로세스가 들어올 수 있도록 한다,.
- 문제점은 싱글 프로세서일 경우 한 프로세스가 실행 중에 멈추게 되면, 다른 프로세스도 들어가지 못하고 s=0 인 상태에서 고착되어 버린다. 즉, 멀티 프로세서에서만 동작할 수 있다.
- 또한 아직, P(S)연산에서 보듯이 busy waiting 문제가 남아 있다…

### OS supported S/W Solution - Spinlock

- 쉽게 말하면 정수형 변수
- 초기화, P(), V() 연산으로만 접근 가능한 변수
- 위의 연산들은 atomic 한 연산 (OS 가 보장해줌)
  - P(S) : 물건을 꺼내는 연산 혹은 자물쇠를 거는 것

    ```java
    P(int S) {
    	while(S <= 0) {
    	// do something
    	}
    	S--;
    }
    ```

  - V(S) : 물건을 집어넣는 과정 혹은 자물쇠를 푸는 것

    ```java
    V(int S) {
    	S++;
    }
    ```

- S : active 라고 할 때 아래의 그림 처럼 active = 1 인 경우는 임계 영역이 비어 있는 경우로 active를 꺼내서 0으로 만들면서 임계 영역에 들어가서 자물쇠를 잠금
- 임계 영역에서 일을 끝내고 나오면서 다시 active = 1을 만들어 다른 프로세스가 들어올 수 있도록 자물쇠를 풀음

![image.png](/assets/images/lec6_spinlock.png)

- 문제점 : CPU가 하나인 싱글 프로세서 상황에서는 만약, P_i 가 임계 영역에서 수행 중이다가 멈춰버리게 되면 ⇒ active 는 그대로 0 이므로 P_j 입장에서는 P(S) 에서 계속 반복문을 돌고 있는 상태가 됨 ⇒ P(S) 가 끝나지 않음. ⇒ P_i 가 다시 들어가고 싶어도 들어갈 수 없음 ⇒ 둘 다 일을 못함
- 즉, CPU가 2개 이상이어 멀티 프로세서 시스템에서만 사용 가능 ⇒ P_i 와 P_j 가 동시에 일을 진행하면 문제가 없어짐
- Spinlock 역시 P(S)에서 반복문을 도는 busy waiting 문제가 남아 있다

### Semaphore

- 1965년 또 그 다잌스트라(Dijstra) 님이 제안
- Busy Waiting 문제를 해결
- 세마포어는 음이 아닌 정수형 변수(S ≥ 0)
  - 초기화 연산, P, V 로만 접근 가능
  - P : Probern (검사)
  - V : Verhogen (증가)
- 여기까지는 스핀락과 동일하다고 할 수 있지만 핵심은 **임의의 S 변수 하나에 ready queue 하나가 할당됨 ⇒** 얘가 어떻게 busy waiting 문제를 해결할 수 있는지 알아보자
- 두 가지 종류의 세마 포어
  - Binary Semaphore ( 0 or 1 )
    - 상호 배제 나 프로세스 동기화에 사용
  - Counting Semaphore ( ≥ 0)
    - Producer - Consumer 문제 등을 해결하는 데 사용