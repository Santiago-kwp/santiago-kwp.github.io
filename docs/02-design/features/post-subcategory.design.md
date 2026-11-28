# post-subcategory 설계 문서

- 작성일: 2026-11-28
- 상태: Design (구현 전)
- Plan: [post-subcategory.plan.md](../../01-plan/features/post-subcategory.plan.md) — §4 소분류 확정본 기준

---

## 1. 개요

### 1.1 목표

- 기존 대분류(`categories`) 아래에 소분류(`subcategory`) 한 단계를 추가해 `/posts/` 목록과 홈 화면에서 글을 2단계로 구분한다.
- 기존 글 URL과 기존 앵커(`/posts/#network` 등)는 그대로 유지한다.

### 1.2 설계 원칙

| 원칙 | 내용 |
|------|------|
| 순수 Liquid | GitHub Pages 기본 빌드(Jekyll 3.x, safe mode) 호환. 커스텀 플러그인이나 Jekyll 4 전용 문법은 쓰지 않는다 |
| 데이터 한곳 관리 | 표시명과 순서는 `_data/categories.yml`에서만 관리한다. 템플릿에 하드코딩하지 않는다 |
| 선택 필드 | `subcategory`가 없거나 데이터에 등록되지 않은 값이어도 글이 목록에서 빠지지 않는다 |
| 기존 패턴 유지 | `post.project == nil` 필터, include 파라미터는 먼저 `assign`한 뒤 전달 (기존 코드 방식) |

### 1.3 Plan에서 남은 결정 사항 — 이 문서에서 확정

| 항목 | 결정 | 근거 |
|------|------|------|
| 홈 박스 노출 범위 | **소분류까지 노출**. 데스크톱은 `max-height` + 스크롤 | 구분해서 보여주는 것이 목적. 현재 19행(대 7 + 소 12) |
| 소분류 없는 글 | 같은 대분류에 소분류 그룹이 하나라도 있으면 **"기타" h3** 아래에 표시. 하나도 없으면 h3 없이 바로 나열 | 소분류가 있는 대분류에서 h3 없는 글이 섞이면 어느 그룹 소속인지 헷갈림 |
| 햄버거 메뉴 | **이번 범위에서 제외** (대분류만 유지) | `dl-menu` 3단 중첩은 UX가 나쁨 |
| 글 페이지 브레드크럼 | **포함** | 변경이 작고, 뒤로 가기 버튼을 소분류 위치로 연결할 수 있음 |
| 그룹 내 정렬 | 기본은 최신순(현재 동작). 소분류마다 `sort: title` 옵션 지원 | 현재 TCP/IP 시리즈가 6→1 역순으로 표시됨(같은 날짜). `tcp-ip`에 `sort: title` 적용 |

---

## 2. 구조

### 2.1 구성 요소

```
_data/categories.yml ──────────────┐  (표시명, 순서, 정렬 옵션)
                                   ▼
_posts/**/*.md ──(categories, subcategory)──▶ site.categories
                                   │
       ┌───────────────────────────┼──────────────────────────┐
       ▼                           ▼                          ▼
_layouts/post-list.html    _layouts/home.html         _layouts/post.html
  (h2 대분류 / h3 소분류)    (주제별 박스 + 소분류)      (브레드크럼, 뒤로 가기)
       │                           │                          │
       └──── _includes/category-label.html / subcategory-label.html ────┘
```

### 2.2 변경 파일 목록

| 파일 | 구분 | 변경 내용 |
|------|------|-----------|
| `_config.yml` | 수정 | `exclude`에 `docs` 추가 |
| `_data/categories.yml` | **신규** | 대·소분류 표시명, 순서, 정렬 옵션 |
| `_includes/category-label.html` | 수정 | `case` 문 → 데이터 조회 + 폴백 |
| `_includes/subcategory-label.html` | **신규** | 소분류 키 → 표시명 |
| `_includes/post-items.html` | **신규** | 글 목록 `<ul>` 렌더링 블록. 대분류·소분류·기타·Uncategorized 그룹에서 같이 사용 |
| `_layouts/post-list.html` | 수정 | 대분류 루프 안에서 소분류 그룹 렌더링 |
| `_layouts/home.html` | 수정 | 대분류 행 아래 소분류 행 추가 |
| `_layouts/post.html` | 수정 | 브레드크럼, 뒤로 가기 링크 앵커 |
| `_sass/typography.scss` | 수정 | `.post-list h3`, `.post-breadcrumb` 스타일 |
| `_sass/site.scss` | 수정 | `.home-categories` 소분류 행, 데스크톱 `max-height` |
| `_posts/**/*.md` (26개) | 수정 | front matter에 `subcategory` 추가 |
| `_includes/nav.html` | 변경 없음 | — |

---

## 3. 데이터 모델

### 3.1 front matter

```yaml
categories: [network]   # 필수(기존), 대분류 1개
subcategory: tcp-ip     # 선택(신규), 소분류 키 1개, 소문자 kebab-case
```

### 3.2 `_data/categories.yml`

순서를 보장하려고 소분류는 **배열**로 정의한다. 대분류 순서는 기존처럼 키 알파벳순(`site.categories | sort`)을 유지한다.

```yaml
# 대분류 키(= front matter categories 값)
#   label: 표시명
#   subcategories: 표시 순서대로
#     - key:   front matter subcategory 값 (앵커 id에도 사용)
#       label: 표시명
#       sort:  (선택) title → 제목 오름차순. 생략 시 최신순

cv:
  label: ComputerVision
  subcategories:
    - key: camera-calibration
      label: 카메라 보정
    - key: image-format
      label: 영상 포맷

linux:
  label: Linux
  subcategories:
    - key: system
      label: 시스템 관리

multimedia:
  label: Multimedia
  subcategories:
    - key: protocol
      label: 스트리밍·프로토콜

network:
  label: Network
  subcategories:
    - key: tcp-ip
      label: TCP/IP
      sort: title
    - key: monitoring
      label: 네트워크 관리·모니터링

python:
  label: Python
  subcategories:
    - key: internals
      label: 동작 원리
    - key: concurrency
      label: 동시성
    - key: convention
      label: 코딩 컨벤션

software:
  label: Software
  subcategories:
    - key: ddd
      label: 도메인 주도 설계

ui_ux:
  label: UI/UX
  subcategories:
    - key: laws-of-ux
      label: UX 법칙
    - key: ux-practice
      label: UX 실무·윤리
```

### 3.3 글별 매핑 (Plan §4 확정본)

| 파일 (`_posts/`) | categories | subcategory |
|------------------|-----------|-------------|
| `cv/2026-11-25-camera-model-calibration.md` | cv | camera-calibration |
| `cv/2026-11-26-camera-calibration-process.md` | cv | camera-calibration |
| `cv/2026-11-25-YUV_420_888-vs-H264.md` | cv | image-format |
| `linux/2026-11-25-Batch_vs_Service.md` | linux | system |
| `linux/2026-11-25-Check_System_configuration.md` | linux | system |
| `multimedia/2026-11-25-what-Is-FFmpeg.md` | multimedia | protocol |
| `multimedia/2026-11-25-what-Is-RTSP-ONVIF.md` | multimedia | protocol |
| `network/2026-11-25-TCP-IP-1-basic.md` ~ `TCP-IP-6-...md` (6개) | network | tcp-ip |
| `network/2026-11-25-SNMP-basic.md` | network | monitoring |
| `python/2026-11-25-Python-code-execution.md` | python | internals |
| `python/2026-11-25-Python-namespace-scopes.md` | python | internals |
| `python/2026-11-25-Python-thread-lock-Intro.md` | python | concurrency |
| `python/2026-11-25-PEP-intro.md` | python | convention |
| `software/2026-11-26-domain-driven-design-chapter1.md` | software | ddd |
| `ui_ux/2026-11-25-Fitts_Law.md` | ui_ux | laws-of-ux |
| `ui_ux/2026-11-25-Doherty_Threshold_Law.md` | ui_ux | laws-of-ux |
| `ui_ux/2026-11-25-Peak-End_Rule.md` | ui_ux | laws-of-ux |
| `ui_ux/2026-11-25-Postel_Law.md` | ui_ux | laws-of-ux |
| `ui_ux/2026-11-25-Tesler_Law.md` | ui_ux | laws-of-ux |
| `ui_ux/2026-11-25-Design_meets_Psychology.md` | ui_ux | ux-practice |
| `ui_ux/2026-11-25-Power_needs_Responsibility.md` | ui_ux | ux-practice |

합계 26개 (cv 3, linux 2, multimedia 2, network 7, python 4, software 1, ui_ux 7).

### 3.4 앵커 id 규칙

| 대상 | id | 예 |
|------|----|----|
| 대분류 (기존) | `{{ cat_key \| slugify }}` | `network`, `ui-ux` |
| 소분류 | `{{ cat_key \| slugify }}-{{ sub_key \| slugify }}` | `network-tcp-ip`, `ui-ux-laws-of-ux` |
| 기타 그룹 | `{{ cat_key \| slugify }}-etc` | `python-etc` |

`slugify`가 `ui_ux`를 `ui-ux`로 바꾸므로(현재 빌드 결과에서 확인), id를 만들 때는 반드시 같은 필터를 거친다.

---

## 4. 컴포넌트 상세

### 4.1 `_includes/category-label.html`

```liquid
{%- assign cat_data = site.data.categories[include.category] -%}
{%- if cat_data.label -%}
{{ cat_data.label }}
{%- else -%}
{{ include.category | replace: '-', ' ' | capitalize }}
{%- endif -%}
```

- 기존 `cv`/`ui_ux` 하드코딩은 데이터 파일로 옮겨지므로 출력은 이전과 같다.

### 4.2 `_includes/subcategory-label.html` (신규)

```liquid
{%- assign sub_label = nil -%}
{%- for sub in site.data.categories[include.category].subcategories -%}
  {%- if sub.key == include.subcategory -%}{%- assign sub_label = sub.label -%}{%- endif -%}
{%- endfor -%}
{%- if sub_label -%}
{{ sub_label }}
{%- else -%}
{{ include.subcategory | replace: '-', ' ' | capitalize }}
{%- endif -%}
```

### 4.3 `_includes/post-items.html` (신규)

`post-list.html`에 있던 `<li>` 마크업을 그대로 옮기고, 글 배열을 파라미터로 받는다. 대분류 그룹, 소분류 그룹, 기타 그룹, Uncategorized 블록에서 같이 쓴다.

```liquid
<ul>
  {% for post in include.posts %}
  <li class="wow fadeInLeft" data-wow-duration="1.5s">
    <a class="zoombtn" href="{{ site.url }}{{ post.url }}">{{ post.title }}</a>
    <p>{{ post.excerpt }}</p>
    <a href="{{ site.url }}{{ post.url }}" class="btn zoombtn">Read More</a>
  </li>
  {% endfor %}
</ul>
```

### 4.4 `_layouts/post-list.html` — 소분류 그룹 알고리즘

대분류 하나마다 다음 순서로 렌더링한다.

1. `category_posts` = 해당 대분류 글 중 `project == nil` (기존과 같음)
2. `<h2 id="{cat}">대분류</h2>` (기존과 같음)
3. **등록된 소분류**: 데이터 파일의 `subcategories` 순서대로 돌면서
   - `sub_posts = category_posts | where: "subcategory", sub.key`
   - `sub.sort == "title"`이면 `sub_posts | sort: "title"`
   - 글이 1개 이상이면 `<h3 id="{cat}-{sub}">` + `post-items`
   - 렌더링한 키를 `registered` 문자열(`,tcp-ip,monitoring,`)에 누적하고 `rendered_groups` 수를 센다
4. **등록되지 않은 소분류**(오타, 데이터 미등록): `category_posts | group_by: "subcategory"`를 돌면서 `group.name != ""`이고 `registered`에 `,{name},`이 없으면 폴백 표시명으로 h3 + 목록 → 글 누락 방지
5. **소분류 없는 글**: `group.name == ""` 그룹(`group_by`는 nil을 `""`로 묶음)
   - `rendered_groups > 0`이면 `<h3 id="{cat}-etc">기타</h3>` + 목록
   - 아니면 h3 없이 `post-items`만 (현재 화면과 동일)

```liquid
{% assign category_label_key = category[0] %}
{% assign cat_slug = category[0] | slugify %}
{% assign cat_data = site.data.categories[category_label_key] %}
{% assign registered = "," %}
{% assign rendered_groups = 0 %}

<h2 id="{{ cat_slug }}">{% include category-label.html category=category_label_key %}</h2>

{% for sub in cat_data.subcategories %}
  {% assign sub_posts = category_posts | where: "subcategory", sub.key %}
  {% if sub.sort == "title" %}{% assign sub_posts = sub_posts | sort: "title" %}{% endif %}
  {% assign registered = registered | append: sub.key | append: "," %}
  {% if sub_posts.size > 0 %}
    {% assign rendered_groups = rendered_groups | plus: 1 %}
    {% assign sub_key = sub.key %}
    <h3 id="{{ cat_slug }}-{{ sub_key | slugify }}">{% include subcategory-label.html category=category_label_key subcategory=sub_key %}</h3>
    {% include post-items.html posts=sub_posts %}
  {% endif %}
{% endfor %}

{% assign groups = category_posts | group_by: "subcategory" %}
{% for group in groups %}
  {% assign needle = "," | append: group.name | append: "," %}
  {% if group.name != "" %}{% unless registered contains needle %}
    {% assign rendered_groups = rendered_groups | plus: 1 %}
    {% assign sub_key = group.name %}
    <h3 id="{{ cat_slug }}-{{ sub_key | slugify }}">{% include subcategory-label.html category=category_label_key subcategory=sub_key %}</h3>
    {% include post-items.html posts=group.items %}
  {% endunless %}{% endif %}
{% endfor %}

{% for group in groups %}
  {% if group.name == "" %}
    {% if rendered_groups > 0 %}<h3 id="{{ cat_slug }}-etc">기타</h3>{% endif %}
    {% include post-items.html posts=group.items %}
  {% endif %}
{% endfor %}
```

- 하단 `Uncategorized` 블록(카테고리 자체가 없는 글)은 유지하고 `post-items.html`을 쓰도록만 바꾼다.

### 4.5 `_layouts/home.html` — 주제별 박스

```html
<li>
  <a href="/posts/#network">Network <span>7</span></a>
  <ul class="home-subcategories">
    <li><a href="/posts/#network-tcp-ip">TCP/IP <span>6</span></a></li>
    <li><a href="/posts/#network-monitoring">네트워크 관리·모니터링 <span>1</span></a></li>
  </ul>
</li>
```

- 소분류 행은 **데이터 파일에 등록됐고 글이 1개 이상인 것만** 표시한다(미등록/기타는 홈에 표시하지 않고, 대분류 숫자에는 포함).
- 숫자는 `category_posts | where: "subcategory", sub.key | size`.

### 4.6 `_layouts/post.html` — 브레드크럼, 뒤로 가기

```html
<!-- h4 날짜 위 -->
<p class="post-breadcrumb">
  <a href="/posts/#network">Network</a> › <a href="/posts/#network-tcp-ip">TCP/IP</a>
</p>
```

- `page.project`가 있거나 `page.categories`가 비어 있으면 표시하지 않는다.
- `page.subcategory`가 없으면 대분류만 표시한다.
- 뒤로 가기 버튼(`fa-chevron-circle-left`) href:
  - 프로젝트 글: `/projects/` (기존과 같음)
  - 소분류 있음: `/posts/#{cat}-{sub}`
  - 대분류만: `/posts/#{cat}`
  - 그 밖: `/posts/` (기존과 같음)

### 4.7 스타일

| 선택자 | 파일 | 내용 |
|--------|------|------|
| `.post-list h3` | `_sass/typography.scss` | `font-rem(18)`, 좌측 여백 10px(`li`의 `margin: 0 10px`에 맞춤), 위 1.25em / 아래 0.5em, `opacity: 0.85` |
| `.post-breadcrumb` | `_sass/typography.scss` | `font-rem(14)`, 링크 색은 기존 헤더 링크 색 상속, hover 시 `$red` |
| `.home-categories .home-subcategories` | `_sass/site.scss` | `padding-left: 1rem`, `font-rem(14)`, `li a` 행 간격 `0.25rem 0`, `opacity: 0.85` |
| `.home-categories` (`min-width: 62.6em`) | `_sass/site.scss` | `top: calc(50% + 2rem); max-height: calc(100vh - 6rem); overflow-y: auto;` — 박스가 길어져 좌상단 메뉴 버튼과 겹치므로 버튼 아래 영역 기준으로 중앙 정렬 (구현 중 반영) |

---

## 5. 화면

### 5.1 `/posts/`

```
┌───────────────────────────────────────┐
│ All Posts                         [⌂] │
│                                       │
│ ComputerVision            ← h2 #cv    │
│   카메라 보정    ← h3 #cv-camera-calibration
│   ┌─────────────────────────────────┐ │
│   │ 더 정밀한 카메라 보정 방식...     │ │
│   │ 카메라 모델과 보정...            │ │
│   └─────────────────────────────────┘ │
│   영상 포맷       ← h3 #cv-image-format│
│   ┌─────────────────────────────────┐ │
│   │ YUV_420_888 vs H.264 ...        │ │
│   └─────────────────────────────────┘ │
│ ...                                   │
│ Network                               │
│   TCP/IP   (제목순: 1 → 6)            │
│   네트워크 관리·모니터링                 │
└───────────────────────────────────────┘
```

### 5.2 홈 박스

```
주제별 포스트
ComputerVision          3
   카메라 보정           2
   영상 포맷             1
Linux                   2
   시스템 관리           2
...
───────────────────────
전체 포스트 보기
```

### 5.3 사용자 흐름

홈 박스 소분류 클릭 → `/posts/#network-tcp-ip`로 이동 → 글 클릭 → 글 상단 브레드크럼 / 뒤로 가기 → 다시 `/posts/#network-tcp-ip`

---

## 6. 새 글 작성 규칙

1. `categories`에 대분류 1개, `subcategory`에 `_data/categories.yml`에 등록된 키 1개를 쓴다.
2. 새 소분류가 필요하면 **먼저 `_data/categories.yml`에 `key`/`label`을 추가**한다. 추가하지 않아도 글은 목록에 나오지만, 표시명이 폴백(`Tcp ip` 형태)이 되고 홈 박스에는 나오지 않는다.
3. 시리즈물처럼 순서가 중요한 소분류는 `sort: title`을 지정하고 제목에 `[시리즈 n]` 접두어를 붙인다. 10편이 넘으면 문자열 정렬 때문에 `[TCP/IP 10]`이 `[TCP/IP 2]` 앞에 오므로 `01` 같은 0 채움 번호를 쓴다.

---

## 7. 구현 순서

1. `_config.yml` `exclude`에 `docs` 추가
2. `_data/categories.yml` 작성, `category-label.html` 교체, `subcategory-label.html` 추가
3. `post-items.html` 추출 후 `post-list.html` 그룹 렌더링 구현
4. 26개 글 front matter에 `subcategory` 추가 (§3.3)
5. `home.html` 소분류 행 추가
6. `post.html` 브레드크럼, 뒤로 가기 링크
7. SCSS 추가
8. 빌드 후 §8 검증

## 8. 검증 항목 (Check 단계 기준)

| # | 항목 | 방법 |
|---|------|------|
| V1 | 빌드 에러/경고 없음 | `bundle exec jekyll build` |
| V2 | `/posts/` 글 수 26 유지, 중복 없음 | `_site/posts/index.html`에서 `class="wow` 개수 = 26 |
| V3 | 기존 앵커 7개 유지 | `id="cv"`, `"linux"`, `"multimedia"`, `"network"`, `"python"`, `"software"`, `"ui-ux"` |
| V4 | 소분류 앵커 12개 생성 | `id="network-tcp-ip"` 등 §3.2 키 전부 |
| V5 | TCP/IP 시리즈가 1 → 6 순서 | 렌더링 순서 확인 |
| V6 | 미등록 키 폴백 | 임시로 `subcategory: test-key` 글 1개 → 목록에 `Test key` h3로 표시되는지 확인 후 되돌림 |
| V7 | 소분류 없는 글 | 임시로 한 글의 `subcategory` 삭제 → `기타` h3 아래 표시되는지 확인 후 되돌림 |
| V8 | 기존 글 URL 변경 없음 | 변경 전/후 `_site` 하위 디렉터리 목록 비교 (`docs/` 제외) |
| V9 | `_site/docs` 없음 | `docs` 빌드 제외 확인 |
| V10 | 홈 박스 레이아웃 | 데스크톱(높이 768px 등 작은 화면 포함)과 모바일 폭에서 박스가 화면을 넘지 않음 |
| V11 | 글 페이지 브레드크럼, 뒤로 가기 앵커 | TCP/IP 글, 프로젝트 글 각각 확인 |
| V12 | Jekyll 3.x 호환 | 사용 문법이 `where`, `where_exp`, `group_by`, `sort`, `slugify`, `append`, `plus`, `contains` 범위인지 리뷰 (선택: `github-pages` gem으로 로컬 빌드) |
