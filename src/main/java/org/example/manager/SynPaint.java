package org.example.manager;

import org.example.user.Join;

import java.awt.*;
import java.io.IOException;

public class SynPaint extends Panel {
    public SynPaint() {
    }

    public static void syn(Graphics g, String[] historyArray) {
        if (historyArray[0].equals("Text")) {
            String record = historyArray[1];
            String[] recordArray = record.split(",");
            System.out.println("Text...");
            Font font = new Font("Courier", 0, 20);
            g.setColor(new Color(Integer.parseInt(recordArray[3])));
            g.setFont(font);
            g.drawString(recordArray[0], Integer.parseInt(recordArray[1]), Integer.parseInt(recordArray[2]));
        }else if (historyArray[0].equals("Line")) {
            String record = historyArray[1];
            String[] recordArray = record.split(",");
            g.setColor(new Color(Integer.parseInt(recordArray[4])));
            System.out.println("Line...");
            g.drawLine(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[1]), Integer.parseInt(recordArray[2]), Integer.parseInt(recordArray[3]));
        }
        else if (historyArray[0].equals("Rec")) {
            String record = historyArray[1];
            String[] recordArray = record.split(",");
            g.setColor(new Color(Integer.parseInt(recordArray[4])));
            System.out.println("Rec...");
            g.drawRect(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[1]), Integer.parseInt(recordArray[2]), Integer.parseInt(recordArray[3]));
        }
        else if (historyArray[0].equals("Tri")) {
            String record = historyArray[1];
            String[] recordArray = record.split(",");
            g.setColor(new Color(Integer.parseInt(recordArray[4])));
            System.out.println("Tri...");
            g.drawLine(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[1]), Integer.parseInt(recordArray[2]), Integer.parseInt(recordArray[3]));
            g.drawLine(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[3]), Integer.parseInt(recordArray[2]), Integer.parseInt(recordArray[3]));
            g.drawLine(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[3]), Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[1]));
        } else if (historyArray[0].equals("Circle")) {
            String record = historyArray[1];
            String[] recordArray = record.split(",");
            g.setColor(new Color(Integer.parseInt(recordArray[4])));
            System.out.println("Circle...");
            g.drawRoundRect(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[1]), Integer.parseInt(recordArray[2]), Integer.parseInt(recordArray[3]), 200, 200);
        }
    }

    public static void update (String history) {
//        System.out.println("Size: " +Build.users.size());
//        for (int i = 0; i < Build.users.size(); ++i) {
//            Connection conUser = Build.users.get(i);
//            System.out.println(conUser);
//            try {
//                conUser.dataOutputStream.writeUTF(history);
//            } catch (IOException var10) {
//                throw new RuntimeException(var10);
//            }
//        }

        try {
            if(Build.users.size() > 0){
                Build.users.get(0).dataOutputStream.writeUTF(history);
            }
        } catch (IOException var10) {
            throw new RuntimeException(var10);
        }
    }

    public static void sendPaint (String history) {
        try {
            System.out.println("send paint");
            Join.connection.dataOutputStream.writeUTF(history);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
