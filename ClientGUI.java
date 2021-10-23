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
import static java.awt.Color.green;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class ClientGUI extends JFrame {

    Client client;
    private final JTextArea enteredText;
    JList online;
    Set<String> sendTo;
    DefaultListModel model;
    JTextField hello;
    

    public ClientGUI(String name, Client client) {
        super(name);
        this.client = client;

        //custom cursor
        Image i = new ImageIcon("src/bobcursor.png").getImage();
        Point p = new Point(0, 0);
        String n = "bob";
        setCursor(getToolkit().createCustomCursor(i, p, n));

        //background
        JLabel image1 = new JLabel(new ImageIcon("src/flowers.png"));

        setContentPane(image1);
        //online users
        sendTo = new HashSet<String>();
        model = new DefaultListModel();
        online = new JList(model);

        getContentPane().setLayout(null);

        //components
        //sent messages
        enteredText = new JTextArea();
        enteredText.setEditable(false);
        
        //Greeting and user's name
        hello = new JTextField();
        hello.setEditable(false);
        hello.setLocation(60, 10);
        hello.setSize(550, 30);
        hello.setFont(hello.getFont().deriveFont(Font.BOLD, 14f));
        getContentPane().add(hello);
        //Boarder for text field
        javax.swing.border.Border border = BorderFactory.createLineBorder(Color.orange);
        hello.setBorder(border);


        JScrollPane scroll = new JScrollPane(this.enteredText,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setLocation(100, 60);
        scroll.setSize(400, 350);
        getContentPane().add(scroll);

        //typing field
        JTextArea message = new JTextArea();
        message.setLineWrap(true);
        JScrollPane messageScroll = new JScrollPane(message,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        messageScroll.setLocation(10, 450);
        messageScroll.setSize(500, 45);
        getContentPane().add(messageScroll);

        //list of online clients

        online.setForeground(Color.green);
        ListSelectionListener listener = null;

        online.addListSelectionListener(listener);
        online.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(online,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setLocation(525, 50);
        scrollPane.setSize(150, 450);
        getContentPane().add(scrollPane);

        //send button
        JButton send = new JButton("Send");
        send.setLocation(200, 500);
        send.setSize(100, 25);
        getContentPane().add(send);

        send.addActionListener((ActionEvent e) -> {

            //send message to server.
            sendTo.add(client.getUser());
            if (online.getSelectedIndices().length > 1) {
                for (int j = 0; j < online.getSelectedIndices().length; j++) {
                    sendTo.add((String) model.getElementAt(j));
                }
            } else{
                sendTo.add((String) model.getElementAt(online.getSelectedIndex()));
            }

            //System.out.println(sendTo);
            client.sendMessage(message.getText(), sendTo);
            message.setText("");
            online.clearSelection();
            sendTo.clear();
            //System.out.println(sendTo.toString());
        });

        //Log Out button
        JButton logOut = new JButton("Log Out");
        logOut.setLocation(575, 500);
        logOut.setSize(100, 25);
        getContentPane().add(logOut);

        logOut.addActionListener((ActionEvent e) -> {
            //System.out.println("Log out button working");
            //send message to server.
            client.logOffUser();
            System.exit(0);
        });
        //close window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 610);
        setResizable(false);

    }

    public void Receive(String[] message) {
        enteredText.append("\n" +"From "+ message[1] + "\n");
        enteredText.append(message[0] + "\n");
    }

    public void ReceiveOnOffline(String message) {
        Color color = null;
        if(message.contains("has entered")){
            color = Color.green;
        } else if(message.contains("all the time")){
            color = Color.pink;
        }

        enteredText.append(message + "\n");
        Highlighter h = enteredText.getHighlighter();
        Highlighter.HighlightPainter p = new DefaultHighlighter.DefaultHighlightPainter(color);
        int p0 = enteredText.getText().indexOf(message);
        int p1 = p0 + message.length();
        try {
            h.addHighlight(p0, p1, p);
        } catch (BadLocationException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
