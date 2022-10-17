package org.example.user;

import org.example.manager.Build;
import org.example.manager.Whiteboard;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Join {
    static String address;
    static int port;
    static String userName;

    public static Connection connection;
    public static Socket socket;

    public static void main(String[] args){
        if (args.length == 3){ // add variable valid or not
            address = args[0];
            port = Integer.parseInt(args[1]);
            userName = args[2];
        } else {
            address = "localhost";
            port = 3030;
            userName = "user";
        }

        try{
            socket = new Socket(address, port);
            connection = new Connection(socket);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EventQueue.invokeLater(()->{
//            new Whiteboard(userName);
            start();
        });


    }

    private static void start(){
        try {
//            InputStream inputStream = socket.getInputStream();
//            OutputStream outputStream = socket.getOutputStream();
//            DataInputStream dataInputStream = new DataInputStream(inputStream);
//            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            connection.dataOutputStream.writeUTF("join " + socket + ";" + userName);
            String join = connection.dataInputStream.readUTF();
            System.out.println(join);
            if(join.equals("Connect")){
                System.out.println("User Connecting");
                new org.example.user.Whiteboard(socket, userName);
            }else{
                System.out.println("Username already exist");
                connection.dataOutputStream.writeUTF("End");
                socket.close();
                System.exit(1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

