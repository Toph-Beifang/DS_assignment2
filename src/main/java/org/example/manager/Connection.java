package org.example.manager;

import org.example.user.Join;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection extends Thread {
    public Socket socket;
    public int port;
    public boolean kick = false;
    public DataInputStream dataInputStream;
    public DataOutputStream dataOutputStream;

    public Connection(Socket socket) {
        try {
            this.socket = socket;
            this.dataInputStream = new DataInputStream(this.socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
    }

    public void start() {
        try {
            while(true) {
                if (!this.isInterrupted()) {
                    // Receive message from users
                    String inputMsg = this.dataInputStream.readUTF();
                    String[] msg = inputMsg.split(" ");
                    if (msg[0].equals("join")) { // Handle join request
                        if (!Build.usersName.contains(msg[1])) { // If the username is unique
                            int joinConfirm = JOptionPane.showConfirmDialog(null, msg[1] + " is trying to join your whiteboard");
                            // Gain owner's confirmation
                            if(joinConfirm == 0){ // Agree the join request
                                Build.usersName.add(msg[1]);
                                this.dataOutputStream.writeUTF("Connect");
                                this.dataOutputStream.flush();
                            } else{ // Reject the join request
                                this.dataOutputStream.writeUTF("No");
                                this.dataOutputStream.flush();
                                socket.close();
                                break;
                            }
                        } else {
                            // Reject user
                            this.dataOutputStream.writeUTF("Reject");
                            this.dataOutputStream.flush();
                            socket.close();
                            break;
                        }
                    } else if (msg[0].equals("User")) { // Update and send current user list
                        Build.createNew.userList.add(msg[1]);
                        List<String> userList = new ArrayList();
                        for (int user = 0; user < Build.createNew.userList.getItemCount(); user++) {
                            userList.add(Build.createNew.userList.getItem(user));
                        }
                        String replace = userList.toString().replace(" ", "");
                        this.dataOutputStream.writeUTF("UserList " + replace);
                    } else if (msg[0].equals("Kicked")) { // Kick user
                        Build.createNew.userList.remove(msg[1]);
                        Build.usersName.remove(msg[1]);
                        // Send new user list to each user
                        if (Build.createNew.userList.getItemCount() > 1) {
                            List<String> userList = new ArrayList();
                            for (int user = 0; user < Build.createNew.userList.getItemCount(); user++) {
                                userList.add(Build.createNew.userList.getItem(user));
                            }
                            String replace = userList.toString().replace(" ", "");
                            this.dataOutputStream.writeUTF("UserList " + replace);
                            this.dataOutputStream.flush();
                        } else{
                            // End socket
                            this.dataOutputStream.writeUTF("End");
                            this.dataOutputStream.flush();
                            socket.close();
                            break;
                        }
                    } else if(msg[0].equals("Chat")){ // Handle live chat information
                        String[] chat = msg[1].split(",");
                        Build.createNew.chat.add(chat[1] + ": " + chat[0]);
                    }else if (msg[0].equals("Text") || msg[0].equals("Line") ||msg[0].equals("Rec") ||msg[0].equals("Tri")||msg[0].equals("Circle")) {
                        // Handle draw functions
                        if (Build.createNew.userList.getItemCount() > 1) {
                            SynPaint.syn(Build.createNew.g, msg);
                        }
                    } else if (msg[0].equals("SynPaint")){
                        // Handle synchronize history to new user
                        this.dataOutputStream.writeUTF("PaintAll " + Build.createNew.recordString);
                    }
                    else{
                        System.out.println("Manager connection break");
                        socket.close();
                        break;
                    }
                }
            }
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }
}