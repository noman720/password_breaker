/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csbl.passwordbreaker.util;

/**
 * File: ClientBean.java
 * Created on Jan 20, 2016, 1:27:36 AM
 * @author NOMAN
 */
public class ClientBean {
    private String LP; //Lower Password
    private String UP; //Upper Password
    private String clientId; //Connected Client Id
    private String ASSIGNED_PASS; //Assigned Password to the Client

    public ClientBean() {
    }

    public ClientBean(String LP, String UP) {
        this.LP = LP;
        this.UP = UP;
    }
    
    

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getLP() {
        return LP;
    }

    public void setLP(String LP) {
        this.LP = LP;
    }

    public String getUP() {
        return UP;
    }

    public void setUP(String UP) {
        this.UP = UP;
    }

    public String getASSIGNED_PASS() {
        return ASSIGNED_PASS;
    }

    public void setASSIGNED_PASS(String ASSIGNED_PASS) {
        this.ASSIGNED_PASS = ASSIGNED_PASS;
    }

    @Override
    public String toString() {
        return "LP: "+LP+" | UP: "+UP+" | clientId: "+clientId+" | ASSIGNED_PASS: "+ASSIGNED_PASS;
    }
    
    
    
}
