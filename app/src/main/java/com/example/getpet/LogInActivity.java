package com.example.getpet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.getpet.Model.Users;
import com.example.getpet.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import io.paperdb.Paper;

public class LogInActivity extends AppCompatActivity {


    private EditText input_phone_number;
    private EditText input_password;
    private Button login_btn;
    private ProgressDialog loading_bar;
    private String parent_database_name = "Users";
    private CheckBox check_box_remember_me;

    //for admin


    private TextView admin_link;
    private TextView not_admin_link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        login_btn = (Button) findViewById(R.id.login_btn);
        input_password = (EditText) findViewById(R.id.login_password_input);
        input_phone_number = (EditText) findViewById(R.id.login_phone_number_input);
        admin_link = (TextView) findViewById(R.id.admin_panel_link);
        not_admin_link = (TextView) findViewById(R.id.not_admin_link);

        loading_bar = new ProgressDialog(this);

        check_box_remember_me = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();

            }
        });

        admin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_btn.setText("Login Admin");
                admin_link.setVisibility(View.INVISIBLE);
                not_admin_link.setVisibility(View.VISIBLE);
                parent_database_name = "Admins";
            }
        });


        not_admin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_btn.setText("Login");
                admin_link.setVisibility(View.VISIBLE);
                not_admin_link.setVisibility(View.INVISIBLE);
                parent_database_name = "Users";

            }
        });

    }



    private void loginUser() {

        String phone = input_phone_number.getText().toString();
        String password = input_password.getText().toString();

        if (TextUtils.isEmpty(phone)) {

            Toast.makeText(this, "Please write your phone", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)) {

            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();

        } else {

            loading_bar.setTitle("Login account");
            loading_bar.setMessage("Please wait while we check your data");
            loading_bar.setCanceledOnTouchOutside(false);
            loading_bar.show();

            AllowAccessToAccount(phone, password);

        }


    }

    private void AllowAccessToAccount(final String phone, final String password) {


        if (check_box_remember_me.isChecked()) {
            Paper.book().write(Prevalent.user_phone_key, phone);
            Paper.book().write(Prevalent.user_password_key, phone);
        }

        final DatabaseReference root_ref;
        root_ref = FirebaseDatabase.getInstance().getReference();


        root_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(parent_database_name).child(phone).exists()) {

                    Users usersdata = dataSnapshot.child(parent_database_name).child(phone).getValue(Users.class);

                    if (usersdata.getPhone().equals(phone)) {
                        if (usersdata.getPassword().equals(password)) {
                            if(parent_database_name.equals("Admins")){

                                Toast.makeText(LogInActivity.this, "Welcome Admin, you logged in successfully ", Toast.LENGTH_SHORT).show();
                                loading_bar.dismiss();

                                Intent intent = new Intent(LogInActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if(parent_database_name.equals("Users")){
                                Toast.makeText(LogInActivity.this, "Log in success ", Toast.LENGTH_SHORT).show();
                                loading_bar.dismiss();

                                Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                                Prevalent.online_user=usersdata;
                                startActivity(intent);
                            }
                        }
                    }
                } else {
                    Toast.makeText(LogInActivity.this, "Account with this " + phone + " number don't exist", Toast.LENGTH_SHORT).show();
                    loading_bar.dismiss();
                    //Toast.makeText(LogInActivity.this, "you need to create a new account", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
