import java.io.*; import java.net.*;
public class EchoClient extends Thread { public EchoClient() {
}
    public void start(String userInput) {
        Socket echoSocket = null; PrintWriter out = null; BufferedReader in = null;
        try {
            echoSocket = new Socket(InetAddress.getByName("localhost"), 2345);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));
        } catch (UnknownHostException e) { System.err.println("Don't know about host."); System.exit(1);
        } catch (IOException e) { System.err.println("Couldn't get I/O"); System.exit(1);
        }
        //BufferedReader stdIn = new BufferedReader( new InputStreamReader(System.in));
        //String userInput;
        try{
            //while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                //if (userInput.equals("Bye.")) break;
//                while(in.readLine() != null){
//                    System.out.println("echo: " + in.readLine());
//                }
            //System.out.println("echo: " + in.readLine());
            String echo = in.readLine();
            echo = echo.replace("[@]", "\n");
            System.out.println("echo: " + echo);

                //System.out.println("echo: " + in.readLine());
                //System.out.println("echo: " + serverInput);
            //}
            out.close();
            in.close();
            //stdIn.close();
            echoSocket.close();
        } catch (IOException ioe) {
            System.out.println("Failed");
            //System.exit(-1);
        }}}
