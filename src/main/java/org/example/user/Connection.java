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
                // Receive message from manager
                String inputMsg = this.dataInputStream.readUTF();
                String[] historyArray = inputMsg.split(" ");
                if(historyArray.length <= 1) { // Handle request of system (join and close)
                    if (inputMsg.equals("Connect")) { // Open a white board
                        EventQueue.invokeLater(() -> {
                            this.whiteboard = new Whiteboard(this, Join.userName);
                        });
                    } else if (inputMsg.equals("Reject")) {
                        // Reject because username already exist
                        System.out.println("Username already exist");
                        Join.users.remove(this);
                        this.socket.close();
                        System.exit(1);
                    }else if (inputMsg.equals("No")) {
                        // Manager reject
                        System.out.println("Owner reject your join request");
                        this.socket.close();
                        System.exit(1);
                    }else if(inputMsg.equals("CloseAll")){
                        // Manager closed window, so close all users' window
                        Join.usersName.clear();
                        for (Connection c:Join.users) {
                            c.whiteboard.dispose();
                        }
                        Join.users.clear();
                        dataOutputStream.writeUTF("clear");
                        this.socket.close();
                        System.exit(1);
                    }
                } else{
                    if(historyArray[0].equals("UserList")){
                        // Get user list after joined in
                        String replace = historyArray[1].replaceAll("^\\[|]$", "");
                        List<String> userList = new ArrayList<String>(Arrays.asList(replace.split(",")));
                        for (String user: userList) {
                            whiteboard.userList.add(user);
                        }
                        // Get paint history after joined in
                        this.dataOutputStream.writeUTF("SynPaint");
                    } else if(historyArray[0].equals("Kick")){
                        // Remove kicked user's information
                        int index = Join.usersName.indexOf(historyArray[1]);
                        Join.usersName.remove(index);
                        Join.users.get(index).whiteboard.dispose();
                        Join.users.remove(index);
                        this.dataOutputStream.writeUTF("Kicked " + historyArray[1]);
                        this.socket.close();
                        System.exit(1);
                    }else if(historyArray[0].equals("Chat")){
                        // Handle live chat
                        String[] chat = historyArray[1].split(",");
                        whiteboard.chat.add(chat[1] + ": " + chat[0]);
                    }
                    else if(historyArray[0].equals("PaintAll")){
                        // Handle all history drawing
                        System.out.println("PaintAll: " + inputMsg);
                        String[] paint = inputMsg.split(" ",2);
                        String[] paintL = paint[1].split(";");
                        for (String line: paintL) {
                            String[] paintLine = line.split(" ");
                            SynPaint.syn(whiteboard.getG(), paintLine);
                        }
                    }
                    else if (historyArray[0].equals("Text") || historyArray[0].equals("Line")
                            ||historyArray[0].equals("Rec") ||historyArray[0].equals("Tri")
                            ||historyArray[0].equals("Circle"))
                    {
                        // Handle real time draw
                        SynPaint.syn(whiteboard.getG(), historyArray);
                    }
                }
            }

        } catch (IOException var5) {
            throw new RuntimeException(var5);
        }
    }
}
