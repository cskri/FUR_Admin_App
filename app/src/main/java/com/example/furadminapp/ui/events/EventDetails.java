package com.example.furadminapp.ui.events;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.furadminapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import java.util.List;

public class EventDetails extends AppCompatActivity {
    TextView mTitle;
    Button backButton;
    Button deleteButton;
    String id;
    String TAG = "EVENT DETAILS";
    private FirebaseFirestore db;
    RecyclerView eventsList;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        backButton = (Button) findViewById(R.id.backButtonEvent);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        deleteButton = (Button) findViewById(R.id.deleteButtonEvent);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetails.this);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Confirm that you want to delete the event");
                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DocumentReference docRef = db.collection("events").document(id);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        List<String> participants = document.toObject(ParticipantDocument.class).participants;

                                        for(int i = 0; participants.size()<i;i++){
                                            if(participants.get(i) != null){
                                                String uId = participants.get(i);
                                                DocumentReference docRef = db.collection("eventUser").document(uId);
                                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                List<String> events = document.toObject(EventDocument.class).eventId;
                                                                events.remove(uId);
                                                                if(events.size() == 0){
                                                                    document.getReference().delete();
                                                                }
                                                                else{
                                                                    document.getReference().update("eventId", events);
                                                                }

                                                            } else {
                                                                Log.d(TAG, "get failed with ", task.getException());
                                                            }
                                                        }
                                                        else {
                                                            Log.d(TAG, "get failed with ", task.getException());
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        document.getReference().delete();
                                        finish();
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                                else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        mTitle = findViewById(R.id.eventTitle);
        Intent i = getIntent();
        id = i.getStringExtra("ID");

        db = FirebaseFirestore.getInstance();
        eventsList = findViewById(R.id.rv_participants);
        DocumentReference docRef = db.collection("events").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        mTitle.setText(document.get("name").toString());
                        findParticipants();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void findParticipants(){
        //Query
        Query query = db.collection("eventUsers").whereArrayContains("eventId", id);

        //RecyclerOptions
        FirestoreRecyclerOptions<ParticipantModel> options = new FirestoreRecyclerOptions.Builder<ParticipantModel>().setQuery(query, ParticipantModel.class).build();

        adapter = new FirestoreRecyclerAdapter<ParticipantModel, ParticipantViewHolder>(options) {
            @NonNull
            @Override
            public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single_participant, parent, false);
                return new ParticipantViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position, @NonNull ParticipantModel model) {
                holder.participantId.setText(model.getUserId());
                holder.button_participant.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EventDetails.this);
                        builder.setTitle("Confirm Deletion");
                        builder.setMessage("Confirm that you want to unsign the participant from event");
                        builder.setPositiveButton("UNSIGN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeEventFromParticipant(id,model.getUserId());
                                finish();
                            }
                        });
                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.d("Firestore Error", e.getMessage());
            }


        };
        eventsList.setHasFixedSize(true);
        eventsList.setLayoutManager(new LinearLayoutManager(this));
        eventsList.setAdapter(adapter);
        adapter.startListening();
    }
    private void removeEventFromParticipant(String eId, String uId){
        DocumentReference docRef = db.collection("eventUsers").document(uId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                Log.d("Events OUTPUT", "SUCCESS");
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Events OUTPUT", "DOCUMENT EXISTS");
                        List<String> events = document.toObject(EventDocument.class).eventId;
                        events.remove(eId);
                        if(events.size() == 0){
                            document.getReference().delete();
                        }
                        else{
                            document.getReference().update("eventId", events);
                        }
                        removeParticipantFromEvent(eId,uId);
                    } else {
                        Log.d("Events OUTPUT", "DOCUMENT NOT EXISTS");
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    private void removeParticipantFromEvent(String eId, String uId){
        DocumentReference docRef = db.collection("events").document(eId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> participants = document.toObject(ParticipantDocument.class).participants;
                        participants.remove(uId);
                        document.getReference().update("participants", participants);

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
                else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    private class ParticipantViewHolder extends RecyclerView.ViewHolder {

        private TextView participantId;
        private Button button_participant;


        public ParticipantViewHolder(@NonNull View itemView) {
            super(itemView);
            participantId = itemView.findViewById(R.id.participantId);
            button_participant = itemView.findViewById(R.id.button_participant);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if(!(adapter == null)){
            adapter.stopListening();
        }

    }

}