package com.example.furadminapp.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.furadminapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateNewsActivity extends AppCompatActivity {
    private TextView newsTitleInput;
    private TextView newsDescriptionInput;
    private TextView newsContentInput;
    private TextView newsImageLink;
    private FirebaseFirestore db;
    private String TAG = "CREATE NEWS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_news);
        findViewById(R.id.newsBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newsTitleInput = findViewById(R.id.newsTitleInput);
        newsDescriptionInput = findViewById(R.id.newsDescriptionInput);
        newsContentInput = findViewById(R.id.newsContentInput);
        newsImageLink = findViewById(R.id.newsImageLink);
        db = FirebaseFirestore.getInstance();
        findViewById(R.id.createNewsButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String uniqueId = UUID.randomUUID().toString();
                Map<String, Object> news = new HashMap<>();
                news.put("id", uniqueId);
                news.put("title", newsTitleInput.getText());
                news.put("description", newsDescriptionInput.getText());
                news.put("content", newsContentInput.getText());
                news.put("imageLink", newsImageLink.getText());
                news.put("date", FieldValue.serverTimestamp());
                db.collection("news").document(uniqueId)
                        .set(news)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                Toast.makeText(CreateNewsActivity.this, "You successfully created a news post", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                    Toast.makeText(CreateNewsActivity.this, "ERROR! An error occurred, post could NOT be created", Toast.LENGTH_SHORT).show();
                                }
                        });
            }
        });
    }
}