/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csbl.passwordbreaker.server;

import com.csbl.passwordbreaker.util.ClientBean;
import com.csbl.passwordbreaker.util.ClientResponseListner;
import com.csbl.passwordbreaker.util.MD5HashUtils;
import com.csbl.passwordbreaker.util.RandomUtils;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File: Server.java
 * Created on Jan 13, 2016, 12:15:39 AM
 * @author NOMAN
 */
public class Server implements ClientResponseListner{
    
    /* MACROS */
    private static int PORT = 1234;
    private static int PASSWORD_LENGTH = 5;
    private static int BASE = 36; //Chanracters 0...9 A...Z
    private static int PASS_RANGE = 1000000; //Chanracters (BASE^PASSWORD_LENGTH)/RANGE
    private static int TOTAL_SLOT = 0; //Chanracters (BASE^PASSWORD_LENGTH)/RANGE
    private static String generatedPassword = null;
    private static int MAX_RETRY = 3;
    
    
    private static Server thisServer;
    
    private static List<ClientBean> clientList = new ArrayList<>();
    
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static Runnable serverRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            System.out.println("Running server listening on port: "+PORT);
            System.out.println("/**************************************************/\n\n");
            //running for listen mode
            while(true){
                try {
                    socket = serverSocket.accept();
                    System.out.println("\n/=========================================/");
                    System.out.println("New Client Found!");
                    System.out.println("/=========================================/\n");
                    //starting server-client connection in separate thread to avoid blocking
                    new EchoServerThread(socket, thisServer).startEchoServer();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
        }
    };
   

    /**
     * Get the generated password
     * @return 
     */
    public String getGeneratedPassword() {
        return Server.generatedPassword;
    }

    /**
     * Set the random password, it generates the plain password, 
     * cause hash password vary with date, 
     * so for long running server date will change and caused never breakable the password.
     */
    public void setGeneratedPassword() {
        Server.generatedPassword = RandomUtils.getRandomPassword(PASSWORD_LENGTH);
        System.out.println("Generated Password: "+getGeneratedPassword());
    }
    /**
     * This method is used to initialize password ranges 
     * and starting server in separate thread
     * @param server 
     */
    public void initServer(Server server){
        //generate slots of password
        TOTAL_SLOT = (int) Math.ceil(Math.pow(BASE, PASSWORD_LENGTH)/PASS_RANGE);
        //System.out.println("TOTAL SLOT: "+TOTAL_SLOT+"ZQ0CC.compartTo(10BFY4) = "+ "ZQ0CC".compareTo("10BFY4"));
        String LP = "00000";
        int LP_INT = Integer.parseInt(LP, Character.MAX_RADIX);
        for(int i=0; i<TOTAL_SLOT; i++){
            int UP_INT = LP_INT+999999; //0->999999 = 1000000
            String UP = Integer.toString(UP_INT, Character.MAX_RADIX).toUpperCase();
            
            if(LP.length() < PASSWORD_LENGTH){
                LP = ("00000" + LP).substring(LP.length());
            }
            
            if(UP.length() < PASSWORD_LENGTH){
                UP = ("00000" + UP).substring(UP.length());
            }
            
            if(LP.compareTo(UP) > 0){
                UP = "ZZZZZ";
            }
            ClientBean cb = new ClientBean(LP, UP);
            clientList.add(cb);
            System.out.println(i+" <-- "+cb);
            
            LP_INT = UP_INT+1;
            LP = Integer.toString(LP_INT, Character.MAX_RADIX).toUpperCase();
        }
        
        System.out.println("\n/**************************************************/");
        //run server on port
        thisServer = server;
        new Thread(serverRunnable).start();

        setGeneratedPassword();
        
        
        
    }

    /**
     * This is the call back method that invoked from client request
     * @param echoServerThread 
     */
    @Override
    public void onRequest(EchoServerThread echoServerThread, String msgType) {
        if (msgType.equals("REQUEST") || msgType.equals("RETRY")) {
            System.out.println(msgType+" from: " + echoServerThread.getClientId());
            //alocate a block
            if (getCountAlocatedSlotForClient(echoServerThread.getClientId()) > MAX_RETRY) {
                clearAlocatedSlotForClient(echoServerThread.getClientId());
                echoServerThread.stopEchoServer();
                return;
            }

            //assign password & client Id to a random free slot
            int index = getEmptySlot();
            clientList.get(index).setClientId(echoServerThread.getClientId());
            clientList.get(index).setASSIGNED_PASS(MD5HashUtils.getMD5HashPassword(getGeneratedPassword()));

            //send message[Hash & Upper & Lower Password] to client
            echoServerThread.sendMessageToClient(clientList.get(index));
        } else if (msgType.equals("SUCCESS")){
            System.out.println("\n/***********************!!!!!!!! ***************************/");
            System.out.println("Client ["+echoServerThread.getClientId()+"] got password !");
            System.out.println("/***********************!!!!!!!! ***************************/\n");
        }
        
    }
    
    
    //random generator instance
    Random r = new Random();
    /**
     * get empty slot for allocating client
     * @return slot id
     */
    private int getEmptySlot(){
        int index = r.nextInt(60);
        //System.out.println("Client<"+index+">");
        ClientBean clientBean = clientList.get(index);
        if(clientBean.getClientId() == null){
            return index;
        } else{
            return getEmptySlot();
        }
    }
    
    /**
     * get assigned slot to the client for retrying
     * @param clientId
     * @return total allocated slots
     */
    private int getCountAlocatedSlotForClient(String clientId){
        int count = 0;
        for(ClientBean clientBean : clientList){
            if(clientBean.getClientId() != null && clientBean.getClientId().equals(clientId)){
                count++;
            }
        }
        return count;
    }
    
    /**
     * clear slots after dropping connection with client
     * @param clientId 
     */
    private void clearAlocatedSlotForClient(String clientId){
        for(int i=0; i<TOTAL_SLOT; i++){
            if(clientList.get(i).getClientId() != null && clientList.get(i).getClientId().equals(clientId)){
                clientList.get(i).setClientId(null);
                clientList.get(i).setASSIGNED_PASS(null);
            }
        }
    }
    
    
    /**
     * main method to run server
     * @param args 
     */
    public static void main(String args[]){
        if (args.length == 1){
            PORT = Integer.parseInt(args[0]);
        }
        
        Server server = new Server();
        server.initServer(server);
    }
    

}
