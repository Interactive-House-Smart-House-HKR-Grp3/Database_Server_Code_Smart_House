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
            serviceAccount = new FileInputStream("theKey/key.json");
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

    //CreateCollection works.
    void createCollection(String collectionName){
        DocumentReference docRef = db.collection(collectionName).document("dummyDocument");
        Map<String, Object> data = new HashMap<>();
        data.put("dummy key", "dummy value");
        docRef.set(data);
        System.out.println("A collection with the name " + collectionName + " is created!");
    }

    //createDocument() works.
    void createDocument(String collectionName, String documentName){
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, Object> data = new HashMap<>();
        data.put("dummy key", "dummy value");
        docRef.set(data);
        System.out.println("A document with the name " + documentName + " is created inside the "
                + collectionName);
    }

    //createElement() works.
    void createElement(String collectionName, String documentName, String keyName, String value){
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, Object> data = new HashMap<>();
        data.put(keyName, value);
        //docRef.set(data);
        docRef.update(data);
        System.out.println("The element " + keyName + ":" + value + "is created" +
                "inside the document " + documentName +
                "which is inside the collection " + collectionName);
    }


    //===================== READ QUERIES ==================================

    String readCollection(String collectionName){
        String word = "";
        ApiFuture<QuerySnapshot> query = db.collection(collectionName).get();

        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = query.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            word = word + "name: " + document.getId() + "[@]";
        }
        return word;
    }


    // readDocument() works but it can be better.
    String readDocument(String collectionName, String documentName){
        String word = "";
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = null;
        try {
            document = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (document.exists()) {
            System.out.println("Document data: " + document.getData());
            word =  "Document data: " + document.getData();

        } else {
            System.out.println("No such document!");
            word = "No such document!";
        }

        return word;
    }


    //readElement() works.
    String readElement(String collectionName, String documentName, String keyName){
        String word = "";
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = null;
        try {
            document = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (document.exists()) {
            System.out.println("Document data: " + document.getData());
            //word =  "Document data: " + document.getData();
            word = document.getString(keyName);

        } else {
            System.out.println("No such document!");
            word = "No such document!";
        }

        return word;
    }

    //===================== UPDATE QUERIES ==================================


    //No logical use for it now. Collections are immutable
    void updateCollection(String collectionName){
        System.out.println("A collection with the name " + collectionName + " is updated!");
    }

    //No logical use for it now. Documents are immutable.
    void updateDocument(String collectionName, String documentName){
        System.out.println("A document with the name " + documentName + " is updated inside the "
                + collectionName);
    }

    //createElement() works.
    void updateElement(String collectionName, String documentName, String keyName, String value){
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, Object> data = new HashMap<>();
        data.put(keyName, value);
        //docRef.set(data);
        docRef.update(data);
        System.out.println("The element " + keyName + ":" + value + "is updated" +
                "inside the document " + documentName +
                "which is inside the collection " + collectionName);
    }

    //======================= DELETE QUERIES =================================

    /** Delete a collection in batches to avoid out-of-memory errors.
     * Batch size may be tuned based on document size (atmost 1MB) and application requirements.
     */
    //deleteCollection() works
    void deleteCollection(String collection2, int batchSize) {
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
    }

    //deleteDocument() works.
    void deleteDocument(String collectionName, String documentName){
        ApiFuture<WriteResult> writeResult = db.collection(collectionName)
                .document(documentName).delete();
    }

    //deleteElement() works
    void deleteElement(String collectionName, String documentName,String key){
        DocumentReference docRef = db.collection(collectionName).document(documentName);
        Map<String, Object> updates = new HashMap<>();
        updates.put(key, FieldValue.delete());
        docRef.update(updates);

    }



    //======================= TEST QUERIES =================================
    String doFirebaseStuff(){
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
        return word;
    } //end of doFirebaseStuff()


    String doFirebaseStuff2(){
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

        return word;
    } //end of doFirebaseStuff()
}
