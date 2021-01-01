import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

public class FireBaseHandler {
    InputStream serviceAccount = null;
    GoogleCredentials credentials = null;
    FirebaseOptions options;
    Firestore db;
    SecureRandom random = new SecureRandom(); //we use that to give name to the FirebaseApp

    FireBaseHandler(){
        //1.Intialization. theoretically the handshake
        //Use a service account
        //InputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream(Configurations.privateKeyPath + "/key.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //GoogleCredentials credentials = null;
        try {
            credentials = GoogleCredentials.fromStream(serviceAccount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();

        //we use SecureRandom to give anew name to the FirebaseApp each time
        String name = String.valueOf(random.nextInt());
        FirebaseApp.initializeApp(options,name);
        db = FirestoreClient.getFirestore(FirebaseApp.getInstance(name));
    }

    // ===================== CREATE QUERIES ==============================

    //CreateCollection works. It is necessary to have at least one document and at least one key and one value.
    //if no applicable parameteres, then it gives the default values that are helping ofr filling the empty space.
    void createCollection(String collectionName, String documentName, String keyName, String value){
        initializeWriting();
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, Object> data = new HashMap<>();
        data.put(keyName, value);
        docRef.set(data);
        DataHolder.writing = false;

    }

    //createDocument() works. It is necessary to have at least one key and one value.
    //if no applicable parameters, then it gives the default values that are helping ofr filling the empty space.
    void createDocument(String collectionName, String documentName, String keyName, String value){
        initializeWriting();
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, Object> data = new HashMap<>();
        data.put(keyName, value);
        docRef.set(data);
        DataHolder.writing = false;

    }

    //createElement() works. It is necessary to have all the parameters.
    //if no applicable parameters, then it gives the default values that are helping ofr filling the empty space.
    void createElement2(String collectionName, String documentName, String keyName, String value){
        initializeWriting();
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, Object> data = new HashMap<>();
        data.put(keyName, value);
        //docRef.set(data);
        docRef.update(data);
        DataHolder.writing = false;
    }

    //createElement() works. It is necessary to have all the parameters.
    //if no applicable parameters, then it gives the default values that are helping ofr filling the empty space.
    void createElement(String collectionName, String documentName, String keyName, String value) {
        initializeWriting();
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, Object> data = new HashMap<>();
        data.put(keyName, value);
        System.out.println(data.get(keyName));
        //docRef.set(data);
        docRef.update(data);
        DataHolder.writing = false;
    }


        //createDocumentFromMap() creates or updates a document from a map that the client provides.
    //it is a really powerful tool and the way to do things.
    //if no applicable parameters, then it gives the default values that are helping ofr filling the empty space.
    void createDocumentFromMap(String collectionName, String documentName, String seed){
        initializeWriting();
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, String> data = seedToMap(seed);
        docRef.set(data);
        DataHolder.writing = false;
    }


    //===================== READ QUERIES ==================================
    //After a heavy testing, this area of code is consdiered functional.

    //heavily tested and it works
    String readCollection(String collectionName){
        String word = "";
        initializeReading();
        ApiFuture<QuerySnapshot> query = db.collection(collectionName).get();
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = query.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            DataHolder.reading =false;
            return "-1";
        }
        assert querySnapshot != null; //i just want the program not to crash by nullpoint exception
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        if(!querySnapshot.isEmpty()){
            for (QueryDocumentSnapshot document : documents) {
                word = document.getId() + "@@";
            }
        } else {
            word = "-1";
        }
        DataHolder.reading =false;
        return word;
    }


    //heavily tested and ti works
    String readDocument(String collectionName, String documentName){
        String word = "";
        initializeReading();
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = null;
        try {
            document = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            DataHolder.reading =false;
            return "-1";
        }
        //assert document != null; //i just want the program not to crash by nullpoint exception
        //(document.exists())
        if (document.exists()) {
            //System.out.println("Document data: " + document.getData());
            word =  syntaxToSeed(String.valueOf(document.getData()));
        } else {
            System.out.println("Problem with: " + documentName + " of " + collectionName);
            System.out.println("No such document!");
            word = "-1";
        }

        DataHolder.reading =false;
        return word;
    }


    //readElement() is heavily tested and it works. It produces a misterious error on the testing
    //when teh client's code is updated while the server runs. it fixes with a restart of the server.
    //Not a pleasant situation but it will need more testing in order to be tackled.
    String readElement(String collectionName, String documentName, String keyName){
        String word = "-1";
        keyName = keyName.trim();

        initializeReading();
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document2;
        document2 = null;

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            document2 = future.get();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int x = 0; x<15; x++){
                int num = 500 * x;
                try {
                    Thread.sleep(num);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (document2 == null){
                    document2 = future.get();
                } else {
                    break;
                }
            }
            if (document2 == null){
                DataHolder.reading =false;
                return  "-1";
            }
        } catch (InterruptedException | ExecutionException e) {
            //System.out.println("ERROR: Yeah the exception ran anyway!");
            e.printStackTrace();
            DataHolder.reading =false;
            return "-1";
        } //for the (InterruptedException | ExecutionException e)

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //assert document2 != null; //i just want the program not to crash by nullpoint exception

        try{
            word = document2.getString(keyName);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            for(int x = 0; x<15; x++){
                try {
                    int num = 500 * x;
                    Thread.sleep(num);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (word == null){
                    word = document2.getString(keyName);
                } else {
                    break;
                }
            }

            if (word == null){
                word = "-1";
            }
        } catch (Exception e){
            System.out.println("ERROR: " + e);
            word = "-1";
        }

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        DataHolder.reading =false;
        return word;
    }

    String readOneElementFromEachDocument(String collectionName, String keyName){
        initializeReading();
        ApiFuture<QuerySnapshot> query = db.collection(collectionName).get();
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = query.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            //DataHolder.reading = false;
            return  "-1";
        } catch (ExecutionException e) {
            e.printStackTrace();
            //DataHolder.reading = false;
            return  "-1";
        }
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        String word = "";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //DataHolder.reading = false;
        for (QueryDocumentSnapshot document : documents) {
//            if (document.contains(keyName)) {
//                word = word + document.getId() + "mpt@mpt";
//                word = word + document.getString(keyName);
//                word = word + "@@";
//            }
            String word2 = "user1";
            String word3 = null;
            String word4 = "";
            word2 = document.getId();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int x =0; x<20; x++){
                word3 = document.getString(keyName);
                //word3 = document.get(keyName).toString();
                //word3 = readElement("users","user1", "name");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(word3 != null){
                    break;
                }
            }
            word4 = word2 + "mpt@mpt";
            word = word + word4;
            //word = word + word3;
            if(word3 == null){
                word = word + "-1";
            } else {
                word = word + word3;
            }
            word = word + "@@";


        }
        DataHolder.reading = false;
        return word;
    }

    //===================== UPDATE QUERIES ==================================


    //No logical use for it now. Collections are immutable
    void updateCollection(String collectionName){
        initializeWriting();
        System.out.println("A collection with the name " + collectionName + " is updated!");
        DataHolder.writing = false;
    }

    //No logical use for it now. Documents are immutable.
    void updateDocument(String collectionName, String documentName){
        initializeWriting();
        System.out.println("A document with the name " + documentName + " is updated inside the "
                + collectionName);
        DataHolder.writing = false;
    }

    //createElement() works.
    void updateElement(String collectionName, String documentName, String keyName, String value){
        initializeWriting();
        System.out.println("updateelement runs");
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, Object> data = new HashMap<>();
        data.put(keyName, value);
        //docRef.set(data);
        docRef.update(data);
        DataHolder.writing = false;

    }

    //======================= DELETE QUERIES =================================

    /** Delete a collection in batches to avoid out-of-memory errors.
     * Batch size may be tuned based on document size (atmost 1MB) and application requirements.
     */
    //deleteCollection() works
    void deleteCollection(String collection2, int batchSize) {
        initializeWriting();
        try {
            CollectionReference collection = db.collection(collection2);
            // retrieve a small batch of documents to avoid out-of-memory errors
            ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
            int deleted = 0;
            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }
            if (deleted >= batchSize) {
                // retrieve and delete another batch
                deleteCollection(collection2, batchSize);
            }
        } catch (Exception e) {
            System.err.println("Error deleting collection : " + e.getMessage());
        }
        DataHolder.writing = false;
    }

    //deleteDocument() works.
    void deleteDocument(String collectionName, String documentName){
        initializeWriting();
        ApiFuture<WriteResult> writeResult = db.collection(collectionName)
                .document(documentName).delete();
        DataHolder.writing = false;
    }

    //deleteElement() works
    void deleteElement(String collectionName, String documentName,String key){
        initializeWriting();
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, Object> updates = new HashMap<>();
        //updates.put(key, "-1");
        updates.put(key, FieldValue.delete());
        docRef.update(updates);
        //docRef.set(updates);
        DataHolder.writing = false;

    }

    void  deleteElement2(String collectionName, String documentName,String key){
        initializeWriting();
        //DocumentReference docRef = db.collection(collectionName).document(documentName);
        HashMap<String,String> data = new HashMap<>();
        DataHolder.writing = false;
        String old = readDocument(collectionName,documentName);
        deleteDocument(collectionName,documentName);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        data = seedToMap(old);
        System.out.println("Old data:" + old);
        data.put(key, "-1");
        String newvalue = mapToSeed(data);
        System.out.println("New data:" + newvalue);
        createDocumentFromMap(collectionName, documentName, newvalue);
        //docRef.set(data);
        //docRef.update(data);

    }

    void  deleteElement3(String collectionName, String documentName,String key){
        initializeWriting();
        String old = readDocument(collectionName,documentName);
        DataHolder.writing = false;
    }



    //======================= TEST QUERIES =================================
    String doFirebaseStuff(){
        initializeWriting();
        System.out.println("");
        System.out.println("doFirebaseStuff() runs!");
        System.out.println("=======================");


//        //1.Intialization. theoretically the handshake
//        //Use a service account
//        InputStream serviceAccount = null;
//        try {
//            serviceAccount = new FileInputStream("theKey/key.json");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        GoogleCredentials credentials = null;
//        try {
//            credentials = GoogleCredentials.fromStream(serviceAccount);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(credentials)
//                .build();
//        FirebaseApp.initializeApp(options);
//
//        Firestore db = FirestoreClient.getFirestore();


        //2.Crud and a confirmed handshake
        DocumentReference docRef = db.collection("users").document("alovelace");
// Add document data  with id "alovelace" using a hashmap
        Map<String, Object> data = new HashMap<>();
        data.put("first", "Ada");
        data.put("last", "Lovelace");
        data.put("born", 1815);
//asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
// ...
// result.get() blocks on response
        try {
            System.out.println("Update time : " + result.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //3.Add another user with a middle name this time
        DocumentReference docRef2 = db.collection("users").document("aturing");
        // Add document data with an additional field ("middle")
        Map<String, Object> data2 = new HashMap<>();
        data2.put("first", "Alan");
        data2.put("middle", "Mathison");
        data2.put("last", "Turing");
        data2.put("born", 1912);

        ApiFuture<WriteResult> result2 = docRef2.set(data2);
        try {
            System.out.println("Update time : " + result2.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //4. Read the data. Iterate the users of the "users" collection
        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection("users").get();
// ...
// query.get() blocks on response
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = query.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        String word = "";
        for (QueryDocumentSnapshot document : documents) {
            word = word + "User: " + document.getId();
            word = word + "First: " + document.getString("first");
            if (document.contains("middle")) {
                word = word + "Middle: " + document.getString("middle");
            } else {
                word = word + "";
            }
            word = word + "Last: " + document.getString("last");
            word = word + "Born: " + document.getLong("born");
        }
        DataHolder.writing = false;
        return word;
    } //end of doFirebaseStuff()


    String doFirebaseStuff2(){
        initializeWriting();
        System.out.println("");
        System.out.println("doFirebaseStuff2() runs!");
        System.out.println("========================");


//        //1.Intialization. theoretically the handshake
//        //Use a service account
//        InputStream serviceAccount = null;
//        try {
//            serviceAccount = new FileInputStream("theKey/key.json");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        GoogleCredentials credentials = null;
//        try {
//            credentials = GoogleCredentials.fromStream(serviceAccount);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setCredentials(credentials)
//                .build();
//        //FirebaseApp.initializeApp(options);
//
//        Firestore db = FirestoreClient.getFirestore();


        //2.Crud and a confirmed handshake
        DocumentReference docRef = db.collection("users").document("alovelace");
// Add document data  with id "alovelace" using a hashmap
        Map<String, Object> data = new HashMap<>();
        data.put("first", "Ada");
        data.put("last", "Lovelace");
        data.put("born", 1815);
//asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
// ...
// result.get() blocks on response
        try {
            System.out.println("Update time : " + result.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //3.Add another user with a middle name this time
        DocumentReference docRef2 = db.collection("users").document("aturing");
        // Add document data with an additional field ("middle")
        Map<String, Object> data2 = new HashMap<>();
        data2.put("first", "Alan");
        data2.put("middle", "Mathison");
        data2.put("last", "Turing");
        data2.put("born", 1912);

        ApiFuture<WriteResult> result2 = docRef2.set(data2);
        try {
            System.out.println("Update time : " + result2.get().getUpdateTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //4. Read the data. Iterate the users of the "users" collection
        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection("users").get();
// ...
// query.get() blocks on response
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = query.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String word = "";
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            word = word + "User: " + document.getId() + "[@]";
            word = word + "First: " + document.getString("first") + "[@]";
            if (document.contains("middle")) {
                word = word + "Middle: " + document.getString("middle") + "[@]";
            }
            word = word + "Last: " + document.getString("last") + "[@]";
            word = word + "Born: " + document.getLong("born") + "[@]";
        }

        DataHolder.writing = false;
        return word;
    } //end of doFirebaseStuff()


    String syntaxToSeed(String json){
        json = json.trim();
        String json2 = json.replace("{", "@@");
        String json3 = json2.replace("}", "");
        json2 = json3.replace(",", "@@");
        json3 = json2.replace("=", "mpt@mpt");
        return json3;
    }

    HashMap<String, String> seedToMap(String seed){
        HashMap<String, String> map =  new HashMap<>();
        String[] temp2 = new String[2];
        String[] temp = seed.split("@new@entry@");
        String key = "No key";
        String value = "No value";
        for (int x=0; x < temp.length; x++){
            String miniseed = temp[x];
            if(!miniseed.equalsIgnoreCase("")){
                temp2 = miniseed.split("@key@value@");
                key = temp2[0].trim();
                if(temp2.length > 1){
                    value = temp2[1].trim();
                }
                map.put(key, value);
            }
        }
        return map;
    }

    HashMap<String, Object> seedToMap2(String seed){
        HashMap<String, Object> map =  new HashMap<>();
        String[] temp2 = new String[2];
        String[] temp = seed.split("@new@entry@");
        String key = "No key";
        String value = "No value";
        for (int x=0; x < temp.length; x++){
            String miniseed = temp[x];
            if(!miniseed.equalsIgnoreCase("")){
                temp2 = miniseed.split("@key@value@");
                key = temp2[0].trim();
                if(temp2.length > 1){
                    value = temp2[1].trim();
                }
                map.put(key, value);
            }
        }
        return map;
    }


    void initializeWriting(){
        while(DataHolder.writing){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DataHolder.writing = true;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void initializeReading(){
        while(DataHolder.reading){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        DataHolder.reading = true;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    String mapToSeed(HashMap<String, String> map){
        String word = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            word = word + "@new@entry@" + entry.getKey() + "@key@value@" + entry.getValue();
        }
        //System.out.println("the pressed is " + word);
        return word;
    }

}
