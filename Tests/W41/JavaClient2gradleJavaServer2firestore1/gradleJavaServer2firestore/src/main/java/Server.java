import java.io.*; import java.net.*;
import java.util.Arrays;

public class Server extends Thread {
    Socket clientSocket=null;
    public Server(Socket clientSocket) { this.clientSocket=clientSocket;
    }
    public void run() { PrintWriter out=null; BufferedReader in = null; try {
        out = new PrintWriter( clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(
                clientSocket.getInputStream()));
    } catch (IOException ioe) { System.out.println("Failed in creating streams"); System.exit(-1);
    }
        String inputLine, outputLine;
        try {
            while ((inputLine = in.readLine()) != null) {
                String word = inputLine;
                if(inputLine.equalsIgnoreCase("1")){
                    FireBaseHandler firebasehandler = new FireBaseHandler();
                    word = firebasehandler.doFirebaseStuff();
                }
                if(inputLine.equalsIgnoreCase("2")){
                    FireBaseHandler firebasehandler = new FireBaseHandler();
                    word = firebasehandler.doFirebaseStuff2();
                }
                out.println(word);
                //out.write(word);
                System.out.println(word);
                if (inputLine.equals("Bye.")) break;
                break;
                //Thread.sleep(5000);

            }
        } catch (IOException ioe) { System.out.println("Failed in reading, writing");
        //System.exit(-1);
        }
        try { clientSocket.close();
            out.close();
            in.close();
        } catch (IOException ioe) { System.out.println("Failed in closing down");
        //System.exit(-1);
        }
    }
}