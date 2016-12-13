package si.poponline.identifiers

import java.util.Map
import java.util.concurrent.ConcurrentHashMap

import com.twitter.logging.Logger

/**
  * Created by samek on 10/11/2016.
  */
object RateLimmiter {
  private val ourInstance: RateLimmiter = new RateLimmiter
  private[RateLimmiter] val log = Logger.get("RateLimmiter")
  def getInstance: RateLimmiter = {
    return ourInstance
  }
}

class RateLimmiter private() {
  private val iplist: ConcurrentHashMap[String, Integer] = new ConcurrentHashMap[String, Integer]
  private val bannedIpList: ConcurrentHashMap[String, Integer] = new ConcurrentHashMap[String, Integer]
  private var lastDelete: Integer = 0
  private var lastBanDelete: Integer = 0
  private var banThreshold: Integer = 10
  private var banIntervalCleanUP: Integer = 60
  private var timeWindow: Integer = 10

  def setLimits(banThreshold: Integer, banIntervalCleanUP: Integer, timeWindow: Integer) {
    this.banThreshold = banThreshold
    this.banIntervalCleanUP = banIntervalCleanUP
    this.timeWindow = timeWindow
    //System.out.print(banThreshold + " "+ banIntervalCleanUP + " " + timeWindow );
  }

  private def get_current_time: Integer = {
     (System.currentTimeMillis / 1000L).toInt
  }

  def check_ip(ipIn: String, host: String): Boolean = {
    val ip = host.replace(".", "_") + "." + ipIn.replace(".", "_")
    if (this.lastDelete.equals(0)) {
      this.lastDelete = this.get_current_time
      this.lastBanDelete = this.get_current_time
    }
    if ((this.get_current_time - this.lastDelete) > this.timeWindow) {
      //we reset it//
      //System.out.println("Resseting map")
      RateLimmiter.log.info("Resseting map")
      import scala.collection.JavaConversions._
      for (entry <- this.iplist.entrySet) {
        //System.out.println(entry.getKey() + " - " + entry.getValue());

        if (entry.getValue > this.banThreshold) {
          RateLimmiter.log.info("Banned ip:"+entry.getKey() + " - " + entry.getValue())
          UDPClient.Send(entry.getKey, entry.getValue)
        }
      }
      this.lastDelete = this.get_current_time
      this.iplist.clear()
    }
    //delete banned list//
    if ((this.get_current_time - this.lastBanDelete > this.banIntervalCleanUP)) {
      //System.out.println("Resseting banned map")
      RateLimmiter.log.info("Resseting banned map")
      this.lastBanDelete = this.get_current_time
      this.bannedIpList.clear()
    }
    var nr: Integer = 0
    if (this.iplist.containsKey(ip)) {
      nr = this.iplist.get(ip)
    }
    if (this.bannedIpList.containsKey(ip) || nr > this.banThreshold) {
      //System.out.println(ip + ": is banned!")
      RateLimmiter.log.info(ip + ": is banned!")
      this.bannedIpList.put(ip, nr)
      //TODO MAKE DRY-RUN
      //return false
    }
    nr += 1
    this.iplist.put(ip, nr)
    return true
  }
}