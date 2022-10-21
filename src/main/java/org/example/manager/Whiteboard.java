package org.example.manager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.example.Drawing;
import org.example.user.Join;

public class Whiteboard extends Frame implements MouseListener, MouseMotionListener, WindowListener, ActionListener {
    private static Drawing drawing;
    public Graphics g;
    public Graphics g2;
    String DrawMode = "";
    Point FirstPoint = new Point(0, 0);
    Point SecondPoint = new Point(0, 0);
    Label currentMode;
    Color color;
    ArrayList<String> drawRecord;

    String history = "";
    int colorRecord;
    public List userList;
    public List chat;

    public Whiteboard(String userName) {
        color = Color.BLACK;
        colorRecord = color.getRGB();
        drawRecord = new ArrayList();
        setBackground(Color.WHITE);
        setSize(800, 800);
        setVisible(true);
        setTitle(userName + "'s Whiteboard");
        addMouseListener(this);
        addMouseMotionListener(this);
        addWindowListener(this);
        Panel CommandPanel = new Panel(new GridLayout(0, 5));
        CommandPanel.setBackground(new Color(200, 200, 200));
        Button lineButton = new Button("Line");
        Button rectangleButton = new Button("Rectangle");
        Button triangleButton = new Button("Triangle");
        Button circleButton = new Button("Circle");
        Button freeHandButton = new Button("Free Hand");
        Button textButton = new Button("Text");
        Button clearButton = new Button("Clear");
        Button colorButton = new Button("Color");
        Button kickButton = new Button("Kick");
        Button chatButton = new Button("Chat");
        lineButton.setActionCommand("Line");
        rectangleButton.setActionCommand("Rectangle");
        triangleButton.setActionCommand("Triangle");
        circleButton.setActionCommand("Circle");
        freeHandButton.setActionCommand("Free Hand");
        textButton.setActionCommand("Text");
        clearButton.setActionCommand("Click to Clear");
        colorButton.setActionCommand("Color");
        chatButton.setActionCommand("Chat");
        lineButton.addActionListener(this);
        rectangleButton.addActionListener(this);
        triangleButton.addActionListener(this);
        circleButton.addActionListener(this);
        freeHandButton.addActionListener(this);
        textButton.addActionListener(this);
        clearButton.addActionListener(this);
        colorButton.addActionListener(this);
        chatButton.addActionListener(e -> {
            String chatText = JOptionPane.showInputDialog("Text input");
            System.out.println("chat " + chat);
            chat.add(userName + ": " + chatText);
            SynPaint.update("Chat " + chatText + "," + userName);
        });
        kickButton.addActionListener(e -> {
            String user = userList.getSelectedItem();
            if(user.equals(userName)){
                JOptionPane.showMessageDialog(null, "You can't kick yourself.");
            }else{
                SynPaint.update("Kick " + user);

            }
        });
        Label ModeLabel = new Label("Current Mode");
        currentMode = new Label();
        Font f1 = new Font("Arial", 1, 15);
        ModeLabel.setFont(f1);
        currentMode.setFont(f1);
        currentMode.setForeground(new Color(20, 200, 240));

        int width = 800;
        int height = 800;

        // Constructs a BufferedImage of one of the predefined image types.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        g =  bufferedImage.getGraphics();
        printAll(g);

        JComboBox menu = new JComboBox();
        menu.setModel(new DefaultComboBoxModel(new String[]{"New", "Save", "Open"}));
        menu.addActionListener((event) -> {
            if (menu.getSelectedItem().equals("Save")) {
                System.out.println("Saving the image");

                File file = new File("myimage2.png");
                try {
                    ImageIO.write(bufferedImage, "png", file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Save as JPEG
                file = new File("myimage.jpg");
                try {
                    ImageIO.write(bufferedImage, "jpg", file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        userList = new List(5);
        userList.add(userName);
        chat = new List(5);

        CommandPanel.add(lineButton);
        CommandPanel.add(rectangleButton);
        CommandPanel.add(triangleButton);
        CommandPanel.add(circleButton);
        CommandPanel.add(freeHandButton);
        CommandPanel.add(textButton);
        CommandPanel.add(clearButton);
        CommandPanel.add(colorButton);
        CommandPanel.add(ModeLabel);
        CommandPanel.add(currentMode);
        CommandPanel.add(menu);
        CommandPanel.add(userList);
        CommandPanel.add(kickButton);
        CommandPanel.add(chat);
        CommandPanel.add(chatButton);
        this.add("North", CommandPanel);

    }

    public void mouseClicked(MouseEvent e) {
        FirstPoint.setLocation(e.getX(), e.getY());
        g.setColor(color);
        System.out.println("color: " + color);
        switch (DrawMode) {
            case "Text":
                String text = JOptionPane.showInputDialog("Text input");
                if (text != null) {
                    Font font = new Font("Courier", 0, 20);
                    g.setFont(font);
                    g.drawString(text, FirstPoint.x, FirstPoint.y);
                    System.out.println("color: " + colorRecord);
                    history = "Text " + text + "," + FirstPoint.x + "," + FirstPoint.y;
                    SynPaint.update(history + "," + color.getRGB());
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
                SynPaint.update(history + "," + color.getRGB());
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
                SynPaint.update(history + "," + color.getRGB());
                break;
            case "Triangle":
                g.setColor(color);
                g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
                g.drawLine(FirstPoint.x, SecondPoint.y, SecondPoint.x, SecondPoint.y);
                g.drawLine(FirstPoint.x, SecondPoint.y, FirstPoint.x, FirstPoint.y);
                history = "Tri " + FirstPoint.x + "," + FirstPoint.y + "," + SecondPoint.x + "," + SecondPoint.y;
                SynPaint.update(history + "," + color.getRGB());
                break;
            case "Circle":
                g.setColor(color);
                double radius = Math.sqrt(Math.pow(Math.abs(FirstPoint.x - SecondPoint.x), 2) + Math.pow(Math.abs(FirstPoint.y - SecondPoint.y), 2));
                g.drawOval(Math.min(FirstPoint.x, SecondPoint.x), Math.min(FirstPoint.y, SecondPoint.y), (int) radius * 2, (int) radius * 2);
                history = "Circle " + Math.min(FirstPoint.x, SecondPoint.x) + "," + Math.min(FirstPoint.y, SecondPoint.y) + "," + (int) radius * 2;
                SynPaint.update(history + "," + color.getRGB());
                break;
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        g.setColor(color);
        if (DrawMode.compareTo("Free Hand") == 0) {
            if (SecondPoint.x != 0 && SecondPoint.y != 0) {
                FirstPoint.x = SecondPoint.x;
                FirstPoint.y = SecondPoint.y;
            }

            SecondPoint.setLocation(e.getX(), e.getY());
            g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
            history = "Line " + FirstPoint.x + "," + FirstPoint.y + "," + SecondPoint.x + "," + SecondPoint.y;
            SynPaint.update(history + "," + color.getRGB());
        }

    }

    public void mouseMoved(MouseEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        SynPaint.update("CloseAll");
        this.dispose();
        System.exit(1);
    }

    public void windowClosed(WindowEvent e) {

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
}