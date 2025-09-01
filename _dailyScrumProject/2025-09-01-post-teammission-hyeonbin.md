---
title: "1차 팀미션 구현 내용 정리"
excerpt: 학생 성적 입력, 정렬, 출력 기능까지 구현했습니다.
date: 2025-09-01
author: hyeonbin-lee
author_profile: true
layout: single
---

## 1. 구현 내용 요약

### 메서드 구현
- 메서드 시그니처를 변경하지 않는 대신, 인터페이스 구성을 변경했습니다.

### 주요 클래스
- Student: 학생 이름 및 점수를 저장하는 객체(직렬화 가능)
- StudentInput: 콘솔에서 학생 정보를 입력받고, 이를 직렬화하여 `student.dat` 파일에 저장
- StudentOutput: `student.dat` 파일에서 학생 객체들을 읽어와 평균 기준 오름차순으로 정렬 후 콘솔창에서 출력
- SortedStudent: `student.dat` 파일에서 읽어온 `Student` 객체들을 평균 기준 오름차순으로 정렬한 결과를 `orderByAvg.dat`에 저장

### 인터페이스 변경사항
- Input 인터페이스를 2개의 인터페이스로 재분할
  - Input -> ObjectLoader, Checker(입력한 정보의 유효성 검사)
- 기존 Input, Output, Printable 인터페이스의 명칭을 다음과 같이 변경
  - Input -> ObjectLoader: 파일에서 역직렬화하여 Student 객체로 변환
  - Output -> ObjectWriter: Student 객체를 직렬화하여 파일에 저장
  - Printable -> Reporter: 콘솔창에 Student 객체의 데이터를 출력

### 추가된 클래스
**AbstractStudentInput**
- StudentInput 클래스의 기본적인 윤곽을 나타내기 위한 추상 메서드
- ObjectWriter, Checker 인터페이스를 구현

**AbstractStudentOutput**
- StudentOutput과 SortedStudent에서 공통으로 사용하는 기능을 구현
- OrderLoader, Reporter 인터페이스를 구현

---

## 구현 코드

![teammission-hyeonbin.png](/assets/images/teammission-hyeonbin.png)

- 과제 안내사항에 명시된 기본 기능에 대해, 사용한 메서드의 시그니처는 아래와 같습니다.

### StudentInput 클래스

- 기존 직렬화 파일 유무 확인 및 로드: `loadCheck(String)`
- 사용법/입력 안내 출력용 메서드: `printUsage()`(파라미터 없음)
- 중복 이름 검사 및 점수 유효성 검사 후 total/average/grade 산출 후 맵에 저장: `checkKeyAndInputData(String, Student)`
- 직렬화 수행: `outputObject(String)`
- **안내사항의 `saveData()`를 `outputObject(String)`로 대체**

### StudentOutput 클래스

- 역직렬화 수행: `loadObjectFromFile(String)`
- 평균 기준 정렬: `rearrangeData(Comparator<Student>)`
- 정렬 결과 출력: `printResult()`
- **안내사항의 `printInfo()` 메서드를 `printResult()`로 대체**

### SortedStudent 클래스

- 역직렬화 수행: `loadObjectFromFile(String)`
- TreeSet 구성 및 정렬: `createTreeSet(Comparator<Student>)`
- 정렬 결과 출력: `printResult()`
- 직렬화 수행: `outputObject(String)`

### 기타 메서드

- 그 외에도 코드를 분리하여 가독성을 높이기 위해 메서드를 추가적으로 사용(해당 메서드에는 주석 표시)
- 자세한 코드는 아래 링크에서 확인 가능
[깃허브 코드 저장소 링크](https://github.com/HyeonBin2379/team-mission-v1/tree/hyeonbin-lee/src/student){:target="_blank"}

---

## 구현 결과 출력

### 1. 학생 점수 입력

![teammission-input-hyeonbin.png](/assets/images/teammission-input-hyeonbin.png)

### 2. 학생 점수 출력(콘솔창)

![teammission-output-hyeonbin.png](/assets/images/teammission-output-hyeonbin.png)

### 3. 학생 점수 정렬 후 TreeSet을 orderByAvg.dat 파일에 저장

![teammission-sort-hyeonbin.png](/assets/images/teammission-sort-hyeonbin.png)