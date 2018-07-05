package jjj.work.utils;

import java.util.Arrays;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  12:24 21/06/2018.
 */
public class MyTest {
    
    public static void main(String[] args) {
        String[] o = new String[10];
        System.out.println("o1="+o);
        for(int i=0; i<o.length-1; i++) {
            o[i] = "aaa";
        }
        String[] n = new String[20];
        String[] x = o;
        System.out.println("x="+x);
        o = n;
        System.out.println("o2="+o);
    
    
    }
}
