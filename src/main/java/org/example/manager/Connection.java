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
            System.out.println("MC start");
            while(true) {
                System.out.println("MC start2: " + dataInputStream);
                if (!this.isInterrupted()) {
//                if (dataInputStream != null) {
                    String inputMsg = this.dataInputStream.readUTF();
                    String[] msg = inputMsg.split(" ");
                    System.out.println("msg: " + inputMsg);
                    if (msg[0].equals("join")) {
                        System.out.println("name: " + msg[1]);
                        if (!Build.usersName.contains(msg[1])) {
                            int joinConfirm = JOptionPane.showConfirmDialog(null, msg[1] + " is trying to join your whiteboard");
                            System.out.println(joinConfirm);
                            if(joinConfirm == 0){
                                Build.usersName.add(msg[1]);
//                            System.out.println("Connection length (MC): " + RunServer.users.size());
                                this.dataOutputStream.writeUTF("Connect");
                                this.dataOutputStream.flush();
                            } else{
                                this.dataOutputStream.writeUTF("No");
                                this.dataOutputStream.flush();
                                socket.close();
                                break;
                            }
                        } else {
                            System.out.println("Manager Reject");
                            this.dataOutputStream.writeUTF("Reject");
                            this.dataOutputStream.flush();
                            socket.close();
                            break;
//                            Join.users.remove(this);
                        }
                    } else if (msg[0].equals("User")) {
                        Build.createNew.userList.add(msg[1]);
                        List<String> userList = new ArrayList();
                        for (int user = 0; user < Build.createNew.userList.getItemCount(); user++) {
                            userList.add(Build.createNew.userList.getItem(user));
                        }
                        String replace = userList.toString().replace(" ", "");
                        this.dataOutputStream.writeUTF("UserList " + replace);
                    } else if (msg[0].equals("Kicked")) {
                        Build.createNew.userList.remove(msg[1]);
                        Build.usersName.remove(msg[1]);
                        System.out.println("kicked: " + Build.createNew.userList.getItemCount());
                        if (Build.createNew.userList.getItemCount() > 1) {
                            List<String> userList = new ArrayList();
                            for (int user = 0; user < Build.createNew.userList.getItemCount(); user++) {
                                userList.add(Build.createNew.userList.getItem(user));
                            }
                            String replace = userList.toString().replace(" ", "");
                            this.dataOutputStream.writeUTF("UserList " + replace);
                            this.dataOutputStream.flush();
                        } else{
                            this.dataOutputStream.writeUTF("End");
                            this.dataOutputStream.flush();
//                            socket.close();
//                            break;
                        }
                    } else if(msg[0].equals("Chat")){
                        String[] chat = msg[1].split(",");
                        Build.createNew.chat.add(chat[1] + ": " + chat[0]);
                    }else if (msg[0].equals("Text") || msg[0].equals("Line") ||msg[0].equals("Rec") ||msg[0].equals("Tri")||msg[0].equals("Circle")) {
//                        String[] userInfo = msg[1].split(";");
                        System.out.println("manager msg:" + msg);
                        if (Build.createNew.userList.getItemCount() > 1) {
                            SynPaint.syn(Build.createNew.g, msg);
                        }
                    } else{
                        System.out.println("Manager connection break");
                        break;
                    }
                }
            }
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }
}