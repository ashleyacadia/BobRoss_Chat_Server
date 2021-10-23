/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ADAAssignment1;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
    File:
    username1~password1
    username2~password2
    username3~password3
*/

public class UserDatabase 
{
    private File users;
    private Map<String, String> userMap;
    
    public UserDatabase()
    {
        users = new File("UserDatabase.txt"); //READS IN FILE
        userMap = new HashMap<String, String>();
        createUserMap();
    }
    
    private void createUserMap() //CONVERT THE FILE TEXT INTO A MAP
    {
        try
        {
            FileInputStream fis = new FileInputStream(users);
            
            try
            {
                byte nextByte = (byte)fis.read();
                while (nextByte != -1)
                {
                    String username = "";
                    while (nextByte != 10 && nextByte != -1 && nextByte != 126)
                    {

                        username += (char)nextByte;
                        nextByte = (byte)fis.read();
                    }

                    if (nextByte == 10)
                    {
                        throw new IOException("Cannot read in users");
                    }

                    String password = "";
                    nextByte = (byte)fis.read();
                    while (nextByte != 10 && nextByte != -1)
                    {
                        password += (char)nextByte;
                        nextByte = (byte)fis.read();
                    }
                    nextByte = (byte)fis.read();
                    userMap.put(username, password);
                }
            }
            catch (IOException e)
            {
                System.err.println("Cannot read in users: " + e);
            }
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Cannot find file: " + e);
        }
    }
    
    public Map<String, String> getMap()
    {
        return userMap;
    }
    
    public void addUser(String username, String password) //ADD A NEW USER TO THE MAP AND TEXT FILE
    {
        String fileInput = "\n" + username + "~" + password;
        byte[] byteArray = fileInput.getBytes();
        
        try
        {
            FileOutputStream fos = new FileOutputStream(users, true);
            try
            {
                fos.write(byteArray);
            }
            catch (IOException e)
            {
                System.err.println("Cannot write to file when adding user: " + e);
            }
            
        }
        catch(FileNotFoundException e)
        {
            System.err.println("Cannot find file when adding user: " + e);
        }

        userMap.put(username, password);
        System.out.println("Added User: " + username + ", " + password);
    }
    
    public String toString() //PRINTS OUT ENTIRE MAP OF USERS
    {
        String toString = "";
        for (String userName : userMap.keySet())
        {
            toString += "Username: " + userName + "\nPassword: " + userMap.get(userName) + "\n";
        }
        return toString;
    }
    
    //MAIN USED TO TEST IF THE FILE IS BEING READ IN PROPERLY
    /*
    public static void main(String[] args) 
    {
        UserDatabase test = new UserDatabase();
        test.addUser("BobRoss", "HappyTrees");
        System.out.println(test.toString());
    }
    */
}
