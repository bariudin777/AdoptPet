package com.example.getpet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView dogs, cats, bunnys, parrots, hamsters, snakes;

    ArrayList<ImageView> arr = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);
        dogs = (ImageView) findViewById(R.id.dogs);
        cats = (ImageView) findViewById(R.id.cats);
        bunnys = (ImageView) findViewById(R.id.bunny);
        parrots = (ImageView) findViewById(R.id.parrots);
        hamsters = (ImageView) findViewById(R.id.hamster);
        snakes = (ImageView) findViewById(R.id.snakes);

        arr.add(dogs);
        arr.add(cats);
        arr.add(bunnys);
        arr.add(parrots);
        arr.add(hamsters);
        arr.add(snakes);

        for (final ImageView img : arr) {


            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                    intent.putExtra("category", img.toString());
                    startActivity(intent);
                }
            });

        }

    }
}
