/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csbl.passwordbreaker.util;

/**
 * File: StringUtils.java
 * Created on Jan 20, 2016, 1:33:43 AM
 * @author NOMAN
 */
public class StringUtils {
    static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static final int CHAR_LEN = CHARACTERS.length();
    
    public static String incrementString(String str, int incrementVal){
        for(int i=0; i<incrementVal; i++){
            str = increment(str);
            if(str.compareTo("ZZZZZ") == 0){
                break;
            }
        }
        return str;
    }
    
    public static String increment(String strAugend){
        StringBuilder sb = new StringBuilder(strAugend.length());
        int carry = 0; //increment by 1
        int increment = 1;
//        System.out.println(strAugend);
//        System.out.println("   +1");
//        System.out.println("---------------------");
        
        for(int i=strAugend.length()-1; i>=0; i--){
            int index = CHARACTERS.indexOf(strAugend.charAt(i));
            index+=carry;
            carry = (index+increment) / CHAR_LEN;
            index = (index+increment) %  CHAR_LEN;
            increment = 0;
//            System.out.println("      ("+i+")carry: "+carry+" | index: "+index);
            
            sb.insert(0, CHARACTERS.charAt(index));
        }
        
//        System.out.println("---------------------\n"+sb.toString());
        return sb.toString();
    }
    
}
