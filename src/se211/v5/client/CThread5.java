package se211.v5.client;

import se211.v5.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class CThread5 extends Thread {
    Socket connSocket;
    ChatRoomGuiWithPrivate chatRoom;
    ObjectInputStream inFromServer;


    public CThread5(Socket clientS, ChatRoomGuiWithPrivate chatRoom1, ObjectInputStream inFromServer1) {
        connSocket = clientS;
        chatRoom = chatRoom1;
        inFromServer = inFromServer1;

    }

    public void run() {
        try {
            while (!connSocket.isClosed()) {
                String editedData = null;

                ChatMessage reObj = (ChatMessage) inFromServer.readObject();
                editedData = reObj.getMessage();

                if (reObj.getType() == 0) {
                    System.out.println(editedData);
                    //   chatRoom.updateChat(editedData);
                } else if (reObj.getType() == 1) {

                    List<String> clientList = getClientsList(reObj);

                    System.out.println("total client:" + clientList.size() + " " + reObj.getMessage());
                    //ChatRoomGui6.updateClients(clientList);
                }
            }

        } catch (SocketException e) {
            System.out.println("client quit");
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println(exception);
        }
    }


    private List<String> getClientsList(ChatMessage meg) throws IOException {
        String clientStr = meg.getMessage();
        ArrayList<String> clientList = new ArrayList<>();
        String[] retArr = clientStr.split(",");
        for (String nickName : retArr) {
            clientList.add(nickName);
        }
        return clientList;
    }

}
