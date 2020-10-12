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
                translator(inputLine, out);
                //out.println(word);
                //out.write(word);
                //System.out.println(word);
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

    void translator(String inputLine, PrintWriter out){
        String word= "No return statement";

        // ==== The code below prepares the input to be used from the translator
        String[] commands2 = inputLine.split("@@");
        String[] commands3 = {"2", "1", "A Collection", "A Document", "A key", "A value"};
        int params = commands2.length;
        String[] commands = new String[6];
        for (int i =0; i < 6; i++){
            if(i<params){
                commands[i] = commands2[i];
            } else {
                commands[i] = commands3[i];
            }
        } //end of for loop
        //============== Preparation ends here ====================================

        if(commands[0].equalsIgnoreCase("1")){
            System.out.println("CREATE Queries");
            CREATEQuery(commands[1], commands[2], commands[3], commands[4], commands[5]);
        }

        if(commands[0].equalsIgnoreCase("2")){
            System.out.println("READ Queries");
            word = READQuery(commands[1], commands[2], commands[3], commands[4], commands[5]);
            //==== Isolated Output stream ============
            out.println(word);
            System.out.println(word);
            //========================================
        }

        if(commands[0].equalsIgnoreCase("3")){
            System.out.println("UPDATE Queries");
            UPDATEQuery(commands[1], commands[2], commands[3], commands[4], commands[5]);
        }

        if(commands[0].equalsIgnoreCase("4")){
            System.out.println("DELETE Queries");
            DELETEQuery(commands[1], commands[2], commands[3], commands[4]);
        }

        if(commands[0].equalsIgnoreCase("5")){
            System.out.println("CUSTOM Queries");
            word = "A CUSTOM query";
            //==== Isolated Output stream ============
            out.println(word);
            System.out.println(word);
            //========================================
        }

        if(commands[0].equalsIgnoreCase("6")){
            System.out.println("TEST Queries");
            if(commands[1].equalsIgnoreCase("1")){
                FireBaseHandler firebasehandler = new FireBaseHandler();
                word = firebasehandler.doFirebaseStuff();
                //==== Isolated Output stream ============
                out.println(word);
                System.out.println(word);
                //========================================
            }
            if(commands[1].equalsIgnoreCase("2")){
                FireBaseHandler firebasehandler = new FireBaseHandler();
                word = firebasehandler.doFirebaseStuff2();
                //==== Isolated Output stream ============
                out.println(word);
                System.out.println(word);
                //========================================
            }
        } //end of if(commands[0].equalsIgnoreCase("6")) AKA TEST Queries

    } //end of translator()


    void CREATEQuery(String choice, String collection, String document, String key, String value){
        if(choice.equalsIgnoreCase("1")) {
            System.out.println("A Collection has been chosen to be created");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.createCollection(collection);
        }

        if(choice.equalsIgnoreCase("2")) {
            System.out.println("A Document has been chosen to be created");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.createDocument(collection, document);
        }

        if(choice.equalsIgnoreCase("3")) {
            System.out.println("A Data element has been chosen to be created");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.createElement(collection, document, key, value);
        }
    }

    String READQuery(String choice, String collection, String document, String key, String value){

        String word = "No return statement";

        if(choice.equalsIgnoreCase("1")) {
            System.out.println("A Collection has been chosen to be read");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            word = firebasehandler.readCollection(collection);
        }

        if(choice.equalsIgnoreCase("2")) {
            System.out.println("A Document has been chosen to be read");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            word = firebasehandler.readDocument(collection,document);
        }

        if(choice.equalsIgnoreCase("3")) {
            System.out.println("A Data element has been chosen to be read");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            word = firebasehandler.readElement(collection,document,key);
        }

        return  word;
    }

    void UPDATEQuery(String choice, String collection, String document, String key, String value){
        if(choice.equalsIgnoreCase("1")) {
            System.out.println("A Collection has been chosen to be updated");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.updateCollection(collection);
        }

        if(choice.equalsIgnoreCase("2")) {
            System.out.println("A Document has been chosen to be updated");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.updateDocument(collection, document);
        }

        if(choice.equalsIgnoreCase("3")) {
            System.out.println("A Data element has been chosen to be updated");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.updateElement(collection,document,key,value);
        }
    }

    void DELETEQuery(String choice, String collection, String document, String key){
        if(choice.equalsIgnoreCase("1")) {
            System.out.println("A Collection has been chosen to be deleted");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.deleteCollection(collection, 256);
        }

        if(choice.equalsIgnoreCase("2")) {
            System.out.println("A Document has been chosen to be deleted");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.deleteDocument(collection, document);
        }

        if(choice.equalsIgnoreCase("3")) {
            System.out.println("A Data element has been chosen to be deleted");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.deleteElement(collection, document, key);
        }
    }
}