package com.example.furadminapp.ui.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.furadminapp.R;
import com.example.furadminapp.databinding.FragmentEventsBinding;
import com.example.furadminapp.ui.beers.BeerFragment;
import com.example.furadminapp.ui.beers.BeerModel;
import com.example.furadminapp.ui.beers.CreateBeerActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.io.InputStream;

public class EventFragment extends Fragment {

    private EventViewModel eventViewModel;
    private FragmentEventsBinding binding;
    private RecyclerView eventsFirestoreList;
    private FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventViewModel =
                new ViewModelProvider(this).get(EventViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        eventsFirestoreList = root.findViewById(R.id.rv_events);
        root.findViewById(R.id.createEventButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CreateEventActivity.class);
                getContext().startActivity(i);
            }
        });

        Query query = db.collection("events").orderBy("date");
        //RecyclerOptions
        FirestoreRecyclerOptions<EventModel> options = new FirestoreRecyclerOptions.Builder<EventModel>().setQuery(query, EventModel.class).build();
        adapter = new FirestoreRecyclerAdapter<EventModel, EventViewHolder>(options) {
            @NonNull
            @Override
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single_event, parent, false);
                return new EventViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull EventModel model) {
                holder.title_event.setText(model.getName());
                holder.description_event.setText(model.getDescription());
                holder.date_event.setText("" + model.getDate());
                holder.price_event.setText("" + model.getPrice());
                new EventFragment.DownloadImageTask(holder.image_event)
                        .execute(model.getImageLink());
                holder.button_event.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), EventDetails.class);
                        i.putExtra("ID",model.getId());
                        getContext().startActivity(i);
                    }
                });
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.d("Firestore Error", e.getMessage());
            }


        };
        eventsFirestoreList.setHasFixedSize(true);
        eventsFirestoreList.setLayoutManager(new LinearLayoutManager(root.getContext()));
        eventsFirestoreList.setAdapter(adapter);


        return root;
    }
    private class EventViewHolder extends RecyclerView.ViewHolder{

        private TextView title_event;
        private TextView description_event;
        private TextView date_event;
        private TextView price_event;
        private ImageView image_event;
        private Button button_event;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title_event = itemView.findViewById(R.id.title_event);
            description_event = itemView.findViewById(R.id.description_event);
            date_event = itemView.findViewById(R.id.date_event);
            price_event = itemView.findViewById(R.id.price_event);
            image_event = itemView.findViewById(R.id.image_event);
            button_event = itemView.findViewById(R.id.button_event);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}