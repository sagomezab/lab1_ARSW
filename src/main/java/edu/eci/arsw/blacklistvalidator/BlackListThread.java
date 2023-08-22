package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;

public class BlackListThread extends Thread{
    
    private HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
    private LinkedList<Integer> blackListOcurrences = new LinkedList<>();

    private int start;
    private int end;
    private String IpAddress;
    private int checkedLists = 0;
    private int ocurrencesCount=0;

    public BlackListThread(int start, int end, String IpAddress){
        this.start = start;
        this.end = end;
        this.IpAddress = IpAddress;
    }

    public void run(){
        for (int i = start; i < end && ocurrencesCount < HostBlackListsValidator.BLACK_LIST_ALARM_COUNT; i++){
            checkedLists++;
            //System.out.println(checkedLists);
            if(skds.isInBlackListServer(i, IpAddress)){
                blackListOcurrences.add(i);
                ocurrencesCount++;
                //System.out.println(ocurrencesCount);
            }
        }
    }

    public int getOcurrencesCount(){ 
        return ocurrencesCount; 
    }

    public LinkedList<Integer> getBlackListOcurrence(){
        return blackListOcurrences;
    }

    public int getCheckedLists(){
        return checkedLists;
    }
}
