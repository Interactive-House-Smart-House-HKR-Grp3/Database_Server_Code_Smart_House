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
