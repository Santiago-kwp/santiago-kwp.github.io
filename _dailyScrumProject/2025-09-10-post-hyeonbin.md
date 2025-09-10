---
title: "JDBC를 활용한 CRUD 구현 코드 분석(이현빈)"
excerpt: 오늘 강의시간에 구현했던 JDBC 코드를 분석하고 정리했습니다.
date: 2025-09-10 19:00 +0900
author: hyeonbin-lee
author_profile: true
layout: single
---

## 순 공부 시간

- 2025.9.10 16:40 ~ 2025.9.10 19:05(134분)

## 학습 목표

- JDBC를 활용하여 DB에 저장된 테이블에서의 데이터 추가, 조회, 갱신, 삭제를 수행하는 방법 숙지하기

---

## 1. 데이터 조작어(DML, Data Manipulation Language)

### 개요

- 데이터베이스에 저장된 데이터를 추가, 조회, 수정, 삭제하는 작업을 수행하는 SQL문을 의미
- 좁은 의미로는 데이터 변경(추가, 수정, 삭제) 작업을 수행하는 SQL문을 지칭

| 기능 | SQL |
| --- | --- |
| 테이블 행 추가 | INSERT |
| 1개 이상의 행 조회 | SELECT |
| 테이블 행 수정 | UPDATE |
| 테이블 행 삭제 | DELETE |

## 데이터 질의어(DQL, Data Query Language)

- 데이터베이스에 저장된 데이터를 조회하기 위해 사용하는 SELECT문을 다른 DML 명령어와 구별하기 위한 명칭

---

## 2. JDBC에서의 CRUD

### DB와의 연동 수행

- ‘DBUtil’ 클래스에서 수행
- 싱글톤 패턴을 구현하기 위해 static 클래스를 사용
- MySQL Driver를 통해 MySQL과 연동하기 위해 프로퍼티 파일을 활용

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBUtil {
		// .properties 파일에 저장된 리소스 정보를 저장
    private static ResourceBundle bundle;

		// 싱글톤 패턴을 적용하기 위해 static 클래스 사용
    static {
        bundle = ResourceBundle.getBundle("util.dbinfo");
        try {
            Class.forName(bundle.getString("driver"));
            System.out.println("드라이버 로딩 성공");
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로딩 실패");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
		        // 사용할 DB의 url, 사용자명, 비밀번호를 입력하여 DB에 연결
            return DriverManager.getConnection(
                    bundle.getString("url"),
                    bundle.getString("username"),
                    bundle.getString("password")
            );
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
```

### Board 클래스

- getter/setter 구현 부분은 생략

```java
import java.util.Date;

public class Board {

    private int bno;
    private String btitle;
    private String bcontent;
    private String bwriter;
    private Date bdate;

    public Board() {}

    public Board(int bno, String bTitle, String bContent, String bWriter, Date bDate) {
        this.bno = bno;
        this.btitle = bTitle;
        this.bcontent = bContent;
        this.bwriter = bWriter;
        this.bdate = bDate;
    }

    // getter, setter 구현 부분(생략)
    // ...

		// 데이터 수정/삭제 기능 구현의 편의를 위해 equals()를 재정의
    @Override
    public boolean equals(Object o) {
        if (o instanceof Board that){
            return bno == that.bno;
        }
        throw new ClassCastException("비교할 대상의 타입이 올바르지 않습니다.");
    }

    @Override
    public String toString() {
        return "Board{" +
                "bno=" + bno +
                ", btitle='" + btitle + '\'' +
                ", bcontent='" + bcontent + '\'' +
                ", bwriter='" + bwriter + '\'' +
                ", bdate=" + bdate +
                '}';
    }
}

```

### JDBC를 활용한 CRUD 수행 방식

- `BoardDAO` 클래스의 메서드를 통해 DB에게 쿼리 요청을 전달하면서, 데이터 변경 발생 시 `List<Board> boards` 에도 반영
- 기본 절차는 아래의 순서대로 진행
    1. DB와의 연결 수행
    2. 요청할 SQL 쿼리 작성
    3. 쿼리를 DB로 전달하기 위한 `PreparedStatement` 객체 생성
        1. `PreparedStatement` 객체는 한번 생성할 시 재사용 가능
    4. SQL 쿼리에서 사용할 실제 값 세팅
    5. DB 서버에서 처리한 쿼리의 결과를 반환받아 후속 처리 진행
        1. 이때의 후속처리는 리스트에서의 작업을 의미

### 데이터 추가

- INSERT문을 사용

```sql
# 테이블의 모든 속성에 대해 실제 값을 설정할 경우
INSERT INTO 테이블명 VALUES(속성값1, 속성값2, ...);

# 테이블의 일부 속성에 대해서만 실제 값을 설정할 경우
INSERT INTO 테이블명(속성1, 속성2, ... 속성N) 
		VALUES(속성값1, 속성값2, ... 속성값N);
```

- JDBC를 사용한 데이터 추가 쿼리
    - 리팩토링 과정에서 쿼리문 작성을 DB 연결보다 먼저 수행하도록 순서를 변경
    - 스레드 단위에서도 게시글 리스트에서의 데이터 추가 작업이 진행되는 동안 다른 스레드가 리스트에 접근하지 않도록 처리

```sql
public boolean createBoard(Board board) {
        // 리스트에 이미 저장된 데이터가 존재하면 기존 리스트를 사용하여 아래 작업을 진행
        // 1. 쿼리 생성
        String sql = "INSERT INTO boardTable(btitle, bcontent, bwriter, bdate) VALUES(?, ?, ?, now())";

        // 2. DB와 연결한 후, insert 쿼리를 담아 DB서버로 요청하기 위한 PreparedStatement 생성
        // - 기본키가 auto_increment 옵션이 적용된 정수값이면, 아래와 같이 두번째 인자를 설정
        // - DBUtil.getConnection()은 싱글톤 객체를 반환
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt
                     = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 3. SQL문에 사용할 실제 값 세팅
            pstmt.setString(1, board.getBtitle());
            pstmt.setString(2, board.getBcontent());
            pstmt.setString(3, board.getBwriter());

            // 4. 서버에서 처리된 insert 쿼리의 결과값을 처리
            synchronized (boards) {
                int affected = pstmt.executeUpdate();
                boolean ok = affected > 0;
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    // 생성된 PK를 Board 객체에 반영한 후 리스트에 저장
                    if (rs.next() && ok) {
                        board.setBno(rs.getInt(1));
                        boards.add(board);
                    }
                }
                return ok;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
```

### 데이터 조회

- 다음의 SELECT문을 사용

```sql
# 특정 BNO값을 가진 테이블의 행만 검색(? 부분은 임의값)
SELECT * FROM boardTable WHERE bno = ?;

# 테이블의 모든 행을 조회
SELECT * FROM boardTable;
```

- JDBC를 사용하여 테이블의 데이터 1개를 검색
    - 이미 전체 게시글 데이터는 `boards` 에 리스트 형태로 저장되어 있으므로, 스트림을 활용하여 코드를 간소화

```sql
public Board searchOne(int bno) {
        // 1. 쿼리 생성
        String sql = "SELECT * FROM boardTable WHERE bno = ?";

        // 2. Connection 성공 후, select 쿼리를 담아 DB서버로 요청하기 위한 PreparedStatement 생성
        // try-with-resources문을 사용하기 위해 순서가 다소 변경됨
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // 3. SQL문에 사용할 실제 값 세팅
            pstmt.setInt(1, bno);

            // 4. 서버에서 처리된 select 쿼리의 결과값을 처리
            try (ResultSet rs = pstmt.executeQuery()) {
                // 검색된 테이블의 행에 관한 Board 객체를 반환
                if (rs.next()) {
                    int targetBno = rs.getInt("bno");
                    return boards.stream()
                            .filter(board -> targetBno == board.getBno())
                            .findAny()
                            .get();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
```

- JDBC를 사용한 전체 데이터 조회 쿼리
    - 최초 1회만 조회 결과를 리스트 형태로 변환
    - 리스트에 이미 데이터가 존재하면 기존 리스트를 반환

```sql
public List<Board> searchAll() {
        // 자바 코드에서 bno 순 내림차순 정렬 수행
        Comparator<Board> comparator = Comparator.comparingInt(Board::getBno).reversed();

        // boards에 이미 데이터가 존재하면 정렬만 수행한 후 기존 리스트 반환
        if (boards != null && !boards.isEmpty()) {
            boards.sort(comparator);
            return boards;
        }

        // boards에 데이터가 존재하지 않으면 전체 조회 쿼리 실행(최초 1회만 실행)
        List<Board> boardList = new ArrayList<>();
        String sql = "SELECT * FROM boardTable";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 전체 조회 쿼리 실행 -> 조회 결과를 리스트로 변환
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Board board = new Board();

                board.setBno(rs.getInt("bno"));
                board.setBtitle(rs.getString("btitle"));
                board.setBcontent(rs.getString("bcontent"));
                board.setBwriter(rs.getString("bwriter"));
                board.setBdate(rs.getDate("bdate"));

                boardList.add(board);
            }
            // 리스트를 bno 기준으로 내림차순 정렬
            boardList.sort(comparator);

            return boardList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // DB에 데이터가 없는 경우에도 빈 리스트를 반환해야 함
        return boardList;
    }
```

### 데이터 수정

- 아래의 UPDATE문을 사용
    - 게시글 수정 시 작성자 닉네임도 변경 가능하다고 가정
    - 게시글 수정 시간을 반영하기 위해 `bdate`도 현재 시간으로 다시 갱신

```sql
# 특정 게시글의 제목, 내용, 작성자를 변경
UPDATE boardTable 
SET 
	btitle = ?, 
	bcontent = ?, 
	bwriter = ?, 
	bdate = now() 
WHERE bno = ?
```

- JDBC를 활용한 특정 데이터 갱신 기능은 아래와 동일
    - 스레드 단위에서도 게시글 리스트에서의 데이터 갱신 작업이 진행되는 동안 다른 스레드가 리스트에 접근하지 않도록 처리

```java
public boolean updateBoard(Board newBoard) {
        // 리스트에 이미 저장된 데이터가 존재하면 기존 데이터를 사용하여 아래 작업을 진행
        // 1. 쿼리 생성
        String sql = "UPDATE boardTable SET btitle = ?, bcontent = ?, bwriter = ?, bdate = now() WHERE bno = ?";

        // 2. DB와 연결 수행 및 update 쿼리를 담아 DB서버로 요청하기 위한 PreparedStatement 생성
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 3. 값 세팅
            pstmt.setString(1, newBoard.getBtitle());
            pstmt.setString(2, newBoard.getBcontent());
            pstmt.setString(3, newBoard.getBwriter());
            pstmt.setInt(4, newBoard.getBno());

            // 4. 서버에서 처리된 update 쿼리의 결과값을 처리
            synchronized (boards) {
                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    // 리스트에서도 변경된 Board 객체를 찾아서 갱신
                    Board board = searchOne(newBoard.getBno());
                    int index = boards.indexOf(board);
                    boards.set(index, board);
                    return true;
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
```

### 데이터 삭제

- 아래의 DELETE문을 사용하여 구현

```sql
DELETE FROM boardTable WHERE bno = ?
```

- JDBC를 활용한 특정 게시글 삭제 기능은 아래와 같이 구현
    - 스레드 단위에서도 게시글 리스트에서의 데이터 삭제 작업이 진행되는 동안 다른 스레드가 리스트에 접근하지 않도록 처리

```sql
public boolean deleteBoard(int bno) {
        // 리스트에 저장된 데이터가 존재하면 기존 데이터를 사용하여 아래 작업을 진행
        // 1. 쿼리 생성
        String sql = "DELETE FROM boardTable WHERE bno = ?";

        // 2. DB 연결 및 update 쿼리를 담아 DB서버로 요청하기 위한 PreparedStatement 생성
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 3. 값 세팅
            pstmt.setInt(1, bno);

            // 4. 서버에서 처리된 delete 쿼리의 결과값을 처리
            synchronized (boards) {
                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    // 리스트에서도 게시물을 삭제
                    Board board = searchOne(bno);
                    boards.remove(board);
                    return true;
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
```

### BoardDAO 전체 구현 코드

* [BoardDAO 전체 구현 코드](https://github.com/HyeonBin2379/jdbc-exercise/blob/main/src/jdbc_boards/model/BoardDAO.java){:target="_blank"}

