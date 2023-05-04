package se211.v5.server;

import se211.v5.ChatMessage;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class SThread5 extends Thread {
    Socket connSocket;
    String clientData;
    String capitalizedData;

    List<String> nameList;
    List<Socket> clientList;
    String nickName;

    List<ObjectOutputStream> clientOutputList;
    ObjectOutputStream out;
    List< ObjectInputStream> clientInputList;
    ObjectInputStream inputStream;

    public SThread5(List<String> clientNameList, String nickName1, Socket clientS, List< Socket> clientListI, List<ObjectInputStream> clientInputList1, ObjectInputStream inputStream1, List<ObjectOutputStream> clientOutputList1, ObjectOutputStream out1) {
        nameList = clientNameList;
        nickName = nickName1;
        connSocket = clientS;
        clientList = clientListI;
        clientInputList = clientInputList1;
        inputStream = inputStream1;
        clientOutputList = clientOutputList1;
        out=out1;
    }

    @Override
    public void run() {
        System.out.println("connected from " + connSocket.getRemoteSocketAddress());

        try {

            boolean userQuit = true;

            while (userQuit) {
                ChatMessage inMeg = (ChatMessage) inputStream.readObject();
                clientData = inMeg.getMessage();

                //System.out.println("data length: " + clientData.length());
                System.out.println(nickName + ":" + clientData);
                capitalizedData = clientData.toUpperCase();

                String outMeg = nickName + ":" + clientData;
                //outClient.println(capitalizedData);
                if(inMeg.getType()==2){

                    userQuit = false;

                    nameList.remove(nickName);
                    clientOutputList.remove(out);
                    clientList.remove(connSocket);
                    clientInputList.remove(inputStream);

                    out.close();
                    inputStream.close();
                    connSocket.close();

                }else{

                    ChatMessage meg = new ChatMessage(0, outMeg);
                    sendToAll(clientOutputList, meg);
                }

                if (clientData.equals("quit")) {
                    userQuit = false;
                }
            }
            //outClient.close();
            inputStream.close();
            connSocket.close();

        } catch (IOException | ClassNotFoundException e) {

            nameList.remove(nickName);
            clientOutputList.remove(out);
            clientList.remove(connSocket);
            clientInputList.remove(inputStream);

            System.out.println( nickName+ " thread error:" + e.getMessage());
        }finally {

            System.out.println(  " hashmap adapted:"+nickName);
        }
    }
    private void sendToAll(List< ObjectOutputStream> clientList, ChatMessage meg) throws IOException {
        for (ObjectOutputStream entity : clientList) {

//            String clientName = entity.getKey();
//            System.out.println("send to client: "+ meg.getMessage());

            ObjectOutputStream individualClient = entity;

            individualClient.writeObject(meg);
            //individualClient.flush();
        }
    }

}




