package com.example.furadminapp.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText newsTitleInput;
    private EditText newsDescriptionInput;
    private EditText newsContentInput;
    private EditText newsImageLink;
    private FirebaseFirestore db;
    private Button createNewsButton;
    private Button backButton;
    private String TAG = "CREATE NEWS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_news);
        findViewById(R.id.beerBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        db = FirebaseFirestore.getInstance();
        backButton = findViewById(R.id.beerBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        createNewsButton = findViewById(R.id.beerSaveButton);
        createNewsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                newsTitleInput = (EditText) findViewById(R.id.newsTitleInput);
                newsDescriptionInput = (EditText) findViewById(R.id.newsDescriptionInput);
                newsContentInput = (EditText) findViewById(R.id.newsContentInput);
                newsImageLink = (EditText) findViewById(R.id.newsImageLink);
                String uniqueId = UUID.randomUUID().toString();
                String title = newsTitleInput.getText().toString();
                String description = newsDescriptionInput.getText().toString();
                String content = newsContentInput.getText().toString();
                String image = newsImageLink.getText().toString();

                if(!title.isEmpty() && !description.isEmpty() && !content.isEmpty() && !image.isEmpty()){
                    Map<String, Object> news = new HashMap<>();
                    news.put("id", uniqueId);
                    news.put("title", title);
                    news.put("description", description);
                    news.put("content", content);
                    news.put("imageLink", image);
                    news.put("date", FieldValue.serverTimestamp());
                    db.collection("news").document(uniqueId)
                            .set(news)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    showAlert();
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
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewsActivity.this);
                    builder.setTitle("Fields not filled correctly");
                    builder.setMessage("Your fields have not been filled correctly. Please revise");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }

            }
        });
    }
    public void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("NEWS POST ADDED");
        builder.setMessage("Your news post has successfully been added");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
}