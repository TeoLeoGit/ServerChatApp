package GUI;

import Server.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ServerGUI extends JFrame {
    private JPanel mainPanel;
    private int serverPort;
    public ServerGUI() {
        setLayout(new BorderLayout());
        mainPanel = new JPanel();
        mainPanel.setLayout(null);

        //default port
        serverPort = 3500;
        final Server[] server = {null};

        JLabel portLabel = new JLabel("Config port number:");
        JTextField portTxt = new JTextField("3500");
        JButton setPortBtn = new JButton("Confirm");
        JButton openPortBtn = new JButton("Open server");
        JButton closePortBtn = new JButton("Close server");
        JButton resetUserListBtn = new JButton("Reset user list");
        JLabel serverInfoLabel = new JLabel("Server default port: 3500");

        portLabel.setBounds(20, 20, 120, 25);
        portTxt.setBounds(150, 20, 80, 25);

        setPortBtn.setBounds(250, 20, 100, 25);
        setPortBtn.setBackground(Color.WHITE);

        openPortBtn.setBounds(20, 60, 140, 25);
        openPortBtn.setBackground(Color.WHITE);

        closePortBtn.setBounds(180, 60, 140, 25);
        closePortBtn.setBackground(Color.WHITE);

        resetUserListBtn.setBounds(520, 20, 80, 25);
        resetUserListBtn.setBackground(Color.WHITE);

        serverInfoLabel.setBounds(20, 90, 200, 25);

        String columnUser[] = {"Username"};
        DefaultTableModel modelUser = new DefaultTableModel(columnUser, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable userTable = new JTable(modelUser);

        userTable.setShowGrid(false);
        userTable.setRowMargin(10);
        userTable.getColumnModel().setColumnMargin(10);
        userTable.setRowHeight(30);

        JScrollPane userPane = new JScrollPane(userTable);
        userPane.getViewport().setBackground(Color.WHITE);
        userPane.setBounds(608, 20, 255, 470);

        //serverPanel.add(new JButton("Hello"));
        mainPanel.add(portLabel);
        mainPanel.add(portTxt);
        mainPanel.add(setPortBtn);
        mainPanel.add(closePortBtn);
        mainPanel.add(openPortBtn);
        mainPanel.add(userPane);
        mainPanel.add(serverInfoLabel);
        mainPanel.add(resetUserListBtn);
        add(mainPanel);


        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(900, 550);
        setPreferredSize(new Dimension(900, 530));
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //event handle

        //change port
        setPortBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int temp = serverPort;
                    serverPort = Integer.parseInt(portTxt.getText());
                    if (serverPort > 0) {
                        serverInfoLabel.setText("Server port setted: " + portTxt.getText());
                    } else
                        serverPort = temp;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(setPortBtn, "Illegal port format");
                }
            }
        });

        //open server
        openPortBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (server[0] != null && !server[0].getServerSocket().isClosed()) {
                    try {
                        Thread.sleep(500);
                        server[0].closeServer();
                        Thread.sleep(500);
                    } catch (IOException | InterruptedException ioException) {
                        ioException.printStackTrace();
                        JOptionPane.showMessageDialog(closePortBtn, "failed to close server");
                    }
                }
                server[0] = new Server(serverPort);
                server[0].start();
                JOptionPane.showMessageDialog(openPortBtn, "Server opened");
                serverInfoLabel.setText("Current server port: " + serverPort);
            }
        });

        //close server
        closePortBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(server[0] != null && !server[0].getServerSocket().isClosed()) {
                    try {
                        Thread.sleep(500);
                        server[0].closeServer();
                        Thread.sleep(500);
                        JOptionPane.showMessageDialog(closePortBtn, "Closed server!");
                        serverInfoLabel.setText("Server closed.");
                    } catch (IOException | InterruptedException ioException) {
                        ioException.printStackTrace();
                        JOptionPane.showMessageDialog(closePortBtn, "failed to close server");
                    }
                }
            }
        });

        //reset active user list
        resetUserListBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) userTable.getModel();
                int rowCount = model.getRowCount();

                for (int i = 0; i < rowCount; i++) {
                    model.removeRow(i);
                }
                //adding new row
                if (server[0] != null && !server[0].getUsers().isEmpty()) {
                    for (String item : server[0].getUsers().keySet()) {
                        model.addRow(new Object[]{item});
                    }
                }
            }
        });
    }
}