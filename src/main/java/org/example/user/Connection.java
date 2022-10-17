package org.example.user;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Connection {
    private Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    public Connection(Socket socket) {
        this.socket = socket;
        try {
            dataInputStream = new DataInputStream(this.socket.getInputStream());
            dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start()  {
        try {
//            String request = dataInputStream.readUTF();
            String history = dataInputStream.readUTF();
            System.out.println("get: "+history);
            String[] historyArray = history.split(" ");
            if(historyArray[0].equals("Text")){
                String record = historyArray[1];
                String[] recordArray = record.split(",");
                System.out.println("record" + record);

//                Font font = new Font("Courier", Font.PLAIN, 20);
//                g.setFont(font);
//                g.drawString(text, FirstPoint.x, FirstPoint.y);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
