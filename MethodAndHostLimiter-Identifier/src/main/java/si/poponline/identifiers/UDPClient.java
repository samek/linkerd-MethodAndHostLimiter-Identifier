package si.poponline.identifiers;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by samek on 11.11.2016.
 */
public class UDPClient {

    public static void Send(String packet, Integer value) {
        try {
            String host = "10.0.0.251";
            int port = 2003;

            Integer unixTime = (int) (System.currentTimeMillis() / 1000L);
            byte[] message = (
                    "linkerd-ip-rstats." +packet+" "+value.toString()+" "+unixTime.toString()
            ).getBytes();

            // Get the internet address of the specified host
            InetAddress address = InetAddress.getByName(host);

            // Initialize a datagram packet with data and address
            DatagramPacket dgram = new DatagramPacket(message, message.length, address, port);

            // Create a datagram socket, send the packet through it, close it.
            DatagramSocket dsocket = new DatagramSocket();
            dsocket.send(dgram);
            //System.out.println("SYSTEM OUT: sending UDP: "+message);
            dsocket.close();
        } catch (Exception e) {
            System.err.println(e);
            //System.out.println("SYSTEM ERROR: cannot send UDP: "+e);
        }
    }

}
