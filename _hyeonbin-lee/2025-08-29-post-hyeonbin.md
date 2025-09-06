---
title: "1차 팀미션 관련 보충 설명 추가(이현빈)"
excerpt: 1차 팀미션 과제의 README의 설명이 부실하다고 느껴서 이에 관한 설명을 보충합니다.
date: 2025-08-29
author: hyeonbin-lee
author_profile: true
layout: single
---

## 메서드 시그니처(method signature)란?
- 메서드의 이름과 파라미터 타입 리스트를 의미
  - 파라미터의 타입을 나열한 순서와 그 개수까지도 메서드 시그니처에 포함됨을 의미
  - 파라미터의 이름은 메서드 시그니처에 해당되지 않음
- 반환값, 접근제한자, throws를 통한 예외 선언은 메서드 시그니처에 해당하지 않음
- 메서드 시그니처는 **메서드 오버로딩**과 밀접하게 연관되어 있음

**참고 내용**
- [Oracle 공식 문서 - 메서드](https://docs.oracle.com/javase/tutorial/java/javaOO/methods.html){:target="_blank"}
- [Java의 메서드 시그니처는 반환 타입을 포함하는가?](https://www.baeldung.com/java-method-signature-return-type){:target="_blank"}

---

## 각 클래스별 메서드 시그니처 정리(필독)

- 메서드 시그니처에 관해 잘못 알고 있었던 내용을 정정하고, README에서 일부 메서드에 관한 설명이 누락된 부분을 발견하여 해당 내용을 보충하는 김에 각 클래스별 메서드 시그니처를 정리합니다.
  (보충 설명이 필요하거나 설명이 잘못된 부분이 발견될 때마다 지속적으로 내용을 업데이트하겠습니다.)


### StudentInput 클래스

- 기존 직렬화 파일 유무 확인 및 로드: `loadCheck(String)`
- 사용법/입력 안내 출력용 메서드: `printUsage()`(파라미터 없음)
- 중복 이름 검사 및 점수 유효성 검사 후 total/average/grade 산출 후 맵에 저장: `checkKeyAndInputData(String, Student)`
- 직렬화 수행: `outputObject(String)`
- **안내사항의 `saveData()`를 Output 인터페이스에 정의된 `outputObject(String)`으로 대체합니다.**

### StudentOutput 클래스

- 역직렬화 수행: `loadObjectFromFile(String)`
- 평균 기준 정렬: `rearrangeData(Comparator<Student>)`
- 정렬 결과 출력: `printResult()`
- **안내사항의 `printInfo()` 메서드를 Printable 인터페이스에 정의된 `printResult()`로 대체합니다.**

### SortedStudent 클래스

- 역직렬화 수행: `loadObjectFromFile(String)`
- TreeSet 구성 및 정렬: `createTreeSet(Comparator<Student>)`
- 정렬 결과 출력: `printResult()`
- 직렬화 수행: `outputObject(String)`

---

## 인터페이스의 추상메서드/디폴트 메서드 관련 안내

- 인터페이스를 사용하여 구현하실 경우, 각자 과제를 수행하는 상황에 따라 매우 불가피한 경우에만 디폴트 메서드로 변경합니다.
- 인터페이스를 사용하시지 않고 구현하실 경우, 안내사항에 주어진 기능을 수행하는 메서드 시그니처를 참고하는 정도로만 사용하고 삭제하셔도 됩니다.

---

## 개인 브랜치 사용 관련 추가 안내사항

**개인 브랜치 생성 후 push**

```
// 생성한 개인 브랜치에서 최초로 push할 시, 아래의 명령어 입력
git push -u origin 개인브랜치

// 이후부터는 아래와 같이 push
git push
```

**깃허브에 있는 자신의 브랜치를 로컬로 불러오기**

- 로컬에는 작업했던 브랜치가 없고, 깃허브에만 개인 브랜치가 있는 경우
- 불가피하게 저장소의 파일을 다시 clone하거나, 실수로 개인 브랜치를 삭제하여 로컬에 main 브랜치만 남게 된 경우

```
// 지정한 개인브랜치를 로컬의 git에 추가함과 동시에, 현재 브랜치를 지정한 브랜치로 변경
git checkout -t origin/개인브랜치
```