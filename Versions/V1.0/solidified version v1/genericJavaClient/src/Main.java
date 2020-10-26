import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
//        while (true){
//            System.out.println("Give your next query");
//            String word = in.nextLine();
//            EchoClient echoClient = new EchoClient();
//            echoClient.start(word, false);
//        }

        //Test script. Everything seems to work fine.
        testScript();
        //testingExists();
        //testDelete();
        //testUpdateElement();
        //testExistsValue();
        //testCreateDocumentFromMap(10);
        //testReadOneOfEach();
        //testReadOneOfEach();
        //testReadOneOfEach();
        //testReadOneOfEach();
        //testCreateUsers();
        //checkNames();





    } //end of main()


    //this is the testscript
    //The write and the read operations need to be distanced between them because writing takes more time
    //than reading and we wish to keep everything multithreaded.
    static void testScript(){
        //Test script. Everything seems to work fine.
        FireStoreQueries fire = new FireStoreQueries();
        HashMap<String, String> map =  new HashMap<>();
        String[] arr;

        //============ Testing is something exists ===============================================
        System.out.println("Testing existsCollection() - false expected:");
        System.out.println(fire.existsCollection("notAValue"));
        System.out.println("Testing existsDocument() - false expected:");
        System.out.println(fire.existsDocument("notAValue", "notAValue"));
        System.out.println("Testing existsKey() - false expected:");
        System.out.println(fire.existsKey("notAValue", "notAValue", "notAValue"));


        //============ Testing the creation of collections ========================================
        System.out.println("Creating collections in several ways...");
        System.out.println("Creating collection testcollection1");
        fire.createCollection("testCollection1");
        System.out.println("Creating collection testcollection2");
        fire.createCollectionWithDocument("testCollection2", "testdocument2.1");
        System.out.println("Creating collection testcollection3");
        fire.createCollectionWithDocumentAndElement("testCollection3", "testdocument3.1",
                "key3.1", "value3.1");

        //============ Testing the creation of Documents ========================================
        System.out.println("Creating documents in several ways...");
        System.out.println("Creating the document testDocument1 on testcollection1");
        fire.createDocument("testCollection1", "testDocument1");
        System.out.println("Creating the document testDocument2 on testcollection1");
        fire.createDocumentWithElement("testCollection1", "testDocument2", "key2",
                "value2");
        System.out.println("Creating the document randomDocument1 on testcollection1");
        fire.createDocument("testCollection1", "randomDocument");
        fire.createElement("testCollection1", "randomDocument" ,"key1", "value1");
        fire.createElement("testCollection1", "randomDocument", "key2", "value2");
        fire.createElement("testCollection1", "randomDocument", "key3", "value3");
        fire.createElement("testCollection1", "randomDocument", "key4", "value4");

        //============ Doing the reading ========================================================
        System.out.println("Waiting 60 seconds to give bit time to the server to do the writing...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Reading testCollection1");
        arr = fire.readCollection("testCollection1");
        for(int x=0; x<arr.length; x++){
            System.out.println("[" + x + "] " + arr[x]);
        }
        System.out.println("Reading testCollection2");
        arr = fire.readCollection("testCollection2");
        for(int x=0; x<arr.length; x++){
            System.out.println("[" + x + "] " + arr[x]);
        }
        System.out.println("Reading testCollection3");
        arr = fire.readCollection("testCollection3");
        for(int x=0; x<arr.length; x++){
            System.out.println("[" + x + "] " + arr[x]);
        }
        System.out.println("Reading testDocument1 from testCollection1");
        map = fire.readDocument("testCollection1", "testDocument1");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("Reading testDocument2 from testCollection1");
        map = fire.readDocument("testCollection1", "testDocument2");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("Reading randomDocument from testCollection1");
        map = fire.readDocument("testCollection1", "randomDocument");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        // ======== wrong readings ===========================================================
        System.out.println("Reading testCollection35. (-1 expected)");
        arr = fire.readCollection("testCollection35");
        for(int x=0; x<arr.length; x++){
            System.out.println("[" + x + "] " + arr[x]);
        }
        System.out.println("Reading testDocument1 from testCollection35. (-1 expected)");
        map = fire.readDocument("testCollection35", "testDocument1");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        //============ Testing is something exists ===============================================
        System.out.println("Waiting 2 seconds to give bit time to the server to do the reading...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Testing existsCollection() - true expected:");
        System.out.println(fire.existsCollection("testCollection1"));

        System.out.println("Waiting 2 seconds to give bit time to the server to do the reading...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Testing existsDocument() - true expected:");
        System.out.println(fire.existsDocument("testCollection1", "randomDocument"));

        System.out.println("Waiting 5 seconds to give bit time to the server to do the reading...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Testing existsKey() - true expected:"); //this one sometimes fails, possibly because of
        //the short time between each read.
        System.out.println(fire.existsKey("testCollection1", "randomDocument",
                "key1"));



        //============ Testing the deletion of Documents ========================================
        System.out.println("Cleaning up by deleting...");
        fire.deleteCollection("testCollection1");
        fire.deleteCollection("testCollection2");
        fire.deleteCollection("testCollection3");
        System.out.println("Testing is done!");
    }


    static void testingExists(){
        FireStoreQueries fire = new FireStoreQueries();
        //============ Testing is something exists ===============================================
        System.out.println("Testing existsCollection() - false expected:");
        System.out.println(fire.existsCollection("notAValue"));
        System.out.println("Testing existsDocument() - false expected:");
        System.out.println(fire.existsDocument("notAValue", "notAValue"));
        System.out.println("Testing existsKey() - false expected:");
        System.out.println(fire.existsKey("notAValue", "notAValue", "notAValue"));

        //============= CREATE A COLLECTION =================================================
        System.out.println("Creating the document randomDocument1 on testcollection1");
        fire.createDocument("testCollection1", "randomDocument");
        fire.createDocument("testCollection2", "randomDocument");
        fire.createDocument("testCollection3", "randomDocument");
        fire.createDocument("testCollection4", "randomDocument");
        fire.createElement("testCollection1", "randomDocument" ,"key1", "value1");
        fire.createElement("testCollection2", "randomDocument", "key2", "value2");
        fire.createElement("testCollection3", "randomDocument", "key3", "value3");
        fire.createElement("testCollection4", "randomDocument", "key4", "value4");

        //============ Testing is something exists ===============================================
        System.out.println("Waiting 60 seconds to give bit time to the server to do the WRITING...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Testing existsCollection() - true expected:");
        System.out.println(fire.existsCollection("testCollection1"));

        System.out.println("Testing existsDocument() - true expected:");
        System.out.println(fire.existsDocument("testCollection1", "randomDocument"));


        System.out.println("Testing existsKey(key1) - true expected:"); //this one sometimes fails, possibly because of
        //the short time between each read.
        System.out.println(fire.existsKey("testCollection1", "randomDocument",
                "key1"));
        System.out.println("Testing existsKey(key2) - true expected:"); //this one sometimes fails, possibly because of
        //the short time between each read.
        System.out.println(fire.existsKey("testCollection2", "randomDocument",
                "key2"));
        System.out.println("Testing existsKey(key3) - true expected:"); //this one sometimes fails, possibly because of
        //the short time between each read.
        System.out.println(fire.existsKey("testCollection3", "randomDocument",
                "key3"));
        System.out.println("Testing existsKey(key4) - true expected:"); //this one sometimes fails, possibly because of
        //the short time between each read.
        System.out.println(fire.existsKey("testCollection4", "randomDocument",
                "key4"));


        System.out.println("Testing existsCollection() - True expected:");
        System.out.println(fire.existsValue("testCollection4",
                "randomDocument", "value4"));


        //============ Testing the deletion of Documents ========================================
        System.out.println("Cleaning up by deleting...");
        fire.deleteCollection("testCollection1");
        fire.deleteCollection("testCollection2");
        fire.deleteCollection("testCollection3");
        fire.deleteCollection("testCollection4");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Testing is done!");
    }//end of testingExists()

    static void testExistsValue(){
        FireStoreQueries fire = new FireStoreQueries();
        //============ Testing is something exists ===============================================
        System.out.println("Testing existsCollection() - false expected:");
        System.out.println(fire.existsValue("notAValue", "notAValue", "notAValue"));
        //============ creating something
        fire.createDocument("testCollection1", "randomDocument");
        fire.createElement("testCollection1", "randomDocument" ,"key1", "value1");
        //============ trying to search if a value exists
        System.out.println("Waiting 60 seconds to let the server do a proper writing");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Testing existsCollection() - true expected:");
        System.out.println(fire.existsValue("testCollection1", "randomDocument" ,
                "value1"));
        //============= deleting the collection =================
        System.out.println("Cleaning up any generated data");
        fire.deleteCollection("testCollection1");
        System.out.println("Terting is done!");
    }

    static  void testCreateDocumentFromMap(int entries){
        HashMap<String,String> map = new HashMap<>();
        FireStoreQueries fire = new FireStoreQueries();
        //creating the collection from the map
        System.out.println("generating entries");
        for(int x=0; x<entries; x++){
            map.put(String.valueOf(x), "value");
        }
        System.out.println("The map is generated:");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("creating the document");
        fire.createDocument("testCollection1", "randomDocument");
        fire.createDocumentfromAMap("testCollection1", "randomDocument", map);
        System.out.println("Waiting 60 seconds to let the server do a proper writing");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //reading the document
        HashMap<String,String> map2 = fire.readDocument("testCollection1", "randomDocument");
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        //cleaning up the generated data
        System.out.println("Cleaning up any generated data");
        fire.deleteCollection("testCollection1");
        System.out.println("Terting is done!");
    }


    static  void testReadOneOfEach(){
        HashMap<String,String> map = new HashMap<>();
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Generating documents (users)");
        fire.createDocument("users", "user1");
        fire.createDocument("users", "user2");
        fire.createDocument("users", "user3");
        fire.createDocument("users", "user4");
        fire.createElement("users", "user1" ,"name", "John");
        fire.createElement("users", "user2" ,"name", "Jane");
        fire.createElement("users", "user3" ,"name", "Alice");
        fire.createElement("users", "user4" ,"name", "Bob");

        System.out.println("Waiting 60 seconds to give bit time to the server to do the WRITING...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("We start the reading...");
        map = fire.readOneElementFromEachDocument("users", "name");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        fire.deleteDocument("users", "user1");
        fire.deleteDocument("users", "user2");
        fire.deleteDocument("users", "user3");
        fire.deleteDocument("users", "user4");
    }

    static void testCreateUsers(){
        FireStoreQueries fire = new FireStoreQueries();
        fire.createDocument("users", "user1");
        fire.createDocument("users", "user2");
        fire.createDocument("users", "user3");
        fire.createDocument("users", "user4");
        fire.createElement("users", "user1" ,"name", "John");
        fire.createElement("users", "user2" ,"name", "Jane");
        fire.createElement("users", "user3" ,"name", "Alice");
        fire.createElement("users", "user4" ,"name", "Bob");
    }

    static void checkNames(){
        HashMap<String,String> map = new HashMap<>();
        FireStoreQueries fire = new FireStoreQueries();
        map = fire.readOneElementFromEachDocument("users", "name");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }

    static void testDelete(){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Creating the document randomDocument1 and testcollection1");
        fire.createDocument("testCollection1", "randomDocument1");
        System.out.println("Creating the document randomDocument1 on testcollection2");
        fire.createDocument("testCollection2", "randomDocument2");
        System.out.println("Creating the document randomDocument3 on testcollection3");
        fire.createDocument("testCollection3", "randomDocument3");

        //Adding elements on the document randomDocument1 of testcollection1
        System.out.println("Adding 4 elements on the document randomDocument1 of testcollection1");
        fire.createElement("testCollection1", "randomDocument1" ,"key1", "value1");
        fire.createElement("testCollection1", "randomDocument1", "key2", "value2");
        fire.createElement("testCollection1", "randomDocument1", "key3", "value3");
        fire.createElement("testCollection1", "randomDocument1", "key4", "value4");

        //Adding 4 elements on the document randomDocument2 of testcollection2
        System.out.println("Adding 4 elements on the document randomDocument2 of testcollection2");
        fire.createElement("testCollection2", "randomDocument2" ,"key1", "value1");
        fire.createElement("testCollection2", "randomDocument2", "key2", "value2");
        fire.createElement("testCollection2", "randomDocument2", "key3", "value3");
        fire.createElement("testCollection2", "randomDocument2", "key4", "value4");

        //Adding 4 elements on the document randomDocument3 of testcollection3
        System.out.println("Adding  4 elements on the document randomDocument3 of testcollection3");
        fire.createElement("testCollection3", "randomDocument3" ,"key1", "value1");
        fire.createElement("testCollection3", "randomDocument3", "key2", "value2");
        fire.createElement("testCollection3", "randomDocument3", "key3", "value3");
        fire.createElement("testCollection3", "randomDocument3", "key4", "value4");

        System.out.println("Waiting 60 seconds to give bit time to the server to do the WRITING...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Deleting testCollection1 entirely");
        System.out.println("Waiting 10 seconds to give bit time to the server to do the WRITING...");
        fire.deleteCollection("testCollection1");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("testing if testCollection1 exists - False expected:");
        String word = String.valueOf(fire.existsCollection("testCollection1"));
        System.out.println(word);

        System.out.println("Deleting randomDocument2 from testCollection2");
        fire.deleteDocument("testCollection2", "randomDocument2");
        System.out.println("Waiting 10 seconds to give bit time to the server to do the WRITING...");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Displaying the testCollection2. -1 is expected:");
        String[] arr = fire.readCollection("testCollection2");
        for(int x=0; x<arr.length; x++){
            System.out.println("[" + x + "] " + arr[x]);
        }

        System.out.println("Deleting ket4 from testCollection3");
        fire.deleteElement("testCollection3", "randomDocument3", "key4");
        HashMap<String,String> map = new HashMap<>();
        System.out.println("Waiting 60 seconds to give bit time to the server to do the WRITING...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map = fire.readDocument("testCollection3", "randomDocument3");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }


        System.out.println("Cleaning up by deleting...");
        fire.deleteCollection("testCollection2");
        fire.deleteCollection("testCollection3");
    } //testDelete() ends here


    static void testUpdateElement(){
        FireStoreQueries fire = new FireStoreQueries();
        System.out.println("Creating the document randomDocument1 and testcollection1");
        fire.createDocument("testCollection1", "randomDocument1");
        //Adding elements on the document randomDocument1 of testcollection1
        System.out.println("Adding 4 elements on the document randomDocument1 of testcollection1");
        fire.createElement("testCollection1", "randomDocument1" ,"key1", "value1");
        fire.createElement("testCollection1", "randomDocument1", "key2", "value2");
        fire.createElement("testCollection1", "randomDocument1", "key3", "value3");
        fire.createElement("testCollection1", "randomDocument1", "key4", "value4");
        HashMap<String,String> map = new HashMap<>();
        System.out.println("Waiting 60 seconds to give bit time to the server to do the WRITING...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map = fire.readDocument("testCollection1", "randomDocument1");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Displaying TestCollection1");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }


        System.out.println("Changing key4 from value4 to yolo1");
        fire.updateElement("testCollection1", "randomDocument1" ,
                "key4", "yolo1");
        System.out.println("Waiting 60 seconds to give bit time to the server to do the WRITING...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map = fire.readDocument("testCollection1", "randomDocument1");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Displaying TestCollection1");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }


        System.out.println("Changing key4 from yolo1 to yolo2");
        fire.updateElement("testCollection1", "randomDocument1" ,
                "key4", "yolo2");
        System.out.println("Waiting 60 seconds to give bit time to the server to do the WRITING...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map = fire.readDocument("testCollection1", "randomDocument1");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Displaying TestCollection1");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }


        System.out.println("Changing key4 from yolo2 to yolo3");
        fire.updateElement("testCollection1", "randomDocument1" ,
                "key4", "yolo3");
        System.out.println("Waiting 60 seconds to give bit time to the server to do the WRITING...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map = fire.readDocument("testCollection1", "randomDocument1");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Displaying TestCollection1");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }


        System.out.println("Changing key4 from yolo3 to yolo4");
        fire.updateElement("testCollection1", "randomDocument1" ,
                "key4", "yolo4");
        System.out.println("Waiting 60 seconds to give bit time to the server to do the WRITING...");
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map = fire.readDocument("testCollection1", "randomDocument1");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Displaying TestCollection1");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        System.out.println("Cleaning up by deleting...");
        fire.deleteCollection("testCollection3");
        System.out.println("Testing updating and element is done");

    }

}
