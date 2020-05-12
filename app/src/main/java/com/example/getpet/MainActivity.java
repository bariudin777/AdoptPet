package com.example.getpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.getpet.Model.Users;
import com.example.getpet.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {


    private Button join_now_btn, login_btn;
    private ProgressDialog loading_bar;

     /*
      Function Name: onCreate
      Description: creates the connection between the view to the java code
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        join_now_btn = (Button) findViewById(R.id.main_join_now_btn);
        login_btn = (Button) findViewById(R.id.main_login_btn);
        loading_bar = new ProgressDialog(this);

        Paper.init(this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });


        join_now_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //create unique key
        String user_phone_key = Paper.book().read(Prevalent.user_phone_key);
        String user_password_key = Paper.book().read(Prevalent.user_password_key);

        if (user_phone_key != "" && user_password_key != "") {
            if (!TextUtils.isEmpty(user_phone_key) && !TextUtils.isEmpty(user_password_key)) {
                AllowAccess(user_phone_key, user_password_key);

                loading_bar.setTitle("AllReady Logged In");
                loading_bar.setMessage("Please wait...");
                loading_bar.setCanceledOnTouchOutside(false);
                loading_bar.show();

            }
        }


    }

     /*
      Function Name: AllowAccess
      Description: Checks if the phone and the password is valid
     */

    private void AllowAccess(final String phone, final String password) {


        final DatabaseReference root_ref;
        root_ref = FirebaseDatabase.getInstance().getReference();


        root_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Users").child(phone).exists()) {

                    Users usersdata = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    if (usersdata.getPhone().equals(phone)) {
                        if (usersdata.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "Log in success ", Toast.LENGTH_SHORT).show();
                            loading_bar.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.online_user = usersdata;
                            startActivity(intent);
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Account with this " + phone + "number don't exist", Toast.LENGTH_SHORT).show();
                    loading_bar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
}
