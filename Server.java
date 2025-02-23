package com.example.project3_1;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private static final Map<String, Socket> users = new HashMap<>();
    private static final Map<String, String> activeChats = new HashMap<>();
    private static final Map<String, String> pendingRequests = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            System.out.println("Server started on port 8000");
            int clientCount = 0;

            while (true) {
                Socket socket = serverSocket.accept();
                clientCount++;
                String nickname = "User" + clientCount;
                users.put(nickname, socket);
                System.out.println(nickname + " connected");
                new Thread(new HandleClient(socket, nickname)).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public static synchronized boolean startChat(String requester, String target) {
        if (users.containsKey(target) && !activeChats.containsKey(target) && !pendingRequests.containsKey(target)) {
            pendingRequests.put(target, requester);
            return true;
        }
        return false;
    }

    public static synchronized boolean acceptChat(String target) {
        String requester = pendingRequests.get(target);
        if (requester != null) {
            activeChats.put(requester, target);
            activeChats.put(target, requester);
            pendingRequests.remove(target);
            return true;
        }
        return false;
    }

    public static synchronized void endChat(String user) {
        String partner = activeChats.remove(user);
        if (partner != null) {
            activeChats.remove(partner);
            Socket partnerSocket = users.get(partner);
            try {
                DataOutputStream partnerOutput = new DataOutputStream(partnerSocket.getOutputStream());
                partnerOutput.writeUTF(user + " has left the chat.");
            } catch (IOException e) {
                System.out.println("Error notifying partner that " + user + " left the chat.");
            }
        }
    }

    public static synchronized void forwardMessage(String user, String message) {
        String partner = activeChats.get(user);
        if (partner != null) {
            Socket partnerSocket = users.get(partner);
            try {
                DataOutputStream partnerOutput = new DataOutputStream(partnerSocket.getOutputStream());
                partnerOutput.writeUTF(user + ": " + message);
            } catch (IOException e) {
                System.out.println("Error forwarding message to " + partner);
            }
        }
    }

    public static synchronized List<String> getUserList() {
        return new ArrayList<>(users.keySet());
    }

    public static synchronized String getChatPartner(String user) {
        return activeChats.get(user);
    }

//    public static synchronized Socket getPartnerSocket(String partner) {
//        return users.get(partner);
//    }

    public static synchronized void removeUser(String nickname) {
        users.remove(nickname);
    }
}
