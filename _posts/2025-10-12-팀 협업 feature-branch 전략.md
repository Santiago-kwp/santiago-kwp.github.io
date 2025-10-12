---
title: "Git 협업 : feature-branch 전략 (with issue 사용하기)"
excerpt: "git으로 팀 협업을 위한 feature-branch 전략입니다."
date: 2025-10-12
author: kiwoong-park
author_profile: true
layout: single
---
## Git feature-branch 전략

### 참조 영상
<iframe width="930" height="522" src="https://www.youtube.com/embed/qJOfzcMG_hs" title="깃&깃헙 브랜치 3개로 협업하기(주니어개발자 팀프로젝트)" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>

### 기본 세팅

```bash
git clone 원격저장소

git branch develop
git branch
git push --set-upstream origin develop
```

- 기본 브랜치를 `main` 에서 `develop`로 아래와 같이 github settings에서 변경하자

![image.png](/assets/images/gitDefaultBranchSetting.png)

- `collaborators` 에서 팀원들 초대!!

### 작업 시작

- 작업을 시작할 때는 먼저 `issues`를 오픈함 → New issue
- 실제 우리가 얘기하는 문제 상황에서의 이슈가 아니라 내가 만드는 모든 것을 이슈로 함 (Ex. 제목:  00 컴포넌트 개발, 내용 : ~~ 개발하겠습니다.)
- Assignees ⇒ assign yourself : 나 자신 할당
- 적절한 라벨 달기 : ex. 컴포넌트
- Submit new issue ⇒ 이슈 번호가 붙음 ⇒ 이후 나의 PR에서 닫을 예정
### Git branch 분기

```bash
git branch feat/컴포넌트
git branch
git switch feat/컴포넌트
```

- 로컬에서 작업하고 자기 브랜치(feat/컴포넌트)를 원격(origin)으로 푸쉬

```bash
git add .
git commit -m "커밋 메시지 : 컨벤션에 따라서"
git push --set-upstream origin feat/컴포넌트
```

![image.png](/assets/images/gitOpenPullRequest.png)

- 리뷰어(`Reviewers`) 및 라벨(`Labels`) 적용
- 팀에서 몇 명이 코드를 `approve`를 해줘야 `merge`를 할 수 있게 만들 수 있음
- `merge`가 되었으면, feat/컴포넌트 브랜치를 원격에서 먼저 삭제함 (`delete branch`)
- 로컬에서 브랜치가 남아있으므로 로컬에서도 삭제함!
    - 이때 자신의 브랜치에 있으면 자신의 브랜치 삭제가 안되므로 `develop`로 브랜치 변경 후(`switch`) 삭제하자

```bash
git switch develop
git branch -D feat/컴포넌트
git branch
```

### 사이클 다시 돌리기

- 로컬의 develop 브랜치 최신 상태 확인하기
    - git fetch를 통해 원격의 develop과 로컬의 develop 상태 비교
    - git pull를 통해 로컬의 develop를 원격의 develop로 변경하기

```bash
git fetch 
git pull
```

- github에서 이슈 만들기 (`New issues`)

- 다시 작업하기 위해 브랜치 만들기

```bash
git branch feat/로그인창
git switch feat/로그인창
```

- 작업 후 add → commit → push (origin에 푸쉬)

```bash
git add .
git commit -m "로그인창 구현"
git push --set-upstream origin feat/로그인창
```

- open a pull request 후에 이슈 닫기를 하려면 아래의 그림처럼 `closes #이슈번호` 를 PR 내용에 작성한다.
    
    ![image.png](/assets/images/gitClosesIssue.png)
    
- create pull request를 날리게 되면 아래와 같이 오른쪽 Development에 '이 PR이 merge에 성공하면 로그인창이라는 오픈된 이슈를 닫을거에요' 라고 함.
(Successfully ~~ close these issues)

![image.png](/assets/images/gitDevelopmentClosesIssue.png)

- 팀원들의 코드 리뷰 후 성공적으로 `merge`가 되었다면 아래 그림과 같이 이슈가 닫힌 것(`Closed`)을 확인할 수 있다.

![image.png](/assets/images/gitClosedIssue.png)

### 🤔 반드시 기억할 내용
### ⚠️ (중요) 항상 내 로컬에 있는 develop와 원격에 있는 develop가  똑같은지 로컬이 최신 상태인지 `git fetch` 로 확인하자

### ‼️ (중요) `git pull` 은 항상 `develop` 브랜치에서 한다

---

### 🧩 있을 법한 상황 : develop 브랜치에서 작업하기?!

- 원래는 `develop` 브랜치에서 `feat` 브랜치를 만들고 작업하려 했는데
- 실수로 `develop` 브랜치에서 작업(커밋 or 작업만)을 해버렸어
- 이제 그 커밋들을 `feat` 브랜치로 옮기고 싶어

---

### ✅ 해결 방법: `git switch` + `git cherry-pick`

### 1️⃣ feat 브랜치 생성

```bash
git switch -c feat

```

- 현재 커밋된 상태에서 `feat` 브랜치를 새로 만듦

### 2️⃣ develop 브랜치로 돌아가서 커밋 로그 확인

```bash
git switch develop
git log

```

- 실수로 develop에 커밋한 커밋 해시를 확인 (예: `abc123`)
### 3️⃣ feat 브랜치로 돌아가서 cherry-pick

```bash
git switch feat
git cherry-pick abc123

```

- 해당 커밋을 `feat` 브랜치로 복사함

### 4️⃣ develop 브랜치에서 커밋 되돌리기 (선택)

```bash
git switch develop
git reset --hard HEAD~1

```

- 실수로 커밋한 내용을 되돌림 (주의: `-hard`는 변경사항을 삭제하므로 백업 필요!)

---

### 🧠 대안: `git stash` + `git switch`

만약 커밋하지 않고 작업만 했던 경우라면:
```bash
git stash
git switch feat
git stash pop

```

- 작업 내용을 `feat` 브랜치로 옮길 수 있다.

---

### 📌 요약

| 상황 | 해결 방법 |
| --- | --- |
| 커밋까지 했음 | `cherry-pick`으로 feat 브랜치에 복사 |
| 커밋 안 했음 | `stash`로 feat 브랜치에 옮기기 |
| develop에서 커밋 제거 | `reset` 또는 `revert` 사용 |