/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author GGPC
 */
package ADAAssignment1;
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.exit;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;

public class Client {

    public static final String HOST_NAME = "localhost";
    public static final int HOST_PORT = 7777;
    private String user; //STRING OF USERNAME
    private GUI gui;
    ClientGUI chatGUI;
    Client client;
    public Set<String> onlineUsers;
    Socket socket;
    BufferedReader br; //INPUT STREAM
    PrintWriter pw; //OUTPUT STREAM

    public Client() {
        try {
            System.out.println("Opening client connection with hostname: \"" + HOST_NAME + "\" and port: \"" + HOST_PORT + "\"\n");
            socket = new Socket(HOST_NAME, HOST_PORT);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);
            onlineUsers = new HashSet<String>();
        } catch (IOException e) {
            System.err.println("Cannot create client socket: " + e);
        }
    }

    public String getUser() //GET STRING USERNAME
    {
        return user;
    }

    public void setUser(String user) //SET STRING USERNAME
    {
        this.user = user;
    }
    
    public Set<String> getOnlineUsers()
    {
        return onlineUsers;
    }
    
    public void setOnlineUsers(Set<String> onlineUsers)
    {
        this.onlineUsers = onlineUsers;
    }

    public void setClientGUI(ClientGUI chatGUI){
        this.chatGUI = chatGUI;
    }
    public void startClientGUI(){
        this.chatGUI.setVisible(true);
    }
    public void setGUI(GUI gui) {
        this.gui = gui;
    }
    
     public void setClient(Client client)
    {
        this.client = client;
    }

    public String checkLoginInfo(String username, String password) //METHOD TO CHECK IF USERNAME AND PASSWORD EXISTS IN THE SYSTEM
    {
        System.out.println("CHECKING LOGIN INFO");
        System.out.println("Checking username: \"" + username + "\" with password: \"" + password + "\"");
        try {
            String sendToServer = "@";
            char[] usernameCharArray = username.toCharArray();
            for (char c : usernameCharArray) {
                sendToServer += ((int) c);
                sendToServer += (' ');
            }

            sendToServer += '~';
            char[] passwordCharArray = password.toCharArray();
            for (char c : passwordCharArray) {
                sendToServer += ((int) c);
                sendToServer += (' ');
            }
            System.out.println("Sending to server: " + sendToServer);
            pw.println(sendToServer);
            String serverResponse = br.readLine();
            if (!serverResponse.equals("Username found in database")) {
                System.out.println("Server didn't find username in database\n");
                return serverResponse;
            } 
            else
            {
                System.out.println("Server found username in database\n Logging in\n");
            }
            setUser(username);
            ReceiveOnlineUsers(br.readLine());
            Receive receive = new Receive(socket,client);
            Thread threadS = new Thread(receive);
            threadS.start();
        } catch (IOException e) {
            System.err.println("Client could not make connection: " + e);
            System.exit(-1);
        }
        return null;
    }
    
    public void ReceiveOnlineUsers(String serverResponse){
        System.out.println("RECEIVING ALL ONLINE USERS");
        int counter = 2;
        Set<String> onlineUsers = new HashSet<String>();
        System.out.print("Online Users: ");
        for (; counter < serverResponse.length(); counter++)
        {
            String user = "";
            while (counter < serverResponse.length() && serverResponse.charAt(counter) != '@')
            {
                String ascii = "";
                while(counter < serverResponse.length() && serverResponse.charAt(counter) != ' ')
                {
                    ascii += serverResponse.charAt(counter);
                    counter++;
                }
                counter++;
                user += (char)Integer.parseInt(ascii);
            }
            System.out.print(user + " ");
            onlineUsers.add(user);
        }
        System.out.println();
        setOnlineUsers(onlineUsers);
        for(String userName : onlineUsers){
            chatGUI.model.addElement(userName);
        }
        chatGUI.online.repaint();
    }

    public String createUser(String username, String password) //METHOD TO CREATE A NEW USER IN THE DATABASE
    {
        System.out.println("CREATING USER");
        System.out.println("Attempting to create username \"" + username + "\" with password \"" + password + "\"");
        try {
            String sendToServer = "*";
            char[] usernameCharArray = username.toCharArray();
            for (char c : usernameCharArray) {
                sendToServer += ((int) c);
                sendToServer += (' ');
            }

            sendToServer += '~';
            char[] passwordCharArray = password.toCharArray();
            for (char c : passwordCharArray) {
                sendToServer += ((int) c);
                sendToServer += (' ');
            }
            System.out.println("Sending to server: " + sendToServer);
            pw.println(sendToServer);
            String serverResponse = br.readLine();
            System.out.println(serverResponse + "\n");
            return serverResponse;
        } catch (IOException e) {
            System.err.println("Client could not make connection: " + e);
            System.exit(-1);
        }
        return null;
    }
    
    public void logOffUser()
    {
        System.out.println("LOGGING OFF USER: \"" + user + "\"");
        String sendToServer = "-";
        char[] usernameCharArray = user.toCharArray();
        for (char c: usernameCharArray)
        {
            sendToServer += ((int)c);
            sendToServer += (' ');
        }
        System.out.println("Sending to server: " + sendToServer);
        pw.println(sendToServer);
        System.out.println("Closing client socket\n");
        try {   
            br.close();
            pw.close();
            socket.close();
            exit(0);
        } catch (IOException e) {
            System.err.println("Couldn't close socket: " + e);
        }
    }

    public void sendMessage(String message, Set<String> users) //METHOD TO SEND A MESSAGE. PARAMETERS ARE THE MESSAGE STRING AND THE SET OF USERS TO SEND IT TO
    {
        System.out.println("SENDING MESSAGE");
        System.out.println("Sending message: \"" + message + "\"");
        char[] messageCharArray = message.toCharArray();
        String sendToServer = "+";
        for (char c : messageCharArray) {
            sendToServer += ((int) c);
            sendToServer += (' ');
        }

        System.out.print("Sending to: ");
        for (String user : users) {
            System.out.print(user + " ");
            char[] usernameCharArray = user.toCharArray();
            sendToServer += '@';
            for (char c : usernameCharArray) {
                sendToServer += ((int) c);
                sendToServer += (' ');
            }
        }
        System.out.println("\nSending to server: " + sendToServer + "\n");
        pw.println(sendToServer);
        
    }
    
    public String onlineUsersToString()
    {
        String toString = "Online Users: ";
        for (String user: client.getOnlineUsers())
        {
            toString += user + " ";
        }
        return toString;
    }
    
    public static void main(String[] args) throws IOException, BadLocationException {
        Client client = new Client(); //CREATES AN INSTANCE OF THE CLIENT
        client.setClient(client);
        
        JFrame frame = new GUI("Every tree needs a friend", client);
        frame.setVisible(true);

        JFrame chat = new ClientGUI("Forest of Friends", client);
        chat.setVisible(false);
        client.setClientGUI((ClientGUI) chat);
        client.setGUI((GUI) frame);
    }

    private class Receive implements Runnable //RECEIVE MESSAGE FROM SERVER THREAD
    {
        Socket socket;
        boolean finished;
        Client client;

        public Receive(Socket socket, Client client) {
            this.socket = socket;
            this.client = client;
            finished = false;
        }

        public void endConnection() {
            finished = true;
        }

        public String[] ReceiveMessage(String serverResponse) //RECEIVES MESSAGE FROM SERVER FROM A CLIENT TO DISPLAY ON THE GUI
        {
            System.out.println("RECEIVING MESSAGE");
            String[] message = new String[2];
            System.out.println(serverResponse);
            
            message[0]="";
            message[1]="";
            int counter = 1;
            for (; serverResponse.charAt(counter) != '@'; counter++) {
                String ascii = "";
                while (serverResponse.charAt(counter) != ' ' && serverResponse.charAt(counter) != '@') {
                    ascii += serverResponse.charAt(counter);
                    counter++;
                }
                message[0] += (char) Integer.parseInt(ascii);
            }
            
            counter++;
            for (; counter < serverResponse.length(); counter++) {
                String ascii = "";
                while (serverResponse.charAt(counter) != ' ' && serverResponse.charAt(counter) != '@') {
                    ascii += serverResponse.charAt(counter);
                    counter++;
                }
                message[1] += (char) Integer.parseInt(ascii);
            }
            System.out.println("Received message: \"" + message[0] + "\" from user: \"" + message[1] + "\"");
            message[1]+= ": ";
            return message;
        }

        public String ReceiveUserStatus(String serverResponse) //DISPLAYS IF A USER HAS JOINED OR LEFT
        {
            System.out.println("RECEIVING USER STATUS");
            String message = "";
            int counter = 2;
            for (; counter < serverResponse.length(); counter++) {
                String ascii = "";
                while (counter < serverResponse.length() && serverResponse.charAt(counter) != ' ') {

                    ascii += serverResponse.charAt(counter);
                    counter++;
                }
                message += (char) Integer.parseInt(ascii);
            }

            switch (serverResponse.charAt(1)) {
                case '+': {
                    System.out.println("User: \"" + message + "\" has joined");
                    Set<String> onlineUsers = client.getOnlineUsers();
                    onlineUsers.add(message);
                    client.setOnlineUsers(onlineUsers);
                    chatGUI.model.addElement(message);
                    chatGUI.online.repaint();
                 
                    return message + " has entered the Forest of Friends";
                }
                case '-': {
                    System.out.println("User: \"" + message + "\" has left");
                    Set<String> onlineUsers = client.getOnlineUsers();
                    onlineUsers.remove(message);
                    client.setOnlineUsers(onlineUsers);
                    chatGUI.model.removeElement(message);
                    chatGUI.online.repaint();
                    
                    return " That's all the time "+message+" has for today.";
                }
            }
            return "ERROR";
        }

        public void run() //THREAD TO CONTINUALLY CHECK FOR SERVER INPUT
        {
            try {

                while (!finished) {
                    String serverResponse = br.readLine();
                    if (serverResponse != null)
                    {
                        System.out.println("Received input from server: " + serverResponse);
                        switch (serverResponse.charAt(0)) {
                            case '+': {
                                //System.out.println("It's a plus!");
                                chatGUI.Receive(ReceiveMessage(serverResponse)); //GUI DISPLAYS STRING
    //                            .enteredText.append(ReceiveMessage(serverResponse));
                                break;
                            }
                            case '@': {
                                //System.out.println("It's a &!");
                                chatGUI.ReceiveOnOffline(ReceiveUserStatus(serverResponse)); //GUI DISPLAYS STRING
                                break;
                            }
                        }
                    }
                    System.out.println();
                }
            } catch (IOException e) {
                System.err.println("Client error with reading from server: " + e);
            }
        }
    }
}
