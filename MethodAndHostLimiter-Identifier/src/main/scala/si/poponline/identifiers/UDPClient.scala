package si.poponline.identifiers

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
  * Created by samek on 11.11.2016.
  */
object UDPClient {
  def Send(packet: String, value: Integer) {
    try {
      val host: String = "10.0.0.251"
      val port: Int = 2003
      val unixTime: Integer = (System.currentTimeMillis / 1000L).toInt
      val message: Array[Byte] = ("linkerd-ip-rstats." + packet + " " + value.toString + " " + unixTime.toString).getBytes
      // Get the internet address of the specified host
      val address: InetAddress = InetAddress.getByName(host)
      // Initialize a datagram packet with data and address
      val dgram: DatagramPacket = new DatagramPacket(message, message.length, address, port)
      // Create a datagram socket, send the packet through it, close it.
      val dsocket: DatagramSocket = new DatagramSocket
      dsocket.send(dgram)
      //System.out.println("SYSTEM OUT: sending UDP: "+message);
      dsocket.close()
    }
    catch {
      case e: Exception => {
        System.err.println(e)
        //System.out.println("SYSTEM ERROR: cannot send UDP: "+e);
      }
    }
  }
}