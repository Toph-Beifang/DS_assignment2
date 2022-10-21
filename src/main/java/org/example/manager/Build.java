//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.example.manager;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class Build {
    static String address;
    static int port;
    static String userName;
    public static Whiteboard createNew;
    private JFrame frame;
    public static List<Connection> users = new ArrayList();
    public static List<String> usersName = new ArrayList();


    public Build() {
    }

    public static void main(String[] args) {
        if (args.length == 3) {
            address = args[0];
            port = Integer.parseInt(args[1]);
            userName = args[2];
        } else {
            address = "localhost";
            port = 3030;
            userName = "sally";
        }

        EventQueue.invokeLater(() -> {
            createNew = new Whiteboard(userName);
        });
        run(port, userName);
    }

    protected static void run(int port, String userName) {
        System.out.println("start");
        usersName.add(userName);
        ServerSocket serverSocket = null;
        Connection connection = null;

        try {
            serverSocket = new ServerSocket(port);
            Socket client;
            while(true) {
                client = serverSocket.accept();
                connection = new Connection(client);
                users.add(connection);
                connection.start();
            }
        } catch (IOException var5) {
            System.out.println("Build failed!");
            System.exit(1);
            var5.printStackTrace();
        }
    }
}
