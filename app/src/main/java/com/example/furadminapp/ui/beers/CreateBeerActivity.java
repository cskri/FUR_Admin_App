package com.example.furadminapp.ui.beers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.furadminapp.R;
import com.example.furadminapp.ui.home.CreateNewsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class CreateBeerActivity extends AppCompatActivity {

    Button scanButton;
    Button saveBeer;
    Button backButton;
    private EditText beerName;
    private EditText beerId;
    private EditText beerType;
    private EditText beerDescription;
    private EditText beerImage;
    private EditText beerSize;
    private EditText beerProof;
    private EditText beerCarbon;
    private FirebaseFirestore db;
    private String TAG = "CREATE BEER";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_beer);
        beerName = (EditText) findViewById(R.id.beerNameInput);
        beerId = (EditText) findViewById(R.id.beerBarcodeInput);
        beerType = (EditText) findViewById(R.id.beerTypeInput);
        beerDescription = (EditText) findViewById(R.id.beerDescriptionInput);
        beerImage = (EditText) findViewById(R.id.beerImageLink);
        beerSize = (EditText) findViewById(R.id.beerSizeInput);
        beerProof = (EditText) findViewById(R.id.beerProofInput);
        beerCarbon = (EditText) findViewById(R.id.beerCarbonInput);
        saveBeer = findViewById(R.id.beerSaveButton);
        backButton = findViewById(R.id.beerBackButton);
        db = FirebaseFirestore.getInstance();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveBeer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = beerName.getText().toString();
                String id = beerId.getText().toString();
                String type = beerType.getText().toString();
                String description = beerDescription.getText().toString();
                String image = beerImage.getText().toString();
                int size = Integer.parseInt(beerSize.getText().toString());
                double proof = Double.parseDouble(beerProof.getText().toString());
                double carbon = Double.parseDouble(beerCarbon.getText().toString());

                if(!name.isEmpty() && !description.isEmpty() && !id.isEmpty() && !type.isEmpty() && !image.isEmpty()){
                    Map<String, Object> beer = new HashMap<>();
                    beer.put("id", id);
                    beer.put("name", name);
                    beer.put("description", description);
                    beer.put("type", type);
                    beer.put("imageLink", image);
                    beer.put("size", size);
                    beer.put("proof", proof);
                    beer.put("carbonEmission", carbon);
                    db.collection("beers").document(id)
                            .set(beer)
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
                                    Toast.makeText(CreateBeerActivity.this, "ERROR! An error occurred, beer could NOT be created", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateBeerActivity.this);
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


        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(CreateBeerActivity.this);
                intentIntegrator.setPrompt("For flash use volume up key");
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.setCaptureActivity(Capture.class);
                intentIntegrator.initiateScan();
            }
        });
    }
    public void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("BEER ADDED");
        builder.setMessage("Your beer has successfully been added");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (intentResult.getContents() != null){
            beerId.setText(intentResult.getContents());
        }
        else{
            Toast.makeText(this, "you did not scan anything",Toast.LENGTH_SHORT).show();
        }
    }
}