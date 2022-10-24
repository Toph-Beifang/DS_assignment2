package org.example.manager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class Whiteboard extends Frame implements MouseListener, MouseMotionListener, WindowListener, ActionListener {
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
        //setting up the canvas
        color = Color.BLACK;
        colorRecord = color.getRGB();
        drawRecord = new ArrayList();
        setBackground(Color.WHITE);
        setSize(800, 800);
        setVisible(true);
        setTitle(userName + "'s Whiteboard");

        //setting up listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addWindowListener(this);

        //setting up the command panel
        Panel CommandPanel = new Panel(new GridLayout(0, 5));
        CommandPanel.setBackground(new Color(200, 200, 200));

        //setting up buttons
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

        //setting up action commands for buttons
        lineButton.setActionCommand("Line");
        rectangleButton.setActionCommand("Rectangle");
        triangleButton.setActionCommand("Triangle");
        circleButton.setActionCommand("Circle");
        freeHandButton.setActionCommand("Free Hand");
        textButton.setActionCommand("Text");
        clearButton.setActionCommand("Click to Clear");
        colorButton.setActionCommand("Color");
        chatButton.setActionCommand("Chat");

        //add action listeners to buttons
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
            if(!chatText.isEmpty()){
                chat.add(userName + ": " + chatText);
                SynPaint.update("Chat " + chatText + "," + userName);
            }
        });

        kickButton.addActionListener(e -> {
            String user = userList.getSelectedItem();
            if(user.equals(userName)){
                JOptionPane.showMessageDialog(null, "You can't kick yourself.");
            }else{
                SynPaint.update("Kick " + user);

            }
        });

        //current drawing mode label
        Label ModeLabel = new Label("Current Mode");
        currentMode = new Label();
        Font f1 = new Font("Arial", 1, 15);
        ModeLabel.setFont(f1);
        currentMode.setFont(f1);
        currentMode.setForeground(new Color(20, 200, 240));

        g =  getGraphics();
        //record file management system
        JComboBox menu = new JComboBox();
        menu.setModel(new DefaultComboBoxModel(new String[]{"New", "Save to","Save as","Open","Close"}));
        menu.addActionListener((event) -> {

            //create a new record file, either save the current canvas to it or create a empty one
            if (menu.getSelectedItem().equals("New")) {
                String[] options = {"Create New Record & Save current canvas", "Creat New Empty Record"};

                int x = JOptionPane.showOptionDialog(null, "Create a New Record File",
                        "Click a button",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

                //if the user choose to Save current canvas before Creating a  New Record
                if (x == 0) {
                    //create a new record file and save the current canvas into it
                    try {
                        String filenameEntered = JOptionPane.showInputDialog("Filename: ");
                        if (filenameEntered==null) {
                            JOptionPane.showMessageDialog(null, "File Name can not be empty");
                        }
                        else{
                            String filename = filenameEntered + ".txt";
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
                        }
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }

                }
                //if the user choose to create a new record without saving current canvas
                else if (x == 1){
                    try {

                        String filenameEntered = JOptionPane.showInputDialog("Filename: ");
                        if (filenameEntered==null) {
                            JOptionPane.showMessageDialog(null, "File Name can not be empty");
                        }
                        else {
                            String filename = filenameEntered + ".txt";
                            File canvasRecord = new File(filename);
                            if (canvasRecord.createNewFile()) {
                                System.out.println("File created: " + canvasRecord.getName());
                                JOptionPane.showMessageDialog(null, "Canvas saved successfully");
                            } else {
                                JOptionPane.showMessageDialog(null, "File already exists.");
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                }
            }

            //choose a file to save to and overwrite it with current canvas's record
            else if (menu.getSelectedItem().equals("Save to")) {
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
                    String filenameEntered = JOptionPane.showInputDialog("Filename: ");
                    if (filenameEntered==null) {
                        JOptionPane.showMessageDialog(null, "File Name can not be empty");
                    }
                    else{
                        String filename = filenameEntered + ".txt";
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
                        recordString = recordLine;
                        SynPaint.update(recordString);
                        bufferedReader.close();

                        String[] recordList= recordLine.split(";");
                        for (String record : recordList) {
                            String[] recordL = record.split(" ");
                            SynPaint.syn(g, recordL);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            //closing a canvas record file
            else if (menu.getSelectedItem().equals("Close")){
                // Close window
                System.out.println("amont:" + userList.getItemCount());
                if(userList.getItemCount() > 1){
                    SynPaint.update("CloseAll");
                }
                this.dispose();
                System.exit(1);
            }
        });


        userList = new List(5);
        userList.add(userName);
        chat = new List(5);

        //adding the buttons to command panel
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
                // Text enter window
                String text = JOptionPane.showInputDialog("Text input");
                if (text != null) {
                    // Set text information to the white board
                    Font font = new Font("Courier", 0, 20);
                    g.setFont(font);
                    g.drawString(text, FirstPoint.x, FirstPoint.y);
                    System.out.println("color: " + colorRecord);
                    history = "Text " + text + "," + FirstPoint.x + "," + FirstPoint.y;
                    recordString += "Text " + text + "," + FirstPoint.x + "," + FirstPoint.y+","+color.getRGB()+";";
                    SynPaint.update(history + "," + color.getRGB());
                }
                break;
            case "Click to Clear":
                // Clear the white board
                recordString = "";
                repaint();
        }

    }

    public void mousePressed(MouseEvent e) {
        // Get x and y coordinate
        FirstPoint.setLocation(0, 0);
        SecondPoint.setLocation(0, 0);
        FirstPoint.setLocation(e.getX(), e.getY());
    }

    public void mouseReleased(MouseEvent e) {
        SecondPoint.setLocation(e.getX(), e.getY());
        switch (DrawMode) {
            case "Line":
                // Set line information to the white board
                g.setColor(color);
                g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
                history = "Line " + FirstPoint.x + "," + FirstPoint.y + "," + SecondPoint.x + "," + SecondPoint.y;
                recordString += "Line "+ FirstPoint.x + "," + FirstPoint.y+","+ SecondPoint.x + "," + SecondPoint.y+","+color.getRGB()+";";
                SynPaint.update(history + "," + color.getRGB());
                break;
            case "Rectangle":
                // Set rectangle information to the white board
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
                recordString += history + "," + color.getRGB() + ";";
                SynPaint.update(history + "," + color.getRGB());
                break;

            case "Triangle":
                // Set triangle information to the white board
                g.setColor(color);
                g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
                g.drawLine(FirstPoint.x, SecondPoint.y, SecondPoint.x, SecondPoint.y);
                g.drawLine(FirstPoint.x, SecondPoint.y, FirstPoint.x, FirstPoint.y);
                history = "Tri " + FirstPoint.x + "," + FirstPoint.y + "," + SecondPoint.x + "," + SecondPoint.y;
                recordString += history + "," + color.getRGB() + ";";
                SynPaint.update(history + "," + color.getRGB());
                break;
            case "Circle":
                // Set circle information to the white board
                g.setColor(color);
                int radius = (int) Math.sqrt(Math.pow(Math.abs(FirstPoint.x - SecondPoint.x), 2) + Math.pow(Math.abs(FirstPoint.y - SecondPoint.y), 2));
                g.drawOval(Math.min(FirstPoint.x, SecondPoint.x)-radius, Math.min(FirstPoint.y, SecondPoint.y)-radius, radius * 2, radius * 2);
                history = "Circle " + (Math.min(FirstPoint.x, SecondPoint.x)-radius) + "," + (Math.min(FirstPoint.y, SecondPoint.y)-radius) + "," + radius * 2;
                recordString += history + "," + color.getRGB() + ";";
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
        // Set free hand information to the white board
        if (DrawMode.compareTo("Free Hand") == 0) {
            if (SecondPoint.x != 0 && SecondPoint.y != 0) {
                FirstPoint.x = SecondPoint.x;
                FirstPoint.y = SecondPoint.y;
            }
            SecondPoint.setLocation(e.getX(), e.getY());
            g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
            history = "Line " + FirstPoint.x + "," + FirstPoint.y + "," + SecondPoint.x + "," + SecondPoint.y;
            recordString += history+ "," + color.getRGB()+";";
            SynPaint.update(history + "," + color.getRGB());
        }

    }

    public void mouseMoved(MouseEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        // Close window and other users' window
        if(userList.getItemCount() > 1){
            SynPaint.update("CloseAll");
        }
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
        // Get action status
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