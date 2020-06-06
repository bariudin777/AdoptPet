package com.example.getpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.getpet.Maps.MapLocationActivity;
import com.example.getpet.Model.AnimalList;
import com.example.getpet.Prevalent.Prevalent;
import com.example.getpet.ViewHolder.AnimalListHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdoptionListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button next_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_list);

        recyclerView = findViewById(R.id.adoption_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        next_btn = (Button) findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdoptionListActivity.this, SendMailActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference adoption_list_ref = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<AnimalList> list = new FirebaseRecyclerOptions.Builder<AnimalList>()
                .setQuery(adoption_list_ref.child("User View").child(Prevalent.online_user.getPhone()).child("Animals"), AnimalList.class).build();

        FirebaseRecyclerAdapter<AnimalList, AnimalListHolder> adapter =
                new FirebaseRecyclerAdapter<AnimalList, AnimalListHolder>(list) {
                    @Override
                    protected void onBindViewHolder(@NonNull AnimalListHolder animalListHolder, int i, @NonNull final AnimalList animalList) {

                        animalListHolder.txt_animal_name.setText(animalList.getA_name().toString());

                        animalListHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // here i will create a dialog box for removing animal from the adoption list

                                CharSequence options[] = new CharSequence[]{
                                        "Remove Animal From List",
                                        "Locate Shelter"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdoptionListActivity.this);
                                builder.setTitle("Animal List Options: ");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            adoption_list_ref.child("User View").child(Prevalent.online_user.getPhone())
                                                    .child("Animals").child(animalList.getPid()).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(AdoptionListActivity.this, "Animal is removed from list", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(AdoptionListActivity.this, HomeActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });

                                        }
                                        if (which == 1) {
                                            Intent intent = new Intent(AdoptionListActivity.this, MapLocationActivity.class);
                                            String loc = "32.081484, 34.801490";
                                            //here i need to create location class that retrieve shelter location
                                            intent.putExtra("location", loc);
                                            startActivity(intent);


                                        }

                                    }
                                });
                                builder.show();

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AnimalListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.animal_list_layout, parent, false);
                        AnimalListHolder holder = new AnimalListHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
