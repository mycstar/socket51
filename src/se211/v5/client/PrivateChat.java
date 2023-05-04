package se211.v5.client;

import se211.v5.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class PrivateChat extends JFrame {

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private ObjectOutputStream out;
    private String recipient;

    public PrivateChat(String sender, String recipient, ObjectOutputStream out) {
        setTitle("Private Chat: " + sender + " -> " + recipient);
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.out = out;
        this.recipient = recipient;

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                sendMessage(sender, recipient, message);
                chatArea.append("You: " + message + "\n");
                messageField.setText("");
            }
        });

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(chatScrollPane, BorderLayout.CENTER);
        container.add(messagePanel, BorderLayout.SOUTH);
    }

    private void sendMessage(String sender, String recipient, String message) {
        try {
            ChatMessage chatMessage = new ChatMessage(3, message);
            out.writeObject(chatMessage);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
