package org.example.user;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.example.Drawing;
import org.example.manager.Build;
import org.example.manager.SynPaint;

public class Whiteboard extends Frame implements MouseListener, MouseMotionListener, WindowListener, ActionListener {
    private static Drawing drawing;
    Graphics g;
    String DrawMode = "";
    Point FirstPoint = new Point(0, 0);
    Point SecondPoint = new Point(0, 0);
    Label currentMode;
    Color color;
    ArrayList<String> drawRecord;

    String history = "";
    int colorRecord;

    public Whiteboard(String userName) {
        color = Color.BLACK;
        colorRecord = color.getRGB();
        drawRecord = new ArrayList();
//        this.connection = connection;
        try {
            Join.connection.dataOutputStream.writeUTF("begin ");
            Join.connection.dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setBackground(Color.WHITE);
        setSize(400, 400);
        setVisible(true);
        setTitle(userName + "'s Whiteboard");
        addMouseListener(this);
        addMouseMotionListener(this);
        addWindowListener(this);
        Panel CommandPanel = new Panel(new GridLayout(0, 3));
        CommandPanel.setBackground(new Color(200, 200, 200));
        Button lineButton = new Button("Line");
        Button rectangleButton = new Button("Rectangle");
        Button triangleButton = new Button("Triangle");
        Button circleButton = new Button("Circle");
        Button freeHandButton = new Button("Free Hand");
        Button textButton = new Button("Text");
        Button clearButton = new Button("Clear");
        Button colorButton = new Button("Color");
        lineButton.setActionCommand("Line");
        rectangleButton.setActionCommand("Rectangle");
        triangleButton.setActionCommand("Triangle");
        circleButton.setActionCommand("Circle");
        freeHandButton.setActionCommand("Free Hand");
        textButton.setActionCommand("Text");
        clearButton.setActionCommand("Click to Clear");
        colorButton.setActionCommand("Color");
        lineButton.addActionListener(this);
        rectangleButton.addActionListener(this);
        triangleButton.addActionListener(this);
        circleButton.addActionListener(this);
        freeHandButton.addActionListener(this);
        textButton.addActionListener(this);
        clearButton.addActionListener(this);
        colorButton.addActionListener(this);
        Label ModeLabel = new Label("Current Mode");
        currentMode = new Label();
        Font f1 = new Font("Arial", 1, 15);
        ModeLabel.setFont(f1);
        currentMode.setFont(f1);
        currentMode.setForeground(new Color(20, 200, 240));
        JComboBox menu = new JComboBox();
        menu.setModel(new DefaultComboBoxModel(new String[]{"New", "Save", "Open"}));
        menu.addActionListener((event) -> {
        });
        CommandPanel.add(lineButton);
        CommandPanel.add(rectangleButton);
        CommandPanel.add(triangleButton);
        CommandPanel.add(circleButton);
        CommandPanel.add(freeHandButton);
        CommandPanel.add(textButton);
        CommandPanel.add(clearButton);
        CommandPanel.add(colorButton);
        CommandPanel.add(menu);
        CommandPanel.add(ModeLabel);
        CommandPanel.add(currentMode);
        this.add("North", CommandPanel);
        g = getGraphics();
    }

    public void mouseClicked(MouseEvent e) {
        FirstPoint.setLocation(e.getX(), e.getY());
        switch (DrawMode) {
            case "Text":
                String text = JOptionPane.showInputDialog("Text input");
                if (text != null) {
                    Font font = new Font("Courier", 0, 20);
                    g.setFont(font);
                    g.drawString(text, FirstPoint.x, FirstPoint.y);
                    history = "Text " + text + "," + FirstPoint.x + "," + FirstPoint.y;
                    SynPaint.sendPaint(history + "," + color.getRGB());
                }
                break;
            case "Click to Clear":
                repaint();
        }

    }

    public void mousePressed(MouseEvent e) {
        FirstPoint.setLocation(0, 0);
        SecondPoint.setLocation(0, 0);
        FirstPoint.setLocation(e.getX(), e.getY());
    }

    public void mouseReleased(MouseEvent e) {
        SecondPoint.setLocation(e.getX(), e.getY());
        switch (DrawMode) {
            case "Line":
                g.setColor(color);
                g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
                history = "Line " + FirstPoint.x + "," + FirstPoint.y + "," + SecondPoint.x + "," + SecondPoint.y;
                SynPaint.sendPaint(history + "," + color.getRGB());
                break;
            case "Rectangle":
                g.setColor(color);
                if (FirstPoint.x < SecondPoint.x && FirstPoint.y > SecondPoint.y) {
                    g.drawRect(FirstPoint.x, SecondPoint.y, Math.abs(FirstPoint.x - SecondPoint.x), Math.abs(FirstPoint.y - SecondPoint.y));
                    history = "Rec " + FirstPoint.x + "," + SecondPoint.y + "," + Math.abs(FirstPoint.x - SecondPoint.x) + "," + Math.abs(FirstPoint.y - SecondPoint.y);
                } else if (FirstPoint.x > SecondPoint.x && FirstPoint.y > SecondPoint.y) {
                    g.drawRect(SecondPoint.x, SecondPoint.y, Math.abs(FirstPoint.x - SecondPoint.x), Math.abs(FirstPoint.y - SecondPoint.y));
                    history = "Rec " + SecondPoint.x + "," + SecondPoint.y + "," + Math.abs(FirstPoint.x - SecondPoint.x) + "," + Math.abs(FirstPoint.y - SecondPoint.y);
                } else if (FirstPoint.x > SecondPoint.x && FirstPoint.y < SecondPoint.y) {
                    g.drawRect(SecondPoint.x, FirstPoint.y, Math.abs(FirstPoint.x - SecondPoint.x), Math.abs(FirstPoint.y - SecondPoint.y));
                    history = "Rec " + SecondPoint.x + "," + FirstPoint.y + "," + Math.abs(FirstPoint.x - SecondPoint.x) + "," + Math.abs(FirstPoint.y - SecondPoint.y);
                } else {
                    g.drawRect(FirstPoint.x, FirstPoint.y, Math.abs(FirstPoint.x - SecondPoint.x), Math.abs(FirstPoint.y - SecondPoint.y));
                    history = "Rec " + FirstPoint.x + "," + FirstPoint.y + "," + Math.abs(FirstPoint.x - SecondPoint.x) + "," + Math.abs(FirstPoint.y - SecondPoint.y);
                }
                SynPaint.sendPaint(history + "," + color.getRGB());
                break;
            case "Triangle":
                g.setColor(color);
                g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
                g.drawLine(FirstPoint.x, SecondPoint.y, SecondPoint.x, SecondPoint.y);
                g.drawLine(FirstPoint.x, SecondPoint.y, FirstPoint.x, FirstPoint.y);
                history = "Tri " + FirstPoint.x + "," + FirstPoint.y + "," + SecondPoint.x + "," + SecondPoint.y;
                SynPaint.sendPaint(history + "," + color.getRGB());
                break;
            case "Circle":
                g.setColor(color);
                g.drawRoundRect(FirstPoint.x, FirstPoint.y, Math.abs(FirstPoint.x - SecondPoint.x), Math.abs(FirstPoint.y - SecondPoint.y), 200, 200);
                history = "Circle " + FirstPoint.x + "," + FirstPoint.y + "," + Math.abs(FirstPoint.x - SecondPoint.x) + "," + Math.abs(FirstPoint.y - SecondPoint.y);
                SynPaint.sendPaint(history + "," + color.getRGB());
                break;
        }

    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        if (DrawMode.compareTo("Free Hand") == 0) {
            if (SecondPoint.x != 0 && SecondPoint.y != 0) {
                FirstPoint.x = SecondPoint.x;
                FirstPoint.y = SecondPoint.y;
            }

            SecondPoint.setLocation(e.getX(), e.getY());
            g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
            history = "Line " + FirstPoint.x + "," + FirstPoint.y + "," + SecondPoint.x + "," + SecondPoint.y;
            SynPaint.sendPaint(history + "," + color.getRGB());
        }

    }

    public void mouseMoved(MouseEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
        this.dispose();
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
        DrawMode = e.getActionCommand();
        currentMode.setText(DrawMode);
        FirstPoint.setLocation(0, 0);
        SecondPoint.setLocation(0, 0);
        if (e.getActionCommand() == "Color") {
            Color color = JColorChooser.showDialog((Component)null, "Pick a color", (Color)null);
            if (color != null) {
                this.color = color;
                colorRecord = color.getRGB();
            }
        }

    }

    public Graphics getG() {
        return g;
    }
}
