package com.example.getpet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getpet.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profile_img_view;
    private EditText full_name_edit_text;
    private EditText user_phone_edit_text;
    private EditText address_edit_text;
    private TextView profile_change_btn, close_btn, save_btn;
    //allow the user to change the image
    private Uri image_uri;
    private String my_uri = "";
    private StorageReference storage_profile_image_eference;
    private String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storage_profile_image_eference = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        //Init the connection between the view
        profile_img_view = (CircleImageView) findViewById(R.id.settings_profile_img);
        full_name_edit_text = (EditText) findViewById(R.id.settings_full_name);
        user_phone_edit_text = (EditText) findViewById(R.id.settings_phone_number);
        address_edit_text = (EditText) findViewById(R.id.settings_address);

        profile_change_btn = (TextView) findViewById(R.id.profile_img_change_btn);
        close_btn = (TextView) findViewById(R.id.close_settings_btn);
        save_btn = (TextView) findViewById(R.id.update_settings_btn);

        userInfoDisplay(profile_img_view, full_name_edit_text, user_phone_edit_text, address_edit_text);

        //When user click on "close" button
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //When user click on "save"(update) button
        save_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")) {
                    userInfoSaved();

                } else {

                    updateUserInfo();
                }
            }

        });


        profile_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";
                CropImage.activity(image_uri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK
                && data != null) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            image_uri = result.getUri();
            profile_img_view.setImageURI(image_uri);
        } else {
            Toast.makeText(SettingsActivity.this, "Error , try again...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }

    }

    private void updateUserInfo() {



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", full_name_edit_text.getText().toString());
        map.put("address", address_edit_text.getText().toString());
        map.put("phoneOrder", user_phone_edit_text.getText().toString());
        reference.child(Prevalent.online_user.getPhone()).updateChildren(map);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        finish();


    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(full_name_edit_text.getText().toString())) {
            Toast.makeText(SettingsActivity.this, "You have to fill your name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address_edit_text.getText().toString())) {
            Toast.makeText(SettingsActivity.this, "You have to fill your address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(user_phone_edit_text.getText().toString())) {
            Toast.makeText(SettingsActivity.this, "You have to fill your phone", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            uploadImage();

        }

    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Upload Profile");
        progressDialog.setMessage("Please wait , while uploading your profile image");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (image_uri != null) {
            final StorageReference file_ref = storage_profile_image_eference.child(Prevalent.online_user.getPhone() + ".jpg");
            StorageTask upload_task = file_ref.putFile(image_uri);
            upload_task.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return file_ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadURL = task.getResult();
                        my_uri = downloadURL.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", full_name_edit_text.getText().toString());
                        map.put("address", address_edit_text.getText().toString());
                        map.put("phoneOrder", user_phone_edit_text.getText().toString());
                        map.put("image", my_uri);
                        reference.child(Prevalent.online_user.getPhone()).updateChildren(map);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error , try again...", Toast.LENGTH_SHORT).show();


                    }

                }
            });
        } else {
            Toast.makeText(SettingsActivity.this, "Image not selected...", Toast.LENGTH_SHORT).show();

        }
    }

    private void userInfoDisplay(final CircleImageView profile_img_view, final EditText full_name_edit_text, final EditText user_phone_edit_text, final EditText address_edit_text) {

        DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.online_user.getPhone());
        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if the user already exists
                if (dataSnapshot.exists()) {
                    //if image is exists- we display the info on settings activity
                    if (dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        Picasso.get().load(image).into(profile_img_view);
                        full_name_edit_text.setText(name);
                        user_phone_edit_text.setText(phone);
                        address_edit_text.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
