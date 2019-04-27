import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

class Client {

    static final String host = "234.0.0.0";
    static final int port = 8888;
    private static DatagramSocket socket;
    private static Receiver receiver;

    Client() throws SocketException {
        socket = new DatagramSocket();
    }

    boolean closed(){
        return socket.isClosed();
    }

    String execute(String command) throws IOException {
        assert command != null;
        if (command.equals("help")) {
            return "help - display usage information\n" +
                   "exit - close the socket\n" +
                   "join - join the group\n" +
                   "leave - leave the group\n";
        }
        if (command.equals("exit")) {
            socket.close();
            return "Socket closed!";
        }
        if (command.equals("join")) {
            if (receiver == null || !receiver.isAlive()) {
                receiver = new Receiver();
                receiver.start();
                return "Joined to the group!";
            } else {
                return "You are already in a group!";
            }
        }
        byte[] msg = command.getBytes();
        DatagramPacket packet = new DatagramPacket(msg, msg.length, InetAddress.getByName(host), port);
        socket.send(packet);
        return "Message processed!";
    }
}