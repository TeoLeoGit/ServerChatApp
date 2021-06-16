package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    private ServerSocket ss;
    private HashMap<String, Socket> users = new HashMap<String, Socket>();
    public Server() {
        String userListString = new String();
        try {
            ss = new ServerSocket(3500);
            while (true) {
                Socket s = null;
                try {
                    s = ss.accept();
                    System.out.println("Talking to client");
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    //Sending user list to client
                    dos.writeUTF(userListString);
                    //receiving new username
                    String username = dis.readUTF();
                    users.put(username, s);
                    userListString = userListString + "-" + username;

                    //multithreading
                    Thread inThread = new ClientInputThread(this, s, dis);
                    inThread.start();
                 } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public HashMap<String, Socket> getUsers() {
        return users;
    }
    public ServerSocket getServerSocket() {
        return ss;
    }
}

class ClientInputThread extends  Thread {
    final DataInputStream dis;
    final Server ss;
    final Socket s;

    public ClientInputThread(Server ss, Socket s ,DataInputStream dis) {
        this.ss = ss;
        this.dis = dis;
        this.s = s;
    }

    public void run() {
        String receivedMsg;
        while(true) {
            try {
                String typeOfData = dis.readUTF();
                receivedMsg = dis.readUTF();
                String[] arrOfData = receivedMsg.split("-", 2);
                if (arrOfData.length > 1) {
                    System.out.println(arrOfData[0] + "-" + arrOfData[1]);
                    Socket receiSock = ss.getUsers().get(arrOfData[0]);
                    ClientOutputThread receiver = new ClientOutputThread(ss, receiSock,
                            new DataOutputStream(receiSock.getOutputStream()), arrOfData[1]);
                    receiver.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ClientOutputThread extends Thread {
    final DataOutputStream dos;
    final Socket s;
    final Server ss;
    final String sendingMsg;

    public ClientOutputThread(Server ss, Socket s, DataOutputStream dos, String sendingMsg) {
        this.dos = dos;
        this.s = s;
        this.ss = ss;
        this.sendingMsg = sendingMsg;
    }

    public void run() {
        try {
            dos.writeUTF(sendingMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//test
