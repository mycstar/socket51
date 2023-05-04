package se211.v5.server;

import se211.v5.ChatMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class BServer5 {

    static List<String> clientNameList = Collections.synchronizedList(new ArrayList<String>());
    static List<Socket> clientList = Collections.synchronizedList(new ArrayList<Socket>());
    ;
    static List< ObjectOutputStream> clientOutputList = Collections.synchronizedList(new ArrayList<ObjectOutputStream>());
    ;
    static List< ObjectInputStream> clientInputList =Collections.synchronizedList(new ArrayList<ObjectInputStream>());
    ;

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        ServerSocket serverS = new ServerSocket(6789);
        System.out.println("server is running...");

        int connNum = 0;
        while (true) {
            Socket connSocket = serverS.accept();
            System.out.println("new Connection number:" + connNum);
            //clientList.put(String.valueOf(connSocket.getPort()), connSocket);

            ObjectInputStream in = new ObjectInputStream(connSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(connSocket.getOutputStream());

            ChatMessage clientMeg = (ChatMessage) in.readObject();
            String clientName = clientMeg.getSender();
            System.out.println(clientMeg.getSender() + " connected");

            // add new client to HashMap
            clientNameList.add(clientName);
            clientList.add( connSocket);
            clientInputList.add( in);
            clientOutputList.add(out);

            // reminder all connected clients
            ChatMessage clientNameMeg = getClientNames(clientNameList);
            clientNameMeg.setSender("Server");
            sendToAll(clientOutputList, clientNameMeg);

            SThread5 a = new SThread5(clientNameList,clientName, connSocket, clientList, clientInputList, in, clientOutputList, out);
            a.start();
            connNum++;
            System.out.println("Total client number: " + connNum);
        }
    }

    private static void sendToAll(List<ObjectOutputStream> clientList, ChatMessage meg) throws IOException {
        System.out.println("send reminder to clients:" + clientList.size()+ " "+meg.getMessage() );
        for (ObjectOutputStream entity : clientList) {

//            String clientName = entity.getKey();
//            System.out.println("send reminder of new client:" + meg.getMessage());
            ObjectOutputStream individualClient = entity;
            try {
                individualClient.writeObject(meg);
            } catch (Exception ex) {

                int index = clientOutputList.indexOf(entity);
                clientOutputList.remove(entity);
                clientList.remove(index);
                clientInputList.remove(index);
                clientNameList.remove(index);
               //System.out.println(  " main thread hashmap adapted:"+clientName);

            }
            //individualClient.flush();
        }
    }

    private static ChatMessage getClientNames(List< String> clientList) {
        StringBuffer clientsStr = new StringBuffer();
        for (String entity : clientList        ) {
            clientsStr.append(entity);
            clientsStr.append(",");
        }
        if (clientsStr.substring(clientsStr.length() - 1).equals(",")) {
            clientsStr.deleteCharAt(clientsStr.length() - 1);
        }

        ChatMessage retMeg = new ChatMessage(1, clientsStr.toString());

        return retMeg;
    }
}
