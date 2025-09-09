---
title: "[OS] Lecture 6. Process Synchronization and Mutual Exclusion (3/7) (박기웅)"
excerpt: "운영체제 학습 내용입니다."
date: 2025-09-09
author: kiwoong-park
author_profile: true
layout: single
---
# [OS] Lecture 6. Process Synchronization (동기화) and Mutual Exclusion (상호배제)  (3/7)

> 운영체제라는 단어에서 느껴지는 먼가 생각보다 딱딱하고 재미없을 것 같은 느낌이었는데 이 교수님 강의가 은은하게 재밌달까… 묘하게 빠져드는 매력이 있는 강의 같다.
>

### 순 공부 시간

25.9.9.화 22:55 ~ 9.10.수 00:00 (65분)

### 학습 목표 : 상호 배제를 위한 소프트웨어적 방법과 하드웨어적 방법에 대한 개념적 이해

### 학습 내용 요약

- n 개의 프로세스가 임계 영역에 하나만 들어갈 수 있도록 보장하는 방법에는 다잌스트라 알고리즘이 있다.
    - 다잌스트라 알고리즘은 3개로 구분되는 flag를 가지면서, turn 이라는 변수를 통해서 임계 영역의 진입을 안함 / 1단계 / 2단계로 구분하여 상호배제를 실시한다.
    - 구현이 복잡하고, 바쁜데 기다리는 busy waiting이 발생한다.
    - 추가로 한줄의 코드가 무조건 실행된다고 가정해야 가능하다.
- H/W 적 상호배제 솔루션에는 TAS (Test And Set) 기계어 명령어를 사용한다.
    - TAS는 현재 타겟의 값을 반환하고(불리언 값으로), 타겟을 true로 만드는 함수로 무조건 실행이 보장되는 원자성과 개별성을 가지고 있어서 이를 이용하여 두 프로세스의 상호배제를 보장할 수 있다.
    - 하지만, 3 개 이상의 프로세스의 경우는 bounded waiting 조건을 만족하지 못하므로 waiting[] 이라는 불리언 배열을 통해서 이를 해결한다.
    - 구현이 비교적 간단하지만, 역시나 busy waiting 문제가 발생한다.

## N-Process Mutual Exclusion - S/W Solution

### Dijkstra’s Algorithm

- flag 변수가 3개로 나뉘어짐

| flag[] 값 | 의미 |
| --- | --- |
| idle | 프로세스가 임계 지역 진입을 시도하고 있지 않은 상태 |
| want-in | 프로세스가 임계 지역 진입을 시작하려는 1단계 |
| in-CS | 프로세스가 임계 지역 진입을 시작한 2단계 및 임계 지역 내에 있을 때 |

![image.png](/assets/images/lec6_dijkstra.png)

- 임계 지역 진입시도 1단계
    - i 번째 프로세스가 자신이 들어가고 싶다고 먼저 깃발을 든다 : flag[i] = want-in
    - 자신의 턴이 되지 않았다면 잠깐 대기한다 : while (turn ≠ i) do
        - 대기하면서 다른 프로세스의 깃발이 내려가면 : if (flag[turn] = idle) then
        - 턴을 자신의 것으로 바꾸고 반복문을 빠져 나온다.
- 임계 지역 진입시도 2단계
    - i 번째 프로세스가 2단계 진입을 시도하기 위해 깃발을 in-CS로 변경 한다.
    - 그러면서 j 라는 카운트에 0을 부여한다.
    - j 가 n 보다는 작으면서(&&) j 가 i 이거나, j 프로세스가 in-CS가 아니라면(즉, j 프로세스가 임계 영역에 들어와 있지 않다면 = i 프로세스 혼자만 in-CS 에 들어와 있다면) j 를 하나씩 증가시키면서 j 가 n 이 되고 나오게 된다.
    - `until (j >= n)` 이라는 뜻은 j 가 n 보다 작으면 다시 repeat 지점으로 돌아간다.
    - j 가 n이상이 되고, 나(= i 프로세스) 혼자 in - CS 상태가 되면 임계 영역으로 드디어 진입할 수 있다!!

### SW solutions 의 문제점

- 속도가 느림
- 구현이 복잡함..
- ME primitive 실행 중 preemption 될 수 있음 ⇒ 코드 한 줄이 무조건 다 수행된다는 것을 가정하지만, 현실은 그렇지 않다.
    - 공유 데이터 수정은 OS가 interrupt를 억제함으로써 해결 가능하나 그만큼 오버헤드가 발생한다.
- Busy waiting : 바쁜데 기다리는데 바쁜 상황 ~ 뺑뺑 돌기만 함

## H/W solution

### Test and Set (TAS) instruction

- test 와 set 을 한 번에 수행하는 기계어
- Machine Instruction 이므로 원자성이 보장되며, 개별적임
    - 실행 중 interrupt를 받지 않음 (preemption 되지 않음)
- 타겟의 현재 값을 반환하고, 타겟을 true 로 바꿔주는 메소드를 한번에 수행함(machine instruction)
    - `boolean TestAndSet (boolean *target)`

![image.png](/assets/images/lec6_tas.png)

- lock 이 false일 때는 `TAS(lock)` 이 현재 값을 반환하므로 false로 반복문을 빠져나가면서 임계영역에 들어간다. 들어가면서 lock은 true로 바꿔주므로
- 다른 프로세스는 먼저 들어간 프로세스가 끝나서 lock을 false로 만들어주지 않는 한 들어가지 못한다.
- 임계 영역에 들어갔던 프로세스가 나오면서 lock을 false로 만들어주면 임계 영역에 진입할 수 있다.
- 3개 이상의 프로세스일 경우, Bounded waiting 조건이 위배된다.
    - 운이 없으면 특정 프로세스가 계속 기다릴 수 있다.

### N-process mutual exclusion with H/W solution

![image.png](/assets/images/lec6_nProcessTas.png)

- waiting[i] 라는 boolean 변수를 통해서 대기 상태인지를 표시하고
- 대기중인 프로세스를 찾아서,
    - 대기 중인 프로세스가 없으면 락을 풀어서 다른 프로세스의 진입을 허용하고
    - 대기 중인 프로세스가 있으면 다음 순서로 임계 영역에 진입하도록 waiting[j]를 false로 만들어줌
- 장점 : 구현이 간단함
- 단점 : 여전히 바쁜데 기다리는 busy waiting 이 발생함

⇒ 이런 busy waiting 문제를 해결하기 위해서 OS가 나선다 ! ⇒ 세마포어