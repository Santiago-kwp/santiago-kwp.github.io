---
title: "[OS] Lecture 7. DeadLock (3/5) (박기웅)"
excerpt: "운영체제 데드락 Prevention 학습 내용입니다."
date: 2025-09-15
author: kiwoong-park
author_profile: true
layout: single
---

### 순 공부 시간

‘25.9.16.00:00 ~ 00:33 (33분)

### 학습 목표 : DeadLock Prevention 에 대해 이해하기

### 학습 내용 요약

- 데드락을 발생시키는 4개의 조건 중 하나를 제거한다면 데드락은 절대 발생하지 않기 때문에 예방할 수 있을 것이다.
- 데드락 발생의 4가지 요건은 아래와 같다.
  - Exclusive use of Resources
  - Non-preemptible Resources
  - Hold and wait
  - Circular wait
- 각각의 요건을 발생하지 않을 수 있게 한다면 비현실적이거나, 자원의 심각한 낭비를 초래한다.
- 즉, 현실적이지 않은 대안이며 우리는 데드락을 예방하기보다는 회피하거나, 발생을 감지(detection)하고 회복(recover) 하는 쪽이 낫다

### DeadLock Prevention (예방!)

- 4개의 발생 필요 조건 중 하나를 제거한다. ⇒ 교착 상태가 절대 일어나지 않음
- Exclusive use of Resources
  - 모든 자원을 공유 허용 → 현실적으로 불가능함
- Non-Preemptible resources
  - 모든 자원에 선점을 허용한다 → 현실적으로 불가능함
  - 유사한 방법으로는 만약, 프로세스가 할당 받을 수 없는 자원을 요청한 경우, 기존에 가지고 있던 자원을 모두 반납하고 작업 취소 → 이후 처음부터 혹은 check-point부터 다시 시작 → 심각한 자원 낭비 발생
- Hold and wait (partial allocation)
  - 필요 자원 한번에 모두 할당 (Total allocation)
  - 자원 낭비 발생 (만약 한 프로세스가 모두 할당 받았는데, 다른 프로세스는 그 프로세스의 자원의 일부만 받으면 프로세스를 끝낼 수 있는 경우에도 기다려야 한다)
  - 무한 대기 현상 발생 가능
- Circular wait
  - 자원들에게 순서를 부여
  - 프로세스는 순서의 증가 방향으로만 자원 요청 가능
    - 만약 자원이 4개 R1 ~ R4  가 있고, 프로세스 1은 자원을 1, 2, 3, 4 가 필요하다.
    - 이때 프로세스 2는 자원을 1, 3이 필요하지만, 프로세스1이 자원 1을 쓰고 있기 때문에 자원을 받을 수 없고, 이때 자원 3이 남는다고 해도 미리 받을 수 없다.
    - 즉, 자원 낭비 발생

### DeadLock Avoidance

- 시스템의 상태를 계속 감시
- 시스템이 데드락 상태가 될 가능성이 있는 자원 할당 요청을 보류
- 시스템을 항상 safe state로 유지

### Safe state 란?

- 모든 프로세스가 정상적 종료 가능한 상태
- Safe sequence가 존재한다 ⇒ safe state
- Unsafe state 는 DeadLock이 발생할 가능성이 있는 상태 → 물론 반드시 발생한다는 것은 아님