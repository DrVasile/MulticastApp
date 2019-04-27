import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Receiver extends Thread {

    private byte[] bytes = new byte[256];

    public void run(){
        try {
            MulticastSocket socket = new MulticastSocket(Client.port);
            InetAddress inetAddress = InetAddress.getByName(Client.host);
            socket.joinGroup(inetAddress);
            System.out.println("Joined the at " + inetAddress);

            while(true) {
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received message : " + received);
                if (received.equals("leave")) {
                    break;
                }
            }

            System.out.println("Left group!");
            socket.leaveGroup(inetAddress);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}