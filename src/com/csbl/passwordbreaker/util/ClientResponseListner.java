/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csbl.passwordbreaker.util;

import com.csbl.passwordbreaker.server.EchoServerThread;

/**
 *
 * @author NOMAN
 */
public interface ClientResponseListner {
    /**
     *
     * @param echoServerThread
     */
    public void onRequest(EchoServerThread echoServerThread, String msgType);
    
}
