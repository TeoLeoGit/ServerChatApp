package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
        while(true) {
            try {
                String receivedSig = dis.readUTF();
                int count = dis.readInt();
                byte[] receivedMsg = new byte[count];
                dis.readFully(receivedMsg);

                //first data sending will be the signature for routing
                // signature: typeOfData-Receivers@Sender
                String[] typeOfData = receivedSig.split("-", 2);
                String[] toUsers = typeOfData[1].split("@", 2);
                String[] receiveUser = toUsers[0].split(", ", -2);
                int loop = receiveUser.length;
                if (receiveUser.length < 1) {

                } else {

                }
                for (int i = 0; i < loop; i++) {
                    String signature = typeOfData[0] + "@" + toUsers[1];

                    //if there are many users that will receive the message(group chat)
                    if (toUsers[0].contains(", "))
                        signature = signature + "=>" + toUsers[0];

                    Socket receiSock = ss.getUsers().get(receiveUser[i]);
                    if (receiSock != null) {
                        ClientOutputThread receiver = new ClientOutputThread(ss, receiSock,
                                new DataOutputStream(receiSock.getOutputStream()), receivedMsg, signature);
                        receiver.start();
                        System.out.println(signature);
                    }
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
    final byte[] sendingMsg;
    final String signature;

    public ClientOutputThread(Server ss, Socket s, DataOutputStream dos, byte[] sendingMsg, String signature) {
        this.dos = dos;
        this.s = s;
        this.ss = ss;
        this.sendingMsg = sendingMsg;
        this.signature = signature;
    }

    public void run() {
        try {
            dos.writeUTF(signature);
            dos.writeInt(sendingMsg.length);
            dos.write(sendingMsg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//test
