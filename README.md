# Course: *Network Programming*
------
# Topic: *UDP Multicast App*
### Author: *Drumea Vasile*
------
## Objectives :
1. Get familiar with UDP Sockets API of the chosen language;

2. Develop a simple multicast app;

## Theory :

UDP (User Datagram Protocol) is an alternative communication protocol to Transmission Control Protocol (TCP) used primarily for establishing low-latency and loss-tolerating connections between applications on the internet.

Both UDP and TCP run on top of the Internet Protocol (IP) and are sometimes referred to as UDP/IP or TCP/IP. But there are important differences between the two.

Where UDP enables process-to-process communication, TCP supports host-to-host communication. TCP sends individual packets and is considered a reliable transport medium; UDP sends messages, called datagrams, and is considered a best-effort mode of communications.

In addition, where TCP provides error and flow control, no such mechanisms are supported in UDP. UDP is considered a connectionless protocol because it doesn't require a virtual circuit to be established before any data transfer occurs.

Java uses MulticastSocket class to create UDP multicast sockets to receive datagram packets sent to a multicast IP address.

A multicast socket is based on a group membership. After creating and bounding a multicast socket, we can join it to the multicast group and any datagram packet sent to the group will be received by the socket as well.

In IPv4, any IP address in the range 224.0.0.0 to 239.255.255.255 can be used as a multicast address to send a datagram packet.

The IP address 224.0.0.0 is reserved and you shouldn't be used in an application.
  
## App structure overview :

1. **Client** - The class responsible for receiving the command, processing it and making the necessary changes, also at the end it returns the response. Its fields include the following : 

~~~
static final String host = "234.0.0.0";    // The IP address used as multicast address
static final int port = 8888;              // The used port
private static DatagramSocket socket;      // Instance of the UDP socket
private static Receiver receiver;          // Instance of the receiver thread
~~~

Then, I have the constructor where I initialize the socket and a boolean function which tells me if the socket is closed or not : 

~~~
Client() throws SocketException {
        socket = new DatagramSocket();
}

boolean closed(){
    return socket.isClosed();
}
~~~

The requests are processed in execute method :

~~~
 String execute(String command) throws IOException {
    assert command != null;
    if (command.equals("help")) {                                 // Help command 
        return "help - display usage information\n" +
               "exit - close the socket\n" +
               "join - join the group\n" +
               "leave - leave the group\n";
    }
    if (command.equals("exit")) {                                 // Exit command    
        socket.close();
        return "Socket closed!";
    }
    if (command.equals("join")) {                                 // Join command to join the multicast group
        if (receiver == null || !receiver.isAlive()) {
            receiver = new Receiver();
            receiver.start();
            return "Joined to the group!";
        } else {
            return "You are already in a group!";
        }
    }
    byte[] msg = command.getBytes();                              // Get byte array from the command   
    DatagramPacket packet = new DatagramPacket(msg, msg.length, InetAddress.getByName(host), port);
    socket.send(packet);                                          // Send the packet to the subscribers
    return "Message processed!";
}
~~~


2. **Server** - The class which makes the connection for the server. It has the following fields : 

~~~

~~~

The connection : 

~~~
try (var listener = new ServerSocket(port)) {
    System.out.println("The server is running on port " + port + "...");
    while (true) {
        var client = listener.accept();
        ClientThread thread = new ClientThread(client);
        thread.start();
    }
}
~~~

Below are declared the coresponding methods for the commands e.g. here's the method for adding a message : 

~~~
void addMessage(String message) {
    synchronized (this) {
        messageList.add(message);
    }
}
~~~

They are synchronized so that we could have concurrent processing.


3. **ClientThread** - A class which extends Thread class and it is the blueprint of the threads created by the server. In the run method we add in the list the thread, passes the request to the Dispatcher and then removes the thread : 

~~~
public void run() {
    try {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        System.out.println("New Client Thread has been created!");

        ECHOServer.getInstance().addClientThread(this);

        while (true) {
            String line = in.readUTF();
            if (line.equals("exit")) {
                break;
            }

            System.out.println(this.getName() + " wrote : " + line);
            Dispatcher.processMessage(out, line);
        }

        ECHOServer.getInstance().removeClientThread(this);
        socket.close();
    } catch (IOException e1) {
        e1.printStackTrace();
    }
}
~~~


4. **Dispatcher** - a final class with a static method which converts our request into 2 components, the message and command and then by a switch statement it computes a response which is written in the output stream of the client : 

~~~
request = request.trim();

String response = request + "\n";
String command;
String message;

if (request.contains(" ")) {
    command = request.substring(0, request.indexOf(" "));
    message = request.substring(request.indexOf(" ") + 1);
} else {
    command = request;
    message = "";
}

switch (command){
    case "help":
        response += "help - available commands :\n" +
                    "about - display some text about the system\n" +
                    "threads - display number of active threads\n" +
                    "time - display the current time on server\n" +
                    "add String - adds a message in the list\n" +
                    "rem String - removes a message from the list\n" +
                    "print-msg - prints all the messages\n";
        break;

    case "about":
        response += "Client Server App\n Author : Wazea\n";
        break;

    case "threads":
        response += "Total active threads on the server: " + ECHOServer.getInstance().getThreadCount();
        break;

    case "time":
        response += "Current server time: " + new Date().toString();
        break;

    case "add":
        ECHOServer.getInstance().addMessage(message);
        response += "Your message has been saved on the server!";
        break;

    case "print-msg":
        response += ECHOServer.getInstance().getMessages();
        break;
}

out.writeUTF(response);
~~~

## Screenshot

![](img/Capture1.PNG)

