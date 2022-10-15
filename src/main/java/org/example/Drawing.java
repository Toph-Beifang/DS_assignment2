package org.example;
import java.awt.*;
import javax.swing.JFrame;

public class Drawing extends Canvas  {

    public static void main(String[] args) {
        JFrame frame = new JFrame("My Drawing");
        Canvas canvas = new Drawing();
        canvas.setSize(400, 400);
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);

    }

    public void paint(Graphics g) {
        paintCircle(100,100);
        paintRectangle();
        paintLine();
        paintText();
        paintTriangle(80,80,60,100,120,100);
    }

    public void paintRectangle() {
        Graphics g = getGraphics();
        g.setColor(Color.red);
        g.drawRect(30, 30, 100, 100);
    }
    public void paintCircle(int x, int y) {
        Graphics g = getGraphics();
        g.setColor(Color.blue);
        g.drawRoundRect(x, y, 90, 90, 200, 200);
    }
    public void paintLine() {
        Graphics g = getGraphics();
        g.setColor(Color.green);
        g.drawLine(20, 50, 360, 100);
    }
    public void paintText() {
        Graphics g = getGraphics();
        g.setColor(Color.black);
        g.drawString("This is gona be awesome", 200, 200);
    }

    public void paintTriangle( int x1, int y1,int x2, int y2, int x3, int y3) {
        Graphics g = getGraphics();
        g.setColor(Color.orange);
        g.drawLine(x1, y1, x2, y2);
        g.drawLine(x2, y2, x3, y3);
        g.drawLine(x3, y3, x1, y1);
    }


}