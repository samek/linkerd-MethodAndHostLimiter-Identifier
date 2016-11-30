package si.poponline.identifiers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by samek on 10/11/2016.
 */
public class RateLimmiter {
    private static RateLimmiter ourInstance = new RateLimmiter();

    public static RateLimmiter getInstance() {
        return ourInstance;
    }

    private ConcurrentHashMap<String, Integer> iplist       = new ConcurrentHashMap<String, Integer>();
    private ConcurrentHashMap<String, Integer> bannedIpList = new ConcurrentHashMap<String, Integer>();
    private Integer lastDelete = 0;
    private Integer lastBanDelete = 0;

    private RateLimmiter() {
    }

    private Integer get_current_time() {
        return (int) (System.currentTimeMillis() / 1000L);
    }

    public Boolean check_ip(String ip, String host) {

        ip = host.replace(".","_")+"."+ip.replace(".","_");

        if (this.lastDelete==0) {
            this.lastDelete = this.get_current_time();
            this.lastBanDelete = this.get_current_time();
        }

        if ( (this.get_current_time() - this.lastDelete)>10) {
            //we reset it//
            System.out.println("Resseting map");
            for (Map.Entry<String, Integer> entry : this.iplist.entrySet())
            {
                //System.out.println(entry.getKey() + " - " + entry.getValue());
                if (entry.getValue()>10)
                    UDPClient.Send(entry.getKey(), entry.getValue());
            }
            this.lastDelete = this.get_current_time();
            //this.iplist = new HashMap<String,Integer>();
            this.iplist.clear();
        }


        //delete banned list//
        if ((this.get_current_time()-this.lastBanDelete>60)) {
            System.out.println("Resseting banned map");

            this.lastBanDelete = this.get_current_time();
            this.bannedIpList.clear();
        }
        Integer nr =0;
        if (this.iplist.containsKey(ip)) {
            nr = this.iplist.get(ip);
        }

        //if (this.bannedIpList.containsKey(ip) || nr>5  ) {
        //    System.out.println(ip+ ": is banned!");
        //    this.bannedIpList.put(ip,nr);
        //    return false;
        //}

        nr++;
        this.iplist.put(ip,nr);
        return true;

    }
}
