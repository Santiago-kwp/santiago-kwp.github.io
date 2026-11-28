# 포스트 하위 카테고리(2단계 분류) 도입 계획

- 작성일: 2026-11-28
- 상태: Plan (구현 전)
- 대상: `santiago-kwp.github.io` (Moonrise 테마 기반 Jekyll 블로그)

---

## 1. 결론: 가능한가?

**가능하다.** 별도 플러그인 없이 front matter 필드 하나와 Liquid 템플릿 수정만으로 구현할 수 있다.

단, Jekyll의 `categories`는 **계층이 없는 평면 목록**이므로 `categories: [network, tcp-ip]`처럼 쓰면 안 된다(§3.1 참고).
대신 **`subcategory` 전용 필드**를 추가하는 방식을 권장한다.

---

## 2. 현재 구조 분석

### 2.1 빌드/배포 환경

| 항목      | 현황                                                                                           | 영향                                                                    |
| --------- | ---------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| Jekyll    | 로컬 `4.3.4` (Gemfile)                                                                         | -                                                                       |
| 배포      | `.github/workflows` 없음 → **GitHub Pages 기본 빌드**(github-pages gem, Jekyll 3.x, safe mode) | 커스텀 Ruby 플러그인 사용 불가. **순수 Liquid로만 구현**해야 함         |
| permalink | `/:title/`                                                                                     | 카테고리가 URL에 포함되지 않음 → **분류를 바꿔도 기존 글 URL은 그대로** |

### 2.2 카테고리 지정 방식

- 각 포스트 front matter의 `categories: [python]` 한 개 값으로 분류.
- `_posts/python/...` 같은 하위 폴더는 **정리용일 뿐 Jekyll 카테고리에 영향 없음**
  (Jekyll은 `_posts` _상위_ 폴더만 카테고리로 해석한다).

### 2.3 카테고리를 사용하는 곳 (수정 대상)

| 파일                                   | 역할                                                                                       |
| -------------------------------------- | ------------------------------------------------------------------------------------------ |
| `_layouts/post-list.html`              | `/posts/` 전체 목록. `site.categories`를 정렬해 카테고리별 `<h2 id="slug">` + 글 목록 출력 |
| `_layouts/home.html`                   | 홈 좌측 "주제별 포스트" 박스. 카테고리명 + 글 수, `/posts/#slug`로 링크                    |
| `_includes/nav.html` (116~128행)       | 햄버거 메뉴 Posts 하위 메뉴. 카테고리명 (글 수)                                            |
| `_includes/category-label.html`        | 카테고리 키 → 표시명 변환 (`cv`→ComputerVision, `ui_ux`→UI/UX, 나머지 capitalize)          |
| `_layouts/post.html`                   | 개별 글 페이지. 현재 카테고리 표시 없음                                                    |
| `_sass/site.scss` (`.home-categories`) | 홈 카테고리 박스 스타일                                                                    |

공통 패턴: 모든 곳에서 `post.project == nil` 필터로 프로젝트 글을 제외한다. 신규 로직도 동일하게 유지해야 한다.

### 2.4 현재 포스트 분포 (26개)

| 카테고리   | 글 수 | 비고                                    |
| ---------- | ----- | --------------------------------------- |
| network    | 7     | TCP/IP 시리즈 6 + SNMP 1                |
| ui_ux      | 7     | UX 법칙 관련                            |
| python     | 4     | 실행 구조, namespace, thread lock, PEP8 |
| cv         | 3     | 카메라 보정 2 + 영상 포맷 1             |
| linux      | 2     |                                         |
| multimedia | 2     |                                         |
| software   | 1     | DDD                                     |

---

## 3. 설계 방안

### 3.1 방안 비교

| 방안                             | 방법                                            | 장점                                                                                            | 단점                                                                                                                      |
| -------------------------------- | ----------------------------------------------- | ----------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| A. `categories`에 2개 값         | `categories: [network, tcp-ip]`                 | 필드 추가 없음                                                                                  | `site.categories`에 `tcp-ip`가 **최상위 카테고리로 섞여 나옴** → 모든 템플릿에서 걸러내야 함. 순서 의존적이라 실수에 취약 |
| **B. `subcategory` 필드 (권장)** | `categories: [network]` + `subcategory: tcp-ip` | 기존 템플릿/데이터 구조 유지, `group_by`로 간단히 그룹핑, 하위 분류가 없는 글도 자연스럽게 처리 | 필드 하나 추가                                                                                                            |
| C. 폴더 구조                     | `network/tcp-ip/_posts/...`                     | front matter 불필요                                                                             | 두 값이 모두 평면 카테고리가 됨(A와 같은 문제), 파일 대규모 이동 필요                                                     |

→ **방안 B 채택.**

### 3.2 front matter 규칙

```yaml
---
layout: post
title: "[TCP/IP 4] 트랜스포트 계층 - TCP와 UDP 제대로 이해하기"
categories: [network] # 대분류 (기존과 동일, 1개)
subcategory: tcp-ip # 소분류 (신규, 선택 사항, 1개)
tags: [TCP/IP, TCP, UDP]
---
```

- `subcategory`는 **선택 사항**. 없으면 해당 대분류의 "기타(미분류)" 그룹에 표시.
- 키는 소문자 kebab-case(`tcp-ip`, `camera-calibration`)로 통일 — 앵커 id로도 사용.

### 3.3 표시명/정렬 데이터 분리: `_data/categories.yml` (신규)

현재 `category-label.html`의 하드코딩된 `case` 문을 데이터 파일로 옮겨 대·소분류 표시명과 순서를 한곳에서 관리한다.

```yaml
# 대분류 키: 표시명, 소분류 목록(표시 순서대로)
network:
  label: Network
  subcategories:
    tcp-ip: TCP/IP
    monitoring: 네트워크 관리·모니터링
cv:
  label: ComputerVision
  subcategories:
    camera-calibration: 카메라 보정
    image-format: 영상 포맷
ui_ux:
  label: UI/UX
  ...
```

- 데이터에 없는 키는 기존처럼 `replace: '-', ' ' | capitalize` 로 폴백 → 데이터 파일 누락으로 빌드가 깨지지 않음.
- 소분류 표시명 출력용 `_includes/subcategory-label.html` 추가.

### 3.4 화면별 변경 사항

#### (1) `/posts/` 전체 목록 — `_layouts/post-list.html` (핵심)

```
Network                       ← h2 id="network"
  TCP/IP                      ← h3 id="network-tcp-ip"
    - [TCP/IP 1] ...
    - [TCP/IP 2] ...
  네트워크 관리·모니터링       ← h3 id="network-monitoring"
    - SNMP 기초 ...
Python
  (소분류 없는 글은 h3 없이 바로 나열)
```

구현 개요 (Jekyll 3/4 공통 Liquid):

```liquid
{% assign category_posts = category[1] | where_exp: "post", "post.project == nil" %}
{% assign groups = category_posts | group_by: "subcategory" %}
{% for group in groups %}
  {% if group.name != "" %}
    <h3 id="{{ category[0] | slugify }}-{{ group.name | slugify }}">
      {% include subcategory-label.html category=category[0] subcategory=group.name %}
    </h3>
  {% endif %}
  <ul> {% for post in group.items %} ... {% endfor %} </ul>
{% endfor %}
```

- 소분류 정렬: `group_by` 결과는 등장 순서이므로, `_data/categories.yml`의 `subcategories` 순서대로 순회 후 남은 그룹(미등록/미분류)을 뒤에 출력하는 방식으로 정렬을 보장한다.
- 기존 `<h2 id="{{ category | slugify }}">` 앵커는 **그대로 유지** → 홈/메뉴의 기존 링크 호환.
- h3 스타일은 `_sass/site.scss`에 소폭 추가(들여쓰기, 폰트 크기).

#### (2) 홈 "주제별 포스트" 박스 — `_layouts/home.html`

- 대분류 행은 유지, 그 아래 소분류를 작은 글씨로 들여써서 표시(글 수 포함).
- 링크: `/posts/#network-tcp-ip`.
- 박스 높이가 늘어나므로 데스크톱(`min-width: 62.6em`)에서 `position: absolute; top: 50%` 배치가 화면을 넘지 않는지 확인 필요. 넘친다면 `max-height` + `overflow-y: auto` 적용, 또는 홈에서는 대분류만 노출하는 것도 선택지.

#### (3) 햄버거 메뉴 — `_includes/nav.html`

- 1차 구현: **변경 최소화** — 대분류만 유지(메뉴 플러그인 `dl-menu`의 3단 중첩은 UX가 좋지 않음).
- 필요 시 대분류 아래 소분류를 들여쓴 항목으로 추가(2차).

#### (4) 개별 글 — `_layouts/post.html` (선택)

- 제목 아래 `Network › TCP/IP` 형태의 브레드크럼 추가, 각각 `/posts/#...` 링크.
- "목록으로" 버튼을 `/posts/#{대분류}-{소분류}`로 연결하면 원래 위치로 돌아갈 수 있음.

---

## 4. 현재 글 기준 소분류

| 대분류     | 소분류 키            | 표시명(안)             | 해당 글                                              |
| ---------- | -------------------- | ---------------------- | ---------------------------------------------------- |
| network    | `tcp-ip`             | TCP/IP                 | TCP-IP-1 ~ 6                                         |
| network    | `monitoring`         | 네트워크 관리·모니터링 | SNMP-basic                                           |
| python     | `internals`          | 동작 원리              | Python-code-execution, Python-namespace-scopes       |
| python     | `concurrency`        | 동시성                 | Python-thread-lock-Intro                             |
| python     | `convention`         | 코딩 컨벤션            | PEP-intro                                            |
| cv         | `camera-calibration` | 카메라 보정            | camera-model-calibration, camera-calibration-process |
| cv         | `image-format`       | 영상 포맷              | YUV_420_888-vs-H264                                  |
| multimedia | `protocol`           | 스트리밍·프로토콜      | what-Is-FFmpeg, what-Is-RTSP-ONVIF                   |
| linux      | `system`             | 시스템 관리            | Check_System_configuration, Batch_vs_Service         |
| software   | `ddd`                | 도메인 주도 설계       | domain-driven-design-chapter1 (+ 이후 DDD 글)        |
| ui_ux      | `laws-of-ux`         | UX 법칙                | Fitts, Doherty, Peak-End, Postel, Tesler             |
| ui_ux      | `ux-practice`        | UX 실무·윤리           | Design_meets_Psychology, Power_needs_Responsibility  |

> 글이 1~2개뿐인 대분류(linux, multimedia, software)는 소분류를 지금 붙이지 않고 글이 늘어날 때 추가해도 된다. 소분류는 선택 필드이므로 혼용해도 문제없다.

---

## 5. 작업 순서

0. **(사전) `docs/` 빌드 제외**: 현재 `_config.yml`의 `exclude`에 `docs`가 없어 이 문서가 `/docs/01-plan/...` 경로로 **공개 사이트에 그대로 배포**된다. `exclude`에 `docs` 추가.
1. `_data/categories.yml` 생성, `_includes/category-label.html`을 데이터 기반으로 변경(폴백 유지) + `_includes/subcategory-label.html` 추가.
2. `_layouts/post-list.html`에 소분류 그룹핑 + h3 앵커 구현, `_sass/site.scss`에 h3 스타일 추가.
3. 기존 26개 글 front matter에 `subcategory` 추가 (§4 확정안 기준).
4. `_layouts/home.html` 홈 박스에 소분류 노출 + 데스크톱 레이아웃 확인.
5. (선택) `post.html` 브레드크럼, `nav.html` 소분류 메뉴.
6. 검증 (§6).

## 6. 검증 기준

- [ ] `bundle exec jekyll build` 경고/에러 없음
- [ ] `/posts/`에서 대분류 → 소분류 → 글 순서로 표시, 소분류 없는 글도 누락 없이 표시 (전체 글 수 26 유지)
- [ ] 기존 앵커 `/posts/#network` 등 동작, 신규 `/posts/#network-tcp-ip` 동작
- [ ] 프로젝트 글(`project` 필드)은 여전히 목록에서 제외
- [ ] 기존 글 URL 변경 없음 (`_site` 내 경로 비교)
- [ ] 홈 박스가 데스크톱/모바일에서 화면을 벗어나지 않음
- [ ] GitHub Pages 기본 빌드(Jekyll 3.x)에서도 동작 — 사용 Liquid 필터(`group_by`, `where_exp`, `slugify`)는 3.x 지원 범위

## 7. 리스크 / 결정 필요 사항

| 항목              | 내용                                                                                                                                        |
| ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| 소분류 체계 확정  | §4 초안 검토 완료 (특히 python, ui_ux 분류 기준)                                                                                            |
| 홈 박스 노출 범위 | 소분류까지 보이도록                                                                                                                         |
| 배포 환경         | 로컬은 Jekyll 4.3, 배포는 GitHub Pages 기본(3.x)으로 추정 → 로컬에서만 되는 문법 사용 금지. 장기적으로 GitHub Actions 배포 전환도 고려 가능 |
| 미분류 그룹 명칭  | 소분류 없는 글을 h3 없이 둘지, "기타" 제목을 붙일지                                                                                         |
