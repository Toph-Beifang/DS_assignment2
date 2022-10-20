package org.example.manager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RunServer {
    public static List<Connection> users = new ArrayList();
    public static List<String> usersName = new ArrayList();

    static void run(int port, String userName) {
        System.out.println("start");
        usersName.add(userName);
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("start connect");
            Socket client;
            while(true) {
                System.out.println("true");
                client = serverSocket.accept();
                Connection connection = new Connection(client);
                users.add(connection);
                System.out.println("Connection length: " + users.size());
                connection.start();
            }
        } catch (IOException var5) {
            System.out.println("Build failed!");
            System.exit(1);
            var5.printStackTrace();
        }
    }
}
