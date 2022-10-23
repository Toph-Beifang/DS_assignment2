package org.example.user;

import org.example.manager.Build;
import org.example.manager.SynPaint;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Connection {
    private Socket socket;

    public DataInputStream dataInputStream = null;
    public DataOutputStream dataOutputStream = null;

    public Whiteboard whiteboard;

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
            while(this.socket.isConnected()) {
//            while(true) {
                System.out.println("UC start");
                String inputMsg = this.dataInputStream.readUTF();
                String[] historyArray = inputMsg.split(" ");
                System.out.println("reading msg: " + inputMsg + ";length: " + historyArray.length);
                if(historyArray.length <= 1) {
                    if (inputMsg.equals("Connect")) {
                        System.out.println("Connect");
                        EventQueue.invokeLater(() -> {
                            this.whiteboard = new Whiteboard(this, Join.userName);
                        });
                    } else if (inputMsg.equals("Reject")) {
                        System.out.println("Username already exist");
//                        this.dataOutputStream.writeUTF("End");
                        Join.users.remove(this);
                        this.socket.close();
                        System.exit(1);
                    }else if (inputMsg.equals("No")) {
                        System.out.println("Owner reject your join request");
                        this.socket.close();
                        System.exit(1);
                    }else if(inputMsg.equals("CloseAll")){
                        Join.usersName.clear();
                        System.out.println("username: " + Join.usersName);
                        System.out.println("user: " + Join.users);
                        for (Connection c:Join.users) {
                            c.whiteboard.dispose();
                        }
                        Join.users.clear();
                        System.out.println("user2: " + Join.users);
                        dataOutputStream.writeUTF("clear");
                        this.socket.close();
                        System.exit(1);
                    } else{
                        break;
                    }
                } else{
                    if(historyArray[0].equals("UserList")){
                        System.out.println("UserList");
                        String replace = historyArray[1].replaceAll("^\\[|]$", "");
                        List<String> userList = new ArrayList<String>(Arrays.asList(replace.split(",")));
                        for (String user: userList) {
                            whiteboard.userList.add(user);
                        }
                        this.dataOutputStream.writeUTF("SynPaint");
                    } else if(historyArray[0].equals("Kick")){
                        System.out.println("Kick");
                        int index = Join.usersName.indexOf(historyArray[1]);
                        System.out.println("kick " + Join.users);
                        Join.usersName.remove(index);
                        Join.users.get(index).whiteboard.dispose();
                        Join.users.remove(index);
                        System.out.println("kick " + Join.users);
                        this.dataOutputStream.writeUTF("Kicked " + historyArray[1]);
                        this.socket.close();
                        System.exit(1);
                    }else if(historyArray[0].equals("Chat")){
                        System.out.println("Chat");
                        String[] chat = historyArray[1].split(",");
                        whiteboard.chat.add(chat[1] + ": " + chat[0]);
                    }
                    else if(historyArray[0].equals("PaintAll")){
                        String[] paint = inputMsg.split(" ",2);
                        String[] paintL = paint[1].split(";");
                        for (String line: paintL) {
                            String[] paintLine = line.split(" ");
                            System.out.println("Line: " + whiteboard.getG());
                            SynPaint.syn(whiteboard.getG(), paintLine);
                        }
                    }
                    else if (historyArray[0].equals("Text") || historyArray[0].equals("Line") ||historyArray[0].equals("Rec") ||historyArray[0].equals("Tri")||historyArray[0].equals("Circle"))
                    {
                        System.out.println("Paint");
                        SynPaint.syn(whiteboard.getG(), historyArray);
                    }
                }
            }

        } catch (IOException var5) {
            throw new RuntimeException(var5);
        }
    }
}
