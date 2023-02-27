package kr.pak;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {

    // 클라이언트 소켓 저장
    Socket s;

    // ChatGUIServer 클래스의 객체를 멤버 변수로 선언, has-a 관계를 위함
    Launchers lc;

    // 입출력
    BufferedReader br;
    PrintWriter pw;

    // 전달할 문자열
    String str;

    // 대화명(ID)
    String name;

    public ServerThread(Launchers lc, Socket s) {
        /* lc = new Launcher(); → 작성 불가, 서버가 두 번 가동되기 때문에 충돌이 일어남
        따라서 매개변수를 이용해서 객체를 얻어온(call by reference) 뒤에 cg와 s값을 초기화해야 함 */

        this.lc = lc;
        this.s = s;

        try {
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            pw = new PrintWriter(s.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println("ERROR>> " + e);
        }
    }

    public void send(String str) {
        pw.println(str);

        pw.flush();
    }

    public void run() {
        try {
            pw.println("대화명을 입력하시오");
            name = br.readLine();

            lc.broadCast("[" + name + "]" + "(이)가 참가");

            while ((str = br.readLine()) != null) {

                lc.broadCast("[" + name + "] " + str);
                System.out.println("[" + name + "] " + str);
            }
        } catch (Exception e) {
            lc.removeThread(this);

            lc.broadCast("[" + name + "]" + "(이)가 연결종료");

            System.out.println(s.getInetAddress() + "의 연결 종료");
        }
    }
}
