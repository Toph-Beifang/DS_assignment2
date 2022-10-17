package org.example.manager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Build {
    static String address;
    static int port;
    static String userName;
    public static Whiteboard createNew;

    private JFrame frame;
    public static ArrayList<Connection> users = new ArrayList<>();

    public static void main(String[] args){
        if (args.length == 3){ // add variable valid or not
            address = args[0];
            port = Integer.parseInt(args[1]);
            userName = args[2];
        } else {
            address = "localhost";
            port = 3030;
            userName = "sally";
        }
        EventQueue.invokeLater(()->{
            createNew = new Whiteboard(userName);
        });
        run(port, userName);
//        new Whiteboard(userName);
    }

    private static void run(int port, String userName) {
        ServerSocket serverSocket;
//        users.add(user);
        System.out.println("start");
        try {
            serverSocket = new ServerSocket(port);
            Socket client;
            while (true){
                client = serverSocket.accept();
                System.out.println("name" + userName);
                Connection connection = new Connection(userName, client);
                users.add(connection);
                connection.start();
            }
        } catch (IOException e) {
            System.out.println("Build failed!");
            System.exit(1);
            e.printStackTrace();
        }

    }
}
