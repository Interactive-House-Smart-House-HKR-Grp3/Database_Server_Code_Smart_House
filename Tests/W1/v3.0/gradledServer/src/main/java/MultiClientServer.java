import java.io.*;
import java.net.*;
public class MultiClientServer extends Thread {

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket= new ServerSocket(Configurations.sourcePort);
            //System.out.println(serverSocket.getInetAddress().getHostAddress());
            System.out.println(InetAddress.getLocalHost().getHostAddress());
        }catch (IOException e) {
            System.out.println("Could not listen on port: 2345");
            System.exit(-1);
        }
        Socket clientSocket = null;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                Server server = new Server(clientSocket);
                System.out.println("A client got accepted");
                server.start();
            } catch (IOException e) { System.out.println("Accept failed:2345");
                System.exit(-1);
            }
        }}}
