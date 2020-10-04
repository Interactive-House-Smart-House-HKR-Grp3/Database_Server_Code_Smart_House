import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;

import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String args[]) throws IOException {
        System.out.println("Hello World!");
        FileInputStream serviceAccount =
                new FileInputStream("thekey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://fireserver-cb3fa.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        Firestore db = FirestoreClient.getFirestore();

        String word = String.valueOf(db.collection("userInfos").listDocuments());

        System.out.println(word);


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
