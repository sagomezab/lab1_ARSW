/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain{
    
    public static void main(String a[]){
        CountThread primero = new CountThread(0,99);
        primero.run();
        //primero.start();
        CountThread segundo = new CountThread(100,199);
        segundo.run();
        //segundo.start();
        CountThread tercero = new CountThread(200,299);
        tercero.run();
        //tercero.start();
    }
    
}
