package com.example.getpet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getpet.Model.Animals;
import com.example.getpet.Prevalent.Prevalent;
import com.example.getpet.ViewHolder.AnimalViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private DatabaseReference animal_ref;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        animal_ref = FirebaseDatabase.getInstance().getReference().child("Animals");

        Paper.init(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_logout, R.id.nav_cart,
                R.id.nav_categories, R.id.nav_settings, R.id.nav_orders)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View header_view = navigationView.getHeaderView(0);
        TextView user_name_text_view = header_view.findViewById(R.id.user_profile_name);
        CircleImageView profile_img = header_view.findViewById(R.id.user_profile_image);

        user_name_text_view.setText(Prevalent.online_user.getName());
        Picasso.get().load(Prevalent.online_user.getImage()).placeholder(R.drawable.profile).into(profile_img);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.nav_cart) {
                    Toast.makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                }
                if (destination.getId() == R.id.nav_orders) {
                    Toast.makeText(HomeActivity.this, "Orders", Toast.LENGTH_SHORT).show();
                }
                if (destination.getId() == R.id.nav_categories) {
                    Toast.makeText(HomeActivity.this, "Categories", Toast.LENGTH_SHORT).show();
                }
                if (destination.getId() == R.id.nav_settings) {
                    Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);


                }
                if (destination.getId() == R.id.nav_logout) {
                    Paper.book().destroy();
                    Toast.makeText(HomeActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Animals> options = new FirebaseRecyclerOptions.Builder<Animals>().setQuery(animal_ref, Animals.class).build();

        FirebaseRecyclerAdapter<Animals, AnimalViewHolder> adapter =
                new FirebaseRecyclerAdapter<Animals, AnimalViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AnimalViewHolder animalViewHolder, int i, @NonNull Animals animals) {
                        animalViewHolder.txt_animal_name.setText(animals.getAnimal_name());
                        animalViewHolder.txt_animal_info.setText(animals.getDescription());
                        Picasso.get().load(animals.getImage()).into(animalViewHolder.img_view);
                    }

                    @NonNull
                    @Override
                    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout, parent, false);
                        AnimalViewHolder hol = new AnimalViewHolder(view);
                        return hol;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
