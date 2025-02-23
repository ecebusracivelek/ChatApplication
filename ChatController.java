package com.example.project3_1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatController {
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;
    @FXML
    private ListView<String> userList;
    @FXML
    private Button requestChatButton;
    @FXML
    private Button acceptChatButton;

    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;

    public void initialize() {
        try {
            socket = new Socket("localhost", 8000);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            output.writeUTF("/list");

            new Thread(() -> {
                try {
                    while (true) {
                        String message = input.readUTF();
                        if (message.startsWith("Online users:")) {
                            String[] users = message.substring(13).split(", ");
                            Platform.runLater(() -> {
                                userList.getItems().clear();
                                userList.getItems().addAll(users);
                            });
                        } else {
                            Platform.runLater(() -> chatArea.appendText(message + "\n"));
                        }
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatArea.appendText("Disconnected from server.\n"));
                }
            }).start();
        } catch (IOException e) {
            chatArea.setText("Unable to connect to server.");
        }
    }

    @FXML
    private void sendMessage(ActionEvent event) {
        try {
            String message = messageField.getText();
            output.writeUTF(message);
            messageField.clear();
        } catch (IOException e) {
            chatArea.appendText("Failed to send message.\n");
        }
    }

    @FXML
    private void requestChat(ActionEvent event) {
        String target = userList.getSelectionModel().getSelectedItem();
        if (target != null) {
            try {
                output.writeUTF("/request " + target);
            } catch (IOException e) {
                chatArea.appendText("Failed to send request.\n");
            }
        }
    }

    @FXML
    private void acceptChat(ActionEvent event) {
        try {
            output.writeUTF("/accept");
        } catch (IOException e) {
            chatArea.appendText("Failed to accept chat.\n");
        }
    }
}
