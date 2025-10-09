---
title: "[CSS] CSS 기초 강의 - CSS의 기본 문법 및 박스모델 (생활코딩) (박기웅)"
excerpt: "CSS를 차근차근 익혀봅니다."
date: 2025-10-09
author: kiwoong-park
author_profile: true
layout: single
---

### 참고 페이지

https://www.udemy.com/course/web-css-k/learn/lecture/34152062#overview

### 순공부 시간 : 25.10.9 2205 ~ 2306 (61분), 2320 ~ 2430 (70분)

### 학습 목표 : CSS의 기본 문법 및 박스 모델 이해하기

### 학습 내용 요약

- CSS는 HTML은 정보 전달에 전념하고, 디자인에 대한 기능은 CSS가 전담하여 효율적으로 코드를 짜기 위해 등장
- `;` 로 디자인 요소를 구분하며, 선택자(selector) + 효과(declaration) + 속성 : 값 (property : value) 으로 구성된다.
- W3CSchools을 활용하여 검색하여 원하는 디자인 스타일을 적용하고, 선택자의 우선순위는 포괄적인 것보다 구체적인 것이 우선순위를 가진다 => Ex. id 선택자 -> 클래스 선택자 -> 태그 선택자 
- 블록 요소는 너비를 지정해주지 않으면 화면을 꽉 채우게 되고, 너비를 지정하면 해당 너비만큼만 차지한다. 이때, margin이 있으면 해당 마진이 화면을 차지하므로 0으로 지정하면 된다.
- 인라인 요소는 내부 컨텐츠만큼만 공간을 차지한다.

## CSS의 등장

기존의 웹 브라우저는 HTML 만으로 디자인을 해야 했음. 다른 태그는 의미가 있으나, 디자인 태그는 특별한 의미가 없음. 시각 장애인 입장에서는 전혀 의미가 없을 수 있음. 

⇒ 태그를 추가하기보다는 새로운 디자인을 입히는 언어를 만들자 

→ 유지보수의 편리성, 가독성을 높여주는 CSS

HTML은 정보에 전념하게 하기 위해서 HTML에서 디자인에 대한 기능을 뻇어온 것이 CSS

HTML에서 디자인을 입히는 것이 CSS를 적용해서 디자인을 입히는 것보다 훨씬 비효율적이기 때문에  

## CSS의 기본 문법

- `<style>  ... </style>` 은 HTML의 문법임.
- 인라인 디자인 요소를 쓸 때 적용하는 `style=”…”` 도 HTML의 문법임.
- `;` 으로 디자인 요소를 구분함. `style=”color:red; text-decoration: none”`
- **Selector** : 선택자 (디자인 요소를 적용하기 위한 선택자)
- **Declaration** : 선택자가 지정하는 태그들에 대해서 어떠한 효과를 줄 것인가를 선언
- **Property : Value** ⇒ 속성과 그 속성에 넣을 값

![image.png](/assets/images/cssSelectorDeclaration.png)

***CSS 속성을 스스로 알아내는 방법***

- 검색을 활용하자! https://www.w3schools.com/css/css_font_size.asp

***CSS 선택자를 스스로 알아내는 방법***

- 같은 그룹으로 묶기 : `class` 속성을 주자 ⇒ `class=”saw”` (HTML의 문법임)
- class 속성은 여러개를 지정할 수 있고 띄어쓰기로 구분한다. ⇒ `class="saw active"`
    - 좋은 방법은 아님. 순서를 바꾸면 속성 적용이 바뀜 ⇒ 나중에 넣은 것이 우선순위가 높음
- class 속성보다 우선순위가 높은 id 속성을 적용하자 ⇒ `id="active"`

```css
a {
	color: black;
}

.saw {
	color: gray;
}

.active {
	color : red;
}

#active {
	color : red;
}
```

- 왜 id 선택자가 가장 우선순위가 높고, 그 다음은 class 선택자 , 그리고 그 다음은 태그 선택자가 우선순위가 높은 것일까?
    - id의 값은 하나의 웹 페이지에서 단 한번만 등장해야 함.
    - 포괄적인 것보다 구체적이고 특징적인 것이 우선순위가 높아야 코드로 디자인을 적용하기 효율적이기 때문
- https://www.w3schools.com/css/css_selectors.asp : css 선택자를 셀프 스터디 해보자

강의 버전

![image.png](/assets/images/cssBoxEx.png)

클론 코딩 버전 (flexbox 활용)

![image.png](/assets/images/cssBoxExCloning.png)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        /* 기본 스타일 */
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            }
        
        #container {
            border: 1px solid black;
            width: 600px;
            height: 600px;
            margin: 0 auto; /* 중앙 정렬 */
            /* 1. 내부 요소를 수직(h1, hr, content-wrapper)으로 배열 */
            display: flex; 
            flex-direction: column; 
        }
        /* 1. section과 main을 묶는 컨테이너에 Flexbox 적용 */
        /* 수평 배열 컨테이너: 남은 공간을 모두 차지하도록 설정 */
        #content-wrapper {
            /* 2. #container 내부의 남은 수직 공간을 모두 차지하도록 확장 */
            flex-grow: 1; 
            
            /* 3. 내부 요소(section, main)를 수평으로 배열 */
            display: flex;
            /* 참고: flex-direction: row; 는 기본값이므로 생략 가능 */
        }
        /* 2. section과 main의 너비 비율 설정 */
        /* flex: 1은 남은 공간을 균등하게 나눠 가지라는 의미 */
        #content-wrapper section {
            flex: 1; 
            padding-right: 20px; /* 구분선과 내용 사이 간격 */
        }

        /* 3. 구분선 추가 (가장 일반적인 방법: section 또는 main에 border 사용) */
        #content-wrapper main {
            flex: 2; /* main에 section보다 더 많은 공간 할당 (예: 2배) */

            /* main의 왼쪽에 얇은 실선(solid) 구분선 추가 */
            border-left: 1px solid #ccc; 
            padding-left: 20px; /* border와 내용 사이 간격 유지 */
        }

        h1 {
            color: skyblue;
            font-size: 50px;
            margin: 10px 0 0 10px;
        }

        hr {
            margin: 0 5px;
            background-color: black; /* The color of the line */
            width: 580px; /* Adjust as needed */
            height: 0.5px; /* The thickness of the line */
            display: inline-block;
        }
    </style>
</head>
<body>
    <div id="container">
        <h1>WEB</h1>
        <hr>
        <div id="content-wrapper">
            <section>
                <ul>
                    <li>HTML</li>
                    <li>CSS</li>
                    <li>JavaScript</li>
                </ul>
            </section>
            <main>
                <h3>HTML</h3>
                <p>HTML is Hypertext Markup Languages...</p>
            </main>
        </div>
        
    </div>
</body>
</html>
```

- 화면 전체를 쓰는 블록 레벨 요소
- 컨텐츠의 크기만큼의 부피를 같는 요소를 인라인 레벨 요소

```css
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        /*
        block-level element : 기본값!
        */
        h1 {
            border-width: 5px;
            border-color: red;
            border-style: solid;
            display: inline; /* inline으로 변경됨 */
        }
        /*
        inline element
        */
        a {
            border-width: 5px;
            border-color: red;
            border-style: solid;
            display: block;
        }
        
    </style>
</head>
<body>
    <h1>CSS</h1>
    Lorem, ipsum dolor sit amet consectetur adipisicing elit. Optio, labore illum harum, officia totam porro quasi aliquam ab explicabo suscipit nisi quaerat minus cupiditate nostrum nihil sunt omnis? Dolores, ex.
    <a href="">Lorem ipsum</a> dolor sit, amet consectetur adipisicing elit. Alias facilis est iusto, natus eos velit vero amet quam praesentium assumenda deleniti dolores aperiam beatae non exercitationem similique hic repudiandae. Doloribus?
</body>
</html>
```

- 선택자 `,` 를 쓰면 공통 적용 가능함
- border의 속성을 공통으로 써서 코드량을 줄이자

```css
    <style>
        /*
        block-level element : 기본값!
        */
        h1, a {
            border : 5px solid red;
            display: inline; /* inline으로 변경됨 */
        }        
    </style>

```

- 블록 레벨 요소라도 `width` 값을 주면 너비만큼만 차지하게 된다.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        body {
            margin: 0; /* 화면 전체를 꽉 채움 */
        }
        h1 {
            text-align:center;
            font-size:45px;
            border-bottom: 1px solid gray;
            margin: 0;
            padding: 20px;
        }
        ol {
            border-right: 1px solid gray;
            width: 100px; /* block이므로 크기를 지정하면 줄어들음 */
            margin : 0; /* 가로 마진을 없애서 공간을 확보한다.*/
            padding : 20px; /* 너무 붙지 않도록 */

        }
    </style>
</head>
<body>
    <h1>WEB</h1>
    <ol>
        <li>HTML</li>
        <li>CSS</li>
        <li>JavaScript</li>
    </ol>
    <main>
        <h3>HTML</h3>
        <p>HTML is Hypertext Markup Languages...</p>
    </main>
</body>
</html>
```