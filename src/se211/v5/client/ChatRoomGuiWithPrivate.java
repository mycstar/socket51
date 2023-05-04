package se211.v5.client;


import se211.v5.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ChatRoomGuiWithPrivate extends JFrame implements ActionListener {

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JList<String> clientList;
    private static DefaultListModel<String> clientsModel;

    private static ArrayList<String> connectedClients;
    private static String clientName;

    // response for send out message to server
    static ObjectOutputStream outToServer;

    // response for receive message from server
    static ObjectInputStream inFromServer;

    // because possible multi chat room exist, need a collection to store all exist private windows
    Map<String, PrivateChat> privateWindowMap = new ConcurrentHashMap<String, PrivateChat>();


    public ChatRoomGuiWithPrivate() {
        setTitle("Chat Room");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(Color.lightGray);
        chatArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });

        JPanel clientPanel = new JPanel(new BorderLayout());
        JLabel clientLabel = new JLabel("Connected User");
        clientPanel.add(clientLabel, BorderLayout.NORTH);

        //String[] clientNames = {"client1", "client2", "client3", "client4", "client5"};
        clientsModel = new DefaultListModel<>();
        clientList = new JList<>(clientsModel);
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String recipient = clientList.getSelectedValue();
                if (recipient != null) {
                    //JOptionPane.showMessageDialog(this, "Printing complete");
                    try {

                        // start the private chat from here, first time pop up one specific private window.
                        File file = new File("abc.txt");
                        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
                        PrivateChat privateW = new PrivateChat("You", recipient, out);
                        privateW.setVisible(true);

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                }
            }
        });
        JScrollPane clientScrollPane = new JScrollPane(clientList);
        clientPanel.add(clientScrollPane, BorderLayout.CENTER);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(chatScrollPane, BorderLayout.CENTER);
        container.add(messagePanel, BorderLayout.SOUTH);
        container.add(clientPanel, BorderLayout.EAST);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == sendButton) {
            String message = messageField.getText();
            chatArea.append("You: " + message + "\n");
            messageField.setText("");
            //send the typed meg to server
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
    //update the client panel when anytime server send back client list,
    // first clean the exist client list, then add all new ones.
    public void updateClients(List<String> clientList) {

        for (int i = 0; i < clientsModel.size(); i++) {

            String oneClient = clientsModel.get(i);
            clientsModel.removeElement(oneClient);
            connectedClients.add(oneClient);
        }
        for (String nickname : connectedClients) {
            connectedClients.add(nickname);
            clientsModel.addElement(nickname);
        }
    }

    public void addClient(String clientName) {
        clientsModel.addElement(clientName);
    }

    public void removeClient(String clientName) {
        clientsModel.removeElement(clientName);
    }

    public static void main(String[] args) {

        Random rand = new Random();
        int randomNum = rand.nextInt(100);
        clientName = "client" + randomNum;


        try {
            ChatRoomGuiWithPrivate chatRoom = new ChatRoomGuiWithPrivate();
            chatRoom.setVisible(true);

            connectedClients = new ArrayList<>();
            //add youself to the client list
            //clientsModel.addElement(clientName);

            //initialize the connection between client and server,
            chat(clientName, chatRoom);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void chat(String nickName, ChatRoomGuiWithPrivate chatRoom) throws Exception {

        int port = 6789;
        String data;
        String editedData;
        Scanner scn = new Scanner(System.in);
        //handshake with server and establish a connection
        Socket clientSocket = new Socket("localhost", port);

        //init the output stream
        outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        //init the input stream
        inFromServer = new ObjectInputStream(clientSocket.getInputStream());

        //send out message, first talk to server, only need to let the server know client name.
        // and receive the response message which hold all connected client
        ChatMessage meg = new ChatMessage(1, nickName);
        meg.setSender(nickName);
        System.out.println(nickName + " connected");
        outToServer.writeObject(meg);

//        List<String> clientList = getClientsList(inFromServer);
//        updateClients(clientList);

        boolean endChat = false;

        CThread5 cthread = new CThread5(clientSocket, chatRoom, inFromServer);
        cthread.start();

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


}
