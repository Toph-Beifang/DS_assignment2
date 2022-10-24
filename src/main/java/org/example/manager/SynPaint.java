package org.example.manager;

import org.example.user.Join;

import java.awt.*;
import java.io.IOException;

public class SynPaint extends Panel {
    public SynPaint() {
    }

    public static void syn(Graphics g, String[] historyArray) {
        if (historyArray[0].equals("Text")) {
            // Handle write text
            String record = historyArray[1];
            String[] recordArray = record.split(",");
            Font font = new Font("Courier", 0, 20);
            // Set text color
            g.setColor(new Color(Integer.parseInt(recordArray[3])));
            g.setFont(font);
            g.drawString(recordArray[0], Integer.parseInt(recordArray[1]), Integer.parseInt(recordArray[2]));
        }else if (historyArray[0].equals("Line")) {
            // Handle draw line
            System.out.println("Line...: " + g);
            String record = historyArray[1];
            String[] recordArray = record.split(",");
            // Set line color
            g.setColor(new Color(Integer.parseInt(recordArray[4])));
            g.drawLine(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[1]),
                    Integer.parseInt(recordArray[2]), Integer.parseInt(recordArray[3]));
        }
        else if (historyArray[0].equals("Rec")) {
            // Handle draw rectangle
            String record = historyArray[1];
            String[] recordArray = record.split(",");
            // Set rectangle color
            g.setColor(new Color(Integer.parseInt(recordArray[4])));
            g.drawRect(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[1]),
                    Integer.parseInt(recordArray[2]), Integer.parseInt(recordArray[3]));
        }
        else if (historyArray[0].equals("Tri")) {
            // Handle draw triangle
            String record = historyArray[1];
            String[] recordArray = record.split(",");
            // Set triangle color
            g.setColor(new Color(Integer.parseInt(recordArray[4])));
            g.drawLine(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[1]),
                    Integer.parseInt(recordArray[2]), Integer.parseInt(recordArray[3]));
            g.drawLine(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[3]),
                    Integer.parseInt(recordArray[2]), Integer.parseInt(recordArray[3]));
            g.drawLine(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[3]),
                    Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[1]));
        } else if (historyArray[0].equals("Circle")) {
            // Handle draw circle
            String record = historyArray[1];
            String[] recordArray = record.split(",");
            // Set circle color
            g.setColor(new Color(Integer.parseInt(recordArray[3])));
            g.drawOval(Integer.parseInt(recordArray[0]), Integer.parseInt(recordArray[1]),
                    Integer.parseInt(recordArray[2]), Integer.parseInt(recordArray[2]));
        }
    }

    public static void update (String history) {
        try {
            // Update all the history to other users
            if(Build.users.size() > 0){
                Build.users.get(0).dataOutputStream.writeUTF(history);
            }
        } catch (IOException var10) {
            throw new RuntimeException(var10);
        }
    }

    public static void sendPaint (String history) {
        try {
            // Send history to owner
            Join.connection.dataOutputStream.writeUTF(history);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
