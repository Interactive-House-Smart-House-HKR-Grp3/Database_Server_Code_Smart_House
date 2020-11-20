package se.hkr.mqtt;

import java.io.*; import java.net.*;
public class EchoClient extends Thread {
    String echo = "no value";
    Socket echoSocket = null; PrintWriter out = null; BufferedReader in = null;

    public EchoClient() {
}
    public void start(String userInput, boolean returning) {
        //Socket echoSocket = null; PrintWriter out = null; BufferedReader in = null;
        try {
            echoSocket = new Socket(InetAddress.getByName(Configurations.ipAddress), Configurations.destinationPort);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));
        } catch (UnknownHostException e) { System.err.println("Don't know about host."); System.exit(1);
        } catch (IOException e) { System.err.println("Couldn't get I/O"); System.exit(1);
        }
        //BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in));
        //String userInput;
        try{
            out.println(userInput);

            if(returning){
                echo = in.readLine();
                //echo = echo.replace("@@", "\n");
                //System.out.println("echo: " + echo);
            }

            out.close();
            in.close();
            //stdIn.close();
            //echoSocket.close();
        } catch (IOException ioe) {
            System.out.println("Failed");
            //System.exit(-1);
        }}

        String returnEcho(){
        return  echo;
        }

        void terminate(){
            try {
                echoSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}
