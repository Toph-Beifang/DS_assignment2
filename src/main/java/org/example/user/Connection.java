package org.example.user;

import org.example.manager.SynPaint;

import java.awt.*;
import java.io.*;
import java.net.Socket;

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
//            while(this.socket.isConnected()) {
            while(true) {
                String inputMsg = this.dataInputStream.readUTF();
                System.out.println("reading msg: " + inputMsg);
                String[] historyArray = inputMsg.split(" ");
                System.out.println("length: " + historyArray.length);
                if(historyArray.length <= 1) {
                    if (inputMsg.equals("Connect")) {
                        System.out.println("User Connecting");
                        EventQueue.invokeLater(() -> {
                            whiteboard = new Whiteboard(Join.userName);
                        });
//                        System.out.println("Join built");
//                        this.dataOutputStream.writeUTF("Text 123,226,186");
//                        System.out.println("Text end");
                    } else if (inputMsg.equals("Reject")) {
                        System.out.println("Username already exist");
//                        this.dataOutputStream.writeUTF("End");
                        this.socket.close();
                        System.exit(1);
                    }
                } else{
                    SynPaint.syn(whiteboard.getG(), historyArray);
                }
            }

        } catch (IOException var5) {
            throw new RuntimeException(var5);
        }
    }
}
