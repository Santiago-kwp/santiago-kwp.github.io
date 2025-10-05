---
title: "[CSS] Flexbox 와 Grid 학습 내용입니다. (박기웅)"
excerpt: "CSS를 차근차근 익혀봅니다."
date: 2025-10-05
author: kiwoong-park
author_profile: true
layout: single
---

### 참고 페이지

https://juliestruly.tistory.com/81

### 순공부 시간 : 25.10.5 21:00 ~ 23:47 (167분)

### 학습 목표 : flexbox의 개념 및 명령어 작동 결과 이해하기

### 학습 내용 요약

- flex를 통해 .container 안의 아이템들에 전체 적용되도록 자식 아이템들을 배치할 수 있음
- `justify-content` : 지정한 축 방향으로 정렬
- `align-items` : 지정한  축의 수직 방향으로 정렬
- `flex-direction: column, row, column-reverse, row-reverse` : 축 방향 및 반전 적용
- `flex-wrap` : 화면 창이 줄어들 때 아이템들을 떨어뜨릴 것인지 말지 결정
- `align-content` : `flex-wrap : wrap` 인 상태에서 축의 수직 방향 정렬 방법을 지정
- `flex-basis` : 축의 방향으로 flex item의 크기를 설정
- `flex-grow` : Flex Container에 공간이 남을 경우 Flex Item의 flex-basis 크기가 얼마나 더 할당 가능한지 나타내는 속성 (즉, 여백을 얼마나 나눠 가질지를 설정)
- `flex-shrink` : flex-grow와 반대로, Flex Container에 공간이 부족해질 때 Flex Item의 axis 방향 크기가 얼마나 줄어들 수 있는지 지정하는 값
- `flex` : `flex-basis`, `flex-grow`, `flex-shrink` 속성은 `flex` 속성 단 하나만 이용해서 한줄로 지정할 수 있다.
    - flex로 한줄로 나타낼 때는 다음과 같은 규칙이 있다.
    
    ```css
    /* One value, unitless number: flex-grow */flex: 2;
    
    /* One value, length or percentage: flex-basis */flex: 10em;
    flex: 30%;
    
    /* Two values: flex-grow | flex-basis */flex: 1 30px;
    
    /* Two values: flex-grow | flex-shrink */flex: 2 2;
    
    /* Three values: flex-grow | flex-shrink | flex-basis */flex: 2 2 10%;
    ```
    
    (1) 값이 한 개일 때,
    
    - 단위가 없으면 flex-grow 값이 된다.
    - 단위가 있으면 flex-basis 값이 된다.
    
    (2) 값이 두 개일 때,
    
    - 첫번째 값은 단위가 없는 숫자여야 한다. 또한 첫번째 값은 flex-grow가 된다.
    - 두번째 값은 단위가 없으면 flex-shrink, 단위가 있거나 auto면 flex-basis가 된다.
    
    (3) 값이 세 개일 때,
    
    - 첫번째 값은 flex-grow, (단위 없어야 함)
    - 두번째 값은 flex-shrink, (단위 없어야 함)
    - 세번째 값은 flex-basis 값이 된다. (단위 있거나 auto여야 함)

## flexbox

```html
<div class="container">
        <div class="item">
            <img src="images/tr-1.png" alt="">
            <p>AAAAAAAAAAAAAA</p>
            <p>AAAAAAAAAAAAAA</p>
            <p>AAAAAAAAAAAAAA</p>
        </div>
        <div class="item">
            <img src="images/tr-2.png" alt="">
            <p>BBBBBBBBBBBBBB</p>
        </div>
        <div class="item">
            <img src="images/tr-3.png" alt="">
            <p>CCCCCCCCCCCCCC</p>
        </div>
    </div> 
```

- `display: flex` 는 일단 `.container` 에 적용하는 것 → 그 안의 자식들이 어떻게 배치가 된다

### flex-direction

- `flex-direction: row` 가 기본값 → 배치:  ( 컨텐츠1 | 컨텐츠2 | 컨텐츠3 )
- `flex-direction: column` → 배치가 위에서 부터 시작하여 순서는 아래와 같음
컨텐츠1
컨텐츠2
컨텐츠3
- `flex-direction: row-reverse` : ( 컨텐츠3 | 컨텐츠2 | 컨텐츠1 )
- `flex-direction: column-reverse` → 배치가 아래부터 시작하여 순서는 아래와 같음
컨텐츠3
컨텐츠2
컨텐츠1

### flex-wrap

- `flex-wrap` : 컨테이너가 아이템들의 폭보다 더 줄어들었을 때 어떻게 할 것인지를 결정하는 것으로 `nowrap` 이 기본값으로, 화면의 너비를 줄여도 아이템들이 아래로 떨어지지 않는다.
- `flex-wrap: wrap` : 화면의 너비를 줄이면 아이템들이 그에 맞춰 아래로 떨어진다.
- `flex-wrap: wrap-reverse` : 화면의 너비를 줄이면 아이템들이 반대 순서로 아래로 떨어짐 ⇒ 잘 쓰이진 않음

### justify-content

- 아이템들을 정렬하는 기능임. 기본값은 `justify-content: flex-start`
- justify는 지금 축 방향으로 정렬해준다. justify는 오뎅꼬치의 꼬치라고 생각하고 축 방향으로 아이템들을 정렬시킨다.
    - `justify-content: end` : 오른쪽 끝으로 모음, `cener` : 중앙 정렬
    - `justify-content: space-between` : 양쪽 끝으로 아이템들을 놓고 사이의 여백을 동일하게 정렬함.
    - `justify-content: space-around` : 양쪽 끝에도 여백을 둬서 모든 아이템들이 같은 여백을 가진다.

### align-items

- `align-items: stretch` : 기본값임. ⇒ 축의 수직 방향으로 스트레치되어 늘어나 있음. 축의 수직 방향으로 아이템을 움직이는 방향을 결정
- `align-items: flex-start`  : 컨텐츠가 축의 수직 방향의 시작인 위로 붙음
- `align-items: flex-end` : 컨텐츠가 축의 수직 방향의 끝인 아래로 붙음
- `align-items: center` : 컨텐츠가 축의  중앙에 정렬됨.
    - 화면 전체의 중앙에 정렬하려면 여기다가 `justify-content: center` 까지 하면 됨

### align-content

- `flex-wrap: wrap` 인 상태여야 가능함.
- `align-content: flex-start, flex-end, center, space-between, space-around`  가능

### container 하위 item에 적용하는 속성들

### flex-grow

- 남은 여백을 각 아이템들이 나눠가지는 비율을 정의
- Ex. `flex-grow : 1`

### flex-basis

- 기본값은 `flex-basis: auto` ⇒ 여백을 나눠 갖음
- `flex-basis: 0` ⇒ 여백을 0으로 만들어 버리기 때문에 결과적으로 실제 컨텐츠들의 너비를 `flex-grow`의 비율로 나눠 갖음

### flex (그냥)

- `flex:1 , flex: 2, flex: 1`   로 정의하면 자동으로 `flex-basis:0` 으로 세팅함
- 하나의 아이템에만 `flex: n` 넣으면 하나만 신축성이 있게 크기가 변함

### align-self (각각 정렬)

- `flex-start, flex-end, center`

### order

- 순서를 의미하며, `order: n` 으로 넣으면 됨

## Grid

### grid-template-columns

```css
.container {
	display: grid;
	grid-template-columns: 40% 60%; /* 4 : 6 쓸데없는 스크롤이 생김*/
	grid-template-columns: 4fr 6fr; /* 4 : 6 스크롤이 안생김  */
	grid-template-columns: repeat(3, 1fr); /* 3개로 나눠짐 */
	grid-template-columns: 200px 1fr; /* 왼쪽만 200px로 고정되고 남은 부분은 늘어남 */
	
	grid-gap: 1rem;  /* 사이 공간 여백 *.
```

- 같은 행일 경우 높이가 맞춰짐

### grid-auto-rows

- 같은 행을 경우 높이를 맞추고 싶을 때는 ?
    - `grid-auto-rows: 200px;`
- `grid-auto-rows: minmax(200px, auto);`  ⇒ 최소 200px을 보장하고, 높이가 넘어간다면 그만큼 늘림
    - 폰트가 늘어남에 따라 변경하려면 `grid-auto-rows: minmax(10em, auto);`

### justify-items

- `justify-items: start, center, end` : 왼쪽, 중앙, 오른쪽 정렬

### align-items

- `align-items: start, center, end` : 위쪽, 중앙, 아래쪽으로 붙음

### justify-self, align-self

- 하나의 아이템만 정렬
- `justify-self : start, center, end`
- `align-self : start, center, end`

### grid-column & grid-rows

- `grid-column: 1/4;` 1열부터 4열까지 차지하게 해주세요!!
- `grid-row: 2/4;` 1행부터 4행까지 차지하게 해주세요
- `grid-column: 3;` 3행부터 시작
- `grid-column: 3; grid-row: 3/5` : 영역을 겹치게 할 수 있음