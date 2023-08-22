/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    public static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        int ocurrencesCount=0;
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        int checkedListsCount=0;
        
        for (int i=0;i<skds.getRegisteredServersCount() && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            checkedListsCount++;
            if (skds.isInBlackListServer(i, ipaddress)){
                blackListOcurrences.add(i);
                ocurrencesCount++;
            }
        }
        
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        return blackListOcurrences;
    }

    public List<Integer> checkHost(String ipaddress, int n){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        int ocurrencesCount=0;
        HostBlacklistsDataSourceFacade skds= HostBlacklistsDataSourceFacade.getInstance();
        int checkedListsCount=0;

        ArrayList<BlackListThread> Threads = new ArrayList<>();
        int NumberOfLists = skds.getRegisteredServersCount()/n;
        //int surplus = skds.getRegisteredServersCount()%n;
        int start = 0;
        int end = 0;
        
        for (int i = 0; i < n; i++){
            start = i * NumberOfLists;
            end = start + NumberOfLists;
            BlackListThread SearchThread = new BlackListThread(start, end, ipaddress);
            Threads.add(SearchThread);
            SearchThread.start();
        }

        //if (surplus > 0){
          //  BlackListThread SearchThread = new BlackListThread(start, end, ipaddress);
            //Threads.add(SearchThread);
            //SearchThread.start();
        //}

        for (BlackListThread BlackList: Threads){
            try {
                BlackList.join();
            } catch (InterruptedException e) {
                System.err.println("Error al esperar a que los hilos terminen: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        for (BlackListThread BlackList: Threads){
            ocurrencesCount += BlackList.getOcurrencesCount();
            checkedListsCount += BlackList.getCheckedLists();
            blackListOcurrences.addAll(BlackList.getBlackListOcurrence());
        }
        
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
