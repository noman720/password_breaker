/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csbl.passwordbreaker.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * File: MD5HashUtils.java Created on Jan 14, 2016, 11:21:18 PM
 *
 * @author NOMAN
 */
public class MD5HashUtils {

    public static String getMD5HashPassword(String password) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        password += sdf.format(new Date());
        
        //System.out.println("PassDate: "+password);
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            
            return sb.toString();
            
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }
    
}
