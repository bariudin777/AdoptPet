package com.example.getpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.getpet.Model.Animals;
import com.example.getpet.ViewHolder.AnimalViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchAnimalActivity extends AppCompatActivity {

    private Button search_btn;
    private EditText input_text;
    private RecyclerView recycler_view;
    private String search_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_animal);
        search_btn = (Button) findViewById(R.id.search_animal_btn);
        input_text = findViewById(R.id.search_animal_name);
        recycler_view = findViewById(R.id.search_list);
        recycler_view.setLayoutManager(new LinearLayoutManager(SearchAnimalActivity.this));


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_input = input_text.getText().toString();
                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Animals");
        FirebaseRecyclerOptions<Animals> options = new FirebaseRecyclerOptions.Builder<Animals>()
                .setQuery(ref.orderByChild("animal_name").startAt(search_input), Animals.class).build();
        FirebaseRecyclerAdapter<Animals, AnimalViewHolder> adapter=
                new FirebaseRecyclerAdapter<Animals, AnimalViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AnimalViewHolder animalViewHolder, int i, @NonNull final Animals animals) {

                        animalViewHolder.txt_animal_name.setText(animals.getAnimal_name());
                        animalViewHolder.txt_animal_info.setText(animals.getDescription());
                        Picasso.get().load(animals.getImage()).into(animalViewHolder.img_view);
                        //for the addition of animal to our cart
                        animalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(SearchAnimalActivity.this, AnimalDetailsActivity.class);
                                intent.putExtra("pid", animals.getAnimal_id());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout,parent,false);
                        AnimalViewHolder holder = new AnimalViewHolder(view);

                        return holder;
                    }
                };
            recycler_view.setAdapter(adapter);
            adapter.startListening();

    }
}
