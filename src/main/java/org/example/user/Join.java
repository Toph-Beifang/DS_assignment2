package org.example.user;

import org.example.manager.Build;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class Join {
    static String address;
    static int port;
    public static String userName;
    public static Connection connection;
    private static Socket socket;

    public static List<Connection> users = new ArrayList();
//    public static List<String> usersName = new ArrayList();

    public Join() {
    }

    public static void main(String[] args) {
        if (args.length == 3) {
            address = args[0];
            port = Integer.parseInt(args[1]);
            userName = args[2];
        } else {
            address = "localhost";
            port = 3030;
            userName = "user";
        }

        try {
            socket = new Socket(address, port);
            connection = new Connection(socket);
            users.add(connection);
        } catch (UnknownHostException var2) {
            throw new RuntimeException(var2);
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }

        run();
        connection.start();
    }

    private static void run() {
        try {
            System.out.println("connection name: " + connection);
            connection.dataOutputStream.writeUTF("join " + userName);
            System.out.println("joined");


        } catch (IOException var1) {
            throw new RuntimeException(var1);
        }
    }
}

