---
title: "[WEB] HTTP 분석 및 이해 : 2. URI와 Web Browser (박기웅)"
excerpt: "백엔드 개발자의 기본 소양을 차곡차곡 쌓아봅시다"
date: 2025-10-26
author: kiwoong-park
author_profile: true
layout: single
---

### 순공부 시간 : 25.10.26 1045 ~ 1153 (68분)

### 학습 목표 : URI 개념 및 웹 브라우저의 요청 흐름 이해

### 학습 내용 요약

- URI는 자원을 고유하게 식별할 수 있는 통일된 방식의 식별자로, URN 과 URL을 포함하는 개념
- URL의 구조 : scheme://[userinfo@]host[:port][/path][?query][#fragment]
  - scheme : 프로토콜명
  - userinfo : 사용자 정보(생략 가능)
  - host : 호스트명
  - port : 포트번호(생략 가능)
  - path : 계층적 구조의 리소스 경로
  - query : key, value 형태의 호스트에 제공하는 파라미터; 쿼리 파라미터 혹은 쿼리 스트링
  - fragment : html 내부 북마크 사용
- 웹 브라우저 요청 흐름
  1. 웹 브라우저가 사용자가 작성한 URL을 해석하여 HTTP 요청 메시지 생성
  2. 소켓 라이브러리를 통해 TCP/IP 패킷으로 만들어 HTTP 요청 메시지를 포함하여 패킷 전송
  3. 서버쪽 소켓을 통해 HTTP 요청 메시지 수신 및 응답 패킷 생성
  4. 응답 패킷을 다시 클라이언트에 전달
  5. 클라이언트 웹 브라우저에서 렌더링

### 학습 내용

## URI

URI (Uniform Resource Identifier)로

- Uniform : 리소스를 식별하는 통일된 방식
- Resource: 자원, URI 로 식별할 수 있는 모든 것(제한 없음)
- Identifier: 식별자로 다른 항목과 구분하는데 필요한 정보
- URI는 URL 과 URN을 포함하는 개념

URL : Locator → 리소스가 있는 위치를 지정

URN : Name → 리소스에 이름 부여

- 위치는 변할 수 있지만 이름은 변하지 않음.
- 하지만, URN 이름만으로는 실제 리소스를 찾을 수 있는 방법이 보편화되지 않음

### URL

전체 문법

`scheme://[userinfo@]host[:port][/path][?query][#fragment]`

- [ ] 대괄호 내용은 생략 가능!
- 예시
  - http://user.password@www.glasscom.com:80/dir/file1.html
  - https://google.com:443/search?q=hello&hl=ko

Scheme

- 주로 프로토콜로 사용
- 프로토콜 : 어떤 방식으로 자원에 접근할 것인가 하는 약속 규칙
  - http, ftp, sftp, file, mailto

UserInfo

- URL에 사용자 정보를 포함해서 인증
- 거의 사용하지 않음

Host

- 웹서버명 혹은 호스트명
- 도메인명 또는 IP 주소를 직접 사용

Port

- 포트번호
- 일반적으로 생략, 생략 시 프로토콜마다 지정된 번호

Path

- 리소스 경로, 계층적 구조

Query

- key = value 형태
- ? 로 시작, & 추가 가능 → `?keyA=valueA&keyB=valueB`
- query parameter, query string 등으로 불림, 웹서버에 제공하는 파라미터, 문자 형태

Fragment

- html 내부 북마크 등에 사용
- 서버에 전송하는 정보 아님

## 웹 브라우저의 요청 흐름

- 웹 브라우저 (클라이언트) 에서 HTTP 요청 메시지 생성
  - DNS 조회 → IP 주소 얻고, 포트번호를 지정하여 메시지 생성
  - HTTP 요청 메시지 : GET /search?q=hello&hl=ko HTTP1.1, Host: www.google.com
- HTTP 요청 메시지 전송
  1. 웹 브라우저가 HTTP 메시지 생성
  2. 소켓 라이브러리를 통해 전달
     1. TCP/IP 연결(IP, PORT)
     2. 데이터 전달
  3. TCP/IP 패킷 생성, HTTP 메시지 포함
- 패킷 생성 : 출발지, 목적지 (IP 주소, Port 번호) + 전송 데이터
- 요청 패킷 전달 ⇒ 서버쪽 소켓을 통해 HTTP 기반 통신
- 응답 패킷 생성 ⇒ 응답 패킷 전달 ⇒ 응답 패킷 수신 ⇒ 브라우저에서 렌더링
