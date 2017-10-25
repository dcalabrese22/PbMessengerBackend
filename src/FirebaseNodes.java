import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseNodes {

    public static void createNodes() throws Exception {
        FileInputStream serviceAccount = new FileInputStream("service-account.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://pbmessages-62c8d.firebaseio.com/")
                .build();
        FirebaseApp.initializeApp(options);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.setValue("conversations");
        reference.child("conversations").child("g5561yqarrWCVLMN92zrYZzoVyk1").setValue("test");
        reference.child("conversations").child("messages").setValue("test");

        Thread.sleep(5000);
        System.exit(0);

    }

    public static void main(String[] args) {
        try {
            createNodes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
