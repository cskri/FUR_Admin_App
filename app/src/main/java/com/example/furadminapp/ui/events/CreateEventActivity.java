package com.example.furadminapp.ui.events;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.furadminapp.R;
import com.example.furadminapp.ui.beers.Capture;
import com.example.furadminapp.ui.beers.CreateBeerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;
import com.google.zxing.integration.android.IntentIntegrator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {

    private EditText title;
    private EditText description;
    private EditText price;
    private EditText image;
    private CalendarView date;
    private EditText time;
    private ArrayList<String> participants = new ArrayList<>();
    Button back;
    Button save;
    private FirebaseFirestore db;
    private String TAG = "CREATE EVENT";
    private String sd;
    Date d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        title = (EditText) findViewById(R.id.eventTitleInput);
        description = (EditText) findViewById(R.id.eventDescriptionInput);
        price = (EditText) findViewById(R.id.eventPriceInput);
        image = (EditText) findViewById(R.id.eventImageLink);
        date = findViewById(R.id.eventCalendarView);
        db = FirebaseFirestore.getInstance();
        String uniqueId = UUID.randomUUID().toString();
        back = findViewById(R.id.eventBackButton);
        save = findViewById(R.id.eventSaveButton);
        time = findViewById(R.id.timeInput);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                int realDay = month+1;
                sd = realDay + "/" + dayOfMonth + "/" + year;

            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = title.getText().toString();
                String eventDescription = description.getText().toString();
                int eventPrice = Integer.parseInt(price.getText().toString());
                String eventImage = image.getText().toString();
                String tt = time.getText().toString();
                d = new Date(sd + " " + tt);
                //long eventDate = date.getDate();
                //Date storeDate = new Date(eventDate);

                if(!eventName.isEmpty() && !eventDescription.isEmpty()  && !eventImage.isEmpty()){
                    Map<String, Object> event = new HashMap<>();
                    event.put("id", uniqueId);
                    event.put("name", eventName);
                    event.put("description", eventDescription);
                    event.put("price", eventPrice);
                    event.put("imageLink", eventImage);
                    event.put("date", d);
                    event.put("participants", participants);

                    db.collection("events").document(uniqueId)
                            .set(event)
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
                                    Toast.makeText(CreateEventActivity.this, "ERROR! An error occurred, beer could NOT be created", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);
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
        builder.setTitle("EVENT POST ADDED");
        builder.setMessage("Your event post has successfully been added");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
}