package hkr.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {
    private Firestore db;
    private Storage storage;

    public DatabaseHelper(){
        FileInputStream serviceAccount;

        try {
            serviceAccount = new FileInputStream("key/digital-receipt-a6570-firebase-adminsdk-eh78q-46b5c2ed8c.json");
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount);

            FirebaseOptions databaseOptions = new FirebaseOptions.Builder()
                    .setCredentials(googleCredentials)
                    .setDatabaseUrl("https://digital-receipt-a6570.firebaseio.com")
                    .build();

            StorageOptions storageOptions = StorageOptions.newBuilder()
                    .setProjectId("digital-receipt-a6570")
                    .setCredentials(googleCredentials)
                    .build();

            storage = storageOptions.getService();

            FirebaseApp.initializeApp(databaseOptions);


        } catch (IOException e) {
            e.printStackTrace();
        }

        db = FirestoreClient.getFirestore();
    }

    public void uploadReceipt(String userid, byte[] fileContent){

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String receiptId = dateFormat.format(date);

        BlobId blobId = BlobId.of("digital-receipt-a6570.appspot.com", userid + "/" + receiptId + ".pdf");
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/pdf").build();
        storage.create(blobInfo, fileContent);

        // Create a Map to store the data we want to set
        Map<String, Object> docData = new HashMap<>();
        docData.put("url", "gs://digital-receipt-a6570.appspot.com/" + userid + "/" + receiptId + ".pdf");
        db.collection("users").document(userid).collection("receipts").add(docData);

    }

}
