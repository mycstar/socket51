package se211.v5.old;

import se211.v5.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ChatRoomGui6 extends JFrame implements ActionListener {

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private static JPanel clientsPanel;
    private static ArrayList<String> connectedClients;
    private static String clientName;

    static ObjectOutputStream outToServer;
    static ObjectInputStream inFromServer;

    public ChatRoomGui6() {
        setTitle("Chat Room");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        clientsPanel = new JPanel();
        clientsPanel.setLayout(new BoxLayout(clientsPanel, BoxLayout.Y_AXIS));

        Dimension preferredSize = new Dimension(100, Integer.MAX_VALUE);
        clientsPanel.setPreferredSize(preferredSize);

        JScrollPane clientsScrollPane = new JScrollPane(clientsPanel);

        connectedClients = new ArrayList<String>();

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(chatScrollPane, BorderLayout.CENTER);
        container.add(messagePanel, BorderLayout.SOUTH);
        container.add(clientsPanel, BorderLayout.EAST);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                System.out.println("window close");
                String message ="exit";
                ChatMessage meg = new ChatMessage(2, message);
                try {
                    outToServer.writeObject(meg);

                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }

                System.exit(0);
            }
        });
    }

    public void updateChat(String message) {

        chatArea.append(message + "\n");
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == sendButton) {
            String message = messageField.getText();
            //chatArea.append("You: " + message + "\n");
            messageField.setText("");
            sendToSever(message);
        }
    }

    private void sendToSever(String message) {

        ChatMessage meg = new ChatMessage(0, message);
        try {
            outToServer.writeObject(meg);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addClient(String clientName) {
        connectedClients.add(clientName);
        clientsPanel.add(new JLabel("You:"+clientName));
        clientsPanel.revalidate();
        clientsPanel.repaint();
    }

    public static void updateClients(List<String> clientList) {
        boolean isFirst = true;
        for (String nickname : connectedClients) {
            if (isFirst) {
                isFirst = false;
            } else {
                connectedClients.remove(nickname);
                Component[] components = clientsPanel.getComponents();
                for (Component component : components) {
                    if (component instanceof JLabel) {
                        if (((JLabel) component).getText().equals(nickname)) {
                            clientsPanel.remove(component);
                        }
                    }
                }
            }
        }

        for (String nickname : clientList) {
            if (!nickname.equals(clientName)) {
                connectedClients.add(nickname);
                clientsPanel.add(new JLabel(nickname));
            }
        }
        clientsPanel.revalidate();
        clientsPanel.repaint();

    }

    public void removeClient(String clientName) {
        connectedClients.remove(clientName);
        Component[] components = clientsPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                if (((JLabel) component).getText().equals(clientName)) {
                    clientsPanel.remove(component);
                }
            }
        }
        clientsPanel.revalidate();
        clientsPanel.repaint();
    }

    public static void main(String[] args) {
        Random rand = new Random();
        int randomNum = rand.nextInt(100);
        clientName = "client" + randomNum;

        try {

            ChatRoomGui6 chatRoom = new ChatRoomGui6();
            chatRoom.setVisible(true);
            chatRoom.addClient(clientName);

            chat(clientName, chatRoom);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void chat(String nickName, ChatRoomGui6 chatRoom) throws Exception {

        String data;
        String editedData;
        Scanner scn = new Scanner(System.in);
        Socket clientSocket = new Socket("localhost", 6789);

        outToServer = new ObjectOutputStream(clientSocket.getOutputStream());

        ChatMessage meg = new ChatMessage(1, nickName);
        meg.setSender(nickName);
        System.out.println(nickName+" connected");

        outToServer.writeObject(meg);

         inFromServer = new ObjectInputStream(clientSocket.getInputStream());

         //List<String> clientList = getClientsList(inFromServer);
         // updateClients(clientList);

        boolean endChat = false;

//        CThread5 cthread = new CThread5(clientSocket, chatRoom, inFromServer);
//        cthread.start();


//        while (!endChat) {
//            data = scn.nextLine();
//            outToServer.writeObject(data);
//
//            if (data.equals("quit")) {
//                endChat = true;
//            }
//        }

/*        clientSocket.close();
        inFromServer.close();
        outToServer.close();*/
    }

    private static List<String> getClientsList(ObjectInputStream inFromServer) throws IOException, ClassNotFoundException {
        Object obj = inFromServer.readObject();
        ChatMessage meg = (ChatMessage) obj;
        String clientStr = meg.getMessage();
        ArrayList<String> clientList = new ArrayList<>();
        String[] retArr = clientStr.split(",");
        for (String nickName : retArr) {
            clientList.add(nickName);
        }
        return clientList;
    }


}

