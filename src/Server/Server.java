package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Server extends Thread {
    private boolean exit;
    private int serverPort;
    private ServerSocket ss;
    private HashMap<String, Socket> users = new HashMap<String, Socket>();
    private String userListString;
    public Server(int portNumber) {
        serverPort = portNumber;
        exit = false;
    }

    public void run() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatappuser", "root", "teo15102000");
            userListString = new String();
            ss = new ServerSocket(serverPort);
            while (!exit) {
                Socket s = null;
                try {
                    s = ss.accept();
                    System.out.println("Received a request to connect");
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    //checking connection
                    String signature = dis.readUTF();
                    String[] action = signature.split(">", 2);
                    if(action.length > 1) {
                        if(action[0].equals("Login")) {
                            String[] usernameAndPw = action[1].split("##", 2);
                            String username = usernameAndPw[0];
                            String password = usernameAndPw[1];
                            if(userListString.contains(username)) {
                                dos.writeUTF("Reject>Already logged in");
                            } else {
                                String query = "select username from accounts where username='" + username +
                                        "' and password= '" + password + "';";
                                Statement stmt = conn.createStatement();
                                ResultSet rs = stmt.executeQuery(query);
                                if(rs.next()) {
                                    //Sending user list to client
                                    dos.writeUTF(userListString.replace("-" + username, ""));

                                    if (!userListString.contains(username)) {
                                        userListString = userListString + "-" + username;
                                        users.put(username, s);
                                    }
                                } else {
                                    dos.writeUTF("Reject>Wrong password");
                                }
                                stmt.close();
                            }
                        }
                        else {
                            //Regist new account
                            try {
                                String[] usernameAndPw = action[1].split("##", 2);
                                String username = usernameAndPw[0];
                                String password = usernameAndPw[1];
                                String query = "insert into accounts (username, password) values (?, ?)";
                                PreparedStatement st = conn.prepareStatement(query);
                                st.setString(1, username);
                                st.setString(2, password);
                                int exec = st.executeUpdate();
                                dos.writeUTF("Registered>" + username);
                            } catch (SQLIntegrityConstraintViolationException ex) {
                                dos.writeUTF("Already Registered>Refuse");
                            }
                        }
                    }
                    else {
                        //not login
                        //Sending user list to client
                        dos.writeUTF(userListString.replace("-" + signature, ""));

                        if (!userListString.contains(signature)) {
                            userListString = userListString + "-" + signature;
                            users.put(signature, s);
                        }

                    }
                    //multithreading
                    Thread inThread = new ClientInputThread(this, s, dis);
                    inThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Socket> getUsers() {
        return users;
    }
    public ServerSocket getServerSocket() {
        return ss;
    }

    public void closeServer() throws IOException {
        exit = true;
        for (String i : users.keySet())
            users.get(i).close();
        if (ss != null)
            ss.close();
    }

    public void removeUserFromStr(String user) {
        this.userListString = this.userListString.replace(user, "");
        System.out.println(userListString);
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
                if (typeOfData[0].equals("Close")) {
                    ClientOutputThread receiver = new ClientOutputThread(ss, s,
                            new DataOutputStream(s.getOutputStream()), receivedMsg, "OKClose");
                    receiver.start();
                    TimeUnit.SECONDS.sleep(1);
                    dis.close();
                    ss.getUsers().remove(typeOfData[1]);
                    String remove = "-" + typeOfData[1];
                    ss.removeUserFromStr(remove);
                    break;
                }
                else {
                    String[] toUsers = typeOfData[1].split("@", 2);

                    String signature = typeOfData[0] + "@" + toUsers[1];
                    if (toUsers[0].contains(", ")) {
                        String[] receiveUser = toUsers[0].split(", ", -2);
                        signature = signature + "=>" + toUsers[0];
                        int loop = receiveUser.length;
                        for (int i = 0; i < loop; i++) {
                            Socket receiSock = ss.getUsers().get(receiveUser[i]);
                            //file to big -> null, != s -> not sending back
                            if (receiSock != null && receiSock != s) {
                                ClientOutputThread receiver = new ClientOutputThread(ss, receiSock,
                                        new DataOutputStream(receiSock.getOutputStream()), receivedMsg, signature);
                                receiver.start();
                                System.out.println(signature);
                            }
                        }
                    } else {
                        Socket receiSock = ss.getUsers().get(toUsers[0]);
                        if (receiSock != null) {
                            ClientOutputThread receiver = new ClientOutputThread(ss, receiSock,
                                    new DataOutputStream(receiSock.getOutputStream()), receivedMsg, signature);
                            receiver.start();
                            System.out.println(signature);
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                break;
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


