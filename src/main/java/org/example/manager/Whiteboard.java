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
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

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
    String recordString = "";
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

        //save current canvas to a record file
        JComboBox menu = new JComboBox();
        menu.setModel(new DefaultComboBoxModel(new String[]{"New", "Save","Save as","Open","Close"}));
        menu.addActionListener((event) -> {

            //create a new record file
            if (menu.getSelectedItem().equals("New")) {
                try {
                    String filename = JOptionPane.showInputDialog("New canvas record file name: ")+".txt";
                    File canvasRecord = new File(filename);
                    if (canvasRecord.createNewFile()) {
                        System.out.println("File created: " + canvasRecord.getName());
                        JOptionPane.showMessageDialog(null, "Canvas saved successfully");
                    } else {
                        JOptionPane.showMessageDialog(null, "File already exists.");
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }

            //choose a file to save to and overwrite it with current canvas's record
            else if (menu.getSelectedItem().equals("Save")) {
                //Choosing a file using filechooser
                JFileChooser filechooser= new JFileChooser();
                if (filechooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    String abtolutePath = filechooser.getSelectedFile().getAbsolutePath();
                    FileWriter myWriter = null;
                    try {
                        myWriter = new FileWriter(abtolutePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        myWriter.write(String.valueOf(recordString));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        myWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    JOptionPane.showMessageDialog(null, "Canvas saved successfully");
                }
            }

            //save record to a new file of a given name
            else if (menu.getSelectedItem().equals("Save as")) {
                try {
                    String filename = JOptionPane.showInputDialog("Filename: ")+".txt";
                    File canvasRecord = new File(filename);
                    if (canvasRecord.createNewFile()) {
                        System.out.println("File created: " + canvasRecord.getName());
                        try {
                            FileWriter myWriter = new FileWriter(filename);
                            myWriter.write(String.valueOf(recordString));
                            myWriter.close();
                            JOptionPane.showMessageDialog(null, "Canvas saved successfully");
                        } catch (IOException e) {
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "File already exists.");
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }

            //Opening existing record files
            else if (menu.getSelectedItem().equals("Open")){
                //Choosing a file using filechooser
                JFileChooser filechooser= new JFileChooser();
                if (filechooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    String abtolutePath = filechooser.getSelectedFile().getAbsolutePath();
                    //try to read from that file
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(abtolutePath));
                        String recordLine = "";
                        recordLine = bufferedReader.readLine();
                        //System.out.println(recordLine);
                        recordString = recordLine;
                        bufferedReader.close();

                        String[] recordList= recordLine.split(";");
                        for (String record : recordList) {
                            recoverCanvas(record);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            else if (menu.getSelectedItem().equals("Close")){
                recordString = "";
                repaint();
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

        g =  getGraphics();
    }
    //a helper function to recover the drawings from the record file
    public void recoverCanvas(String record){

        String[] parameterList = record.split("/");
        //redrawing the lines from record
        if (parameterList[0].equals("Line")) {
            int FirstPointX = Integer.parseInt(parameterList[1]);
            int FirstPointY = Integer.parseInt(parameterList[2]);
            int SecondPointX = Integer.parseInt(parameterList[3]);
            int SecondPointY = Integer.parseInt(parameterList[4]);
            g.setColor(new Color(Integer.parseInt(parameterList[5])));
            g.drawLine(FirstPointX, FirstPointY, SecondPointX, SecondPointY);
        }
        //redrawing the texts from record
        else if (parameterList[0].equals("Text")) {
            String text = parameterList[1];
            int FirstPointX = Integer.parseInt(parameterList[2]);
            int FirstPointY = Integer.parseInt(parameterList[3]);
            g.setColor(new Color(Integer.parseInt(parameterList[4])));
            //g.setFont(font);
            g.drawString(text, FirstPointX, FirstPointY);
        }
        //redrawing the triangles from record
        else if (parameterList[0].equals("Tri")) {
            int FirstPointX = Integer.parseInt(parameterList[1]);
            int FirstPointY = Integer.parseInt(parameterList[2]);
            int SecondPointX = Integer.parseInt(parameterList[3]);
            int SecondPointY = Integer.parseInt(parameterList[4]);

            g.setColor(new Color(Integer.parseInt(parameterList[5])));
            g.drawLine(FirstPointX, FirstPointY, SecondPointX , SecondPointY);
            g.drawLine(FirstPointX, SecondPointY, SecondPointX , SecondPointY);
            g.drawLine(FirstPointX, SecondPointY, FirstPointX, FirstPointY);
        }
        //redrawing the circles from record
        else if (parameterList[0].equals("Circle")) {
            int X = Integer.parseInt(parameterList[1]);
            int Y = Integer.parseInt(parameterList[2]);
            int radius = Integer.parseInt(parameterList[3]);

            g.setColor(new Color(Integer.parseInt(parameterList[4])));
            g.drawOval(X, Y, radius, radius);
        }
        //redrawing the rectangles from record
        else if (parameterList[0].equals("Rec")) {
            int X = Integer.parseInt(parameterList[1]);
            int Y = Integer.parseInt(parameterList[2]);
            int Height = Integer.parseInt(parameterList[3]);
            int Width = Integer.parseInt(parameterList[4]);

            g.setColor(new Color(Integer.parseInt(parameterList[5])));
            g.drawRect(X,Y,Height,Width);
        }
        //redrawing the freehands from record
        else if (parameterList[0].equals("Freehand")) {
            int FirstPointX = Integer.parseInt(parameterList[1]);
            int FirstPointY = Integer.parseInt(parameterList[2]);
            int SecondPointX = Integer.parseInt(parameterList[3]);
            int SecondPointY = Integer.parseInt(parameterList[4]);
            g.setColor(new Color(Integer.parseInt(parameterList[5])));
            g.drawLine(FirstPointX, FirstPointY, SecondPointX, SecondPointY);
        }
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
                    recordString += "Text/" + text + "/" + FirstPoint.x + "/" + FirstPoint.y+"/"+colorRecord+";";
                    SynPaint.update(history + "," + color.getRGB());
                }
                break;
            case "Click to Clear":
                recordString = "";
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
                recordString += "Line/"+ FirstPoint.x + "/" + FirstPoint.y+"/"+ SecondPoint.x + "/" + SecondPoint.y+"/"+colorRecord+";";
                SynPaint.update(history + "," + color.getRGB());
                break;
            case "Rectangle":
                g.setColor(color);
                if (FirstPoint.x < SecondPoint.x && FirstPoint.y > SecondPoint.y) {
                    g.drawRect(FirstPoint.x, SecondPoint.y, Math.abs(FirstPoint.x - SecondPoint.x), Math.abs(FirstPoint.y - SecondPoint.y));
                    history = "Rec " + FirstPoint.x + "," + SecondPoint.y + "," + Math.abs(FirstPoint.x - SecondPoint.x) + "," + Math.abs(FirstPoint.y - SecondPoint.y);
                    recordString += "Rec/" + FirstPoint.x + "/" + SecondPoint.y + "/" + Math.abs(FirstPoint.x - SecondPoint.x) + "/" + Math.abs(FirstPoint.y - SecondPoint.y)+"/"+colorRecord+";";
                } else if (FirstPoint.x > SecondPoint.x && FirstPoint.y > SecondPoint.y) {
                    g.drawRect(SecondPoint.x, SecondPoint.y, Math.abs(FirstPoint.x - SecondPoint.x), Math.abs(FirstPoint.y - SecondPoint.y));
                    history = "Rec " + SecondPoint.x + "," + SecondPoint.y + "," + Math.abs(FirstPoint.x - SecondPoint.x) + "," + Math.abs(FirstPoint.y - SecondPoint.y);
                    recordString += "Rec/" + SecondPoint.x + "/" + SecondPoint.y + "/" + Math.abs(FirstPoint.x - SecondPoint.x) + "/" + Math.abs(FirstPoint.y - SecondPoint.y)+"/"+colorRecord+";";
                } else if (FirstPoint.x > SecondPoint.x && FirstPoint.y < SecondPoint.y) {
                    g.drawRect(SecondPoint.x, FirstPoint.y, Math.abs(FirstPoint.x - SecondPoint.x), Math.abs(FirstPoint.y - SecondPoint.y));
                    history = "Rec " + SecondPoint.x + "," + FirstPoint.y + "," + Math.abs(FirstPoint.x - SecondPoint.x) + "," + Math.abs(FirstPoint.y - SecondPoint.y);
                    recordString += "Rec/" + SecondPoint.x + "/" + FirstPoint.y + "/" + Math.abs(FirstPoint.x - SecondPoint.x) + "/" + Math.abs(FirstPoint.y - SecondPoint.y)+"/"+colorRecord+";";
                } else {
                    g.drawRect(FirstPoint.x, FirstPoint.y, Math.abs(FirstPoint.x - SecondPoint.x), Math.abs(FirstPoint.y - SecondPoint.y));
                    history = "Rec " + FirstPoint.x + "," + FirstPoint.y + "," + Math.abs(FirstPoint.x - SecondPoint.x) + "," + Math.abs(FirstPoint.y - SecondPoint.y);
                    recordString +="Rec/" + FirstPoint.x + "/" + FirstPoint.y + "/" + Math.abs(FirstPoint.x - SecondPoint.x) + "/" + Math.abs(FirstPoint.y - SecondPoint.y)+"/"+colorRecord+";";
                }
                SynPaint.update(history + "," + color.getRGB());
                break;

            case "Triangle":
                g.setColor(color);
                g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
                g.drawLine(FirstPoint.x, SecondPoint.y, SecondPoint.x, SecondPoint.y);
                g.drawLine(FirstPoint.x, SecondPoint.y, FirstPoint.x, FirstPoint.y);
                history = "Tri " + FirstPoint.x + "," + FirstPoint.y + "," + SecondPoint.x + "," + SecondPoint.y;
                recordString += "Tri/"+ FirstPoint.x + "/" + FirstPoint.y+"/"+ SecondPoint.x + "/" + SecondPoint.y+"/"+colorRecord+";";
                SynPaint.update(history + "," + color.getRGB());
                break;
            case "Circle":
                g.setColor(color);
                double radius = Math.sqrt(Math.pow(Math.abs(FirstPoint.x - SecondPoint.x), 2) + Math.pow(Math.abs(FirstPoint.y - SecondPoint.y), 2));
                g.drawOval(Math.min(FirstPoint.x, SecondPoint.x), Math.min(FirstPoint.y, SecondPoint.y), (int) radius * 2, (int) radius * 2);
                history = "Circle " + Math.min(FirstPoint.x, SecondPoint.x) + "," + Math.min(FirstPoint.y, SecondPoint.y) + "," + (int) radius * 2;
                recordString += "Circle/"+ Math.min(FirstPoint.x, SecondPoint.x) + "/" + Math.min(FirstPoint.y, SecondPoint.y)+"/"+  (int) radius * 2 +"/"+colorRecord+";";
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
            recordString += "Freehand/" + FirstPoint.x + "/" + FirstPoint.y + "/" + SecondPoint.x + "/" + SecondPoint.y+"/"+colorRecord+";";
            drawRecord.add(history+","+color+";");
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