package org.example.manager;

import org.example.Drawing;
import org.example.user.Join;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class Whiteboard extends Frame implements MouseListener, MouseMotionListener, WindowListener,ActionListener{
    private static Drawing drawing;
    String DrawMode = "";
    //starting and ending point for line and rectangle drawing
    Point FirstPoint = new Point(0,0);
    Point SecondPoint = new Point(0,0);
    //A label display the current drawing mode
    Label currentMode;
    Color color = Color.BLACK;

    ArrayList<String> drawRecord = new ArrayList<>();

//    public static void main(String[] args) {
//        new Whiteboard();
//    }


    public Whiteboard(String userName) {
        //this.drawing = drawing;
        //set up the canvas
        setBackground(Color.WHITE);
        setSize(400, 400);
        setVisible(true);
        setTitle(userName + "'s Whiteboard");


        //add listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addWindowListener(this);

        //add control panel
        Panel CommandPanel = new Panel(new GridLayout(0,3));
        CommandPanel.setBackground(new Color(200,200,200));

        //add buttons
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

        //Prepare Labels
        Label ModeLabel = new Label("Current Mode");
        currentMode = new Label();
        Font f1 = new Font("Arial", Font.BOLD, 15);
        ModeLabel.setFont(f1);
        currentMode.setFont(f1);
        currentMode.setForeground(new Color(20,200,240));


        // File options
        JComboBox menu = new JComboBox();
        menu.setModel(new DefaultComboBoxModel(new String[] {"New", "Save", "Open"}));
        menu.addActionListener(event -> {

        });

        //3.3 Pack the Panel & give to Frame
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

        /*
        JTextField textField = new JTextField();
        final String[] str = {"testing text "};
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                     str[0] = textField.getText();
                }
            }
        });
        textField.setText(str[0]);
        this.add(BorderLayout.SOUTH,textField);
        */

        this.add(BorderLayout.NORTH, CommandPanel);
    }



    /*
    @Override
    public void paint(Graphics g){
        if (FirstPoint.equals(SecondPoint))
            return;
        switch(DrawMode)
        {
            case "Free Hand":
                //Sample 11: Free Hand Drawing
                g.drawLine(
                        FirstPoint.x,
                        FirstPoint.y,
                        SecondPoint.x,
                        SecondPoint.y);
                break;
        }
    }
    */



    @Override
    public void mouseClicked(MouseEvent e) {

        FirstPoint.setLocation(e.getX(), e.getY());
        Graphics g = getGraphics();
        switch(DrawMode) {
            case "Text":
                String text = JOptionPane.showInputDialog("Text input");
                if(text != null){
                    Font font = new Font("Courier", Font.PLAIN, 20);
                    g.setFont(font);
                    g.drawString(text, FirstPoint.x, FirstPoint.y);
                    // test send Text
                    String history = "Text " + text + "," + FirstPoint.x + "," + FirstPoint.y;
//                    System.out.println(Join.connection.);
//                    for (int i = 0; i < Build.users.size(); i++) {
//                        Connection conUser = Build.users.get(i);
//                        System.out.println(conUser.userName);
//                        try {
//                            conUser.dataOutputStream.writeUTF(history);
//                        } catch (IOException ex) {
//                            throw new RuntimeException(ex);
//                        }
//                    }
                }
                break;
            case "Click to Clear":
                repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //Sample 05: Set First Point
        FirstPoint.setLocation(0, 0);
        SecondPoint.setLocation(0, 0);
        FirstPoint.setLocation(e.getX(), e.getY());

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //Sample 06: Set Second Point
        SecondPoint.setLocation(e.getX(), e.getY());

        Graphics g = getGraphics();
        switch(DrawMode)
        {
            case "Line":
                g.setColor(color);
                g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
                break;
            case "Rectangle":
                g.setColor(color);
                if (FirstPoint.x < SecondPoint.x && FirstPoint.y > SecondPoint.y){
                    // lower left to upper right
                    g.drawRect(FirstPoint.x, SecondPoint.y, Math.abs(FirstPoint.x-SecondPoint.x), Math.abs(FirstPoint.y-SecondPoint.y));
                } else if (FirstPoint.x > SecondPoint.x && FirstPoint.y > SecondPoint.y){
                    // lower right to upper left
                    g.drawRect(SecondPoint.x, SecondPoint.y, Math.abs(FirstPoint.x-SecondPoint.x), Math.abs(FirstPoint.y-SecondPoint.y));
                } else if (FirstPoint.x > SecondPoint.x && FirstPoint.y < SecondPoint.y){
                    // upper right to lower left
                    g.drawRect(SecondPoint.x, FirstPoint.y, Math.abs(FirstPoint.x-SecondPoint.x), Math.abs(FirstPoint.y-SecondPoint.y));
                }else{
                    // upper left to lower right
                    g.drawRect(FirstPoint.x, FirstPoint.y, Math.abs(FirstPoint.x-SecondPoint.x), Math.abs(FirstPoint.y-SecondPoint.y));
                }
                break;
            case "Triangle":
                g.setColor(color);
                g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
                g.drawLine( FirstPoint.x  , SecondPoint.y, SecondPoint.x, SecondPoint.y);
                g.drawLine( FirstPoint.x , SecondPoint.y, FirstPoint.x, FirstPoint.y);
                break;
            case "Circle":
                g.setColor(color);
                g.drawRoundRect(FirstPoint.x, FirstPoint.y, Math.abs(FirstPoint.x-SecondPoint.x), Math.abs(FirstPoint.y-SecondPoint.y), 200, 200);
                break;

        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Graphics g = getGraphics();
        if (DrawMode.compareTo("Free Hand") == 0)
        {
            if (SecondPoint.x != 0 && SecondPoint.y != 0)
            {
                FirstPoint.x = SecondPoint.x;
                FirstPoint.y = SecondPoint.y;
            }
            SecondPoint.setLocation(e.getX(), e.getY());
            g.drawLine(FirstPoint.x, FirstPoint.y, SecondPoint.x, SecondPoint.y);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {


    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        this.dispose();
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Sample 04: Set the Draw Mode
        DrawMode = e.getActionCommand();
        currentMode.setText(DrawMode);
        FirstPoint.setLocation(0, 0);
        SecondPoint.setLocation(0, 0);
        if(e.getActionCommand() == "Color"){
            Color color = JColorChooser.showDialog(null, "Pick a color", null);
            if (color != null){
                this.color = color;
            }
        }
    }
}
