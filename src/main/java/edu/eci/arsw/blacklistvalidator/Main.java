/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){
        long start = System.currentTimeMillis();
        HostBlackListsValidator hblv=new HostBlackListsValidator();
        //List<Integer> blackListOcurrences=hblv.checkHost("200.24.34.55", 1);
        //int nucleos = Runtime.getRuntime().availableProcessors();
        //List<Integer> blackListOcurrences=hblv.checkHost("200.24.34.55", nucleos*2);
        //List<Integer> blackListOcurrences=hblv.checkHost("200.24.34.55", 50);
        List<Integer> blackListOcurrences=hblv.checkHost("200.24.34.55", 100);
        System.out.println("The host was found in the following blacklists:"+blackListOcurrences);
        //System.out.println("Cantidad de nucleos del procesador "+ nucleos*2);
        long end = System.currentTimeMillis();
        double time = (end - start);
        System.out.println("Time: " + time);
        
    }
    
}
