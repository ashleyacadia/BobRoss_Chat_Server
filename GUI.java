/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ashley C
 */
package ADAAssignment1;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class GUI extends JFrame {

    Client client;

    public GUI(String name, Client client) throws IOException, BadLocationException {
        super(name);
        this.client = client;
        //System.out.println(client.getUser());

        //custom cursor
        Image i = new ImageIcon("src/pngbarn.png").getImage();
        Point p = new Point(0, 0);
        String n = "Brush";
        setCursor(getToolkit().createCustomCursor(i, p, n));

        getContentPane().setLayout(null);
        JLabel image1 = new JLabel(new ImageIcon("src/Bob-Ross-3.jpg"));
        setContentPane(image1);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 400); // manually computed sizes
        setLocationRelativeTo(null);
        setResizable(false);
        
        //Username text field
        JTextField username = new JTextField("User Name");
        username.setLocation(160, 100);
        username.setSize(150, 25);
        getContentPane().add(username);
        //Username focous listener 
        username.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                username.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if ("".equals(username.getText())) {
                    username.setText("User Name");
                }
            }
        });
        
        //Password text field
        JPasswordField password = new JPasswordField("Password");
        password.setLocation(160, 125);
        password.setSize(150, 25);
        getContentPane().add(password);
        password.setEchoChar((char) 0);
        //password focous listener
        password.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                password.setText("");
                password.setEchoChar('*');
            }

            @Override
            public void focusLost(FocusEvent e) {
                if ("".equals(password.getText())) {
                    password.setText("Password");
                    password.setEchoChar((char) 0);
                }
            }
        });
        
        //Button to create a new account
        JButton newAccount = new JButton("Create a little rascal here!");
        newAccount.setLocation(170, 10);
        newAccount.setSize(200, 20);
        getContentPane().add(newAccount);
       
        
         //Button to create new account. 
                JButton create = new JButton("Create!");
                create.setLocation(225, 125);
                create.setSize(150, 20);
                getContentPane().add(create);
                 create.setVisible(false);
        
        //Text area to display message from server/client
        JTextArea success = new JTextArea();
        

        success.setEditable(false);
        success.setLocation(10, 10);
        success.setForeground(Color.green);
        success.setOpaque(false);

        success.setSize(200, 20);
        getContentPane().add(success);
        success.setVisible(false);

        //Text area to display if an error occurs from server/client
        JTextArea error = new JTextArea("Issue with user name or password");
        error.setEditable(false);
        error.setLocation(150, 75);
        error.setForeground(Color.red);
        error.setOpaque(false);

        Highlighter highlighter = error.getHighlighter();
        HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
        int p2 = error.getText().indexOf("Issue with user name or password");
        int p3 = p2 + "Issue with user name or password".length();
        highlighter.addHighlight(p2, p3, painter);
        error.setSize(200, 20);
        getContentPane().add(error);
        error.setVisible(false);

        //Button to log into the chat server
        JButton chat = new JButton("Enter the Forest of Friends!");
        chat.setLocation(140, 150);
        chat.setSize(190, 20);
        getContentPane().add(chat);
        
        //Action event listener for the login button
        chat.addActionListener((ActionEvent e) -> {
            //System.out.println("Button working");
            String loginCheck = client.checkLoginInfo(username.getText(), password.getText());

            if (loginCheck == null) {
                setVisible(false);
                client.chatGUI.setVisible(true);
                client.chatGUI.hello.setText("Welcome "+client.getUser()+"! Select a friend from "
                        + "your list to send them a happy message.");
            } else {
                image1.setIcon(new ImageIcon("src/bob.gif"));
                error.setVisible(true);
                error.setText(loginCheck);
                JButton tryAgain = new JButton("Try again");
                tryAgain.setLocation(140, 150);
                tryAgain.setSize(190, 20);
                getContentPane().add(tryAgain);

                chat.setVisible(false);
                username.setVisible(false);
                password.setVisible(false);
                success.setVisible(false);

                tryAgain.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        image1.setIcon(new ImageIcon("src/Bob-Ross-3.jpg"));
                        chat.setVisible(true);
                        username.setText("User Name");
                        username.setVisible(true);
                        password.setText("Password");
                        password.setEchoChar((char) 0);
                        password.setVisible(true);
                        error.setVisible(false);
                        tryAgain.setVisible(false);
                    }
                });
            }
            //if return true then close window and open clientGUI
            //else change background to other image with error message
        });
//Button to take user to login screen
                            JButton logIn = new JButton("Log In");
                            logIn.setLocation(170, 10);
                            logIn.setSize(200, 20);
                            getContentPane().add(logIn);
                            logIn.setVisible(false);
                            //Action listener for Log in button
                            logIn.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {

                                    image1.setIcon(new ImageIcon("src/Bob-Ross-3.jpg"));
                                    username.setText("User Name");
                                    username.setVisible(true);
                                    password.setText("Password");
                                    password.setEchoChar((char) 0);
                                    password.setVisible(true);
                                    create.setVisible(false);

                                    chat.setVisible(true);
                                    newAccount.setVisible(true);
                                    logIn.setVisible(false);
                                    success.setVisible(false);
                                    error.setVisible(false);
                                }
                            });
        //create new account button
        //action listener
        newAccount.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Set new account screen
                image1.setIcon(new ImageIcon("src/newAccBob.gif"));
                error.setVisible(false);
                username.setText("Type a username");
                username.setLocation(225, 75);
                password.setText("Type a password");
                password.setEchoChar((char) 0);
                password.setLocation(225, 100);
                chat.setVisible(false);
                newAccount.setVisible(false);
                logIn.setVisible(true);
                logIn.setText("Return to log in screen");
                create.setVisible(true);
                
               
                //Action listener for creating a new account
                create.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        
                        //checks username and password can be created
                        String displayMessage = client.createUser(username.getText(), password.getText());
                        //System.out.println(displayMessage);
                        
                        //if return true then close window and open clientGUI
                        if (displayMessage.compareTo("Successfully created user") == 0) {
                            
                            image1.setIcon(new ImageIcon("src/bobwelcome.gif"));
                            username.setVisible(false);
                            password.setVisible(false);
                            create.setVisible(false);
                            error.setVisible(false);
                            
                            success.setVisible(true);
                            success.setText(displayMessage);
                            Highlighter h = success.getHighlighter();
                            HighlightPainter p = new DefaultHighlighter.DefaultHighlightPainter(Color.white);
                            int p0 = success.getText().indexOf(success.getText());
                            int p1 = p0 + success.getText().length();
                            try {
                                h.addHighlight(p0, p1, p);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } 
                        //else change background to other image with error message
                        else if (displayMessage.compareTo("Username already exists") == 0) {
                            success.setVisible(false);
                            error.setVisible(true);
                            error.setLocation(200, 40);
                            error.setText(displayMessage);
                            Highlighter x = error.getHighlighter();
                            HighlightPainter y = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
                            int p6 = error.getText().indexOf(error.getText());
                            int p7 = p6 + error.getText().length();
                            try {
                                x.addHighlight(p6, p7, y);
                            } catch (BadLocationException ex) {
                                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            username.setText("User Name");
                            password.setText("Password");
                            password.setEchoChar((char) 0);
                        }
                    }
                });
            }
        });
    }
}
