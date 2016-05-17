/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csbl.passwordbreaker.client;

import com.csbl.passwordbreaker.util.MD5HashUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File: Server.java
 * Created on Jan 13, 2016, 12:15:39 AM
 * @author NOMAN
 */
public final class Client {
    
    /* MACROS */
    private static String ADDRESS = "localhost";
    private static int PORT = 1234;
    
    private static int PASSWORD_LENGTH = 5;
    
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread readerThread;

    /**
     * Default constructor
     */
    public Client() {
        try {
            socket = new Socket(ADDRESS, PORT); //connected
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new PrintWriter(this.socket.getOutputStream());
            
            sendMessageToServer("REQUEST");
            
        } catch (IOException ioe) {
            System.out.println("No server found on "+ADDRESS+":"+PORT);
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ioe);
        }

        /**
         * This thread maintain continuous communication with server
         */
        this.readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (reader != null) {
                    while (true) {
                        try {
                            String _RES = reader.readLine();
                            if(_RES.equals("RESPONSE")){
                                String hash = reader.readLine();
                                String LP = reader.readLine();
                                String UP = reader.readLine();
                                System.out.println("Server Says: hash=>"+hash+" | LP=>"+LP+" | UP=>"+UP);
                                
                                checkPassword(hash, LP, UP);
                                
                            } else if(_RES.equals("QUIT_CLIENT")){
                                System.out.println("Client closing!");
                                return;
                            }
                            
                            
                            //push message through callBack
                            
                            //ACK
                            //sendMessageToServer("Hello Server, Thanks");
                        } catch (SocketException se){
                            return;
                        } catch (IOException ex) {
                            System.out.println("Server Not Found!");
                            //Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                            return;
                        }
                    }
                }
            }
        });

    }
    
    /**
     * Start client in separate thread
     */
    public void startClient(){
        this.readerThread.start();
    }
    
    /**
     * Stop client thread
     */
    public void stopClient(){
        this.readerThread.suspend();
    }
    
    /**
     * Send message to server
     * @param msg 
     */
    public void sendMessageToServer(String msg){
        if (writer != null) {
            System.out.println("Client[You] Says: " + msg);
            writer.println(msg);
            writer.flush();
        }
    }

    /**
     * Check password to break the actual password.
     * @param hash MD5 hash of password given by server
     * @param LP plain lower password given by server
     * @param UP plain upper password given by server
     */
    private void checkPassword(String hash, String LP, String UP){
        String password = LP;
        int password_int = Integer.parseInt(password, Character.MAX_RADIX);
        do{
            if(hash.equals(MD5HashUtils.getMD5HashPassword(password))){
                System.out.println("Password Found! =====> "+password);
                sendMessageToServer("SUCCESS");
                return;
            }
            
            if(password.compareTo("ZZZZZ") == 0){
                break;
            }
            
            password_int += 1; // Integer.parseInt(password, Character.MAX_RADIX)+1;
            password = Integer.toString(password_int, Character.MAX_RADIX).toUpperCase();

            if(password.length() < PASSWORD_LENGTH){
                password = ("00000" + password).substring(password.length());
            }
                
            
        }while(password.compareTo(UP) <= 0);
        
        System.out.println("Retrying !!!");
        
        sendMessageToServer("RETRY");
    }
    
    
    /**
     * Main method to run client
     * @param args 
     */
    public static void main(String args[]){
        if (args.length == 2){
            ADDRESS = args[0];
            PORT = Integer.parseInt(args[1]);
        }
        new Client().startClient();
    }
}
