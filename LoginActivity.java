package com.example.vegify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button Registerbtn;

    EditText memail,mpassword;
    Button mloginbtn;
    ProgressBar progressBar;
    FirebaseAuth fauth;
    RadioGroup mradiogroup;
    RadioButton mradiobutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        memail=findViewById(R.id.linputemail);
        mpassword=findViewById(R.id.linputpassword);
        progressBar=findViewById(R.id.progressBar);
        fauth=FirebaseAuth.getInstance();
        mloginbtn=findViewById(R.id.lloginbtn);
        mradiogroup=findViewById(R.id.lradiogrp);

        mloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=memail.getText().toString().trim();
                String password=mpassword.getText().toString().trim();
                int rid=mradiogroup.getCheckedRadioButtonId();
                mradiobutton=findViewById(rid);

                String type=mradiobutton.getText().toString();

                if(TextUtils.isEmpty(email)){
                    memail.setError("E-Mail is Required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mpassword.setError("Password is Required.");
                    return;
                }
                if(password.length() < 5){
                    mpassword.setError("Password must be atleast 6 characters.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

            fauth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful()){
                       Toast.makeText(LoginActivity.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                       if(type.equals("Vendor")){
                           startActivity(new Intent(getApplicationContext(),Vendor_home.class));
                       }
                       else{
                           startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                       }


                   }
                   else {
                       Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   }
                }
            });

            }
        });





        Registerbtn = (Button) findViewById(R.id.RegisterButton);

        Registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });
    }

    public void openRegisterActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}