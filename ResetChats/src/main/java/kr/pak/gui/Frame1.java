package kr.pak.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Frame1 extends JFrame implements ActionListener {

    String title;

    JTextArea textArea;
    JTextField textField;

    Socket socket;
    BufferedReader br;
    PrintWriter pw;

    // 서버로 전송할 문자열과 서버에서 받아올 문자열 변수
    String str, str1;

    JButton connectButton;

    public Frame1(String title) {
        this.title = title;
        openGUIs();
    }

    public void openGUIs() {
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(title);

        ImageIcon img = new ImageIcon("src/main/java/kr/pak/gui/icons.png");
        setIconImage(img.getImage());

        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(null);

        //TextField
        textField = new JTextField();
        textField.setBounds(0, 434, 384, 27);
        textField.requestFocus();
        textField.addActionListener(this);
        getContentPane().add(textField);

        //화면
        textArea = new JTextArea();
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(0, 20, 384, 390);
        getContentPane().add(scrollPane);

        //sendButton
        JButton sendButton = new JButton("전송 (Enter)");
        sendButton.setBounds(0, 410, 384, 23); //0, 441, 384, 20
        getContentPane().add(sendButton);

        connectButton = new JButton("연결");
        connectButton.setBounds(0, 0, 192, 20);
        getContentPane().add(connectButton);

        JButton unconnectButton = new JButton("연결 끊기");
        unconnectButton.setEnabled(false);
        unconnectButton.setBounds(192, 0, 192, 20);
        getContentPane().add(unconnectButton);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!(connectButton.isEnabled())) {
                    str = textField.getText();

                    textField.setText("");

                    pw.println(str);
                    pw.flush();
                } else {
                    textArea.append("연결이 되어있지 않습니다!\n");
                    textField.setText("");
                }
            }
        });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectButton.setEnabled(false);
                unconnectButton.setEnabled(true);

                try {
                    // 클라이언트 측 소켓 정보 초기화
                    // Socket(host, port), host: 접속 서버 IP 주소, port: 서버 포트 번호
                    String hostIP = JOptionPane.showInputDialog(null, "서버의 IP를 입력하세요:", "입력창", JOptionPane.QUESTION_MESSAGE);
                    socket = new Socket(hostIP, 8093);
                    if (socket != null) {
                        textArea.append("Socket>>" + socket);

                        //서버와 Stream연결
                        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        //PrintWriter스트림의 autoFlush기능 활성화
                        pw = new PrintWriter(socket.getOutputStream(), true);
                    } else {
                        textArea.append("서버가 열려있지 않습니다!");
                    }
                } catch (Exception exception) {
                    textArea.append("접속 오류>>" + exception);
                    connectButton.setEnabled(true);
                    unconnectButton.setEnabled(false);
                }

                Thread ct = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 더 이상 입력을 받을 수 없을 때까지 JTextArea(채팅창)에 출력
                        if (unconnectButton.isEnabled()) {
                            try {
                                while ((str1 = br.readLine()) != null) {
                                    textArea.append(str1 + "\n");
                                    textArea.setCaretPosition(textArea.getDocument().getLength());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                ct.start();
            }
        });

        unconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectButton.setEnabled(true);
                unconnectButton.setEnabled(false);
                try {
                    socket.close();
                    br.close();
                    pw.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        setVisible(true); //이건 무조건 마지막에 와야함
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        str = textField.getText();
        if (!(connectButton.isEnabled())) {

            textField.setText("");

            pw.println(str);
            pw.flush();
        } else {
            textArea.append("연결이 되어있지 않습니다!\n");
            textField.setText("");
        }
    }
}
