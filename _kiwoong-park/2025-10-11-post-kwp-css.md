---
title: "[CSS] CSS 기초 강의 - 그리드와 반응형 디자인 (생활코딩) (박기웅)"
excerpt: "CSS를 차근차근 익혀봅니다."
date: 2025-10-11
author: kiwoong-park
author_profile: true
layout: single
---

### 참고 페이지

https://www.udemy.com/course/web-css-k/learn/lecture/34152062#overview

### 순공부 시간 : 25.10.11 1440 ~ 1549 (69분), 1709 ~ 1759 (50분), 1811 ~ 1833 (32분)


### 학습 목표 : CSS의 그리드 및 반응형 디자인 개념 이해하기

### 학습 내용 요약

- 그리드는 웹 페이지를 격자 형태로 구분하여 너비 및 높이를 조절할 수 있다. 컨텐츠의 크기가 늘어나더라도 설정한 격자를 유지한다.
- 미디어 쿼리를 통해 화면의 너비에 따라서 다른 형태의 스타일을 적용할 수 있다. 

## 그리드

아무런 의미가 없는 디자인 요소가 필요할 때는 `div` 태그나, `span` 태그를 쓰자!

그리드를 적용하고 싶은 대상을 div 태그로 묶어서 `display: grid` 를 적용하고,

`grid-template-columns` 속성을 통해 그리드의 열의 너비를 지정한다.

```css
    <style>
        #grid {
            border: 1px solid pink;
            display: grid;
            grid-template-columns: 150px 1fr; 
            /* NAVIGATION은 150px로 고정, 
            ARTICLE은 나머지 영역으로 화면 크기에 따라 늘어나고, 줄어듧 */
        }
        div {
            border : 1px solid gray;
        }

    </style>
</head>
<body>
    <div id="grid">
        <div>NAVIGATION</div>
        <div>ARTICLE</div>
    </div>
```

![image.png](attachment:4fe4c9de-aaf7-404f-aad5-1755379cca13:image.png)

- 화면의 크기에 따라 컨텐츠가 길다면 박스가 세로로 늘어나고, 같은 요소로 묶인 NAVIGATION의 박스도 세로가 늘어난다!
- 화면의 분할 단위 : `fr` ⇒ `2fr 1fr` ⇒ 화면의 2/3, 1/3 을 차지하게 함
- 그리드를 지원하는 브라우저를 확인하자! https://caniuse.com/?search=grid
    - 강의 영상만 해도 75%의 사용률을 보였는데 현재는 벌써 95%의 사용률을 보인다. 즉 그리드를 잘 익히고 잘 사용하자!

### Grid exercise

그리드 연습을 해보자

https://cssgridgarden.com/#ko

- 그리드 start에서 end는 항상 늘어나야되는 것은 아니다!

```css
#garden {
  display: grid;
  grid-template-columns: 20% 20% 20% 20% 20%;
  grid-template-rows: 20% 20% 20% 20% 20%;
}

#water {
  grid-column-start: 5;
  grid-column-end: 1;

```

- 그리드 왼쪽의 기준이 아닌 오른쪽으로 기준을 하고싶다면, **`grid-column-start`** 와 **`grid-column-end`**를 음수로 설정하시면 됩니다. 예를들어, -1로 오른쪽 첫뻔재 세로선을 지정하실 수 있습니다.
- 그리드 선의 시작과 끝 위치를 기준으로 그리드 항목을 정의하는 대신, **`span`**을 이용하여 열(column)의 넓이를 지정할 수 있습니다. **`span`**은 양수만 설정 가능합니다.

```css
#water {
  grid-column-start: 2;
	grid-column-end: span 2; /* 시작에서 2개 */
}
```

- **`span`** keyword와 **`grid-column-start`**를 이용하여 마지막 위치에서 상대적으로 항목의 넓이를 설정이 가능합니다.

```css
#water {
	grid-column-start: span 3; /* 끝에서 3개 */
  grid-column-end: 6;
}
```

- **`grid-column-start`**와 **`grid-column-end`**를 입력하는 것은 불편합니다. 다행스럽게, **`grid-column`**는 한번에 입력가능한 단축해서 설정으로, /(슬래쉬)로 구분됩니다.
    - 예를 들면, **`grid-column: 2 / 4;`**는 그리드 항목을 두번째 수직선에서 네번째 수직선까지로 설정
    
    ```css
    #water {
    grid-column: 4/6; /* 4 번째에서 5번째 까지 */
    }
    ```
    
- flexbox와 별개로 CSS 그리드를 설정하면 컬럼과 행 두가지 측면에서 쉽게 그리드 항목을 배치할 수 있습니다. **`grid-row-start`**는 **`grid-column-start`** 수직선을 제외하곤 동일하게 작동합
    
    ```css
    #water {
    grid-row-start:3; /* 3번째 행에 배치 */
    }
    
    #water {
    grid-row: 3/6; /* 3번째 행부터 5번째 행까지 배치 */
    }
    
    #poison { /* 2열 5행에 배치 */
    grid-column : 2;
    grid-row : 5;
    }
    ```
    
- 또한 **`grid-column`**와 **`grid-row`**를 span과 함께 사용하여 넓은 영역을 지정할 수 있습니다

```css
#water {
grid-column: 2 / span 4; /* 2열부터 4개 열에 배치 */
grid-row : 1 / span 5;   /* 1행부터 5개 행에 배치 */
}
```

- **`grid-area`**은 /(슬래쉬)로 구분지어 **`grid-row-start`**, **`grid-column-start`**, **`grid-row-end`**, **`grid-column-end`**순으로 입력 가능합니다.

```css
#water {
grid-area: 1 / 2 / 4 / 6; /*1행부터 3행까지, 2열부터 5열까지 */
}
```

- 그리드 요소들이 **`grid-area`**, **`grid-column`**, **`grid-row`**, 기타 등을 사용하지 않고, 표시될 경우 소스코드에 기입된 순서대로 표기됩니다. table 레이아웃에 비해 grid 시스템의 장점인 **`order`** 속성을 이용하면 이를 재정의가 가능합니다.
    - 그리드의 모든 요소들은 **`order`**의 값이 0이지만, **`z-index`**와 같이 양수와 음수의 값 모두 설정이 가능합니다.
    - `order` 속성의 기본 원리
    - **기본값:** 모든 Grid 아이템의 기본 `order` 값은 **`0`*입니다.
    - **배치 순서:** 아이템은 `order` 값이 **작은 순서대로** 정렬됩니다 (**오름차순**).
        - ...order: -1,order: 0,order: 1,order: 2... 순서로 배치됩니다.
    - **동일 값 처리:** `order` 값이 같은 아이템들끼리는 **HTML 소스 코드에 나타나는 순서**대로 배치됩니다.
    
    ```css
    .water {
      order: 0;
    }
    
    .poison {
    order: -1;
    }
    ```
    
- 동일한 너비의 열(column)들을 지정할려면 불편할 수 있습니다. 다행스럽게도 **`repeat`** 함수가 이 문제를 해결해줍니다.

```css
#garden {
  display: grid;
	grid-template-columns: repeat(8, 12.5%);
  grid-template-rows: 20% 20% 20% 20% 20%;
}
```

- **`grid-template-columns`**은 백분율 같은 값뿐만 아니라, 픽셀 및 em과 같은 길이 단위도 허용합니다. 또한 서로 다른 단위를 함께 사용할 수도 있습니다.

```css
#garden {
  display: grid;
	grid-template-columns: 100px 3em 40%
  grid-template-rows: 20% 20% 20% 20% 20%;
}
```

- Grid는 새로운 단위인 fractional **`fr`**를 소개하고 있습니다. 각 **`fr`** 단위들은 사용가능한 공간을 하나로 공유하여 할당합니다. 예시로, 두개의 element들을 **`1fr`**과 **`3fr`**로 설정시, 공간이 4개의 동일한 크기로 공유됩니다.

```css
#garden {
  display: grid;
	grid-template-columns: 1fr 5ft /* 각각 1/6, 5/6 차지 */
  grid-template-rows: 20% 20% 20% 20% 20%;
}
```

- 열(column)을 pixel, percentage, 혹은 em으로 설정시, **`fr`**로 설정된 다른 열(column)의 남은 공간으로 나뉘어집니다.
    - 여기에 당근은 왼쪽에 50px, 잡초는 오른쪽에 50px로 되어있습니다. **`grid-template-columns`**를 사용하여 2개의 열(column), 그리고 **`fr`**를 사용하여 나머지 공간을 차지하는 3개의 열(column)까지 만들어보세요.
    
    ```css
    #garden {
      display: grid;
    grid-template-columns: 50px 1fr 1fr 1fr 50px;
      grid-template-rows: 20% 20% 20% 20% 20%;
    }
    ```
    

- 이제 당신의 정원 왼쪽 75px에 잡초가 있습니다. 나머지 공간의 3/5에는 당근이 자라고 있으며, 반대인 2/5에는 잡초가 넘치고있습니다.

```css
#garden {
  display: grid;
	grid-template-columns: 75px 3fr 2fr;
  grid-template-rows: 100%;
}
```

- **`grid-template`**은 **`grid-template-rows`**와 **`grid-template-columns`**를 조합한 단축 속성입니다.
    - 예를 들어, **`grid-template: 50% 50% / 200px;`**은 각각 50% 인 두개의 행(row)과 200px 너비의 한개의 열(column)의 그리드를 생성합니다.
    - **`grid-template`**을 사용하여 상단 60%와 왼쪽 200px를 포함하는 영역에 물을주세요.
    
    ```css
    grid-template: 60% / 200px;
    ```
    
- 당신의 정원은 멋집니다. 여기 정원 바닥 50px을 남겨두고 나머지 모두를 당근으로 채웠습니다.
    - 불행하게도, 당신의 당근 왼쪽 20%는 잡초로 우거져 있습니다. 정원의 치료를 위해 CSS grid를 사용해보시기 바랍니다.
    
    ```css
    #garden {
      display: grid;
    	grid-template: 1fr 50px / 20% 80%;
    }
    ```
    

## 반응형 디자인

> 화면의 크기에 따라서 웹 페이지의 각 요소들이 반응해서 최적화된 모양으로 바뀌게 하는 것 ⇒ 수많은 하드웨어 화면에서 알맞게 컨텐츠를 보여줘야 한다. ⇒ 반응형 디자인
> 

### 미디어 쿼리

- 화면의 크기에 따라 보이게도 하고, 안보이게도 할 수 있다.

```css
<style>
        div {
            border: 10px solid green;
            font-size: 60px;
        }
        /* screen width > 800px 이면 안보이게 하자 */
        @media(min-width:800px) {
            div {
            display:none;
            }
        }
         /* screen width < 800px 이면 안보이게 하자 */
        @media(max-width:800px) {
            div {
            display:none;
            }
        }
        
    </style>
  </head>
  <body>
    <div>
        Reponsive
    </div>
```

- 화면의 크기에 따라 블럭요소로 바꾼다던지, 라인을 제거하는 등의 디자인 요소를 적용할 수 있다.

```css
@media (max-width: 800px) {
        #grid {
          /* 800px 이하면 블럭요소로 바뀐다. */
          display: block;
          ol { /* 오른쪽 테두리 라인 제거 */
            border-right: none;
          }
        }
        h1 { /* h1 아래 라인 제거 */
          border-bottom: none;
        }
      }
```