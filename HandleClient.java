package com.example.project3_1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HandleClient implements Runnable{
    private final Socket socket;
    private final String nickname;

    public HandleClient(Socket socket, String nickname) {
        this.socket = socket;
        this.nickname = nickname;
    }

    @Override
    public void run() {
        try{
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeUTF("Welcome, " + nickname + "!");

            while (true) {
                String message = input.readUTF();

                if (message.equalsIgnoreCase("/list")) {
                    output.writeUTF("Online users: " + Server.getUserList());
                } else if (message.startsWith("/request ")) {
                    String target = message.split(" ")[1];
                    if (Server.startChat(nickname, target)) {
                        output.writeUTF("Chat request sent to " + target);
                    } else {
                        output.writeUTF("Failed to send request.");
                    }
                } else if (message.equalsIgnoreCase("/accept")) {
                    if (Server.acceptChat(nickname)) {
                        output.writeUTF("Chat started with " + Server.getChatPartner(nickname));
                    } else {
                        output.writeUTF("No chat request found.");
                    }
                } else if (message.equalsIgnoreCase("/leave")) {
                    Server.endChat(nickname);
                    output.writeUTF("You left the chat.");
                } else {
                    String partner = Server.getChatPartner(nickname);
                    if (partner != null) {
                        Server.forwardMessage(nickname, message);
                    } else {
                        output.writeUTF("No active chat.");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + nickname);
            Server.removeUser(nickname);
        }
    }
}
