package com.example.getpet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String category_name, description, animal_name;
    private String save_curr_date, save_curr_time;
    private Button add_new_animal_btn;
    private EditText input_name, input_description;
    private ImageView animal_img;
    private static final int gallery_pick = 1;
    private Uri img_uri;
    private String p_key;
    private StorageReference storage_img_ref;
    private String download_img_url;
    private DatabaseReference animal_ref;
    private ProgressDialog loading_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        //Toast.makeText(this, "Welcome Admin...", Toast.LENGTH_SHORT).show();
        category_name = getIntent().getExtras().get("category").toString();
        storage_img_ref = FirebaseStorage.getInstance().getReference().child("Animal Images");
        animal_ref = FirebaseDatabase.getInstance().getReference().child("Animals");


        add_new_animal_btn = (Button) findViewById(R.id.add_new_animal);
        animal_img = (ImageView) findViewById(R.id.select_animal_img);
        input_name = (EditText) findViewById(R.id.animal_name);
        input_description = (EditText) findViewById(R.id.description);
        loading_bar = new ProgressDialog(this);

        animal_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        add_new_animal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateAnimalData();

            }
        });

    }

    private void OpenGallery() {

        Intent gallery_intent = new Intent();
        gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
        gallery_intent.setType("image/*");
        startActivityForResult(gallery_intent, gallery_pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallery_pick && resultCode== RESULT_OK && data != null) {

            img_uri = data.getData();
            animal_img.setImageURI(img_uri);


        }

    }

    private void validateAnimalData() {

        description = input_description.getText().toString();
        animal_name = input_name.getText().toString();


        if (img_uri == null) {
            Toast.makeText(this, "Animal image is mandatory..", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please Describe the animal..", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(animal_name)) {
            Toast.makeText(this, "Please Describe the animal name..", Toast.LENGTH_SHORT).show();
        } else {

            storeAnimalInfo();

        }

    }

    private void storeAnimalInfo() {

        loading_bar.setTitle("Adding new Animal");
        loading_bar.setMessage("Please wait, while we are adding the animal");
        loading_bar.setCanceledOnTouchOutside(false);
        loading_bar.show();




        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat curr_date = new SimpleDateFormat("MMM,dd,yyyy");
        save_curr_date = curr_date.format(calendar.getTime());
        SimpleDateFormat curr_time = new SimpleDateFormat("HH:mm:ss a");
        save_curr_time = curr_time.format(calendar.getTime());
        //create random key for each product
        //instead using fire-base unique key, i will use the date and the time
        //TODO- change this
        p_key = save_curr_date + save_curr_time;

        final StorageReference path = storage_img_ref.child(img_uri.getLastPathSegment() + p_key + ".jpg");

        final UploadTask uploadTask = path.putFile(img_uri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error : " + msg, Toast.LENGTH_SHORT).show();
                loading_bar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Animal image upload successes!", Toast.LENGTH_SHORT).show();
                Task<Uri> url_task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();

                        }

                        download_img_url = path.getDownloadUrl().toString();
                        return path.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminAddNewProductActivity.this, "Getting animal image url successfully", Toast.LENGTH_SHORT).show();
                            download_img_url = task.getResult().toString();
                            saveAnimalInfoToDatabese();


                        }

                    }
                });
            }
        });

    }

    private void saveAnimalInfoToDatabese() {


        HashMap<String, Object> animal_map = new HashMap<>();
        animal_map.put("animal_id", p_key);
        animal_map.put("date", save_curr_date);
        animal_map.put("time", save_curr_time);
        animal_map.put("description", description);
        animal_map.put("image", download_img_url);
        animal_map.put("category", category_name);
        animal_map.put("animal_name", animal_name);

        animal_ref.child(p_key).updateChildren(animal_map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Intent intent = new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                    startActivity(intent);

                    loading_bar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Animal is added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    loading_bar.dismiss();
                    String msg = task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error : " + msg, Toast.LENGTH_SHORT).show();

                }

            }
        });


    }


}
