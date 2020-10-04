import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String args[]) throws IOException {
        System.out.println("Hello World!");
        FileInputStream serviceAccount =
                new FileInputStream("theKey/key.json");

        //new FirebaseOptions.Builder()
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://fireserver-cb3fa.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        Firestore db = FirestoreClient.getFirestore();
        String word = db.collection("userInfos").document("testUser").get().toString();

        //String word = String.valueOf(db.collection("userInfos").listDocuments());

        System.out.println(word);



        DocumentReference docRef = db.collection("userInfos").document("testUser");
        String word2 = docRef.get().toString();
        System.out.println(word2);


        Map<String, Object> data = new HashMap<>();
        data.put("first", "Ada");
        ApiFuture<WriteResult> result = docRef.set(data);
        String word3 = null;
        try {
            word3 = result.get().getUpdateTime().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(word3);



        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection("userInfos").get();
        QuerySnapshot querySnapshot = null;
        try {
            querySnapshot = query.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        word3 = documents.get(0).getString("house");
        System.out.println(word3);
        for (QueryDocumentSnapshot document : documents) {
            System.out.println("User: " + document.getId());
            System.out.println("First: " + document.getString("first"));
        }


//        DocumentReference docRef = db.collection("users").document("alovelace");
//// Add document data  with id "alovelace" using a hashmap
//        Map<String, Object> data = new HashMap<>();
//        data.put("first", "Ada");
//        data.put("last", "Lovelace");
//        data.put("born", 1815);
////asynchronously write data
//        ApiFuture<WriteResult> result = docRef.set(data);
//// ...
//// result.get() blocks on response
//        try {
//            System.out.println("Update time : " + result.get().getUpdateTime());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

    }




}
