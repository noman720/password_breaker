/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csbl.passwordbreaker.server;

import com.csbl.passwordbreaker.util.ClientBean;
import com.csbl.passwordbreaker.util.ClientResponseListner;
import com.csbl.passwordbreaker.util.RandomUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File: EchoServerThread.java 
 * Created on Jan 13, 2016, 12:33:37 AM
 * @author NOMAN
 */
public class EchoServerThread {

    private String clientId;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread readerThread;
    private Socket mSocket;

    private static ClientResponseListner clientResponseListner;
    
    /**
     * Constructor to initialize echo server
     * @param socket
     * @param listner 
     */
    public EchoServerThread(Socket socket, ClientResponseListner listner) {
        mSocket = socket;
        clientId = RandomUtils.generateClientId();
        clientResponseListner = listner;
        final Socket socket_io = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        this.readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (reader != null) {
                    while (true) {
                        try {
                            if(socket_io.isClosed()){
                                return;
                            }
                            String msg = reader.readLine();
                            System.out.println("Client Says: "+msg);
                            if(msg.equals("REQUEST") || msg.equals("SUCCESS") || msg.equals("RETRY")){
                                clientResponseListner.onRequest(EchoServerThread.this, msg);
                                //sendMessageToClient(MD5HashUtils.getMD5HashPassword(Server.getGeneratedPassword()));
                            }
                            //push message through callBack
                        } catch (SocketException se){
                            System.out.println("Client Closed Connection!");
                            return;
                        } catch (IOException ex) {
                            Logger.getLogger(EchoServerThread.class.getName()).log(Level.SEVERE, null, ex);
                            return;
                        }
                    }
                }
            }
        });

    }
    
    /**
     * Start echo server to start channel or socket
     */
    public void startEchoServer(){
        this.readerThread.start();
        
        // send a message to client first time
        //sendMessageToClient("Hi Client, got u :)");
    }
    /**
     * Stop echo server to stop channel or socket
     */
    public void stopEchoServer(){
        quitClient();
        System.out.println("Stopping EchoServer");
        try {
            this.readerThread.suspend();
            this.mSocket.close();
            this.reader.close();
        } catch (IOException ex) {
            System.out.println("Exception Stopping EchoServer");
            Logger.getLogger(EchoServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * get client id
     * @return 
     */
    public String getClientId() {
        return clientId;
    }
    
    /**
     * Send message to client
     * @param clientBean 
     */
    public void sendMessageToClient(ClientBean clientBean){
        if(writer != null){
            System.out.println("Server[You] Says: "+clientBean+"\n");
            String LP = clientBean.getLP();
            String UP = clientBean.getUP();
            //send response as json of password hash and password range
            //String json = "{\"HASH\":\""+msg+"\", \"UP\": \""+UP+"\", \"LP\": \""+LP+"\"}";
            
            writer.println("RESPONSE");
            writer.println(clientBean.getASSIGNED_PASS());
            writer.println(LP);
            writer.println(UP);
            writer.flush();
        }
    }
    
    /**
     * Close the connection
     */
    public void quitClient(){
        if(writer != null){
            writer.println("QUIT_CLIENT");
            writer.flush();
        }
    }
    
    
}
