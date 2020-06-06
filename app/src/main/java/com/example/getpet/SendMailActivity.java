package com.example.getpet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.getpet.MailAPI.JavaMailApi;

public class SendMailActivity extends AppCompatActivity {


    private EditText email;
    private Button send_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);

        email = findViewById(R.id.email_addr);
        send_btn = (Button) findViewById(R.id.send_mail_btn);


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
                Intent intent = new Intent(SendMailActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


    }

    private void sendMail() {

//        String r_list = email.getText().toString();
//        String[] recipients = r_list.split(",");
//        String subject = "GetPet - Confirmation Mail ";
//        String msg = "Thank you For your Adoption!";
//
//
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.putExtra(Intent.EXTRA_EMAIL,recipients);
//        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
//        intent.putExtra(Intent.EXTRA_TEXT,msg);
//        intent.setType("message/rfc822");
//        startActivity(Intent.createChooser(intent,"Choose an email client"));


        String mEmail = email.getText().toString();
        String subject = "GetPet - Confirmation Mail ";
        String msg = "Thank you For your Adoption!\n" +
                "\n" +
                "\n" +
                "GetPet Support Team";

        JavaMailApi javaMailApi = new JavaMailApi(this, mEmail, subject, msg);
        javaMailApi.execute();
        Toast.makeText(SendMailActivity.this, "Mail has been sent successfully", Toast.LENGTH_SHORT).show();
    }
}
