package org.example.manager;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Connection extends Thread {
    public Socket socket;
    public int port;
//    public ArrayList<String> userName = new ArrayList<>();
    public String userName;

    public boolean kick = false;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

//    public Connection(String userName, Socket socket) {
//        this.userName.add(userName);
//        this.socket = socket;
//    }
    public Connection(String userName, Socket socket) {
        this.socket = socket;
//        this.userName.add(userName);
        this.userName = userName;
        try {
            dataInputStream = new DataInputStream(this.socket.getInputStream());
            dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        try {
//            InputStream inputStream = socket.getInputStream();
//            OutputStream outputStream = socket.getOutputStream();
//            DataInputStream dataInputStream = new DataInputStream(inputStream);
//            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            String inputMsg = dataInputStream.readUTF();
            while (!isInterrupted()){
                String[] msg = inputMsg.split(" ");
                if(msg[0].equals("join")){
//                    socket.close();
//                    System.out.println("msg: "+msg);
                    System.out.println("msg " + msg[1]);
                    String[] userInfo = msg[1].split(";");
                    System.out.println("name: "+userInfo[1]);
                    if(!userName.contains(userInfo[1])){
                        System.out.println("Manager Connecting");
//                        Build.users.add(new Connection(userInfo[1], ));
                        dataOutputStream.writeUTF("Connect");
                    } else{
                        System.out.println("Manager Reject");
                        dataOutputStream.writeUTF("Reject");
                        dataOutputStream.flush();
                        Build.users.remove(this);
                        break;
                    }

                }
            }

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}
