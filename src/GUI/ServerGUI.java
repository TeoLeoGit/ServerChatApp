package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI extends JFrame {
    private JPanel mainPanel;
    public ServerGUI() {
        setLayout(new BorderLayout());
        mainPanel = new JPanel();
        mainPanel.setLayout(null);

        JLabel portLabel = new JLabel("Config port number:");
        JTextField portTxt = new JTextField();
        JButton setPortBtn = new JButton("Confirm");
        JButton openPortBtn = new JButton("Open server");
        JButton closePortBtn = new JButton("Close server");

        portLabel.setBounds(20, 20, 120, 25);
        portTxt.setBounds(150, 20, 80, 25);

        setPortBtn.setBounds(250, 20, 100, 25);
        setPortBtn.setBackground(Color.WHITE);

        openPortBtn.setBounds(20, 60, 140, 25);
        openPortBtn.setBackground(Color.WHITE);

        closePortBtn.setBounds(180, 60, 140, 25);
        closePortBtn.setBackground(Color.WHITE);



        String columnUser[] = {"Username", "Chat"};
        DefaultTableModel modelUser = new DefaultTableModel(columnUser, 0);
        modelUser.addRow(new Object[]{"Username", "Start chatting"});
        JTable userTable = new JTable(modelUser);
        ButtonEditor chatCell = new ButtonEditor(new JTextField());
        ButtonRenderer chatBtn = new ButtonRenderer();

        userTable.getColumnModel().getColumn(1).setCellEditor(chatCell);
        userTable.getColumnModel().getColumn(1).setCellRenderer(chatBtn);
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
        add(mainPanel);


        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(900, 550);
        setPreferredSize(new Dimension(900, 530));
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //event handle
        //connect to a server

    }
}
