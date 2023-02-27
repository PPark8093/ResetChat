package kr.pak;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Launchers {

    // 클라이언트와 연결할 때만 필요한 ServerSocket 클래스
    ServerSocket ss;

    // 서버로 접속한 클라이언트 Socket을 저장할 멤버 변수
    Socket s;

    // 접속 클라이언트 정보 실시간 저장
    Vector v;

    // kr.pak.ServerThread 자료형 멤버 변수 선언, has-a 관계 설정을 위함
    ServerThread st;

    public Launchers() {
        v = new Vector();

        try {
            ss = new ServerSocket(8093);
            System.out.println("Socket>> " + ss);
            System.out.println("채팅서버 가동중...");

            while (true) {
                s = ss.accept();
                System.out.println("허용>> " + s);

                st = new ServerThread(this, s);

                this.addThread(st);

                st.start();
            }
        } catch (Exception e) {
            System.out.println("서버 접속 실패>> " + e);
        }
    }


    public void addThread(ServerThread st) {
        v.add(st);
    }

    public void removeThread(ServerThread st) {
        v.remove(st);
    }

    public void broadCast(String str) {
        for (int i = 0; i < v.size(); i++) {

            ServerThread st = (ServerThread) v.elementAt(i);

            st.send(str);
        }
    }

    public static void main(String[] args) {
        new Launchers();
    }
}
