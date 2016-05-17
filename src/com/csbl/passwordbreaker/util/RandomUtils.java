/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csbl.passwordbreaker.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * File: RandomUtils.java
 * Created on Jan 14, 2016, 1:44:36 AM
 * @author NOMAN
 */
public class RandomUtils {
    static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random random = new Random();

    public static String getRandomPassword(int length){
       StringBuilder sb = new StringBuilder(length);
       for( int i = 0; i < length; i++ ) 
          sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
       return sb.toString();
    }
    
    public static String generateClientId(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date());
    }
}
