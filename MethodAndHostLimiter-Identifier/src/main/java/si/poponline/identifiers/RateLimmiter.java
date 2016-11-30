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
    private Integer banThreshold = 10;
    private Integer banIntervalCleanUP = 60;
    private Integer timeWindow = 10;

    private RateLimmiter() {
    }

    public void  setLimits(Integer banThreshold, Integer banIntervalCleanUP, Integer timeWindow) {
        this.banThreshold = banThreshold;
        this.banIntervalCleanUP = banIntervalCleanUP;
        this.timeWindow = timeWindow;

        //System.out.print(banThreshold + " "+ banIntervalCleanUP + " " + timeWindow );
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

        if ( (this.get_current_time() - this.lastDelete)>this.timeWindow) {
            //we reset it//
            System.out.println("Resseting map");
            for (Map.Entry<String, Integer> entry : this.iplist.entrySet())
            {
                //System.out.println(entry.getKey() + " - " + entry.getValue());
                if (entry.getValue()>this.banThreshold)
                    UDPClient.Send(entry.getKey(), entry.getValue());
            }
            this.lastDelete = this.get_current_time();
            //this.iplist = new HashMap<String,Integer>();
            this.iplist.clear();
        }


        //delete banned list//
        if ((this.get_current_time()-this.lastBanDelete>this.banIntervalCleanUP)) {
            System.out.println("Resseting banned map");

            this.lastBanDelete = this.get_current_time();
            this.bannedIpList.clear();
        }
        Integer nr =0;
        if (this.iplist.containsKey(ip)) {
            nr = this.iplist.get(ip);
        }

        if (this.bannedIpList.containsKey(ip) || nr>this.banThreshold  ) {
            System.out.println(ip+ ": is banned!");
            this.bannedIpList.put(ip,nr);
            return false;
        }

        nr++;
        this.iplist.put(ip,nr);
        return true;

    }
}
