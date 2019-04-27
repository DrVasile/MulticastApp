import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
    private static final int port = 8888;
    private static byte[] bytes = new byte[256];

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("Started server on port " + port);

        while (true) {
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
            socket.receive(packet);
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(bytes, bytes.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());
            socket.send(packet);
            System.out.println("Received : " + received);

            if (received.equals("exit")) {
                break;
            }
        }

        socket.close();
    }
}