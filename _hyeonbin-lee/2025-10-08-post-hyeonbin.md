---
title: "[WMS] 1차 프로젝트 README(이현빈)"
excerpt: 1차 프로젝트에서 로그인, 회원관리 기능을 담당하면서 주로 고민했던 내용을 중심으로 정리했습니다.
date: 2025-10-08 21:10 +0900
author: hyeonbin-lee
author_profile: true
layout: single
---

## 1. 패키지 구조

```
📦src
 ┣ 📂config
 ┃ ┣ 📂user
 ┃ ┃ ┣ 📜dbinfo.properties
 ┃ ┃ ┗ 📜DBUtil.java
 ┃ ┣ 📜dbinfo.properties
 ┃ ┗ 📜DBUtil.java
 ┣ 📂constant
 ┃ ┗ 📂user
 ┃     ┣ 📂validation
 ┃     ┃ ┣ 📜InputValidCheck.java
 ┃     ┃ ┣ 📜LoginValidCheck.java
 ┃     ┃ ┣ 📜MenuNumberValidCheck.java
 ┃     ┃ ┗ 📜UserManagementValidCheck.java
 ┃     ┣ 📜InputMessage.java
 ┃     ┣ 📜LoginPage.java
 ┃     ┣ 📜ManagerPage.java
 ┃     ┣ 📜MemberPage.java
 ┃     ┣ 📜UserPage.java
 ┃     ┗ 📜WMSPage.java
 ┣ 📂controller
 ┃ ┣ 📂user
 ┃ ┃ ┣ 📜AbstractUserManageMenu.java (abstract class)
 ┃ ┃ ┣ 📜LoginMenu.java
 ┃ ┃ ┣ 📜ManagerManageMenu.java
 ┃ ┃ ┣ 📜MemberManageMenu.java
 ┃ ┃ ┣ 📜UserManageMenu.java (interface)
 ┃ ┃ ┗ 📜WMSMenu.java
 ┃ ┗ 📜MainMenu.java
 ┣ 📂domain
 ┃ ┗ 📂user
 ┃     ┣ 📜Manager.java
 ┃     ┣ 📜Member.java
 ┃     ┗ 📜User.java
 ┣ 📂exception
 ┃ ┗ 📂user
 ┃     ┣ 📜FailedToAccessLoginDataException.java
 ┃     ┣ 📜FailedToAccessUserDataException.java
 ┃     ┣ 📜FailedToApproveUserException.java
 ┃     ┣ 📜FailedToAssignCargoToManagerException.java
 ┃     ┣ 📜FailedToDeleteUserException.java
 ┃     ┣ 📜FailedToDeleteUserRoleException.java
 ┃     ┣ 📜FailedToLoginException.java
 ┃     ┣ 📜FailedToReadUserException.java
 ┃     ┣ 📜FailedToRegisterException.java
 ┃     ┣ 📜FailedToRestoreUserException.java
 ┃     ┣ 📜FailedToUpdateUserException.java
 ┃     ┣ 📜FailedToUpdateUserRoleException.java
 ┃     ┣ 📜InvalidUserDataException.java
 ┃     ┣ 📜NotAllowedUserException.java
 ┃     ┗ 📜UserNotFoundException.java
 ┣ 📂model
 ┃ ┗ 📂user
 ┃     ┣ 📜LoginDAO.java
 ┃     ┣ 📜ManagerDAO.java
 ┃     ┣ 📜MemberDAO.java
 ┃     ┗ 📜UserDAO.java(interface)
 ┣ 📂service
 ┃ ┗ 📂user
 ┃     ┣ 📜LoginService.java (interface)
 ┃     ┣ 📜LoginServiceImpl.java
 ┃     ┣ 📜LogoutService.java (interface)
 ┃     ┣ 📜ManagerService.java (interface)
 ┃     ┣ 📜ManagerServiceImpl.java
 ┃     ┣ 📜MemberService.java (interface)
 ┃     ┣ 📜MemberServiceImpl.java
 ┃     ┗ 📜UserService.java (interface)
 ┗ 📂view
     ┣ 📂user
     ┃ ┣ 📜ConsoleView.java
     ┃ ┗ 📜Main.java
     ┗ 📜Main.java
```

---

## 2. 트러블 슈팅

### 전체 코드

- [로그인/회원관리 기능 구현 코드 링크](https://github.com/HyeonBin2379/CoffeeWMS_1stPJ_SSG9th/tree/feature/hyeonbin/src){:target="_blank"}

### 회원가입 완료 시 회원 등록 및 권한 부여

- 입력한 회원정보는 우선적으로 Users(회원정보) 테이블에 추가된다.
- Users 테이블에 신규 가입한 회원의 정보가 추가되면, `회원가입유형` 속성값에 따라 `Members`(일반회원) 또는 `Managers`(관리자) 테이블 중 1개의 테이블에도 신규 회원 정보를 추가하는 AFTER INSERT 트리거를 실행하는 방식으로 해결했다.

```sql
# 회원가입 기능을 처리하기 위한 프로시저
DROP PROCEDURE IF EXISTS register;
DELIMITER $$
CREATE PROCEDURE register(
    IN id varchar(15),
    IN pwd varchar(20),
    IN name varchar(10),
    IN phone varchar(13),
    IN email varchar(30),
    IN company_code char(12),
    IN address varchar(255),
    IN register_type varchar(10),
    OUT affected BOOLEAN
)
BEGIN
    DECLARE approval varchar(10);
    DECLARE found_count int;
    DECLARE admin_count int;

    -- 중복된 아이디가 있는지 확인
    select count(user_id) into found_count from users where user_id = id and user_approval = '승인완료';
    select count(user_id) into admin_count from users where user_type = '총관리자';

    if (found_count > 0 or (register_type = '총관리자' and admin_count > 0)) then
        set approval = '미승인';		-- 이미 승인완료된 아이디가 존재하면 미승인 처리
    else
        set approval = '승인완료';	-- 사용가능한 아이디면 승인완료처리
    end if;

    -- 회원정보 추가
    insert into users(
        user_id, user_approval, user_pwd, user_name, user_phone, user_email,
        user_company_code, user_address, user_join_date, user_type
    ) values(id, approval, pwd, name, phone, email,
             company_code, address, now(), register_type);

    if exists(select member_id from members where member_id = (select user_id from users where user_id = id and user_approval = '승인완료')) then
        set affected = 1;
    elseif exists(select manager_id from managers where manager_id = (select user_id from users where user_id = id and user_approval = '승인완료')) then
        set affected = 1;
    else
        set affected = 0;
    end if;
END $$
DELIMITER ;

-- 회원가입용 트리거 추가: 새로운 회원정보 추가 시 자동으로 일반회원, 관리자 권한 중 하나를 부여
DROP TRIGGER IF EXISTS authorize;
DELIMITER $$
CREATE TRIGGER authorize
	AFTER INSERT ON users FOR EACH ROW
BEGIN
	IF (new.user_approval = '승인완료' and new.user_type = '일반회원') then
		insert into members
		values(
			new.user_id, new.user_pwd, new.user_name, new.user_phone, 
			new.user_email, new.user_company_code, new.user_address,
			false, now(), date_add(now(), interval 1 year))
		on duplicate key update
		    member_pwd = NEW.user_pwd, member_company_name = NEW.user_name, member_phone = NEW.user_phone,
		    member_email = new.user_email, member_company_code = NEW.user_email, member_address = NEW.user_address,
		    member_login = false, member_start_date = now(), member_expired_date = date_add(now(), interval 1 year);
	
	-- 가입승인된 회원의 가입유형이 창고관리자 또는 총관리자면 관리자 권한을 부여
	ELSEIF (new.user_approval = '승인완료' and new.user_type like '%관리자') then
		insert into managers
		values(
			new.user_id, new.user_pwd, new.user_name, new.user_phone, 
			new.user_email, false, now(), new.user_type
		)
		on duplicate key update
             manager_pwd = NEW.user_pwd, manager_name = NEW.user_name, manager_phone = NEW.user_phone,
             manager_email = new.user_email, manager_login = false, manager_hire_date = now(), manager_position = NEW.user_type;
	END IF;
END $$
DELIMITER ;
```

- 위와 같이 Users 테이블과는 별도로 Members, Managers 테이블을 만든 이유는, 트리거를 통해 승인 완료된 회원만 Members나 Managers에 저장될 수 있게 함으로써 WMS의 다른 기능을 실행할 때 미승인된 회원정보를 조회하게 되는 상황을 원천 차단하기 위해서였다.
- 하지만 위와 같은 설계에서는 동일한 회원정보를 여러 개의 물리적인 테이블에 따로 저장하게 되었다. 이로 인해, 회원정보 수정, 회원 권한 복구/삭제 기능을 구현할 때는 Users 테이블의 데이터가 변경될 때 Members나 Managers 테이블에서도 변경 사항을 자동으로 반영해주는 트리거를 추가해야 한다는 번거로움이 존재했다.
- 결국 문제의 핵심은 회원정보의 일관성을 유지하면서도, 테이블 조회 시 특정 데이터만 선별적으로 유지하는 것이었다. 나중에서야 알게 되었지만, 내가 고민했던 문제에 관해서는 아래의 이유들로 인해 DB의 뷰를 활용하는 것이 최선의 방법이었다.
    - DB의 뷰는 실제 테이블에 저장된 데이터에 바탕을 둔 가상의 테이블
        - 뷰 자체에 데이터가 저장되는 것은 아니기 때문에, 뷰를 조회한다는 것은 결국 원본 테이블에 관한 필터링을 수행하는 것과 유사하므로 조회 쿼리에 기반한 기존의 로그인 로직을 개선할 수 있다.
        - 뷰는 별도의 트리거/쿼리문을 사용하지 않아도 원본 테이블에서의 변경사항을 자동으로 반영할 수 있으므로 회원 수정/탈퇴 로직을 개선하는 것도 가능하다.
    - 1개의 물리적인 테이블에 대해, 테이블의 일부 속성 또는 특정 조건을 충족하는 행으로만 구성된 여러 개의 뷰를 사용할 수 있다.
        - 회원정보에 관한 실제 테이블은 Users만 생성하고, 승인완료된 회원의 정보는 그 회원가입유형에 따라 Members, Managers라는 뷰를 조회하는 방식으로 개선할 수 있다.
    - 뷰는 원본이 되는 테이블을 그대로 노출하지 않으므로 보안성을 강화할 수 있다.
        - 원본인 Users 테이블에 `user_login` 속성을 추가하지만, Members, Managers 뷰를 구성하는 속성에는 해당 속성을 사용하지 않음으로써 일반회원/관리자 정보 조회 시 이 속성을 은닉할 수 있다. (아래의 CREATE VIEW문 참조)
- 1차 프로젝트를 리팩토링할 때는 위의 개선안을 실제로 반영하지 못했지만, 2차 프로젝트부터는 뷰를 활용해서 조회 쿼리를 개선할 수 있을 것 같다.
(만약 Members, Managers 테이블을 뷰로 대체한다면 대략적으로 아래와 같이 사용할 수 있을 것 같다.)

```sql
-- 일반회원 조회용 뷰 생성
CREATE VIEW members AS
SELECT 
	user_id as member_id, 
	user_pwd as member_pwd, 
	user_name as member_company_name, 
	user_phone as member_phone, 
	user_email as member_email,
	user_company_code as member_company_code,
	user_address as member_address,
	user_join_date as member_contract_start,
	date_add(member_contract_start, interval 1 year) as member_contract_expired
FROM users
WHERE user_approval = '승인완료' and user_type = '일반회원' 
		and user_login = true;

-- 총관리자/창고관리자 조회용 뷰 생성
CREATE VIEW managers AS
SELECT
	user_id as member_id, 
	user_pwd as member_pwd, 
	user_name as member_company_name, 
	user_phone as member_phone, 
	user_email as member_email,
	user_join_date as manager_hire_date
	user_type as manager_position
FROM users
WHERE user_approval = '승인완료' and user_type like '%관리자'
		and user_login = true;
```

### 공통 기능과 관리자 전용 회원관리 기능의 구분

- 회원 관리 기능을 구현할 때 가장 많이 고민했던 부분은, 로그인한 회원의 권한에 따라 사용 가능한 기능을 구분하면서도 공통 기능을 제공할 수 있도록 하는 것이었다.
- 그리고 일반회원용 회원관리 기능과 관리자 전용 회원관리 기능은 큰 틀에서 조회, 수정, 삭제라는 3가지 기능을 사용한다는 점은 동일했지만, 그 의미상으로는 다음과 같은 차이가 있었다. 이 차이로 인해 관리자용 메뉴에서 서브메뉴를 제공하는 것이 불가피해졌다.
    - 일반회원용 메뉴: 자신에 관한 조회, 수정, 탈퇴
    - 관리자용 메뉴: 자신에 관한 조회, 수정, 탈퇴 + 관리자 전용 기능
- 이에 대한 방법을 고민하던 중, 로그인 기능을 실행했을 때 최종적으로 반환받을 `Manager` 클래스와 `Member` 클래스가 `User` 클래스를 상속받도록 구현했던 방식을 재활용하기로 했다. 결국 큰 틀에서는 동일한 메서드 시그니처의 구현부가 달라지는 것이었기 때문에, 아래와 같이 인터페이스를 사용하여 공통 기능을 정의한 다음 이를 이를 바탕으로 일반회원용 회원관리 기능과 관리자용 회원관리 기능을 구현했다.

![usermanagemenu.png](/assets/images/usermanagemenu.png)

- 위와 같이, `UserManageMenu` 인터페이스에서는 회원 조회, 수정, 삭제 기능을 수행할 메서드를 각각 `read()`, `update()`, `delete()`로 정의했다. 그런 다음, `MemberManageMenu` 클래스와 `ManagerManageMenu` 에서 구현 양상을 달리하는 방식으로 일반회원용 기능과 관리자용 기능을 구분할 수 있었다.

(아래의 코드는 회원관리 기능을 사용하기 위한 전체적인 로직과, 일반회원용 기능과 관리자용 기능에서 `read()` 메서드를 각각 구현한 코드)

```java
// AbstractUserManageMenu 클래스의 run() 메서드: 회원관리 기능의 전체 로직
@Override
public boolean run() {
    boolean quitMenu = false;
    boolean hasLogout = false;

    while (!quitMenu && !hasLogout) {
        try {
            printMenu();
            String menuNum = input.readLine();
            menuNumberValidCheck.checkMenuNumber("^[1-4]", menuNum);
            switch (menuNum) {
                case "1" -> read();
                case "2" -> update();
                case "3" -> hasLogout = delete();
                case "4" -> quitMenu = exitMenu();
            }
        } catch (IOException
                 | IllegalArgumentException
                 | InvalidUserDataException
                 | FailedToReadUserException
                 | FailedToUpdateUserException
                 | FailedToDeleteUserException
                 | FailedToAccessUserDataException e) {
            System.out.println(e.getMessage());
        }
    }
    return hasLogout;
}
```

```java
// MemberManageMenu에서 구현한 read() 메서드
@Override
public void read() {
    System.out.println(UserPage.CURRENT_USER_SELECT);
    currentMember = (Member) memberService.findMyDetails(currentMember.getId());
    MemberPage.details(currentMember);
}
```

```java
// ManagerManageMenu에서 구현한 read() 메서드
@Override
public void read() throws IOException {
    boolean quitRead = false;
    while (!quitRead) {
        try {
            String menuNum = consoleView.promptAndRead(ManagerPage.MANAGER_SELECT_TITLE.toString());
            menuNumberValidCheck.checkMenuNumber("^[1-4]", menuNum);
            switch (menuNum) {
                case "1" -> readOneUser();
                case "2" -> readAllUsers();
                case "3" -> readUsersByRole();
                case "4" -> quitRead = quit();
            }
        } catch (IllegalArgumentException
                 | NotAllowedUserException
                 | FailedToReadUserException e) {
            System.out.println(e.getMessage());
        }
    }
}
```

---

## 3. 로그인/회원관리 기능에서 개선할 사항

- 로그인/로그아웃 방식 수정 - 세션과 로그 테이블의 활용
- Member 또는 Manager 객체 생성 시 빌더 패턴 적용(완료)
- 비밀번호, 핸드폰번호, 이메일 등의 민감정보 마스킹(일부 완료)
    - [정보보호 및 개인정보보호 관리체계(ISMS-P) 인증기준 안내서(2023.11.) 106p 참조](https://isms.kisa.or.kr/main/ispims/notice/)
    - 주요 개인정보 마스킹 규칙
        - 이름: 이름의 가운데 글자 마스킹
        ex) `홍*동`, `이*`, `선**녀`
        - 비밀번호: 실제 비밀번호 길이에 관계없이 문자열 `********`로 대체
        - 연락처(휴대폰번호): 가운데 4자리 마스킹
        ex) `010-****-1234`
        - 이메일 주소: ID 중 앞 2자리를 제외한 나머지
    - 마스킹 규칙은 창고관리자가 현재 WMS에 가입된 일반회원 목록을 조회할 때만 적용