import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatServer {

  // 서버의 포트 번호 정적 변수
  private static int PORT;
  // 서버가 관리할 스레드 풀
  private static final ExecutorService POOL = Executors.newCachedThreadPool();
  private static final AtomicInteger CLIENT_SEQ = new AtomicInteger(1);
  // 사용자 닉네임 관리 정적 변수 지정
  private static final Set<String> userNicknames = new HashSet<>();

  // 모든 클라이언트의 출력 스트림을 관리하는 동기화된 집합
  private static final Set<PrintWriter> clientWriters = Collections.synchronizedSet(
      new HashSet<>());


  public static void main(String[] args) {

    // 필수 인자(포트 번호)의 개수를 확인합니다.
    if (args.length != 1) {
      System.err.println("사용법: java -cp out ChatServer <port>");
      System.exit(1); // 잘못된 사용법이면 프로그램 종료
    }

    try {
      // 명령줄 인자로부터 포트 번호를 받습니다.
      PORT = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      System.err.println("오류: 포트 번호는 유효한 숫자여야 합니다.");
      System.exit(1);
    }

    System.out.println("[Server] Starting on port " + PORT);
    // Ctrl+C 시 스레드 풀 (POOL) 정리
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("\n[Server] Shutting down...");
      POOL.shutdownNow();
    }));

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      while (true) {
        Socket socket = serverSocket.accept();

        int id = CLIENT_SEQ.getAndIncrement();

        POOL.submit(new ClientHandler(socket, id));
      }
    } catch (IOException e) {
      System.err.println("[Server] Error: " + e.getMessage());
    }
  }

  // 모든 클라이언트에게 메시지를 브로드캐스팅하는 메서드
  private static void broadcast(String message) {
    // 한 스레드가 브로드캐스트 하는 동안 전체 clientWriters가 나가거나(remove), 추가되어(add) 반복문에 영향을 미치지 못하게 해야 한다.
    synchronized (clientWriters) {
      for (PrintWriter writer : clientWriters) {
        writer.println(message);
      }
    }
  }

  private static class ClientHandler implements Runnable {

    private final Socket socket;
    private final int clientId;

    ClientHandler(Socket socket, int clientId) {
      this.socket = socket;
      this.clientId = clientId;
    }

    @Override
    public void run() {
      try (
          BufferedReader in = new BufferedReader(
              new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
          PrintWriter out = new PrintWriter(
              new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
      ) {

        // 닉네임 유효성 검사
        String nickName = "";
        boolean isValidNickName = false;
        while (!isValidNickName) {
          // 클라이언트의 첫번째 입력을 닉네임으로 받기
          nickName = in.readLine();

          // 중복되거나, 공백인 경우
          if (nickName == null || nickName.trim().isEmpty()) {
            out.println("ERROR: 닉네임이 유효하지 않습니다. 다시 시도해 주세요.");
            System.out.println("닉네임 입력 오류 클라이언트 발생");
          } else if (userNicknames.contains(nickName)) {
            out.println("ERROR: 이미 사용중인 닉네임이니다. 다시 시도해 주세요.");
            System.out.println("닉네임 입력 중복 클라이언트 발생");
          } else {
            isValidNickName = true; // 정상 입력시 루프 탈출
            userNicknames.add(nickName); // 클라이언트 유저 정보에 추가
            // 1. 클라이언트 연결 시 출력 스트림 목록에 추가
            clientWriters.add(out);
            // welcome message out to client
            out.println("환영합니다.  " + nickName
                + " 님. ,현재 접속자 목록을 보고 싶으시면 '/who',현재의 채팅을 끝내시려면 '/quit'를 입력하세요!");

            broadcast(nickName + " joined!"); // 각 클라이언트의 접속을 브로드캐스트 해줌
          }
        }

        String line;
        while ((line = in.readLine()) != null) {
          // 유저의 채팅을 읽고 서버 콘솔창에 출력
          System.out.println(
              "[Server] From Client#" + clientId + ", 닉네임: " + nickName + "> " + line);

          // 유저의 채팅 내용에 따른 흐름제어문
          if ("/quit".equalsIgnoreCase(line.trim())) {
            out.println("안녕히 가세요 " + nickName + "님.");
            broadcast("[" + nickName + "] 님이 퇴장하셨습니다.");
            userNicknames.remove(nickName); // 유저 퇴장 시 접속 닉네임에서 제외
            break;
          } else if ("/who".equals(line.trim())) {
            String currentUsers = String.join("님, ", userNicknames);
            out.println("현재 접속자 : " + currentUsers + "님");
          }
          // 클라이언트의 입력이 공백인 경우 브로드 캐스트 하지 않음
          else if (line.trim().equals("\n")) {
            continue;
          }
          // 나머지의 경우 각 클라이언트의 채팅을 브로드 캐스트 해줌
          else {
            broadcast("[" + nickName + "] " + line);
          }
        }
      } catch (IOException e) {
        System.err.println("[Server] Client#" + clientId + " I/O error: " + e.getMessage());
      } finally {
        try {
          socket.close();
        } catch (IOException ignored) {
        }
        System.out.println("[Server] Client#" + clientId + " disconnected.");
      }
    }
  }
}
