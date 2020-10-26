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
        String[] commands3 = {"2", "1", "A dummy Collection", "A  dummy Document", "A  dummy key", "A dummy value"};
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
            //System.out.println("CREATE Queries");
            CREATEQuery(commands[1], commands[2], commands[3], commands[4], commands[5]);
        }

        if(commands[0].equalsIgnoreCase("2")){
            //System.out.println("READ Queries");
            word = READQuery(commands[1], commands[2], commands[3], commands[4], commands[5]);
            //==== Isolated Output stream ============
            out.println(word);
            System.out.println(word);
            //========================================
        }

        if(commands[0].equalsIgnoreCase("3")){
            //System.out.println("UPDATE Queries");
            UPDATEQuery(commands[1], commands[2], commands[3], commands[4], commands[5]);
        }

        if(commands[0].equalsIgnoreCase("4")){
            //System.out.println("DELETE Queries");
            DELETEQuery(commands[1], commands[2], commands[3], commands[4]);
        }

        if(commands[0].equalsIgnoreCase("5")){
            //System.out.println("CUSTOM Queries");
            word = "A CUSTOM query";
            //==== Isolated Output stream ============
            out.println(word);
            System.out.println(word);
            //========================================
        }

        if(commands[0].equalsIgnoreCase("6")){
            //System.out.println("TEST Queries");
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


    //After a heavy testing, this area of code is considered functional.
    void CREATEQuery(String choice, String collection, String document, String key, String value){
        if(choice.equalsIgnoreCase("1")) {
            //System.out.println("A Collection has been chosen to be created");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.createCollection(collection, document, key, value);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String check = firebasehandler.readCollection(collection);
            if(check == null){
                firebasehandler.createCollection(collection, document, key, value);
            } else {
                if(check.equalsIgnoreCase("-1")){
                    firebasehandler.createCollection(collection, document, key, value);
                } else {
                    if(!check.contains(collection)){
                        firebasehandler.createCollection(collection, document, key, value);
                    }
                }
            }
        }

        if(choice.equalsIgnoreCase("2")) {
            //System.out.println("A Document has been chosen to be created");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.createDocument(collection, document, key, value);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String check = firebasehandler.readCollection(collection);
            if(check == null){
                firebasehandler.createDocument(collection, document, key, value);
            } else {
                if(check.equalsIgnoreCase("-1")){
                    firebasehandler.createDocument(collection, document, key, value);
                } else {
                    if (!check.contains(document)) {
                        firebasehandler.createDocument(collection, document, key, value);
                    }
                }
            }
        }

        if(choice.equalsIgnoreCase("3")) {
            //System.out.println("A Data element has been chosen to be created");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.createElement(collection, document, key, value);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String check = firebasehandler.readCollection(collection);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(check == null){
                firebasehandler.createElement(collection, document, key, value);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                check = firebasehandler.readCollection(collection);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(check == null){
                    firebasehandler.createElement(collection, document, key, value);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    check = firebasehandler.readCollection(collection);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(check.equalsIgnoreCase("-1")){
                        firebasehandler.createElement(collection, document, key, value);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        check = firebasehandler.readCollection(collection);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(check.equalsIgnoreCase("-1")){
                            firebasehandler.createElement(collection, document, key, value);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            check = firebasehandler.readCollection(collection);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if(check.equalsIgnoreCase("-1")){
                                firebasehandler.createElement(collection, document, key, value);
                            } else {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                check = firebasehandler.readDocument(collection, document);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!check.contains(key)) {
                                    firebasehandler.createElement(collection, document, key, value);
                                    try {
                                        Thread.sleep(4000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    check = firebasehandler.readDocument(collection, document);
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (!check.contains(key)) {
                                        firebasehandler.createElement(collection, document, key, value);
                                        try {
                                            Thread.sleep(5000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        check = firebasehandler.readDocument(collection, document);
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (!check.contains(key)) {
                                            firebasehandler.createElement(collection, document, key, value);
                                            try {
                                                Thread.sleep(6000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            check = firebasehandler.readDocument(collection, document);
                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (!check.contains(key)) {
                                                firebasehandler.createElement(collection, document, key, value);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            check = firebasehandler.readDocument(collection, document);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!check.contains(key)) {
                                firebasehandler.createElement(collection, document, key, value);
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                check = firebasehandler.readDocument(collection, document);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!check.contains(key)) {
                                    firebasehandler.createElement(collection, document, key, value);
                                    try {
                                        Thread.sleep(5000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    check = firebasehandler.readDocument(collection, document);
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (!check.contains(key)) {
                                        firebasehandler.createElement(collection, document, key, value);
                                        try {
                                            Thread.sleep(6000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        check = firebasehandler.readDocument(collection, document);
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if (!check.contains(key)) {
                                            firebasehandler.createElement(collection, document, key, value);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        check = firebasehandler.readDocument(collection, document);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!check.contains(key)) {
                            firebasehandler.createElement(collection, document, key, value);
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            check = firebasehandler.readDocument(collection, document);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!check.contains(key)) {
                                firebasehandler.createElement(collection, document, key, value);
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                check = firebasehandler.readDocument(collection, document);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!check.contains(key)) {
                                    firebasehandler.createElement(collection, document, key, value);
                                    try {
                                        Thread.sleep(6000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    check = firebasehandler.readDocument(collection, document);
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (!check.contains(key)) {
                                        firebasehandler.createElement(collection, document, key, value);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if(check.equalsIgnoreCase("-1")){
                        firebasehandler.createElement(collection, document, key, value);
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        check = firebasehandler.readDocument(collection,document);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!check.contains(key)) {
                            firebasehandler.createElement(collection, document, key, value);
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            check = firebasehandler.readDocument(collection,document);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!check.contains(key)) {
                                firebasehandler.createElement(collection, document, key, value);
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                check = firebasehandler.readDocument(collection,document);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!check.contains(key)) {
                                    firebasehandler.createElement(collection, document, key, value);
                                    try {
                                        Thread.sleep(6000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    check = firebasehandler.readDocument(collection,document);
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (!check.contains(key)) {
                                        firebasehandler.createElement(collection, document, key, value);
                                    }
                                }
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                check = firebasehandler.readCollection(collection);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(check.equalsIgnoreCase("-1")){
                    firebasehandler.createElement(collection, document, key, value);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    check = firebasehandler.readCollection(collection);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(check.equalsIgnoreCase("-1")){
                        firebasehandler.createElement(collection, document, key, value);
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        check = firebasehandler.readDocument(collection, document);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!check.contains(key)) {
                            firebasehandler.createElement(collection, document, key, value);
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            check = firebasehandler.readDocument(collection, document);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!check.contains(key)) {
                                firebasehandler.createElement(collection, document, key, value);
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                check = firebasehandler.readDocument(collection, document);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!check.contains(key)) {
                                    firebasehandler.createElement(collection, document, key, value);
                                    try {
                                        Thread.sleep(6000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    check = firebasehandler.readDocument(collection, document);
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (!check.contains(key)) {
                                        firebasehandler.createElement(collection, document, key, value);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    check = firebasehandler.readDocument(collection, document);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!check.contains(key)) {
                        firebasehandler.createElement(collection, document, key, value);
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        check = firebasehandler.readDocument(collection, document);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!check.contains(key)) {
                            firebasehandler.createElement(collection, document, key, value);
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            check = firebasehandler.readDocument(collection, document);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!check.contains(key)) {
                                firebasehandler.createElement(collection, document, key, value);
                                try {
                                    Thread.sleep(6000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                check = firebasehandler.readDocument(collection, document);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!check.contains(key)) {
                                    firebasehandler.createElement(collection, document, key, value);
                                }
                            }
                        }
                    }
                }
            } else {
                if(check.equalsIgnoreCase("-1")){
                    firebasehandler.createElement(collection, document, key, value);
                } else {
                    check = firebasehandler.readDocument(collection,document);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!check.contains(key)) {
                        firebasehandler.createElement(collection, document, key, value);
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        check = firebasehandler.readDocument(collection,document);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!check.contains(key)) {
                            firebasehandler.createElement(collection, document, key, value);
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            check = firebasehandler.readDocument(collection,document);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!check.contains(key)) {
                                firebasehandler.createElement(collection, document, key, value);
                                try {
                                    Thread.sleep(6000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                check = firebasehandler.readDocument(collection,document);
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!check.contains(key)) {
                                    firebasehandler.createElement(collection, document, key, value);
                                }
                            }
                        }
                    }
                }
            }
        }

        if(choice.equalsIgnoreCase("4")) {
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.createDocumentFromMap(collection, document, key);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String check = firebasehandler.readCollection(collection);
//            String check2 = firebasehandler.readDocument(collection, document);
//            int l1 = check2.split("mpt@mpt").length;
//            int l2 = key.split("@new@entry@").length;
//            System.out.println("Ratio: " + l1 + "-" + l2);
            //System.out.println("Key: " + key);
            //System.out.println("Check: " + check);
            if(check == null){
                firebasehandler.createDocumentFromMap(collection, document, key);
            } else {
                if(check.equals("-1")){
                    firebasehandler.createDocumentFromMap(collection, document, key);
                } else {
                    //We can do bit better here
                    if (!check.contains(document)) {
                        firebasehandler.createDocumentFromMap(collection, document, key);
                    }else {
                        //We can do bit better here
                        check = firebasehandler.readDocument(collection, document);
                        int length1 = check.split("mpt@mpt").length;
                        int length2 = key.split("@new@entry@").length;
                        //System.out.println("Ratio: " + length1 + "-" + length2);
                        if (length1 != length2) {
                            firebasehandler.createDocumentFromMap(collection, document, key);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            check = firebasehandler.readDocument(collection, document);
                            int length3 = check.split("mpt@mpt").length;
                            int length4 = key.split("@new@entry@").length;
                            if (length3 != length4) {
                                firebasehandler.createDocumentFromMap(collection, document, key);
                            }
                        }
                    }
                }
            }
        }
    }

    //After a heavy testing, this area of code is considered functional.
    String READQuery(String choice, String collection, String document, String key, String value){

        String word = "No return statement";

        if(choice.equalsIgnoreCase("1")) {
            //System.out.println("A Collection has been chosen to be read");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            word = firebasehandler.readCollection(collection);
        }

        if(choice.equalsIgnoreCase("2")) {
            //System.out.println("A Document has been chosen to be read");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            word = firebasehandler.readDocument(collection,document);
        }

        if(choice.equalsIgnoreCase("3")) {
            //System.out.println("A Data element has been chosen to be read");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            word = firebasehandler.readElement(collection,document,key);
        }

        if(choice.equalsIgnoreCase("4")) {
            //System.out.println("A Collection has been chosen to be checked if exists");
            String word2 = "-1";
            FireBaseHandler firebasehandler = new FireBaseHandler();
            word2 = firebasehandler.readCollection(collection);
            if(word2 != null){
                if(word2.equalsIgnoreCase("-1")){
                    word = "false";
                } else {
                    word = "true";
                }
            } else {
                word = "false";
            }
        }

        if(choice.equalsIgnoreCase("5")) {
            //System.out.println("A Document has been chosen to be checked if exist");
            String word2 = "-1";
            FireBaseHandler firebasehandler = new FireBaseHandler();
            word2 = firebasehandler.readDocument(collection,document);
            if(word2 != null){
                if(word2.equalsIgnoreCase("-1")){
                    word = "false";
                } else {
                    word = "true";
                }
            } else {
                word = "false";
            }
        }


        if(choice.equalsIgnoreCase("6")) {
            //System.out.println("A Data element has been chosen to be checked if exist");
            String word2 = null;
            FireBaseHandler firebasehandler = new FireBaseHandler();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            //word2 = firebasehandler.readElement(collection,document,key);

            //Making sure that the document exists. that save us a bunch of time.
            String word3 = firebasehandler.readDocument(collection,document);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(word3 != null){
                if(word3.equalsIgnoreCase("-1")){
                    return "false";
                } else {
                    word = "true";
                }
            } else {
                return "false";
            }

            while(word2 == null){
                word2 = firebasehandler.readElement(collection,document,key);
            }

            if(word2 != null){
                if(word2.equalsIgnoreCase("-1")){
                    word = "false";
                } else {
                    word = "true";
                }
            } else {
                //System.out.println("ERROR: The server got null from reading");
                word = "false";
            }
        }

        //1@@7 is for reading one value of a specific from each document in a collection
        if(choice.equalsIgnoreCase("7")){
            FireBaseHandler firebasehandler = new FireBaseHandler();
            word = firebasehandler.readOneElementFromEachDocument(collection,key);
        }

        return  word;
    } //end of READQuery()

    void UPDATEQuery(String choice, String collection, String document, String key, String value){
        if(choice.equalsIgnoreCase("1")) {
            //System.out.println("A Collection has been chosen to be updated");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.updateCollection(collection);
        }

        if(choice.equalsIgnoreCase("2")) {
            //System.out.println("A Document has been chosen to be updated");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.updateDocument(collection, document);
        }

        if(choice.equalsIgnoreCase("3")) {
            //System.out.println("A Data element has been chosen to be updated");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.updateElement(collection,document,key,value);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String check = firebasehandler.readDocument(collection,document);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!check.contains(value)){
                firebasehandler.updateElement(collection,document,key,value);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String check2 = firebasehandler.readDocument(collection,document);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!check2.contains(value)){
                    firebasehandler.updateElement(collection,document,key,value);
                }
            }
        }
    }

    void DELETEQuery(String choice, String collection, String document, String key){
        if(choice.equalsIgnoreCase("1")) {
            //System.out.println("A Collection has been chosen to be deleted");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.deleteCollection(collection, 256);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!firebasehandler.readCollection(collection).equalsIgnoreCase("-1")){
                firebasehandler.deleteCollection(collection, 256);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!firebasehandler.readCollection(collection).equalsIgnoreCase("-1")){
                    firebasehandler.deleteCollection(collection, 256);
                }
            }
        }

        if(choice.equalsIgnoreCase("2")) {
            //System.out.println("A Document has been chosen to be deleted");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.deleteDocument(collection, document);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!firebasehandler.readDocument(collection,document).equalsIgnoreCase("-1")){
                firebasehandler.deleteDocument(collection, document);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!firebasehandler.readDocument(collection,document).equalsIgnoreCase("-1")){
                    firebasehandler.deleteDocument(collection, document);
                }
            }
        }

        if(choice.equalsIgnoreCase("3")) {
            System.out.println("A Data element has been chosen to be deleted");
            FireBaseHandler firebasehandler = new FireBaseHandler();
            firebasehandler.deleteElement(collection, document, key);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(firebasehandler.readDocument(collection,document).contains(key)){
                firebasehandler.deleteElement(collection, document, key);
            }
        }
    }
}