
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//import ChatRoom.*;
package ADAAssignment1;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author cqb2150
 */
public class Server {

    private boolean stopRequested;
    Map<String, Socket> onlineUsers;
    UserDatabase userDatabase;
    public static final int PORT = 7777;

    public Server() {
        userDatabase = new UserDatabase(); //CREATES INSTANCE OF USERDATABASE
        onlineUsers = new HashMap<String, Socket>();
        stopRequested = false;
    }

    public void startServer() {
        stopRequested = false;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started at " + InetAddress.getLocalHost() + " on port " + PORT);
        } catch (IOException e) {
            System.err.println("Server can't listen on port " + e);
            System.exit(-1);
        }

        Thread stopThread = new Thread(new StopRequested(serverSocket));
        stopThread.start();
        
        try {
            while (!stopRequested) {
                Socket socket = serverSocket.accept();
                System.out.println("Connection made with " + socket.getInetAddress() + "\n");
                Thread thread = new Thread(new ChatRoom(socket));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("can't accept client connection: " + e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

    private class StopRequested implements Runnable
    {
        ServerSocket serverSocket;
        Scanner scan = new Scanner(System.in);
        
        public StopRequested(ServerSocket serverSocket)
        {
            this.serverSocket = serverSocket;
        }
        
        public void run()
        {
            while (!stopRequested)
            {
                if (scan.nextLine().equals("QUIT"))
                {
                    stopRequested = true;
                }
            }
            
            System.out.println("Closing server...");
            for (Socket socket: onlineUsers.values())
            {
                try {
                    socket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Cannot close server: " + e);
                }
            }
            System.exit(0);
        }
    }
    
    private class ChatRoom implements Runnable //THREAD TO SEND AND RECEIVE MESSAGES
    {
        private String username = "";
        private Socket socket;
        private BufferedReader br;
        private boolean closeSocket = false;

        public ChatRoom(Socket socket) {
            this.socket = socket;
            try {
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                System.out.println("Unable to create BufferedReader: " + e);
            }
        }

        public boolean CheckUserStatus(String clientResponse) //VERIFIES IF THE USER CAN LOGIN IN DEPENDING ON THEIR LOGIN DETAILS
        {
            System.out.println("CHECKING LOGIN DETAILS");
            String username = "";
            int counter = 1;
            for (; clientResponse.charAt(counter) != '~'; counter++) {
                String ascii = "";
                while (clientResponse.charAt(counter) != ' ' && clientResponse.charAt(counter) != '~') {
                    ascii += clientResponse.charAt(counter);
                    counter++;
                }
                username += (char) Integer.parseInt(ascii);
            }

            counter++;
            String password = "";
            for (; counter < clientResponse.length(); counter++) {
                String ascii = "";
                while (clientResponse.charAt(counter) != ' ') {
                    ascii += clientResponse.charAt(counter);
                    counter++;
                }
                password += (char) Integer.parseInt(ascii);
            }
            System.out.println("Attempting to login: \"" + username + "\" with password: \"" + password + "\" to the database");
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                if (userDatabase.getMap().containsKey(username) && password.equals(userDatabase.getMap().get(username))) {
                    if (onlineUsers.containsKey(username)) {
                        System.out.println("User already logged in");
                        pw.println("User already logged in");
                    } else {
                        System.out.println("Username found in database");
                        this.username = username;
                        onlineUsers.put(username, socket);
                        pw.println("Username found in database");
                        pw.println(allOnlineUsers());
                        userOnline(true);
                    }

                } else {
                    System.out.println("Incorrect username or password");
                    pw.println("Incorrect username or password");
                }
            } catch (IOException e) {
                System.err.println("Could not create PrintWriter");
            }

            return password.equals(userDatabase.getMap().get(username));
        }

        public String ReceiveMessage(String clientResponse) //RECEIVES MESSAGE FROM CLIENT TO SEND TO THE SET OF CLIENTS
        {
            System.out.println("SENDING MESSAGE");
            String message = "+";
            int counter = 1;
            for (; clientResponse.charAt(counter) != '@'; counter++) {
                message += clientResponse.charAt(counter);
            }

            message += '@';
            char[] messageCharArray = username.toCharArray();
            for (char c : messageCharArray) {
                message += ((int) c);
                message += (' ');
            }

            Set<String> sendToUsers = new HashSet<String>();

            counter++;
            for (; counter < clientResponse.length(); counter++) {
                String user = "";
                while (counter < clientResponse.length() && clientResponse.charAt(counter) != '@') {
                    String ascii = "";
                    while (counter < clientResponse.length() && clientResponse.charAt(counter) != ' ') {
                        ascii += clientResponse.charAt(counter);
                        counter++;
                    }
                    counter++;
                    user += (char) Integer.parseInt(ascii);
                }
                sendToUsers.add(user);
            }

            System.out.println("Message: \"" + message + "\"");
            
            try {
                System.out.print("Sending to: ");
                for (String user : sendToUsers) {
                    System.out.print(user + " ");
                    if (onlineUsers.containsKey(user)) {
                        PrintWriter pw = new PrintWriter(onlineUsers.get(user).getOutputStream(), true);
                        pw.println(message);
                    } else {
                        //throw new IOException("User doesn't exist in the database Thrown");
                    }
                }
                System.out.println();
            } catch (IOException e) {
                System.err.println("User doesn't exist in the database: " + e);
            }
            return message;
        }

        public void userOnline(boolean online) //SENDS STRING TO ALL CLIENTS IF A CLIENT JOINS/LEAVES
        {
            System.out.println("CHANGING ONLINE STATUS");

            try {
                char[] usernameCharArray = username.toCharArray();
                String usernameAscii = "@";
                if (online) {
                    System.out.println(username + " Logged in");
                    usernameAscii += '+';
                } else {
                    System.out.println(username + " Logged out");
                    usernameAscii += '-';
                }
                for (char c : usernameCharArray) {
                    usernameAscii += ((int) c);
                    usernameAscii += (' ');
                }
                System.out.println("User Status: " + usernameAscii);
                System.out.print("Sending to: ");
                for (String user : onlineUsers.keySet()) {
                    if (!username.equals(user)) {
                        System.out.print(user + " ");
                        PrintWriter pw = new PrintWriter(onlineUsers.get(user).getOutputStream(), true);
                        pw.println(usernameAscii);
                    }
                }
                System.out.println();
            } catch (IOException e) {
                System.err.println("Unable to send User Status to all users: " + e);
            }
        }

        public void addUserToDatabase(String clientResponse) //ADDS A NEW USER TO THE DATABASE
        {
            System.out.println("ADDING USER TO DATABASE");
            String username = "";
            int counter = 1;
            for (; clientResponse.charAt(counter) != '~'; counter++) {
                String ascii = "";
                while (clientResponse.charAt(counter) != ' ' && clientResponse.charAt(counter) != '~') {
                    ascii += clientResponse.charAt(counter);
                    counter++;
                }
                username += (char) Integer.parseInt(ascii);
            }

            counter++;
            String password = "";
            for (; counter < clientResponse.length(); counter++) {
                String ascii = "";
                while (clientResponse.charAt(counter) != ' ') {
                    ascii += clientResponse.charAt(counter);
                    counter++;
                }
                password += (char) Integer.parseInt(ascii);
            }

            System.out.println("Attempting to add: " + username + " with password: " + password + " to the database");
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                if (!userDatabase.getMap().containsKey(username)) {
                    userDatabase.addUser(username, password);
                    System.out.println("Successfully created user");
                    pw.println("Successfully created user");

                } else {
                    System.out.println("Username already exists");
                    pw.println("Username already exists");
                }
            } catch (IOException e) {
                System.err.println("Failed to create user");
            }
        }

        private String allOnlineUsers() {
            String sendAllOnlineUsers = "*";
            for (String onlineUser : onlineUsers.keySet()) {
                if (!username.equals(onlineUser)) {
                    sendAllOnlineUsers += "@";
                    char[] onlineUserAscii = onlineUser.toCharArray();
                    for (char c : onlineUserAscii) {
                        sendAllOnlineUsers += ((int) c);
                        sendAllOnlineUsers += (' ');
                    }
                }
            }
            return sendAllOnlineUsers;
        }

        public void logOutUser(String clientResponse) {
            String user = "";
            int counter = 1;
            for (; counter < clientResponse.length(); counter++) 
            {
                String ascii = "";
                while (counter < clientResponse.length() && clientResponse.charAt(counter) != ' ') 
                {
                    ascii += clientResponse.charAt(counter);
                    counter++;
                }
                user += (char) Integer.parseInt(ascii);
            }
            userOnline(false);
            closeSocket = true;
        }

        public String toString()
        {
            String toString = "Users Online: ";
            for(String user: onlineUsers.keySet())
            {
                toString += (user + ", ");
            }
            return toString;
        }
        
        public void run() //THREAD TO READ IN ANY IMPUT FRPOM CLIENTS
        {
            try {

                do {
                    String clientResponse = br.readLine();
                    System.out.println("Read input from: " + username + " with message: " + clientResponse);

                    if (clientResponse != null) {
                        switch (clientResponse.charAt(0)) //CHECKS WHAT THE CLIENT IS TELLING THE SERVER
                        {
                            case '@': //CHECK LOGIN DETAILS
                            {
                                CheckUserStatus(clientResponse);
                                break;
                            }
                            case '+': //SEND MESSAGE
                            {
                                ReceiveMessage(clientResponse);
                                break;
                            }
                            case '*': //ADD USER TO THE DATABASE
                            {
                                addUserToDatabase(clientResponse);
                                break;
                            }
                            case '-': //USER LOGGED OUT
                            {
                                logOutUser(clientResponse);
                                break;
                            }
                        }
                    }
                    System.out.println(toString());
                    System.out.println();
                } while (!stopRequested && !closeSocket);
                System.out.println("Closing connection with " + username);
                onlineUsers.remove(username);
                br.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Could not create bufferedReader: " + e);
            }
        }
    }
}
