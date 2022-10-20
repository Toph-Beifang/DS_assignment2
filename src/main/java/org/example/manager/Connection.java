package org.example.manager;

import org.example.user.Join;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
                System.out.println("MC start2");
                if (!this.isInterrupted()) {
                    String inputMsg = this.dataInputStream.readUTF();
                    String[] msg = inputMsg.split(" ");
                    System.out.println("msg: " + inputMsg);
                    if (msg[0].equals("join")) {
                        System.out.println("name: " + msg[1]);
                        if (!Build.usersName.contains(msg[1])) {
                            Build.usersName.add(msg[1]);
//                            System.out.println("Connection length (MC): " + RunServer.users.size());
                            this.dataOutputStream.writeUTF("Connect");
                            this.dataOutputStream.flush();
                        } else {
                            System.out.println("Manager Reject");
                            this.dataOutputStream.writeUTF("Reject");
                            this.dataOutputStream.flush();
//                            Join.users.remove(this);
                        }
                    } else {
//                        String[] userInfo = msg[1].split(";");
                        System.out.println("manager msg:" + msg);
                        SynPaint.syn(Build.createNew.g, msg);
                    }


                }
            }
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }
}